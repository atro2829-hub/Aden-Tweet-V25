package com.adentweets.app.domain.model

enum class NotificationType {
    LIKE, RETWEET, FOLLOW, REPLY, QUOTE, MENTION, MESSAGE
}

data class Notification(
    val notificationId: String = "",
    val type: NotificationType = NotificationType.LIKE,
    val fromUid: String = "",
    val postId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val fromUser: User? = null
) {
    val formattedTime: String
        get() {
            val now = System.currentTimeMillis()
            val diff = now - createdAt
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                seconds < 60 -> "${seconds}s"
                minutes < 60 -> "${minutes}m"
                hours < 24 -> "${hours}h"
                days < 7 -> "${days}d"
                else -> java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault())
                    .format(java.util.Date(createdAt))
            }
        }

    companion object {
        fun createMessage(fromUser: User, type: NotificationType): String {
            return when (type) {
                NotificationType.LIKE -> "liked your post"
                NotificationType.RETWEET -> "reposted your post"
                NotificationType.FOLLOW -> "followed you"
                NotificationType.REPLY -> "replied to your post"
                NotificationType.QUOTE -> "quoted your post"
                NotificationType.MENTION -> "mentioned you"
                NotificationType.MESSAGE -> "sent you a message"
            }
        }
    }
}