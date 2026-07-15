package com.adentweets.app.core.util

object Constants {
    // Firebase Nodes
    const val USERS_NODE = "users"
    const val POSTS_NODE = "posts"
    const val FEEDS_NODE = "feeds"
    const val MESSAGES_NODE = "messages"
    const val CONVERSATIONS_NODE = "conversations"
    const val NOTIFICATIONS_NODE = "notifications"
    const val TRENDING_NODE = "trending"
    const val SEARCH_INDEX_NODE = "searchIndex"
    const val REPORTS_NODE = "reports"

    // User sub-nodes
    const val PROFILE_NODE = "profile"
    const val FOLLOWERS_NODE = "followers"
    const val FOLLOWING_NODE = "following"
    const val USER_POSTS_NODE = "posts"
    const val BOOKMARKS_NODE = "bookmarks"
    const val MUTED_USERS_NODE = "mutedUsers"
    const val BLOCKED_USERS_NODE = "blockedUsers"

    // Post sub-nodes
    const val LIKES_NODE = "likes"
    const val RETWEETS_NODE = "retweets"
    const val COMMENTS_NODE = "comments"

    // Pagination
    const val PAGE_SIZE = 10

    // Media limits
    const val MAX_POST_IMAGES = 4
    const val MAX_POST_VIDEO_DURATION_MS = 60_000L
    const val MAX_POST_CHARS = 280
    const val MAX_USERNAME_LENGTH = 15
    const val MIN_USERNAME_LENGTH = 4
    const val MAX_DISPLAY_NAME_LENGTH = 50
    const val MAX_BIO_LENGTH = 160

    // Base64 size limits (in KB)
    const val MAX_AVATAR_SIZE_KB = 200
    const val MAX_BANNER_SIZE_KB = 500
    const val MAX_POST_IMAGE_SIZE_KB = 500
    const val MAX_POST_GIF_SIZE_KB = 1024
    const val MAX_POST_VIDEO_SIZE_KB = 8192
    const val MAX_DM_IMAGE_SIZE_KB = 500
    const val MAX_DM_VIDEO_SIZE_KB = 5120

    // Dimensions
    const val AVATAR_DIMENSION = 400
    const val BANNER_WIDTH = 1500
    const val BANNER_HEIGHT = 500
    const val POST_IMAGE_MAX_WIDTH = 1080
    const val GIF_MAX_WIDTH = 480

    // DataStore keys
    const val DATASTORE_NAME = "aden_tweet_prefs"
    const val KEY_THEME_MODE = "theme_mode"
    const val KEY_FONT_SCALE = "font_scale"
    const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"

    // Animation durations
    const val ANIMATION_FAST = 150
    const val ANIMATION_NORMAL = 300
    const val ANIMATION_SLOW = 500

    // Visibility
    const val VISIBILITY_PUBLIC = "public"
    const val VISIBILITY_FOLLOWERS = "followers"
    const val VISIBILITY_MENTIONED = "mentioned"
}