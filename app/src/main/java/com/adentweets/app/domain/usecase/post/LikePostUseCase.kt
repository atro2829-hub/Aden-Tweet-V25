package com.adentweets.app.domain.usecase.post

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, isCurrentlyLiked: Boolean): Resource<Unit> {
        return if (isCurrentlyLiked) {
            postRepository.unlikePost(postId)
        } else {
            postRepository.likePost(postId)
        }
    }
}