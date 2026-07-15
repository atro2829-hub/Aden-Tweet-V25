package com.adentweets.app.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.media.FirebaseMediaSource
import com.adentweets.app.domain.repository.MediaRepository
import com.adentweets.app.domain.repository.MediaUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val firebaseMediaSource: FirebaseMediaSource
) : MediaRepository {

    override suspend fun compressAndEncodeImage(context: Context, uri: Uri, type: MediaUseCase) =
        firebaseMediaSource.compressAndEncodeImage(context, uri, type)

    override suspend fun compressAndEncodeVideo(context: Context, uri: Uri, type: MediaUseCase) =
        firebaseMediaSource.compressAndEncodeVideo(context, uri, type)

    override suspend fun getVideoThumbnail(context: Context, uri: Uri) =
        firebaseMediaSource.getVideoThumbnail(context, uri)

    override suspend fun decodeBase64ToBitmap(base64: String) =
        firebaseMediaSource.decodeBase64ToBitmap(base64)

    override suspend fun decodeBase64ToBytes(base64: String) =
        firebaseMediaSource.decodeBase64ToBytes(base64)
}