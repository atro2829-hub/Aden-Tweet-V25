package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Conversation
import com.adentweets.app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun sendMessage(conversationId: String, content: String, mediaBase64: String?, mediaType: String?): Resource<Message>
    suspend fun deleteMessage(conversationId: String, messageId: String, forEveryone: Boolean): Resource<Unit>
    suspend fun createConversation(otherUid: String): Resource<Conversation>
    suspend fun getConversations(): Resource<List<Conversation>>
    suspend fun getMessages(conversationId: String, pageSize: Int, lastKey: String?): Resource<List<Message>>
    suspend fun markAsRead(conversationId: String): Resource<Unit>
    suspend fun addReaction(conversationId: String, messageId: String, emoji: String): Resource<Unit>
    suspend fun removeReaction(conversationId: String, messageId: String): Resource<Unit>
    suspend fun archiveConversation(conversationId: String): Resource<Unit>
    fun observeConversations(): Flow<List<Conversation>>
    fun observeMessages(conversationId: String): Flow<List<Message>>
    fun observeOnlineStatus(uid: String): Flow<Boolean>
}