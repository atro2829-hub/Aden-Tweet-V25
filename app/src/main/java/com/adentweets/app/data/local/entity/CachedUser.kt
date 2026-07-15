package com.adentweets.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_users")
data class CachedUser(
    @PrimaryKey val uid: String,
    val username: String,
    val displayName: String,
    val bio: String,
    val avatarBase64: String = "",
    val bannerBase64: String = "",
    val isVerified: Boolean,
    val followerCount: Long,
    val followingCount: Long,
    val postCount: Long,
    val isPrivate: Boolean,
    val isPremium: Boolean,
    val location: String = "",
    val website: String = "",
    val cachedAt: Long = System.currentTimeMillis()
)