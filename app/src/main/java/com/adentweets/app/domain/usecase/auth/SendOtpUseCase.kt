package com.adentweets.app.domain.usecase.auth

import android.app.Activity
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.AuthRepository
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        phoneNumber: String,
        activity: Activity
    ): Resource<Unit> {
        return authRepository.sendOtp(phoneNumber, activity)
    }
}