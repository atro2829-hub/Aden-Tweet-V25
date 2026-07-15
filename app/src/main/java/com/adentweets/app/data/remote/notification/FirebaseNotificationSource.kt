package com.adentweets.app.data.remote.notification

import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Notification
import com.adentweets.app.domain.model.NotificationType
import com.adentweets.app.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNotificationSource @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) {
    private val notificationsRef = database.getReference(Constants.USERS_NODE)
    private val usersRef = database.getReference(Constants.USERS_NODE)

    suspend fun getNotifications(pageSize: Int, lastKey: String?): Resource<List<Notification>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            var query = notificationsRef.child(uid).child(Constants.NOTIFICATIONS_NODE)
                .orderByChild("createdAt").limitToLast(pageSize)
            if (lastKey != null) {
                query = query.endBefore(lastKey).limitToLast(pageSize)
            }
            val snapshot = query.get().await()
            val notifications = mutableListOf<Notification>()
            for (child in snapshot.children.reversed()) {
                val notif = child.getValue(Notification::class.java)
                if (notif != null) {
                    val fromUser = getUser(notif.fromUid)
                    notifications.add(notif.copy(notificationId = child.key!!, fromUser = fromUser))
                }
            }
            Resource.Success(notifications)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get notifications", 9999)
        }
    }

    suspend fun markAsRead(notificationId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            notificationsRef.child(uid).child(Constants.NOTIFICATIONS_NODE)
                .child(notificationId).child("isRead").setValue(true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark as read", 9999)
        }
    }

    suspend fun markAllAsRead(): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val snapshot = notificationsRef.child(uid).child(Constants.NOTIFICATIONS_NODE).get().await()
            val updates = mutableMapOf<String, Any>()
            for (child in snapshot.children) {
                updates["${child.key}/isRead"] = true
            }
            if (updates.isNotEmpty()) {
                notificationsRef.child(uid).child(Constants.NOTIFICATIONS_NODE).updateChildren(updates).await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark all as read", 9999)
        }
    }

    suspend fun deleteNotification(notificationId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            notificationsRef.child(uid).child(Constants.NOTIFICATIONS_NODE)
                .child(notificationId).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete notification", 9999)
        }
    }

    suspend fun clearAllNotifications(): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            notificationsRef.child(uid).child(Constants.NOTIFICATIONS_NODE).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to clear notifications", 9999)
        }
    }

    fun observeNotifications(): Flow<List<Notification>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val ref = notificationsRef.child(uid).child(Constants.NOTIFICATIONS_NODE).orderByChild("createdAt")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifs = mutableListOf<Notification>()
                for (child in snapshot.children.reversed()) {
                    val notif = child.getValue(Notification::class.java)
                    if (notif != null) notifs.add(notif.copy(notificationId = child.key!!))
                }
                trySend(notifs)
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeUnreadCount(): Flow<Int> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val ref = notificationsRef.child(uid).child(Constants.NOTIFICATIONS_NODE).orderByChild("isRead").equalTo(false)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.childrenCount.toInt()) }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    private suspend fun getUser(uid: String): User? {
        return try {
            val snapshot = usersRef.child(uid).child(Constants.PROFILE_NODE).get().await()
            if (snapshot.exists()) snapshot.getValue(User::class.java)?.copy(uid = uid) else null
        } catch (e: Exception) { null }
    }
}