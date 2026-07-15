package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.notification.FirebaseNotificationSource
import com.adentweets.app.domain.model.Notification
import com.adentweets.app.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val firebaseNotificationSource: FirebaseNotificationSource
) : NotificationRepository {

    override suspend fun getNotifications(pageSize: Int, lastKey: String?) =
        firebaseNotificationSource.getNotifications(pageSize, lastKey)

    override suspend fun markAsRead(notificationId: String) =
        firebaseNotificationSource.markAsRead(notificationId)

    override suspend fun markAllAsRead() = firebaseNotificationSource.markAllAsRead()

    override suspend fun deleteNotification(notificationId: String) =
        firebaseNotificationSource.deleteNotification(notificationId)

    override suspend fun clearAllNotifications() = firebaseNotificationSource.clearAllNotifications()

    override fun observeNotifications() = firebaseNotificationSource.observeNotifications()
    override fun observeUnreadCount() = firebaseNotificationSource.observeUnreadCount()
}