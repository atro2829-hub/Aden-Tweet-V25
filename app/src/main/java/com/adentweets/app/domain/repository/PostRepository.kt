package com.adentweets.app.domain.repository

import com.adentweets.app.core.util.Resource
import com.adentweets.app.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    suspend fun createPost(content: String, mediaBase64List: List<String>, mediaTypes: List<String>, visibility: String, replyToPostId: String, pollOptions: List<String>?, pollDuration: Int?): Resource<Post>
    suspend fun deletePost(postId: String): Resource<Unit>
    suspend fun editPost(postId: String, newContent: String): Resource<Unit>
    suspend fun getPost(postId: String): Resource<Post>
    suspend fun likePost(postId: String): Resource<Unit>
    suspend fun unlikePost(postId: String): Resource<Unit>
    suspend fun retweetPost(postId: String): Resource<Unit>
    suspend fun undoRetweet(postId: String): Resource<Unit>
    suspend fun quotePost(postId: String, quoteContent: String): Resource<Post>
    suspend fun bookmarkPost(postId: String): Resource<Unit>
    suspend fun removeBookmark(postId: String): Resource<Unit>
    suspend fun pinPost(postId: String): Resource<Unit>
    suspend fun unpinPost(postId: String): Resource<Unit>
    suspend fun getUserPosts(uid: String, pageSize: Int, lastKey: String?): Resource<List<Post>>
    suspend fun getReplies(postId: String, pageSize: Int, lastKey: String?): Resource<List<Post>>
    suspend fun getBookmarks(pageSize: Int, lastKey: String?): Resource<List<Post>>
    fun observePost(postId: String): Flow<Post?>
    fun getUserPostsFlow(uid: String): Flow<List<Post>>
}