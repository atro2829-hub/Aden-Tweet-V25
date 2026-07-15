package com.adentweets.app.domain.repository

import android.content.Context
import android.net.Uri
import com.adentweets.app.core.util.Resource

interface MediaRepository {
    suspend fun compressAndEncodeImage(context: Context, uri: Uri, type: MediaUseCase): Resource<String>
    suspend fun compressAndEncodeVideo(context: Context, uri: Uri, type: MediaUseCase): Resource<String>
    suspend fun getVideoThumbnail(context: Context, uri: Uri): Resource<String>
    suspend fun decodeBase64ToBitmap(base64: String): Resource<android.graphics.Bitmap>
    suspend fun decodeBase64ToBytes(base64: String): Resource<ByteArray>
}

enum class MediaUseCase {
    AVATAR, BANNER, POST_IMAGE, POST_VIDEO, POST_GIF, DM_IMAGE, DM_VIDEO
}