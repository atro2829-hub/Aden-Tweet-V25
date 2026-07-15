package com.adentweets.app.domain.usecase.message

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Conversation
import com.adentweets.app.domain.repository.MessageRepository
import javax.inject.Inject

class GetConversationsUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(): Resource<List<Conversation>> {
        return messageRepository.getConversations()
    }
}