package com.adentweets.app.domain.model

data class TrendingTopic(
    val hashtag: String = "",
    val postCount: Long = 0,
    val category: String = "",
    val rank: Int = 0
) {
    val displayHashtag: String get() = if (hashtag.startsWith("#")) hashtag else "#$hashtag"
}