package com.adentweets.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_posts")
data class CachedPost(
    @PrimaryKey val postId: String,
    val authorUid: String,
    val content: String,
    val mediaItemsJson: String = "[]",
    val createdAt: Long,
    val likeCount: Long,
    val retweetCount: Long,
    val replyCount: Long,
    val viewCount: Long,
    val isPinned: Boolean,
    val visibility: String,
    val replyToPostId: String = "",
    val quotePostId: String = "",
    val feedType: String = "following"
)