package com.adentweets.app.data.remote.search

import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSearchSource @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val postsRef = database.getReference(Constants.POSTS_NODE)
    private val usersRef = database.getReference(Constants.USERS_NODE)
    private val trendingRef = database.getReference(Constants.TRENDING_NODE)

    suspend fun searchUsers(query: String, pageSize: Int): Resource<List<User>> {
        return try {
            // Search by username prefix
            val snapshot = usersRef.child(Constants.SEARCH_INDEX_NODE)
                .orderByChild("username").startAt(query.lowercase()).endAt(query.lowercase() + "\uf8ff")
                .limitToFirst(pageSize).get().await()
            val users = mutableListOf<User>()
            for (child in snapshot.children) {
                val uid = child.key ?: continue
                val userSnap = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
                val user = userSnap.getValue(User::class.java)?.copy(uid = uid)
                if (user != null) users.add(user)
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search failed", 9999)
        }
    }

    suspend fun searchPosts(query: String, pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return try {
            // Client-side search since RTDB doesn't support full-text search natively
            val snapshot = postsRef.orderByChild("createdAt").limitToLast(200).get().await()
            val allPosts = snapshot.children.mapNotNull { child ->
                child.getValue(Post::class.java)?.let { it.copy(postId = child.key!!) }
            }
            val filtered = allPosts.filter { post ->
                post.content.contains(query, ignoreCase = true)
            }.sortedByDescending { it.createdAt }.take(pageSize)
            Resource.Success(filtered)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search failed", 9999)
        }
    }

    suspend fun searchHashtags(query: String): Resource<List<TrendingTopic>> {
        return try {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val snapshot = trendingRef.child(dateStr).get().await()
            val topics = mutableListOf<TrendingTopic>()
            var rank = 1
            for (child in snapshot.children) {
                val hashtag = child.key ?: continue
                if (hashtag.contains(query, ignoreCase = true)) {
                    val postCount = child.getValue(Long::class.java) ?: 0L
                    topics.add(TrendingTopic(hashtag = hashtag, postCount = postCount, rank = rank++))
                }
            }
            Resource.Success(topics)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Hashtag search failed", 9999)
        }
    }

    suspend fun getTrendingTopics(limit: Int): Resource<List<TrendingTopic>> {
        return try {
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val snapshot = trendingRef.child(dateStr).get().await()
            val topics = mutableListOf<TrendingTopic>()
            var rank = 1
            for (child in snapshot.children.sortedByDescending { it.getValue(Long::class.java) ?: 0L }) {
                if (rank > limit) break
                val hashtag = child.key ?: continue
                val postCount = child.getValue(Long::class.java) ?: 0L
                topics.add(TrendingTopic(hashtag = hashtag, postCount = postCount, rank = rank++))
            }
            Resource.Success(topics)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get trending", 9999)
        }
    }

    suspend fun getPostsByHashtag(hashtag: String, pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return try {
            val cleanTag = hashtag.removePrefix("#").lowercase()
            val snapshot = postsRef.orderByChild("createdAt").limitToLast(200).get().await()
            val filtered = snapshot.children.mapNotNull { child ->
                val post = child.getValue(Post::class.java) ?: return@mapNotNull null
                if (post.content.contains("#$cleanTag", ignoreCase = true) ||
                    post.content.contains("#${cleanTag}", ignoreCase = true)) {
                    post.copy(postId = child.key!!)
                } else null
            }.sortedByDescending { it.createdAt }.take(pageSize)
            Resource.Success(filtered)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get hashtag posts", 9999)
        }
    }

    suspend fun getSuggestedUsers(pageSize: Int): Resource<List<User>> {
        return try {
            val snapshot = usersRef.child(Constants.SEARCH_INDEX_NODE)
                .orderByChild("followerCount").limitToLast(pageSize).get().await()
            val users = mutableListOf<User>()
            for (child in snapshot.children) {
                val uid = child.key ?: continue
                val userSnap = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
                val user = userSnap.getValue(User::class.java)?.copy(uid = uid)
                if (user != null) users.add(user)
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get suggestions", 9999)
        }
    }
}