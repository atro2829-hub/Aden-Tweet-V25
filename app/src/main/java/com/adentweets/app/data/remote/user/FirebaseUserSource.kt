package com.adentweets.app.data.remote.user

import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserSource @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) {
    private val usersRef = database.getReference(Constants.USERS_NODE)

    suspend fun getUserProfile(uid: String): Resource<User> {
        return try {
            val snapshot = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
            if (!snapshot.exists()) return Resource.Error("User not found", 4001)
            val user = snapshot.getValue(User::class.java)!!.copy(uid = uid)
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get user profile", 4001)
        }
    }

    suspend fun updateProfile(
        displayName: String?,
        bio: String?,
        location: String?,
        website: String?,
        avatarBase64: String?,
        bannerBase64: String?
    ): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val updates = mutableMapOf<String, Any>()
            displayName?.let { updates["displayName"] = it }
            bio?.let { updates["bio"] = it }
            location?.let { updates["location"] = it }
            website?.let { updates["website"] = it }
            avatarBase64?.let { updates["avatarBase64"] = it }
            bannerBase64?.let { updates["bannerBase64"] = it }
            if (updates.isNotEmpty()) {
                usersRef.child(uid).child(Constants.PROFILE_NODE).updateChildren(updates).await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile", 9999)
        }
    }

    suspend fun followUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            usersRef.child(uid).child(Constants.FOLLOWING_NODE).child(targetUid).setValue(true).await()
            usersRef.child(targetUid).child(Constants.FOLLOWERS_NODE).child(uid).setValue(true).await()
            usersRef.child(uid).child(Constants.PROFILE_NODE).child("followingCount")
                .runTransaction(object : Transaction.Handler {
                    override fun doTransaction(data: MutableData): Transaction.Result {
                        val count = data.getValue(Long::class.java) ?: 0L
                        data.value = count + 1
                        return Transaction.success(data)
                    }
                    override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
                })
            usersRef.child(targetUid).child(Constants.PROFILE_NODE).child("followerCount")
                .runTransaction(object : Transaction.Handler {
                    override fun doTransaction(data: MutableData): Transaction.Result {
                        val count = data.getValue(Long::class.java) ?: 0L
                        data.value = count + 1
                        return Transaction.success(data)
                    }
                    override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
                })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to follow user", 9999)
        }
    }

    suspend fun unfollowUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            usersRef.child(uid).child(Constants.FOLLOWING_NODE).child(targetUid).removeValue().await()
            usersRef.child(targetUid).child(Constants.FOLLOWERS_NODE).child(uid).removeValue().await()
            usersRef.child(uid).child(Constants.PROFILE_NODE).child("followingCount")
                .runTransaction(object : Transaction.Handler {
                    override fun doTransaction(data: MutableData): Transaction.Result {
                        val count = data.getValue(Long::class.java) ?: 0L
                        data.value = (count - 1).coerceAtLeast(0)
                        return Transaction.success(data)
                    }
                    override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
                })
            usersRef.child(targetUid).child(Constants.PROFILE_NODE).child("followerCount")
                .runTransaction(object : Transaction.Handler {
                    override fun doTransaction(data: MutableData): Transaction.Result {
                        val count = data.getValue(Long::class.java) ?: 0L
                        data.value = (count - 1).coerceAtLeast(0)
                        return Transaction.success(data)
                    }
                    override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
                })
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unfollow user", 9999)
        }
    }

    suspend fun blockUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            usersRef.child(uid).child(Constants.BLOCKED_USERS_NODE).child(targetUid).setValue(true).await()
            // Also unfollow if following
            usersRef.child(uid).child(Constants.FOLLOWING_NODE).child(targetUid).removeValue().await()
            usersRef.child(targetUid).child(Constants.FOLLOWERS_NODE).child(uid).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to block user", 9999)
        }
    }

    suspend fun unblockUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            usersRef.child(uid).child(Constants.BLOCKED_USERS_NODE).child(targetUid).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unblock user", 9999)
        }
    }

    suspend fun muteUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            usersRef.child(uid).child(Constants.MUTED_USERS_NODE).child(targetUid).setValue(true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mute user", 9999)
        }
    }

    suspend fun unmuteUser(targetUid: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            usersRef.child(uid).child(Constants.MUTED_USERS_NODE).child(targetUid).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to unmute user", 9999)
        }
    }

    suspend fun getFollowers(uid: String, pageSize: Int, lastKey: String?): Resource<List<User>> {
        return try {
            var query = usersRef.child(uid).child(Constants.FOLLOWERS_NODE).orderByKey().limitToFirst(pageSize)
            if (lastKey != null) query = query.startAfter(lastKey).limitToFirst(pageSize)
            val snapshot = query.get().await()
            val users = mutableListOf<User>()
            for (child in snapshot.children) {
                val followerUid = child.key ?: continue
                val userSnapshot = usersRef.child(followerUid).child(Constants.PROFILE_NODE).get().await()
                val user = userSnapshot.getValue(User::class.java)?.copy(uid = followerUid)
                if (user != null) users.add(user)
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get followers", 9999)
        }
    }

    suspend fun getFollowing(uid: String, pageSize: Int, lastKey: String?): Resource<List<User>> {
        return try {
            var query = usersRef.child(uid).child(Constants.FOLLOWING_NODE).orderByKey().limitToFirst(pageSize)
            if (lastKey != null) query = query.startAfter(lastKey).limitToFirst(pageSize)
            val snapshot = query.get().await()
            val users = mutableListOf<User>()
            for (child in snapshot.children) {
                val followingUid = child.key ?: continue
                val userSnapshot = usersRef.child(followingUid).child(Constants.PROFILE_NODE).get().await()
                val user = userSnapshot.getValue(User::class.java)?.copy(uid = followingUid)
                if (user != null) users.add(user)
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get following", 9999)
        }
    }

    suspend fun getBlockedUsers(): Resource<List<User>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val snapshot = usersRef.child(uid).child(Constants.BLOCKED_USERS_NODE).get().await()
            val users = mutableListOf<User>()
            for (child in snapshot.children) {
                val blockedUid = child.key ?: continue
                val userSnapshot = usersRef.child(blockedUid).child(Constants.PROFILE_NODE).get().await()
                val user = userSnapshot.getValue(User::class.java)?.copy(uid = blockedUid)
                if (user != null) users.add(user)
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get blocked users", 9999)
        }
    }

    suspend fun getMutedUsers(): Resource<List<User>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val snapshot = usersRef.child(uid).child(Constants.MUTED_USERS_NODE).get().await()
            val users = mutableListOf<User>()
            for (child in snapshot.children) {
                val mutedUid = child.key ?: continue
                val userSnapshot = usersRef.child(mutedUid).child(Constants.PROFILE_NODE).get().await()
                val user = userSnapshot.getValue(User::class.java)?.copy(uid = mutedUid)
                if (user != null) users.add(user)
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get muted users", 9999)
        }
    }

    suspend fun searchUsers(query: String, pageSize: Int): Resource<List<User>> {
        return try {
            val snapshot = usersRef.child(Constants.SEARCH_INDEX_NODE)
                .orderByChild("username")
                .startAt(query.lowercase())
                .endAt(query.lowercase() + "\uf8ff")
                .limitToFirst(pageSize)
                .get().await()
            val users = mutableListOf<User>()
            for (child in snapshot.children) {
                val uid = child.key ?: continue
                val userSnapshot = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
                val user = userSnapshot.getValue(User::class.java)?.copy(uid = uid)
                if (user != null) users.add(user)
            }
            Resource.Success(users)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Search failed", 9999)
        }
    }

    fun observeUserProfile(uid: String): Flow<User?> = callbackFlow {
        val ref = usersRef.child(uid).child(Constants.PROFILE_NODE)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)?.copy(uid = uid)
                trySend(user)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun isFollowing(targetUid: String): Flow<Boolean> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) { trySend(false); return@callbackFlow }
        val ref = usersRef.child(uid).child(Constants.FOLLOWING_NODE).child(targetUid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.exists()) }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun isBlocked(targetUid: String): Flow<Boolean> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) { trySend(false); return@callbackFlow }
        val ref = usersRef.child(uid).child(Constants.BLOCKED_USERS_NODE).child(targetUid)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.exists()) }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getMutualFollowers(uid: String): Flow<List<String>> = callbackFlow {
        val currentUid = auth.currentUser?.uid
        if (currentUid == null) { trySend(emptyList()); return@callbackFlow }
        val ref = usersRef.child(uid).child(Constants.FOLLOWERS_NODE)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val followerUids = snapshot.children.mapNotNull { it.key }
                kotlinx.coroutines.GlobalScope.launch {
                    val myFollowing = usersRef.child(currentUid).child(Constants.FOLLOWING_NODE).get().await()
                    val myFollowingUids = myFollowing.children.mapNotNull { it.key }.toSet()
                    trySend(followerUids.filter { it in myFollowingUids })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}