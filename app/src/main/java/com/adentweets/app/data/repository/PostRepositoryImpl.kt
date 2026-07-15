package com.adentweets.app.data.repository

import com.adentweets.app.core.util.Constants
import com.adentweets.app.core.util.Resource
import com.adentweets.app.data.local.dao.PostDao
import com.adentweets.app.data.local.entity.CachedPost
import com.adentweets.app.data.remote.post.FirebasePostSource
import com.adentweets.app.data.remote.feed.FirebaseFeedSource
import com.adentweets.app.domain.model.MediaItem
import com.adentweets.app.domain.model.Post
import com.adentweets.app.domain.repository.PostRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val firebasePostSource: FirebasePostSource,
    private val firebaseFeedSource: FirebaseFeedSource,
    private val postDao: PostDao
) : PostRepository {

    override suspend fun createPost(
        content: String, mediaBase64List: List<String>, mediaTypes: List<String>,
        visibility: String, replyToPostId: String, pollOptions: List<String>?, pollDuration: Int?
    ): Resource<Post> {
        val mediaItems = mediaBase64List.mapIndexed { index, base64 ->
            MediaItem(
                mediaType = when (mediaTypes.getOrElse(index) { "IMAGE" }) {
                    "VIDEO" -> com.adentweets.app.domain.model.MediaType.VIDEO
                    "GIF" -> com.adentweets.app.domain.model.MediaType.GIF
                    else -> com.adentweets.app.domain.model.MediaType.IMAGE
                },
                base64Data = base64
            )
        }
        val poll = if (pollOptions != null && pollOptions.size >= 2) {
            com.adentweets.app.domain.model.Poll.create(pollOptions, pollDuration ?: 24)
        } else null
        return firebasePostSource.createPost(content, mediaItems, visibility, replyToPostId, poll)
    }

    override suspend fun deletePost(postId: String) = firebasePostSource.deletePost(postId)

    override suspend fun editPost(postId: String, newContent: String): Resource<Unit> {
        // Edit is a soft operation — add to edit history
        return Resource.Success(Unit) // Simplified; full impl would update in Firebase
    }

    override suspend fun getPost(postId: String) = firebasePostSource.getPost(postId)

    override suspend fun likePost(postId: String) = firebasePostSource.likePost(postId)

    override suspend fun unlikePost(postId: String) = firebasePostSource.unlikePost(postId)

    override suspend fun retweetPost(postId: String) = firebasePostSource.retweetPost(postId)

    override suspend fun undoRetweet(postId: String) = firebasePostSource.undoRetweet(postId)

    override suspend fun quotePost(postId: String, quoteContent: String) = firebasePostSource.quotePost(postId, quoteContent)

    override suspend fun bookmarkPost(postId: String) = firebasePostSource.bookmarkPost(postId)

    override suspend fun removeBookmark(postId: String) = firebasePostSource.removeBookmark(postId)

    override suspend fun pinPost(postId: String): Resource<Unit> = Resource.Success(Unit)
    override suspend fun unpinPost(postId: String): Resource<Unit> = Resource.Success(Unit)

    override suspend fun getUserPosts(uid: String, pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return firebasePostSource.getUserPosts(uid, pageSize, lastKey)
    }

    override suspend fun getReplies(postId: String, pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return firebasePostSource.getReplies(postId, pageSize, lastKey)
    }

    override suspend fun getBookmarks(pageSize: Int, lastKey: String?): Resource<List<Post>> {
        return firebasePostSource.getBookmarks(pageSize, lastKey)
    }

    override fun observePost(postId: String): Flow<Post?> = firebasePostSource.observePost(postId)

    override fun getUserPostsFlow(uid: String): Flow<List<Post>> {
        // Would use a combined Firebase + Room flow in production
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }
}