package com.adentweets.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_messages")
data class CachedMessage(
    @PrimaryKey val messageId: String,
    val conversationId: String,
    val senderUid: String,
    val content: String,
    val mediaBase64: String = "",
    val mediaType: String = "",
    val createdAt: Long,
    val isRead: Boolean = false
)