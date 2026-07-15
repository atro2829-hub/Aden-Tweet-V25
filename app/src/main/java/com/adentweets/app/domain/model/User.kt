package com.adentweets.app.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val displayName: String = "",
    val bio: String = "",
    val avatarBase64: String = "",
    val bannerBase64: String = "",
    val location: String = "",
    val website: String = "",
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val followerCount: Long = 0,
    val followingCount: Long = 0,
    val postCount: Long = 0,
    val isPrivate: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val fcmToken: String = "",
    val lastSeen: Long = System.currentTimeMillis(),
    val isOnline: Boolean = false
) {
    val formattedJoinDate: String
        get() = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale("ar"))
            .format(java.util.Date(createdAt))

    val profileUrl: String get() = "@$username"
}