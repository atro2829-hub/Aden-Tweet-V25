package com.adentweets.app.domain.usecase.post

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class RetweetPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, isCurrentlyRetweeted: Boolean): Resource<Unit> {
        return if (isCurrentlyRetweeted) {
            postRepository.undoRetweet(postId)
        } else {
            postRepository.retweetPost(postId)
        }
    }
}