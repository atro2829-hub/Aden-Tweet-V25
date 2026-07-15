package com.adentweets.app.data.local.dao

import androidx.room.*
import com.adentweets.app.data.local.entity.CachedUser
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM cached_users WHERE uid = :uid")
    fun getUser(uid: String): Flow<CachedUser?>

    @Query("SELECT * FROM cached_users WHERE username LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<CachedUser>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: CachedUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<CachedUser>)

    @Query("DELETE FROM cached_users WHERE cachedAt < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)
}