package com.adentweets.app.domain.model

data class MediaItem(
    val mediaType: MediaType = MediaType.IMAGE,
    val base64Data: String = "",
    val thumbnailBase64: String = "",
    val width: Int = 0,
    val height: Int = 0,
    val durationMs: Long = 0
) {
    val isVideo: Boolean get() = mediaType == MediaType.VIDEO
    val isGif: Boolean get() = mediaType == MediaType.GIF
    val isImage: Boolean get() = mediaType == MediaType.IMAGE
    val hasThumbnail: Boolean get() = thumbnailBase64.isNotBlank()
}