package com.adentweets.app.domain.model

data class Conversation(
    val conversationId: String = "",
    val participants: Map<String, Boolean> = emptyMap(),
    val lastMessage: String = "",
    val lastMessageTime: Long = 0L,
    val unreadCount: Map<String, Long> = emptyMap(),
    val participantDetails: Map<String, User> = emptyMap()
) {
    fun getOtherParticipantUid(currentUid: String): String? {
        return participants.keys.firstOrNull { it != currentUid }
    }

    fun getOtherParticipant(currentUid: String): User? {
        val otherUid = getOtherParticipantUid(currentUid) ?: return null
        return participantDetails[otherUid]
    }

    fun getUnreadCountForUser(uid: String): Long {
        return unreadCount[uid] ?: 0L
    }

    val formattedLastMessageTime: String
        get() {
            val now = System.currentTimeMillis()
            val diff = now - lastMessageTime
            val days = diff / (1000 * 60 * 60 * 24)
            return when {
                days == 0L -> java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date(lastMessageTime))
                days == 1L -> "Yesterday"
                days < 7 -> java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
                    .format(java.util.Date(lastMessageTime))
                else -> java.text.SimpleDateFormat("MMM d", java.util.Locale.getDefault())
                    .format(java.util.Date(lastMessageTime))
            }
        }
}