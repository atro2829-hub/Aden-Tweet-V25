package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    val isAuthenticated: Flow<Boolean>

    suspend fun loginWithEmail(email: String, password: String): Resource<User>
    suspend fun registerWithEmail(email: String, password: String, displayName: String, username: String): Resource<User>
    suspend fun loginWithGoogle(idToken: String): Resource<User>
    suspend fun loginWithPhone(verificationId: String, otp: String): Resource<User>
    suspend fun sendOtp(phoneNumber: String, activity: android.app.Activity): Resource<Unit>
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    suspend fun logout(): Resource<Unit>
    suspend fun getCurrentUser(): User?
    fun getUid(): String?
}