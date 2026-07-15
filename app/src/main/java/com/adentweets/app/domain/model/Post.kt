package com.adentweets.app.domain.model

data class Post(
    val postId: String = "",
    val authorUid: String = "",
    val content: String = "",
    val mediaItems: List<MediaItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val likeCount: Long = 0,
    val retweetCount: Long = 0,
    val replyCount: Long = 0,
    val viewCount: Long = 0,
    val isPinned: Boolean = false,
    val isSensitive: Boolean = false,
    val visibility: String = "public",
    val replyToPostId: String = "",
    val quotePostId: String = "",
    val poll: Poll? = null,
    val isEdited: Boolean = false,
    val editHistory: List<EditRecord> = emptyList(),
    val author: User? = null,
    val isLikedByCurrentUser: Boolean = false,
    val isRetweetedByCurrentUser: Boolean = false,
    val isBookmarkedByCurrentUser: Boolean = false
) {
    val formattedDate: String
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

    val hasMedia: Boolean get() = mediaItems.isNotEmpty()
    val hasImages: Boolean get() = mediaItems.any { it.mediaType == MediaType.IMAGE }
    val hasVideo: Boolean get() = mediaItems.any { it.mediaType == MediaType.VIDEO }
    val hasGif: Boolean get() = mediaItems.any { it.mediaType == MediaType.GIF }
    val hasPoll: Boolean get() = poll != null
    val isReply: Boolean get() = replyToPostId.isNotBlank()
    val isQuote: Boolean get() = quotePostId.isNotBlank() && content.isNotBlank()
    val mediaDisplayCount: Int get() = mediaItems.size.coerceIn(0, 4)
}

data class EditRecord(
    val editedAt: Long = System.currentTimeMillis(),
    val previousContent: String = ""
)

enum class MediaType {
    IMAGE, VIDEO, GIF
}