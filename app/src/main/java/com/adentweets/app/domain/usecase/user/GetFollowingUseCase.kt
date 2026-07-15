package com.adentweets.app.domain.usecase.user

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.UserRepository
import javax.inject.Inject

class GetFollowingUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(uid: String, pageSize: Int = 20, lastKey: String? = null): Resource<List<User>> {
        return userRepository.getFollowing(uid, pageSize, lastKey)
    }
}