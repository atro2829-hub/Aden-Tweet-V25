package com.adentweets.app.presentation.screens.bookmarks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.PostRepository
import com.adentweets.app.domain.usecase.post.BookmarkPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val bookmarkPostUseCase: BookmarkPostUseCase
) : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadBookmarks() }

    fun loadBookmarks() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = postRepository.getBookmarks(20, null)) {
                is Resource.Success -> _posts.value = result.data
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }

    fun removeBookmark(post: Post) {
        viewModelScope.launch {
            bookmarkPostUseCase(post.postId, true)
            _posts.value = _posts.value.filter { it.postId != post.postId }
        }
    }
}