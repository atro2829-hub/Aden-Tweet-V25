package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    suspend fun getFollowingFeed(pageSize: Int, lastKey: String?): Resource<List<Post>>
    suspend fun getForYouFeed(pageSize: Int, lastKey: String?): Resource<List<Post>>
    fun observeFollowingFeed(): Flow<List<Post>>
    fun getNewPostsCount(): Flow<Int>
    suspend fun loadMorePosts(lastKey: String?, pageSize: Int, feedType: String): Resource<List<Post>>
}