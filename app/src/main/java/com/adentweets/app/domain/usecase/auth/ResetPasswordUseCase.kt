package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        return authRepository.sendPasswordResetEmail(email)
    }
}