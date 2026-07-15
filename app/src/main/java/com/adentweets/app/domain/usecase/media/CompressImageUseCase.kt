package com.adentweets.app.domain.usecase.media

import android.content.Context
import android.net.Uri
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.MediaRepository
import com.adentweets.app.domain.repository.MediaUseCase
import javax.inject.Inject

class CompressImageUseCase @Inject constructor(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(context: Context, uri: Uri, type: MediaUseCase): Resource<String> {
        return mediaRepository.compressAndEncodeImage(context, uri, type)
    }
}