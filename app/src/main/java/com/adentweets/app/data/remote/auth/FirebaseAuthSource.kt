package com.adentweets.app.data.remote.auth

import android.util.Log
import com.adentweets.app.core.util.AdenTweetException
import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.ErrorCodes
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.google.firebase.auth.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {
    private val usersRef = database.getReference(Constants.USERS_NODE)

    suspend fun loginWithEmail(email: String, password: String): Resource<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = getUserFromDatabase(result.user!!.uid)
            Resource.Success(user)
        } catch (e: FirebaseAuthException) {
            Resource.Error(
                message = mapFirebaseAuthError(e),
                errorCode = ErrorCodes.AUTH_INVALID_CREDENTIALS
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed", ErrorCodes.UNKNOWN)
        }
    }

    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        username: String
    ): Resource<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user!!.uid
            val user = User(
                uid = uid,
                email = email,
                username = username.lowercase(),
                displayName = displayName,
                createdAt = System.currentTimeMillis(),
                isOnline = true,
                lastSeen = System.currentTimeMillis()
            )
            usersRef.child(uid).child(Constants.PROFILE_NODE).setValue(user).await()
            usersRef.child(uid).child("username").setValue(username.lowercase()).await()
            Resource.Success(user)
        } catch (e: FirebaseAuthUserCollisionException) {
            Resource.Error("Email already registered", ErrorCodes.AUTH_EMAIL_ALREADY_EXISTS)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Resource.Error("Password is too weak", ErrorCodes.AUTH_WEAK_PASSWORD)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed", ErrorCodes.UNKNOWN)
        }
    }

    suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user!!.uid
            val existingUser = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
            if (existingUser.exists()) {
                val user = existingUser.getValue(User::class.java)!!.copy(uid = uid)
                Resource.Success(user)
            } else {
                val firebaseUser = result.user!!
                val newUser = User(
                    uid = uid,
                    email = firebaseUser.email ?: "",
                    username = (firebaseUser.displayName?.lowercase()?.replace(" ", "") ?: "user") + uid.take(4),
                    displayName = firebaseUser.displayName ?: "User",
                    avatarBase64 = "",
                    createdAt = System.currentTimeMillis(),
                    isOnline = true,
                    lastSeen = System.currentTimeMillis()
                )
                usersRef.child(uid).child(Constants.PROFILE_NODE).setValue(newUser).await()
                Resource.Success(newUser)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Google sign-in failed", ErrorCodes.UNKNOWN)
        }
    }

    suspend fun sendOtp(
        phoneNumber: String,
        activity: android.app.Activity,
        callback: (PhoneAuthCredential) -> Unit
    ): Resource<Unit> {
        return try {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        // Auto-verification handled in ViewModel
                    }

                    override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                        Log.e("AuthSource", "OTP failed: ${e.message}")
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        // Stored in ViewModel
                    }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send OTP", ErrorCodes.UNKNOWN)
        }
    }

    suspend fun verifyOtp(verificationId: String, otp: String): Resource<User> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user!!.uid
            val existingUser = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
            if (existingUser.exists()) {
                val user = existingUser.getValue(User::class.java)!!.copy(uid = uid)
                Resource.Success(user)
            } else {
                val newUser = User(
                    uid = uid,
                    username = "user${uid.take(4)}",
                    displayName = "User",
                    createdAt = System.currentTimeMillis(),
                    isOnline = true,
                    lastSeen = System.currentTimeMillis()
                )
                usersRef.child(uid).child(Constants.PROFILE_NODE).setValue(newUser).await()
                Resource.Success(newUser)
            }
        } catch (e: Exception) {
            Resource.Error("Invalid OTP code", ErrorCodes.AUTH_INVALID_CREDENTIALS)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send reset email", ErrorCodes.UNKNOWN)
        }
    }

    suspend fun logout(): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                usersRef.child(uid).child(Constants.PROFILE_NODE).child("isOnline").setValue(false)
                usersRef.child(uid).child(Constants.PROFILE_NODE).child("lastSeen")
                    .setValue(ServerValue.TIMESTAMP)
            }
            auth.signOut()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Logout failed", ErrorCodes.UNKNOWN)
        }
    }

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        // Note: This returns a partial user; full user should be fetched from DB
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            displayName = firebaseUser.displayName ?: ""
        )
    }

    fun getUid(): String? = auth.currentUser?.uid

    fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                usersRef.child(firebaseUser.uid).child(Constants.PROFILE_NODE)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            if (user != null) {
                                trySend(user.copy(uid = firebaseUser.uid))
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun isAuthenticated(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private suspend fun getUserFromDatabase(uid: String): User {
        val snapshot = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
        return if (snapshot.exists()) {
            snapshot.getValue(User::class.java)!!.copy(uid = uid)
        } else {
            throw AdenTweetException("User profile not found", ErrorCodes.USER_NOT_FOUND)
        }
    }

    private fun mapFirebaseAuthError(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
            "ERROR_USER_NOT_FOUND" -> "No account found with this email"
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Try again later."
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            else -> e.message ?: "Authentication failed"
        }
    }
}