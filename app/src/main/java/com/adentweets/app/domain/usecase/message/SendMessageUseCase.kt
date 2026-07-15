package com.adentweets.app.domain.usecase.message

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Message
import com.adentweets.app.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        mediaBase64: String? = null,
        mediaType: String? = null
    ): Resource<Message> {
        return messageRepository.sendMessage(conversationId, content, mediaBase64, mediaType)
    }
}