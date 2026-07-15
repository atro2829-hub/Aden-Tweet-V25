package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.message.FirebaseMessageSource
import com.adentweets.app.domain.model.Conversation
import com.adentweets.app.domain.model.Message
import com.adentweets.app.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val firebaseMessageSource: FirebaseMessageSource
) : MessageRepository {

    override suspend fun sendMessage(conversationId: String, content: String, mediaBase64: String?, mediaType: String?) =
        firebaseMessageSource.sendMessage(conversationId, content, mediaBase64, mediaType)

    override suspend fun deleteMessage(conversationId: String, messageId: String, forEveryone: Boolean) =
        firebaseMessageSource.deleteMessage(conversationId, messageId, forEveryone)

    override suspend fun createConversation(otherUid: String) = firebaseMessageSource.createConversation(otherUid)

    override suspend fun getConversations() = firebaseMessageSource.getConversations()

    override suspend fun getMessages(conversationId: String, pageSize: Int, lastKey: String?) =
        firebaseMessageSource.getMessages(conversationId, pageSize, lastKey)

    override suspend fun markAsRead(conversationId: String) = firebaseMessageSource.markAsRead(conversationId)

    override suspend fun addReaction(conversationId: String, messageId: String, emoji: String) =
        firebaseMessageSource.addReaction(conversationId, messageId, emoji)

    override suspend fun removeReaction(conversationId: String, messageId: String) =
        firebaseMessageSource.removeReaction(conversationId, messageId)

    override suspend fun archiveConversation(conversationId: String) =
        firebaseMessageSource.archiveConversation(conversationId)

    override fun observeConversations() = firebaseMessageSource.observeConversations()
    override fun observeMessages(conversationId: String) = firebaseMessageSource.observeMessages(conversationId)
    override fun observeOnlineStatus(uid: String) = firebaseMessageSource.observeOnlineStatus(uid)
}