package com.adentweets.app.data.remote.message

import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Conversation
import com.adentweets.app.domain.model.Message
import com.adentweets.app.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMessageSource @Inject constructor(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) {
    private val messagesRef = database.getReference(Constants.MESSAGES_NODE)
    private val conversationsRef = database.getReference(Constants.CONVERSATIONS_NODE)
    private val usersRef = database.getReference(Constants.USERS_NODE)

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        mediaBase64: String?,
        mediaType: String?
    ): Resource<Message> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val messageId = UUID.randomUUID().toString()
            val message = Message(
                messageId = messageId,
                senderUid = uid,
                content = content,
                mediaBase64 = mediaBase64 ?: "",
                mediaType = mediaType ?: "",
                createdAt = System.currentTimeMillis()
            )
            messagesRef.child(conversationId).child(Constants.MESSAGES_NODE).child(messageId).setValue(message).await()

            val updates = mapOf(
                "lastMessage" to (content.ifBlank { if (mediaBase64 != null) "📷 Photo" else "" }),
                "lastMessageTime" to message.createdAt
            )
            conversationsRef.child(conversationId).updateChildren(updates).await()

            // Increment unread for other participant
            val convSnapshot = conversationsRef.child(conversationId).get().await()
            val participants = convSnapshot.child("participants").children.mapNotNull { it.key }
            for (participantUid in participants) {
                if (participantUid != uid) {
                    conversationsRef.child(conversationId).child("unreadCount").child(participantUid)
                        .runTransaction(object : Transaction.Handler {
                            override fun doTransaction(data: MutableData): Transaction.Result {
                                val count = data.getValue(Long::class.java) ?: 0L
                                data.value = count + 1
                                return Transaction.success(data)
                            }
                            override fun onComplete(error: DatabaseError?, committed: Boolean, data: DataSnapshot?) {}
                        })
                }
            }

            Resource.Success(message)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to send message", 9999)
        }
    }

    suspend fun deleteMessage(conversationId: String, messageId: String, forEveryone: Boolean): Resource<Unit> {
        return try {
            if (forEveryone) {
                messagesRef.child(conversationId).child(Constants.MESSAGES_NODE).child(messageId).child("isDeleted").setValue(true).await()
            } else {
                messagesRef.child(conversationId).child(Constants.MESSAGES_NODE).child(messageId).removeValue().await()
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete message", 9999)
        }
    }

    suspend fun createConversation(otherUid: String): Resource<Conversation> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            // Check if conversation already exists
            val convSnapshot = conversationsRef.orderByChild("participants/$uid").equalTo(true).get().await()
            for (child in convSnapshot.children) {
                val conv = child.getValue(Conversation::class.java)
                if (conv?.participants?.containsKey(otherUid) == true) {
                    return Resource.Success(conv.copy(conversationId = child.key!!))
                }
            }
            // Create new
            val conversationId = UUID.randomUUID().toString()
            val conversation = Conversation(
                conversationId = conversationId,
                participants = mapOf(uid to true, otherUid to true),
                lastMessage = "",
                lastMessageTime = System.currentTimeMillis(),
                unreadCount = emptyMap()
            )
            conversationsRef.child(conversationId).setValue(conversation).await()
            messagesRef.child(conversationId).child(Constants.MESSAGES_NODE).setValue(emptyMap<String, Any>()).await()
            Resource.Success(conversation)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create conversation", 9999)
        }
    }

    suspend fun getConversations(): Resource<List<Conversation>> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            val snapshot = conversationsRef.get().await()
            val conversations = mutableListOf<Conversation>()
            for (child in snapshot.children) {
                val conv = child.getValue(Conversation::class.java)
                if (conv != null && conv.participants.containsKey(uid)) {
                    // Load participant details
                    val otherUid = conv.participants.keys.firstOrNull { it != uid }
                    val otherUser = otherUid?.let {
                        val userSnap = usersRef.child(it).child(Constants.PROFILE_NODE).get().await()
                        userSnap.getValue(User::class.java)?.copy(uid = it)
                    }
                    conversations.add(conv.copy(
                        conversationId = child.key!!,
                        participantDetails = if (otherUser != null) mapOf(otherUser.uid to otherUser) else emptyMap()
                    ))
                }
            }
            conversations.sortByDescending { it.lastMessageTime }
            Resource.Success(conversations)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get conversations", 9999)
        }
    }

    suspend fun getMessages(conversationId: String, pageSize: Int, lastKey: String?): Resource<List<Message>> {
        return try {
            var query = messagesRef.child(conversationId).child(Constants.MESSAGES_NODE)
                .orderByKey().limitToLast(pageSize)
            if (lastKey != null) {
                query = query.endBefore(lastKey).limitToLast(pageSize)
            }
            val snapshot = query.get().await()
            val messages = mutableListOf<Message>()
            for (child in snapshot.children) {
                val msg = child.getValue(Message::class.java)
                if (msg != null && !msg.isDeleted) messages.add(msg.copy(messageId = child.key!!))
            }
            messages.reverse()
            Resource.Success(messages)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get messages", 9999)
        }
    }

    suspend fun markAsRead(conversationId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            conversationsRef.child(conversationId).child("unreadCount").child(uid).setValue(0).await()
            // Also mark individual messages as read
            val msgSnapshot = messagesRef.child(conversationId).child(Constants.MESSAGES_NODE).get().await()
            for (child in msgSnapshot.children) {
                val senderId = child.child("senderId").getValue(String::class.java)
                val isRead = child.child("isRead").getValue(Boolean::class.java) ?: false
                if (senderId != uid && !isRead) {
                    messagesRef.child(conversationId).child(Constants.MESSAGES_NODE).child(child.key!!)
                        .child("isRead").setValue(true).await()
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to mark as read", 9999)
        }
    }

    suspend fun addReaction(conversationId: String, messageId: String, emoji: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            messagesRef.child(conversationId).child(Constants.MESSAGES_NODE)
                .child(messageId).child("reactions").child(uid).setValue(emoji).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add reaction", 9999)
        }
    }

    suspend fun removeReaction(conversationId: String, messageId: String): Resource<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Resource.Error("Not authenticated", 2001)
            messagesRef.child(conversationId).child(Constants.MESSAGES_NODE)
                .child(messageId).child("reactions").child(uid).removeValue().await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove reaction", 9999)
        }
    }

    suspend fun archiveConversation(conversationId: String): Resource<Unit> {
        return try {
            conversationsRef.child(conversationId).child("archived").setValue(true).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to archive", 9999)
        }
    }

    fun observeConversations(): Flow<List<Conversation>> = callbackFlow {
        val uid = auth.currentUser?.uid ?: return@callbackFlow
        val ref = conversationsRef
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val conversations = mutableListOf<Conversation>()
                for (child in snapshot.children) {
                    val conv = child.getValue(Conversation::class.java)
                    if (conv != null && conv.participants.containsKey(uid)) {
                        conversations.add(conv.copy(conversationId = child.key!!))
                    }
                }
                trySend(conversations.sortedByDescending { it.lastMessageTime })
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeMessages(conversationId: String): Flow<List<Message>> = callbackFlow {
        val ref = messagesRef.child(conversationId).child(Constants.MESSAGES_NODE)
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg = snapshot.getValue(Message::class.java)?.copy(messageId = snapshot.key!!)
                if (msg != null) {
                    trySend(listOf(msg))
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addChildEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun observeOnlineStatus(uid: String): Flow<Boolean> = callbackFlow {
        val ref = usersRef.child(uid).child(Constants.PROFILE_NODE).child("isOnline")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) { trySend(snapshot.getValue(Boolean::class.java) ?: false) }
            override fun onCancelled(error: DatabaseError) {}
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}