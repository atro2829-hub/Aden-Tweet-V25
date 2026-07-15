package com.adentweets.app.presentation.screens.notifications.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Notification
import com.adentweets.app.domain.usecase.notification.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markNotificationsReadUseCase: MarkNotificationsReadUseCase
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadNotifications() }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = getNotificationsUseCase()) {
                is Resource.Success -> {
                    _notifications.value = result.data
                    _unreadCount.value = result.data.count { !it.isRead }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            markNotificationsReadUseCase()
            _notifications.value = _notifications.value.map { it.copy(isRead = true) }
            _unreadCount.value = 0
        }
    }
}