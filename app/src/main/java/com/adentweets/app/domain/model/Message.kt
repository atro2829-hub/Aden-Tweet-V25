package com.adentweets.app.domain.model

data class Message(
    val messageId: String = "",
    val senderUid: String = "",
    val content: String = "",
    val mediaBase64: String = "",
    val mediaType: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val replyToMessageId: String = "",
    val reactions: Map<String, String> = emptyMap(),
    val isDeleted: Boolean = false,
    val sender: User? = null
) {
    val hasMedia: Boolean get() = mediaBase64.isNotBlank()
    val isImage: Boolean get() = mediaType == "IMAGE"
    val isVideo: Boolean get() = mediaType == "VIDEO"

    val formattedTime: String
        get() = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(createdAt))
}