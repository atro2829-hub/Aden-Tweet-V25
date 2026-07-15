package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(uid: String): Resource<User>
    suspend fun updateProfile(displayName: String?, bio: String?, location: String?, website: String?, avatarBase64: String?, bannerBase64: String?): Resource<Unit>
    suspend fun followUser(uid: String): Resource<Unit>
    suspend fun unfollowUser(uid: String): Resource<Unit>
    suspend fun blockUser(uid: String): Resource<Unit>
    suspend fun unblockUser(uid: String): Resource<Unit>
    suspend fun muteUser(uid: String): Resource<Unit>
    suspend fun unmuteUser(uid: String): Resource<Unit>
    suspend fun getFollowers(uid: String, pageSize: Int, lastKey: String?): Resource<List<User>>
    suspend fun getFollowing(uid: String, pageSize: Int, lastKey: String?): Resource<List<User>>
    suspend fun getBlockedUsers(): Resource<List<User>>
    suspend fun getMutedUsers(): Resource<List<User>>
    suspend fun searchUsers(query: String, pageSize: Int): Resource<List<User>>
    fun observeUserProfile(uid: String): Flow<User?>
    fun isFollowing(uid: String): Flow<Boolean>
    fun isBlocked(uid: String): Flow<Boolean>
    fun getMutualFollowers(uid: String): Flow<List<String>>
}