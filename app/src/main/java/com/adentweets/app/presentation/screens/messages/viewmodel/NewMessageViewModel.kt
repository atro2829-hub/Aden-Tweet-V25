package com.adentweets.app.presentation.screens.messages.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Conversation
import com.adentweets.app.domain.usecase.message.GetConversationsUseCase
import com.adentweets.app.domain.usecase.search.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewMessageViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {
    val searchQuery = MutableStateFlow("")
    private val _searchResults = MutableStateFlow<List<com.adentweets.app.domain.model.User>>(emptyList())
    val searchResults: StateFlow<List<com.adentweets.app.domain.model.User>> = _searchResults

    fun searchUsers() {
        if (searchQuery.value.isBlank()) return
        viewModelScope.launch {
            when (val result = searchUseCase.searchUsers(searchQuery.value)) {
                is Resource.Success -> _searchResults.value = result.data
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }
    }
}