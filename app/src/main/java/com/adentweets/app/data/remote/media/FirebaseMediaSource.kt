package com.adentweets.app.data.remote.media

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.adentweets.app.core.util.Base64Utils
import com.adentweets.app.core.util.Resource
import com.adentweets.app.core.util.ErrorCodes
import com.adentweets.app.domain.repository.MediaUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMediaSource @Inject constructor() {

    suspend fun compressAndEncodeImage(context: Context, uri: Uri, type: MediaUseCase): Resource<String> {
        return try {
            val result = when (type) {
                MediaUseCase.AVATAR -> Base64Utils.encodeAvatar(context, uri)
                MediaUseCase.BANNER -> Base64Utils.encodeBanner(context, uri)
                MediaUseCase.POST_IMAGE -> Base64Utils.encodePostImage(context, uri)
                MediaUseCase.DM_IMAGE -> Base64Utils.encodeDMImage(context, uri)
                else -> Base64Utils.encodePostImage(context, uri)
            }
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to process image", ErrorCodes.MEDIA_COMPRESS_FAILED)
        }
    }

    suspend fun compressAndEncodeVideo(context: Context, uri: Uri, type: MediaUseCase): Resource<String> {
        return try {
            val maxSizeKb = when (type) {
                MediaUseCase.POST_VIDEO -> com.adentweets.app.core.util.Constants.MAX_POST_VIDEO_SIZE_KB
                MediaUseCase.DM_VIDEO -> com.adentweets.app.core.util.Constants.MAX_DM_VIDEO_SIZE_KB
                else -> com.adentweets.app.core.util.Constants.MAX_POST_VIDEO_SIZE_KB
            }
            val result = Base64Utils.encodeVideoFile(context, uri, com.adentweets.app.core.util.Constants.MAX_POST_VIDEO_DURATION_MS, maxSizeKb)
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to process video", ErrorCodes.MEDIA_COMPRESS_FAILED)
        }
    }

    suspend fun getVideoThumbnail(context: Context, uri: Uri): Resource<String> {
        return try {
            val result = Base64Utils.encodeVideoThumbnail(context, uri)
            Resource.Success(result)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get thumbnail", ErrorCodes.MEDIA_COMPRESS_FAILED)
        }
    }

    fun decodeBase64ToBitmap(base64: String): Resource<Bitmap> {
        val bitmap = Base64Utils.decodeBase64ToBitmap(base64)
        return if (bitmap != null) Resource.Success(bitmap)
        else Resource.Error("Failed to decode image", ErrorCodes.MEDIA_DECODE_FAILED)
    }

    fun decodeBase64ToBytes(base64: String): Resource<ByteArray> {
        val bytes = Base64Utils.decodeBase64ToBytes(base64)
        return if (bytes != null) Resource.Success(bytes)
        else Resource.Error("Failed to decode", ErrorCodes.MEDIA_DECODE_FAILED)
    }
}