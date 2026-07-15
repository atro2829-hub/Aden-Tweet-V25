package com.adentweets.app.presentation.screens.explore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.usecase.search.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(private val searchUseCase: SearchUseCase) : ViewModel() {
    private val _trending = MutableStateFlow<List<TrendingTopic>>(emptyList())
    val trending: StateFlow<List<TrendingTopic>> = _trending
    private val _suggestedUsers = MutableStateFlow<List<User>>(emptyList())
    val suggestedUsers: StateFlow<List<User>> = _suggestedUsers
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init { loadData() }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = searchUseCase.getTrendingTopics()) {
                is Resource.Success -> _trending.value = result.data
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            when (val result = searchUseCase.getSuggestedUsers()) {
                is Resource.Success -> _suggestedUsers.value = result.data
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
            _isLoading.value = false
        }
    }
}