package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.remote.feed.FirebaseFeedSource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepositoryImpl @Inject constructor(
    private val firebaseFeedSource: FirebaseFeedSource
) : FeedRepository {

    override suspend fun getFollowingFeed(pageSize: Int, lastKey: String?) =
        firebaseFeedSource.getFollowingFeed(pageSize, lastKey)

    override suspend fun getForYouFeed(pageSize: Int, lastKey: String?) =
        firebaseFeedSource.getForYouFeed(pageSize, lastKey)

    override fun observeFollowingFeed() = firebaseFeedSource.observeFollowingFeed()

    override fun getNewPostsCount(): Flow<Int> = kotlinx.coroutines.flow.flowOf(0)

    override suspend fun loadMorePosts(lastKey: String?, pageSize: Int, feedType: String) =
        firebaseFeedSource.loadMorePosts(lastKey, pageSize, feedType)
}