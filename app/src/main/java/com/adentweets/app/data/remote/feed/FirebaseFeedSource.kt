package com.adentweets.app.data.remote.feed

import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseFeedSource @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) {
    private val feedsRef = database.getReference(Constants.FEEDS_NODE)
    private val postsRef = database.getReference(Constants.POSTS_NODE)
    private val usersRef = database.getReference(Constants.USERS_NODE)

    suspend fun getFollowingFeed(pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            var query = feedsRef.child(uid).orderByKey().limitToLast(pageSize)
            if (lastKey != null) {
                query = query.endBefore(lastKey).limitToLast(pageSize)
            }
            val snapshot = query.get().await()
            val postIds = snapshot.children.reversed().mapNotNull { it.key }
            val posts = fetchPostsByIds(postIds)
            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get feed", 9999)
        }
    }

    suspend fun getForYouFeed(pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            // For You: combine own feed + popular posts from followed users
            var query = feedsRef.child(uid).orderByKey().limitToLast(pageSize)
            if (lastKey != null) {
                query = query.endBefore(lastKey).limitToLast(pageSize)
            }
            val snapshot = query.get().await()
            val postIds = snapshot.children.reversed().mapNotNull { it.key }
            val posts = fetchPostsByIds(postIds)

            // Also fetch some recent popular posts
            val recentPosts = postsRef.orderByChild("createdAt").limitToLast(20).get().await()
            val popularPostIds = recentPosts.children
                .sortedByDescending { it.child("likeCount").getValue(Long::class.java) ?: 0L }
                .take(5)
                .mapNotNull { it.key }
                .filter { it !in postIds }

            val popularPosts = fetchPostsByIds(popularPostIds)
            val combined = (posts + popularPosts).distinctBy { it.postId }.take(pageSize)
            Resource.Success(combined)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get For You feed", 9999)
        }
    }

    fun observeFollowingFeed(): Flow<List<Post>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val feedListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val postId = snapshot.key ?: return
                kotlinx.coroutines.GlobalScope.launch {
                    val post = fetchSinglePost(postId)
                    if (post != null) trySend(listOf(post))
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        feedsRef.child(uid).addChildEventListener(feedListener)
        awaitClose { feedsRef.child(uid).removeEventListener(feedListener) }
    }

    suspend fun loadMorePosts(lastKey: String?, pageSize: Int, feedType: String): Resource<List<Post>> {
        return if (feedType == "following") getFollowingFeed(pageSize, lastKey)
        else getForYouFeed(pageSize, lastKey)
    }

    private suspend fun fetchPostsByIds(postIds: List<String>): List<Post> {
        val uid = auth.currentUser?.uid
        val posts = mutableListOf<Post>()
        for (postId in postIds) {
            val post = fetchSinglePost(postId) ?: continue
            val isLiked = uid?.let {
                postsRef.child(postId).child(Constants.LIKES_NODE).child(it).get().await().exists()
            } ?: false
            val isRetweeted = uid?.let {
                postsRef.child(postId).child(Constants.RETWEETS_NODE).child(it).get().await().exists()
            } ?: false
            val isBookmarked = uid?.let {
                usersRef.child(it).child(Constants.BOOKMARKS_NODE).child(postId).get().await().exists()
            } ?: false
            posts.add(post.copy(
                isLikedByCurrentUser = isLiked,
                isRetweetedByCurrentUser = isRetweeted,
                isBookmarkedByCurrentUser = isBookmarked
            ))
        }
        return posts
    }

    private suspend fun fetchSinglePost(postId: String): Post? {
        return try {
            val snapshot = postsRef.child(postId).get().await()
            if (!snapshot.exists()) return null
            val post = snapshot.getValue(Post::class.java) ?: return null
            val author = try {
                val userSnap = usersRef.child(post.authorUid).child(Constants.PROFILE_NODE).get().await()
                userSnap.getValue(com.adentweets.app.domain.model.User::class.java)?.copy(uid = post.authorUid)
            } catch (e: Exception) { null }
            post.copy(author = author)
        } catch (e: Exception) { null }
    }
}