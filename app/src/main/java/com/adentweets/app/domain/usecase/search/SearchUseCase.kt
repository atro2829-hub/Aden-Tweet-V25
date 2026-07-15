package com.adentweets.app.domain.usecase.search

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.SearchRepository
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend fun searchUsers(query: String, pageSize: Int = 20): Resource<List<User>> {
        return searchRepository.searchUsers(query, pageSize)
    }

    suspend fun searchPosts(query: String, pageSize: Int = 10, lastKey: String? = null): Resource<List<Post>> {
        return searchRepository.searchPosts(query, pageSize, lastKey)
    }

    suspend fun getTrendingTopics(limit: Int = 10): Resource<List<TrendingTopic>> {
        return searchRepository.getTrendingTopics(limit)
    }

    suspend fun getPostsByHashtag(hashtag: String, pageSize: Int = 10, lastKey: String? = null): Resource<List<Post>> {
        return searchRepository.getPostsByHashtag(hashtag, pageSize, lastKey)
    }

    suspend fun getSuggestedUsers(pageSize: Int = 10): Resource<List<User>> {
        return searchRepository.getSuggestedUsers(pageSize)
    }
}