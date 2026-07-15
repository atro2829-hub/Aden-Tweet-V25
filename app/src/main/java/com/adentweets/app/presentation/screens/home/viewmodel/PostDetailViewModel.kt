package com.adentweets.app.presentation.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.usecase.post.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    private val getRepliesUseCase: GetRepliesUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val retweetPostUseCase: RetweetPostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post
    private val _replies = MutableStateFlow<List<Post>>(emptyList())
    val replies: StateFlow<List<Post>> = _replies
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = getPostUseCase(postId)) {
                is Resource.Success -> _post.value = result.data
                is Resource.Error -> _error.value = result.message
                is Resource.Loading -> {}
            }
            loadReplies(postId)
            _isLoading.value = false
        }
    }

    fun loadReplies(postId: String) {
        viewModelScope.launch {
            when (val result = getRepliesUseCase(postId)) {
                is Resource.Success -> _replies.value = result.data
                else -> {}
            }
        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            _post.value = _post.value?.copy(
                isLikedByCurrentUser = !post.isLikedByCurrentUser,
                likeCount = if (!post.isLikedByCurrentUser) post.likeCount + 1 else (post.likeCount - 1).coerceAtLeast(0)
            )
            likePostUseCase(post.postId, post.isLikedByCurrentUser)
        }
    }

    fun toggleRetweet(post: Post) {
        viewModelScope.launch {
            _post.value = _post.value?.copy(
                isRetweetedByCurrentUser = !post.isRetweetedByCurrentUser,
                retweetCount = if (!post.isRetweetedByCurrentUser) post.retweetCount + 1 else (post.retweetCount - 1).coerceAtLeast(0)
            )
            retweetPostUseCase(post.postId, post.isRetweetedByCurrentUser)
        }
    }

    fun toggleBookmark(post: Post) {
        viewModelScope.launch {
            _post.value = _post.value?.copy(isBookmarkedByCurrentUser = !post.isBookmarkedByCurrentUser)
            bookmarkPostUseCase(post.postId, post.isBookmarkedByCurrentUser)
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            deletePostUseCase(postId)
        }
    }
}