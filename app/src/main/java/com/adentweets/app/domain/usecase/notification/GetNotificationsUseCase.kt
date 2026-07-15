package com.adentweets.app.domain.usecase.notification

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Notification
import com.adentweets.app.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(pageSize: Int = 20, lastKey: String? = null): Resource<List<Notification>> {
        return notificationRepository.getNotifications(pageSize, lastKey)
    }
}