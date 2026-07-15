package com.adentweets.app.data.remote.post

import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebasePostSource @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) {
    private val postsRef = database.getReference(Constants.POSTS_NODE)
    private val usersRef = database.getReference(Constants.USERS_NODE)
    private val feedsRef = database.getReference(Constants.FEEDS_NODE)

    suspend fun createPost(
        content: String,
        mediaItems: List<MediaItem>,
        visibility: String,
        replyToPostId: String,
        poll: Poll?
    ): Resource<Post> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val postId = UUID.randomUUID().toString()
            val post = Post(
                postId = postId,
                authorUid = uid,
                content = content,
                mediaItems = mediaItems,
                createdAt = System.currentTimeMillis(),
                visibility = visibility,
                replyToPostId = replyToPostId,
                poll = poll
            )

            // Write post
            postsRef.child(postId).setValue(post).await()

            // Add to user's posts
            usersRef.child(uid).child(Constants.USER_POSTS_NODE).child(postId).setValue(true).await()

            // If reply, add to parent's comments
            if (replyToPostId.isNotBlank()) {
                postsRef.child(replyToPostId).child(Constants.COMMENTS_NODE).child(postId).setValue(true).await()
                postsRef.child(replyToPostId).child("replyCount")
                    .runTransaction(object : Transaction.Handler {
                        override fun doTransaction(currentData: MutableData): Transaction.Result {
                            val count = currentData.getValue(Long::class.java) ?: 0L
                            currentData.value = count + 1
                            return Transaction.success(currentData)
                        }
                        override fun onComplete(error: com.google.firebase.database.DatabaseError?, committed: Boolean, currentData: com.google.firebase.database.DataSnapshot?) {}
                    })
            }

            // Fan-out to followers' feeds (only for top-level posts)
            if (replyToPostId.isBlank()) {
                fanOutToFollowers(uid, postId)
            }

            Resource.Success(post)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create post", 3003)
        }
    }

    private suspend fun fanOutToFollowers(authorUid: String, postId: String) {
        val followersSnapshot = usersRef.child(authorUid).child(Constants.FOLLOWERS_NODE).get().await()
        for (followerUid in followersSnapshot.children) {
            feedsRef.child(followerUid.key.toString()).child(postId).setValue(true).await()
        }
        // Also add to own feed
        feedsRef.child(authorUid).child(postId).setValue(true).await()
    }

    suspend fun deletePost(postId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val snapshot = postsRef.child(postId).get().await()
            if (!snapshot.exists()) return Resource.Error("Post not found", 3001)
            val post = snapshot.getValue(Post::class.java)!!
            if (post.authorUid != uid) return Resource.Error("Cannot delete other user's post", 6001)

            postsRef.child(postId).removeValue().await()
            usersRef.child(uid).child(Constants.USER_POSTS_NODE).child(postId).removeValue().await()
            if (post.replyToPostId.isNotBlank()) {
                postsRef.child(post.replyToPostId).child(Constants.COMMENTS_NODE).child(postId).removeValue().await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete post", 3002)
        }
    }

    suspend fun getPost(postId: String): Resource<Post> {
        return try {
            val snapshot = postsRef.child(postId).get().await()
            if (!snapshot.exists()) return Resource.Error("Post not found", 3001)
            val post = snapshot.getValue(Post::class.java)!!
            val author = getUser(post.authorUid)
            val uid = auth.currentUser?.uid
            val isLiked = uid?.let { postsRef.child(postId).child(Constants.LIKES_NODE).child(it).get().await().exists() } ?: false
            val isRetweeted = uid?.let { postsRef.child(postId).child(Constants.RETWEETS_NODE).child(it).get().await().exists() } ?: false
            val isBookmarked = uid?.let { usersRef.child(it).child(Constants.BOOKMARKS_NODE).child(postId).get().await().exists() } ?: false
            Resource.Success(post.copy(author = author, isLikedByCurrentUser = isLiked, isRetweetedByCurrentUser = isRetweeted, isBookmarkedByCurrentUser = isBookmarked))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get post", 3001)
        }
    }

    suspend fun likePost(postId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            postsRef.child(postId).child(Constants.LIKES_NODE).child(uid).setValue(true).await()
            postsRef.child(postId).child("likeCount").runTransaction(object : Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val count = data.getValue(Long::class.java) ?: 0L
                    data.value = count + 1
                    return Transaction.success(data)
                }
                override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
            })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to like post", 9999)
        }
    }

    suspend fun unlikePost(postId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            postsRef.child(postId).child(Constants.LIKES_NODE).child(uid).removeValue().await()
            postsRef.child(postId).child("likeCount").runTransaction(object : Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val count = data.getValue(Long::class.java) ?: 0L
                    data.value = (count - 1).coerceAtLeast(0)
                    return Transaction.success(data)
                }
                override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
            })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unlike post", 9999)
        }
    }

    suspend fun retweetPost(postId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            postsRef.child(postId).child(Constants.RETWEETS_NODE).child(uid).setValue(System.currentTimeMillis()).await()
            postsRef.child(postId).child("retweetCount").runTransaction(object : Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val count = data.getValue(Long::class.java) ?: 0L
                    data.value = count + 1
                    return Transaction.success(data)
                }
                override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
            })
            feedsRef.child(uid).child(postId).setValue(true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to retweet", 9999)
        }
    }

    suspend fun undoRetweet(postId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            postsRef.child(postId).child(Constants.RETWEETS_NODE).child(uid).removeValue().await()
            postsRef.child(postId).child("retweetCount").runTransaction(object : Transaction.Handler {
                override fun doTransaction(data: MutableData): Transaction.Result {
                    val count = data.getValue(Long::class.java) ?: 0L
                    data.value = (count - 1).coerceAtLeast(0)
                    return Transaction.success(data)
                }
                override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
            })
            feedsRef.child(uid).child(postId).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to undo retweet", 9999)
        }
    }

    suspend fun quotePost(postId: String, quoteContent: String): Resource<Post> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val newPostId = UUID.randomUUID().toString()
            val newPost = Post(
                postId = newPostId,
                authorUid = uid,
                content = quoteContent,
                quotePostId = postId,
                createdAt = System.currentTimeMillis()
            )
            postsRef.child(newPostId).setValue(newPost).await()
            usersRef.child(uid).child(Constants.USER_POSTS_NODE).child(newPostId).setValue(true).await()
            fanOutToFollowers(uid, newPostId)
            Resource.Success(newPost)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to quote post", 9999)
        }
    }

    suspend fun bookmarkPost(postId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            usersRef.child(uid).child(Constants.BOOKMARKS_NODE).child(postId).setValue(true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to bookmark", 9999)
        }
    }

    suspend fun removeBookmark(postId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            usersRef.child(uid).child(Constants.BOOKMARKS_NODE).child(postId).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove bookmark", 9999)
        }
    }

    suspend fun getReplies(postId: String, pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return try {
            var query = postsRef.child(postId).child(Constants.COMMENTS_NODE)
                .orderByKey().limitToFirst(pageSize)
            if (lastKey != null) {
                query = query.startAfter(lastKey).limitToFirst(pageSize)
            }
            val snapshot = query.get().await()
            val posts = mutableListOf<Post>()
            for (commentRef in snapshot.children) {
                val commentId = commentRef.key ?: continue
                val postSnapshot = postsRef.child(commentId).get().await()
                val post = postSnapshot.getValue(Post::class.java)?.let { p ->
                    val author = getUser(p.authorUid)
                    p.copy(author = author)
                }
                if (post != null) posts.add(post)
            }
            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get replies", 9999)
        }
    }

    suspend fun getUserPosts(uid: String, pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return try {
            var query = usersRef.child(uid).child(Constants.USER_POSTS_NODE)
                .orderByKey().limitToLast(pageSize)
            if (lastKey != null) {
                query = query.endBefore(lastKey).limitToLast(pageSize)
            }
            val snapshot = query.get().await()
            val posts = mutableListOf<Post>()
            for (postRef in snapshot.children) {
                val postId = postRef.key ?: continue
                val postSnapshot = postsRef.child(postId).get().await()
                val post = postSnapshot.getValue(Post::class.java)?.let { p ->
                    val author = getUser(p.authorUid)
                    p.copy(author = author)
                }
                if (post != null) posts.add(post)
            }
            posts.reverse()
            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user posts", 9999)
        }
    }

    suspend fun getBookmarks(pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            var query = usersRef.child(uid).child(Constants.BOOKMARKS_NODE)
                .orderByKey().limitToLast(pageSize)
            if (lastKey != null) {
                query = query.endBefore(lastKey).limitToLast(pageSize)
            }
            val snapshot = query.get().await()
            val posts = mutableListOf<Post>()
            for (bookmarkRef in snapshot.children) {
                val postId = bookmarkRef.key ?: continue
                val postSnapshot = postsRef.child(postId).get().await()
                val post = postSnapshot.getValue(Post::class.java)?.let { p ->
                    val author = getUser(p.authorUid)
                    p.copy(author = author, isBookmarkedByCurrentUser = true)
                }
                if (post != null) posts.add(post)
            }
            posts.reverse()
            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get bookmarks", 9999)
        }
    }

    suspend fun getFeedPosts(feedKeys: Map<String, Boolean>, pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return try {
            val postIds = feedKeys.keys.toList()
            if (postIds.isEmpty()) return Resource.Success(emptyList())

            val startIdx = if (lastKey != null) postIds.indexOf(lastKey) + 1 else 0
            val batch = postIds.drop(startIdx).take(pageSize)

            val posts = mutableListOf<Post>()
            val uid = auth.currentUser?.uid
            for (postId in batch) {
                val snapshot = postsRef.child(postId).get().await()
                val post = snapshot.getValue(Post::class.java)?.let { p ->
                    val author = getUser(p.authorUid)
                    val isLiked = uid?.let { postsRef.child(postId).child(Constants.LIKES_NODE).child(it).get().await().exists() } ?: false
                    val isRetweeted = uid?.let { postsRef.child(postId).child(Constants.RETWEETS_NODE).child(it).get().await().exists() } ?: false
                    val isBookmarked = uid?.let { usersRef.child(it).child(Constants.BOOKMARKS_NODE).child(postId).get().await().exists() } ?: false
                    p.copy(author = author, isLikedByCurrentUser = isLiked, isRetweetedByCurrentUser = isRetweeted, isBookmarkedByCurrentUser = isBookmarked)
                }
                if (post != null) posts.add(post)
            }
            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get feed", 9999)
        }
    }

    fun observePost(postId: String): Flow<Post?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val post = snapshot.getValue(Post::class.java)
                if (post != null) {
                    trySend(post)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        postsRef.child(postId).addValueEventListener(listener)
        awaitClose { postsRef.child(postId).removeEventListener(listener) }
    }

    private suspend fun getUser(uid: String): User? {
        return try {
            val snapshot = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
            if (snapshot.exists()) snapshot.getValue(User::class.java)?.copy(uid = uid) else null
        } catch (e: Exception) { null }
    }
}