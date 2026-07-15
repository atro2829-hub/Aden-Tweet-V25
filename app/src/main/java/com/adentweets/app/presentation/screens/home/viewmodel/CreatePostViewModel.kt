package com.adentweets.app.presentation.screens.home.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.MediaUseCase
import com.adentweets.app.domain.usecase.media.CompressImageUseCase
import com.adentweets.app.domain.usecase.post.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase,
    private val compressImageUseCase: CompressImageUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val content = MutableStateFlow("")
    val selectedImageUris = MutableStateFlow<List<Uri>>(emptyList())
    val encodedMedia = MutableStateFlow<List<String>>(emptyList())
    val mediaTypes = MutableStateFlow<List<String>>(emptyList())
    val isUploading = MutableStateFlow(false)

    private val _postResult = MutableStateFlow<Resource<Post>?>(null)
    val postResult: StateFlow<Resource<Post>?> = _postResult
    private val _isPosted = MutableStateFlow(false)
    val isPosted: StateFlow<Boolean> = _isPosted

    val characterCount get() = content.value.length
    val isPostEnabled get() = content.value.isNotBlank() && characterCount <= Constants.MAX_POST_CHARS && selectedImageUris.value.size <= Constants.MAX_POST_IMAGES

    fun updateContent(text: String) { content.value = text }

    fun addImage(uri: Uri) {
        if (selectedImageUris.value.size < Constants.MAX_POST_IMAGES) {
            selectedImageUris.value = selectedImageUris.value + uri
            viewModelScope.launch { encodeImage(uri, selectedImageUris.value.size - 1) }
        }
    }

    fun removeImage(index: Int) {
        val current = selectedImageUris.value.toMutableList()
        val encoded = encodedMedia.value.toMutableList()
        val types = mediaTypes.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            encoded.removeAt(index)
            types.removeAt(index)
            selectedImageUris.value = current
            encodedMedia.value = encoded
            mediaTypes.value = types
        }
    }

    private suspend fun encodeImage(uri: Uri, index: Int) {
        when (val result = compressImageUseCase(context, uri, MediaUseCase.POST_IMAGE)) {
            is Resource.Success -> {
                val newEncoded = encodedMedia.value.toMutableList()
                while (newEncoded.size <= index) newEncoded.add("")
                newEncoded[index] = result.data
                encodedMedia.value = newEncoded
                val newTypes = mediaTypes.value.toMutableList()
                while (newTypes.size <= index) newTypes.add("IMAGE")
                newTypes[index] = "IMAGE"
                mediaTypes.value = newTypes
            }
            else -> {}
        }
    }

    fun createPost(replyToPostId: String = "") {
        if (!isPostEnabled) return
        viewModelScope.launch {
            isUploading.value = true
            _postResult.value = Resource.Loading()
            _postResult.value = createPostUseCase(
                content = content.value,
                mediaBase64List = encodedMedia.value,
                mediaTypes = mediaTypes.value,
                replyToPostId = replyToPostId
            )
            isUploading.value = false
            if (_postResult.value is Resource.Success) _isPosted.value = true
        }
    }

    fun clear() {
        content.value = ""
        selectedImageUris.value = emptyList()
        encodedMedia.value = emptyList()
        mediaTypes.value = emptyList()
        _postResult.value = null
        _isPosted.value = false
    }
}