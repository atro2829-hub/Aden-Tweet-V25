package com.adentweets.app.domain.usecase.post

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class GetRepliesUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, pageSize: Int = 10, lastKey: String? = null): Resource<List<Post>> {
        return postRepository.getReplies(postId, pageSize, lastKey)
    }
}