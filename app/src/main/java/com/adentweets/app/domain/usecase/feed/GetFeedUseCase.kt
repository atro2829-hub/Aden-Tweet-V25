package com.adentweets.app.domain.usecase.feed

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.FeedRepository
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository
) {
    suspend operator fun invoke(feedType: String, pageSize: Int = 10, lastKey: String? = null): Resource<List<Post>> {
        return when (feedType) {
            "following" -> feedRepository.getFollowingFeed(pageSize, lastKey)
            else -> feedRepository.getForYouFeed(pageSize, lastKey)
        }
    }
}