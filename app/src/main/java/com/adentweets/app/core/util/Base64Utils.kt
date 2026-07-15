package com.adentweets.app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Base64
import com.adentweets.app.core.util.ErrorCodes.MEDIA_COMPRESS_FAILED
import com.adentweets.app.core.util.ErrorCodes.MEDIA_DECODE_FAILED
import com.adentweets.app.core.util.ErrorCodes.MEDIA_TOO_LARGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object Base64Utils {

    // ========== ENCODING ==========

    suspend fun encodeImageFromUri(
        context: Context,
        uri: Uri,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int,
        maxSizeKb: Int
    ): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw AdenTweetException("Cannot open image", MEDIA_COMPRESS_FAILED)

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
            ?: throw AdenTweetException("Cannot decode image", MEDIA_DECODE_FAILED)
        inputStream.close()

        val compressed = compressBitmap(originalBitmap, maxWidth, maxHeight, quality, maxSizeKb)
        originalBitmap.recycle()

        val base64 = Base64.encodeToString(compressed, Base64.NO_WRAP)
        val sizeKb = base64.length / 1024

        if (sizeKb > maxSizeKb * 1.4) {
            throw AdenTweetException("Image too large even after compression ($sizeKb KB)", MEDIA_TOO_LARGE)
        }

        base64
    }

    suspend fun encodeVideoThumbnail(
        context: Context,
        videoUri: Uri,
        maxWidth: Int = 480,
        quality: Int = 70
    ): String = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(context, videoUri)
            val frame = retriever.getFrameAtTime(1_000_000) // 1 second
                ?: throw AdenTweetException("Cannot extract video frame", MEDIA_DECODE_FAILED)

            val compressed = compressBitmap(frame, maxWidth, maxWidth, quality, 200)
            frame.recycle()
            Base64.encodeToString(compressed, Base64.NO_WRAP)
        } finally {
            retriever.release()
        }
    }

    suspend fun encodeVideoFile(
        context: Context,
        uri: Uri,
        maxDurationMs: Long,
        maxSizeKb: Int
    ): String = withContext(Dispatchers.IO) {
        // Read the file and convert to Base64
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw AdenTweetException("Cannot open video", MEDIA_COMPRESS_FAILED)

        val file = File(context.cacheDir, "video_${UUID.randomUUID()}.mp4")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()

        // Check duration
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMs = durationStr?.toLongOrNull() ?: 0L
        retriever.release()

        if (durationMs > maxDurationMs) {
            file.delete()
            throw AdenTweetException("Video must be under ${maxDurationMs / 1000} seconds", MEDIA_TOO_LARGE)
        }

        val bytes = file.readBytes()
        file.delete()

        if (bytes.size / 1024 > maxSizeKb) {
            throw AdenTweetException("Video too large (${bytes.size / 1024} KB, max ${maxSizeKb} KB)", MEDIA_TOO_LARGE)
        }

        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    // ========== DECODING ==========

    fun decodeBase64ToBitmap(base64: String): Bitmap? {
        return try {
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun decodeBase64ToBytes(base64: String): ByteArray? {
        return try {
            Base64.decode(base64, Base64.DEFAULT)
        } catch (e: Exception) {
            null
        }
    }

    // ========== COMPRESSION ==========

    private fun compressBitmap(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int,
        quality: Int,
        maxSizeKb: Int
    ): ByteArray {
        val resized = resizeBitmap(bitmap, maxWidth, maxHeight)
        var currentQuality = quality

        while (currentQuality >= 20) {
            val stream = ByteArrayOutputStream()
            resized.compress(Bitmap.CompressFormat.JPEG, currentQuality, stream)
            val bytes = stream.toByteArray()

            if (bytes.size / 1024 <= maxSizeKb) {
                if (resized != bitmap) resized.recycle()
                return bytes
            }
            currentQuality -= 10
        }

        // Last resort: resize smaller
        val smaller = resizeBitmap(bitmap, maxWidth / 2, maxHeight / 2)
        val stream = ByteArrayOutputStream()
        smaller.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        if (resized != bitmap) resized.recycle()
        smaller.recycle()
        return stream.toByteArray()
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) return bitmap

        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    // ========== HELPERS ==========

    fun getBase64SizeKb(base64: String): Int = base64.length / 1024

    fun isEmptyBase64(base64: String?): Boolean = base64.isNullOrBlank()

    suspend fun encodeAvatar(context: Context, uri: Uri): String {
        return encodeImageFromUri(
            context, uri,
            Constants.AVATAR_DIMENSION, Constants.AVATAR_DIMENSION,
            75, Constants.MAX_AVATAR_SIZE_KB
        )
    }

    suspend fun encodeBanner(context: Context, uri: Uri): String {
        return encodeImageFromUri(
            context, uri,
            Constants.BANNER_WIDTH, Constants.BANNER_HEIGHT,
            75, Constants.MAX_BANNER_SIZE_KB
        )
    }

    suspend fun encodePostImage(context: Context, uri: Uri): String {
        return encodeImageFromUri(
            context, uri,
            Constants.POST_IMAGE_MAX_WIDTH, Constants.POST_IMAGE_MAX_WIDTH,
            80, Constants.MAX_POST_IMAGE_SIZE_KB
        )
    }

    suspend fun encodeDMImage(context: Context, uri: Uri): String {
        return encodeImageFromUri(
            context, uri,
            Constants.POST_IMAGE_MAX_WIDTH, Constants.POST_IMAGE_MAX_WIDTH,
            80, Constants.MAX_DM_IMAGE_SIZE_KB
        )
    }
}