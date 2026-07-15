package com.adentweets.app.domain.usecase.user

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        displayName: String? = null,
        bio: String? = null,
        location: String? = null,
        website: String? = null,
        avatarBase64: String? = null,
        bannerBase64: String? = null
    ): Resource<Unit> {
        return userRepository.updateProfile(
            displayName, bio, location, website, avatarBase64, bannerBase64
        )
    }
}