package com.adentweets.app.domain.usecase.post

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class QuotePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, quoteContent: String): Resource<Post> {
        return postRepository.quotePost(postId, quoteContent)
    }
}