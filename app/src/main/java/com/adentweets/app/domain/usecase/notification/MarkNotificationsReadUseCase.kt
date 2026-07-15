package com.adentweets.app.domain.usecase.notification

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.NotificationRepository
import javax.inject.Inject

class MarkNotificationsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String? = null): Resource<Unit> {
        return if (notificationId != null) {
            notificationRepository.markAsRead(notificationId)
        } else {
            notificationRepository.markAllAsRead()
        }
    }
}