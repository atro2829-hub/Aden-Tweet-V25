package com.adentweets.app.domain.usecase.post

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.repository.PostRepository
import javax.inject.Inject

class BookmarkPostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, isCurrentlyBookmarked: Boolean): Resource<Unit> {
        return if (isCurrentlyBookmarked) {
            postRepository.removeBookmark(postId)
        } else {
            postRepository.bookmarkPost(postId)
        }
    }
}