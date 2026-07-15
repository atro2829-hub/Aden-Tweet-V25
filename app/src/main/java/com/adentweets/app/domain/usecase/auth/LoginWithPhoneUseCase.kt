package com.adentweets.app.domain.usecase.auth

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(verificationId: String, otp: String): Resource<User> {
        return authRepository.loginWithPhone(verificationId, otp)
    }
}