package com.adentweets.app.domain.usecase.post

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        content: String,
        mediaBase64List: List<String> = emptyList(),
        mediaTypes: List<String> = emptyList(),
        visibility: String = "public",
        replyToPostId: String = "",
        pollOptions: List<String>? = null,
        pollDuration: Int? = null
    ): Resource<Post> {
        return postRepository.createPost(
            content = content,
            mediaBase64List = mediaBase64List,
            mediaTypes = mediaTypes,
            visibility = visibility,
            replyToPostId = replyToPostId,
            pollOptions = pollOptions,
            pollDuration = pollDuration
        )
    }
}