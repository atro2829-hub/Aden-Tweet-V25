package com.adentweets.app.domain.usecase.user

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.UserRepository
import javax.inject.Inject

class FollowUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, isCurrentlyFollowing: Boolean): Resource<Unit> {
        return if (isCurrentlyFollowing) {
            userRepository.unfollowUser(uid)
        } else {
            userRepository.followUser(uid)
        }
    }
}