package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.search.FirebaseSearchSource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.model.TrendingTopic
import com.adentweets.app.domain.model.User
import com.adentweets.app.domain.repository.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val firebaseSearchSource: FirebaseSearchSource
) : SearchRepository {

    override suspend fun searchUsers(query: String, pageSize: Int) =
        firebaseSearchSource.searchUsers(query, pageSize)

    override suspend fun searchPosts(query: String, pageSize: Int, lastKey: String?) =
        firebaseSearchSource.searchPosts(query, pageSize, lastKey)

    override suspend fun searchHashtags(query: String) =
        firebaseSearchSource.searchHashtags(query)

    override suspend fun getTrendingTopics(limit: Int) =
        firebaseSearchSource.getTrendingTopics(limit)

    override suspend fun getPostsByHashtag(hashtag: String, pageSize: Int, lastKey: String?) =
        firebaseSearchSource.getPostsByHashtag(hashtag, pageSize, lastKey)

    override suspend fun getSuggestedUsers(pageSize: Int) =
        firebaseSearchSource.getSuggestedUsers(pageSize)
}