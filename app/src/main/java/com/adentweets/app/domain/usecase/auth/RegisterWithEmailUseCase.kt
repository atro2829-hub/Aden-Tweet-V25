package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, displayName: String, username: String): Resource<User> {
        return authRepository.registerWithEmail(email, password, displayName, username)
    }
}