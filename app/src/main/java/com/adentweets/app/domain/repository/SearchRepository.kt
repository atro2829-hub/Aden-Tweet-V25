package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User

interface SearchRepository {
    suspend fun searchUsers(query: String, pageSize: Int): Resource<List<User>>
    suspend fun searchPosts(query: String, pageSize: Int, lastKey: String?): Resource<List<Post>>
    suspend fun searchHashtags(query: String): Resource<List<TrendingTopic>>
    suspend fun getTrendingTopics(limit: Int): Resource<List<TrendingTopic>>
    suspend fun getPostsByHashtag(hashtag: String, pageSize: Int, lastKey: String?): Resource<List<Post>>
    suspend fun getSuggestedUsers(pageSize: Int): Resource<List<User>>
}