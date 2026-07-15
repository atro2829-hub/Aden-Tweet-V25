package com.adentweets.app.presentation.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.user.*
import com.adentweets.app.domain.usecase.post.LikePostUseCase
import com.adentweets.app.domain.usecase.post.BookmarkPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val followUserUseCase: FollowUserUseCase,
    private val getFollowersUseCase: GetFollowersUseCase,
    private val getFollowingUseCase: GetFollowingUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val bookmarkPostUseCase: BookmarkPostUseCase
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    val selectedTab = MutableStateFlow(0)
    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing

    fun loadProfile(uid: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = getUserProfileUseCase(uid)) {
                is Resource.Success -> _user.value = result.data
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun toggleFollow(uid: String) {
        viewModelScope.launch {
            followUserUseCase(uid, _isFollowing.value)
            _isFollowing.value = !_isFollowing.value
        }
    }

    fun toggleLike(post: Post) {
        viewModelScope.launch {
            val updated = _posts.value.map {
                if (it.postId == post.postId) it.copy(
                    isLikedByCurrentUser = !it.isLikedByCurrentUser,
                    likeCount = if (!it.isLikedByCurrentUser) it.likeCount + 1 else (it.likeCount - 1).coerceAtLeast(0)
                ) else it
            }
            _posts.value = updated
            likePostUseCase(post.postId, post.isLikedByCurrentUser)
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