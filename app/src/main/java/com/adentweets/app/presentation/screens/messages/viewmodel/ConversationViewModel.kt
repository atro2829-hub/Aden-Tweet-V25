package com.adentweets.app.presentation.screens.messages.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Message
import com.adentweets.app.domain.usecase.message.SendMessageUseCase
import com.adentweets.app.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages
    val messageText = MutableStateFlow("")
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _otherUserName = MutableStateFlow("")
    val otherUserName: StateFlow<String> = _otherUserName

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = messageRepository.getMessages(conversationId, 30, null)) {
                is Resource.Success -> _messages.value = result.data
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            messageRepository.markAsRead(conversationId)
            _isLoading.value = false
        }
    }

    fun sendMessage(conversationId: String) {
        val text = messageText.value.trim()
        if (text.isBlank()) return
        viewModelScope.launch {
            messageText.value = ""
            sendMessageUseCase(conversationId, text)
        }
    }
}