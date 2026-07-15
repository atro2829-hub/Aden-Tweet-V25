package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.user.FirebaseUserSource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firebaseUserSource: FirebaseUserSource
) : UserRepository {

    override suspend fun getUserProfile(uid: String) = firebaseUserSource.getUserProfile(uid)

    override suspend fun updateProfile(
        displayName: String?, bio: String?, location: String?,
        website: String?, avatarBase64: String?, bannerBase64: String?
    ) = firebaseUserSource.updateProfile(displayName, bio, location, website, avatarBase64, bannerBase64)

    override suspend fun followUser(uid: String) = firebaseUserSource.followUser(uid)
    override suspend fun unfollowUser(uid: String) = firebaseUserSource.unfollowUser(uid)
    override suspend fun blockUser(uid: String) = firebaseUserSource.blockUser(uid)
    override suspend fun unblockUser(uid: String) = firebaseUserSource.unblockUser(uid)
    override suspend fun muteUser(uid: String) = firebaseUserSource.muteUser(uid)
    override suspend fun unmuteUser(uid: String) = firebaseUserSource.unmuteUser(uid)

    override suspend fun getFollowers(uid: String, pageSize: Int, lastKey: String?) = firebaseUserSource.getFollowers(uid, pageSize, lastKey)
    override suspend fun getFollowing(uid: String, pageSize: Int, lastKey: String?) = firebaseUserSource.getFollowing(uid, pageSize, lastKey)
    override suspend fun getBlockedUsers() = firebaseUserSource.getBlockedUsers()
    override suspend fun getMutedUsers() = firebaseUserSource.getMutedUsers()
    override suspend fun searchUsers(query: String, pageSize: Int) = firebaseUserSource.searchUsers(query, pageSize)

    override fun observeUserProfile(uid: String) = firebaseUserSource.observeUserProfile(uid)
    override fun isFollowing(uid: String) = firebaseUserSource.isFollowing(uid)
    override fun isBlocked(uid: String) = firebaseUserSource.isBlocked(uid)
    override fun getMutualFollowers(uid: String) = firebaseUserSource.getMutualFollowers(uid)
}