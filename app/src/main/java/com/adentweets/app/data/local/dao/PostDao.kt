package com.adentweets.app.data.local.dao

import androidx.room.*
import com.adentweets.app.data.local.entity.CachedPost
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM cached_posts WHERE feedType = :feedType ORDER BY createdAt DESC LIMIT :limit")
    fun getPostsByFeed(feedType: String, limit: Int = 50): Flow<List<CachedPost>>

    @Query("SELECT * FROM cached_posts WHERE authorUid = :uid ORDER BY createdAt DESC")
    fun getPostsByUser(uid: String): Flow<List<CachedPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<CachedPost>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CachedPost)

    @Query("DELETE FROM cached_posts WHERE postId = :postId")
    suspend fun deletePost(postId: String)

    @Query("DELETE FROM cached_posts WHERE feedType = :feedType")
    suspend fun clearFeed(feedType: String)

    @Query("SELECT COUNT(*) FROM cached_posts WHERE feedType = :feedType")
    suspend fun getPostCount(feedType: String): Int
}