package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotifications(pageSize: Int, lastKey: String?): Resource<List<Notification>>
    suspend fun markAsRead(notificationId: String): Resource<Unit>
    suspend fun markAllAsRead(): Resource<Unit>
    suspend fun deleteNotification(notificationId: String): Resource<Unit>
    fun observeNotifications(): Flow<List<Notification>>
    fun observeUnreadCount(): Flow<Int>
    suspend fun clearAllNotifications(): Resource<Unit>
}