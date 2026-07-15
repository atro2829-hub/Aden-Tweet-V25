package com.adentweets.app.data.repository

import android.app.Activity
import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.auth.FirebaseAuthSource
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthSource: FirebaseAuthSource
) : AuthRepository {

    override val currentUser: Flow<User?> get() = firebaseAuthSource.observeCurrentUser()
    override val isAuthenticated: Flow<Boolean> get() = firebaseAuthSource.isAuthenticated()

    override suspend fun loginWithEmail(email: String, password: String): Resource<User> {
        return firebaseAuthSource.loginWithEmail(email, password)
    }

    override suspend fun registerWithEmail(email: String, password: String, displayName: String, username: String): Resource<User> {
        return firebaseAuthSource.registerWithEmail(email, password, displayName, username)
    }

    override suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return firebaseAuthSource.loginWithGoogle(idToken)
    }

    override suspend fun loginWithPhone(verificationId: String, otp: String): Resource<User> {
        return firebaseAuthSource.verifyOtp(verificationId, otp)
    }

    override suspend fun sendOtp(phoneNumber: String, activity: Activity): Resource<Unit> {
        return firebaseAuthSource.sendOtp(phoneNumber, activity) {}
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return firebaseAuthSource.sendPasswordResetEmail(email)
    }

    override suspend fun logout(): Resource<Unit> {
        return firebaseAuthSource.logout()
    }

    override suspend fun getCurrentUser(): User? {
        return firebaseAuthSource.getCurrentUser()
    }

    override fun getUid(): String? {
        return firebaseAuthSource.getUid()
    }
}