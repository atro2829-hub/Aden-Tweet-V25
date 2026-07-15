package com.adentweets.app.presentation.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.usecase.feed.GetFeedUseCase
import com.adentweets.app.domain.usecase.post.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFeedUseCase: GetFeedUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val retweetPostUseCase: RetweetPostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase
) : ViewModel() {

    val selectedTab = MutableStateFlow(0) // 0=For You, 1=Following
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing
    private val _lastKey = MutableStateFlow<String?>(null)
    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val cachedPosts = mutableMapOf<String, List<Post>>()

    init { loadFeed() }

    fun switchTab(index: Int) {
        selectedTab.value = index
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            _lastKey.value = null
            _error.value = null
            val feedType = if (selectedTab.value == 0) "foryou" else "following"
            when (val result = getFeedUseCase(feedType, 10)) {
                is Resource.Success -> {
                    _posts.value = result.data
                    _lastKey.value = result.data.lastOrNull()?.postId
                    _hasMore.value = result.data.size >= 10
                    cachedPosts[feedType] = result.data
                }
                is Resource.Error -> _error.value = result.message
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun loadMore() {
        if (_isLoading.value || !_hasMore.value) return
        viewModelScope.launch {
            _isLoading.value = true
            val feedType = if (selectedTab.value == 0) "foryou" else "following"
            when (val result = getFeedUseCase(feedType, 10, _lastKey.value)) {
                is Resource.Success -> {
                    _posts.value = _posts.value + result.data
                    _lastKey.value = result.data.lastOrNull()?.postId
                    _hasMore.value = result.data.size >= 10
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadFeed()
            _isRefreshing.value = false
        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            val updated = _posts.value.map {
                if (it.postId == post.postId) {
                    val newLiked = !it.isLikedByCurrentUser
                    it.copy(
                        isLikedByCurrentUser = newLiked,
                        likeCount = if (newLiked) it.likeCount + 1 else (it.likeCount - 1).coerceAtLeast(0)
                    )
                } else it
            }
            _posts.value = updated
            likePostUseCase(post.postId, post.isLikedByCurrentUser)
        }
    }

    fun toggleRetweet(post: Post) {
        viewModelScope.launch {
            val updated = _posts.value.map {
                if (it.postId == post.postId) {
                    val newRt = !it.isRetweetedByCurrentUser
                    it.copy(
                        isRetweetedByCurrentUser = newRt,
                        retweetCount = if (newRt) it.retweetCount + 1 else (it.retweetCount - 1).coerceAtLeast(0)
                    )
                } else it
            }
            _posts.value = updated
            retweetPostUseCase(post.postId, post.isRetweetedByCurrentUser)
        }
    }

    fun toggleBookmark(post: Post) {
        viewModelScope.launch {
            val updated = _posts.value.map {
                if (it.postId == post.postId) it.copy(isBookmarkedByCurrentUser = !it.isBookmarkedByCurrentUser)
                else it
            }
            _posts.value = updated
            bookmarkPostUseCase(post.postId, post.isBookmarkedByCurrentUser)
        }
    }
}