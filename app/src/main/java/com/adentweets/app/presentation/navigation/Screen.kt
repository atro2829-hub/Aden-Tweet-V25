package com.adentweets.app.presentation.navigation

import com.google.firebase.auth.FirebaseAuth

sealed class Screen(val route: String) {
    // Auth
    data object Splash : Screen("splash")
    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object PhoneVerify : Screen("phone_verify")

    // Main
    data object Home : Screen("home")
    data object CreatePost : Screen("post/create") {
        fun createRoute(replyToPostId: String = "") = "post/create?replyToPostId=$replyToPostId"
    }
    data object PostDetail : Screen("post/{postId}") {
        fun createRoute(postId: String) = "post/$postId"
    }
    data object ThreadView : Screen("post/{postId}/thread") {
        fun createRoute(postId: String) = "post/$postId/thread"
    }

    // Profile
    data object Profile : Screen("profile/{uid}") {
        fun createRoute(uid: String) = "profile/$uid"
        fun currentUserRoute(): String {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            return "profile/$uid"
        }
    }
    data object EditProfile : Screen("profile/edit")
    data object Followers : Screen("profile/{uid}/followers") {
        fun createRoute(uid: String) = "profile/$uid/followers"
    }
    data object Following : Screen("profile/{uid}/following") {
        fun createRoute(uid: String) = "profile/$uid/following"
    }

    // Explore
    data object Explore : Screen("explore")
    data object SearchResults : Screen("search?q={query}") {
        fun createRoute(query: String) = "search?q=${java.net.URLEncoder.encode(query, "UTF-8")}"
    }
    data object TrendingTopic : Screen("trend/{hashtag}") {
        fun createRoute(hashtag: String) = "trend/${java.net.URLEncoder.encode(hashtag, "UTF-8")}"
    }

    // Notifications
    data object Notifications : Screen("notifications")

    // Messages
    data object Messages : Screen("messages")
    data object Conversation : Screen("messages/{conversationId}") {
        fun createRoute(conversationId: String) = "messages/$conversationId"
    }
    data object NewMessage : Screen("messages/new")

    // Bookmarks
    data object Bookmarks : Screen("bookmarks")

    // Settings
    data object Settings : Screen("settings")
    data object AccountSettings : Screen("settings/account")
    data object PrivacySettings : Screen("settings/privacy")
    data object NotificationSettings : Screen("settings/notifications")
    data object AppearanceSettings : Screen("settings/appearance")
    data object SecuritySettings : Screen("settings/security")
    data object BlockedAccounts : Screen("settings/blocked")
    data object MutedAccounts : Screen("settings/muted")

    // Media
    data object FullImageViewer : Screen("media/image")
    data object VideoPlayer : Screen("media/video")

    // Other
    data object PostMetrics : Screen("post/{postId}/metrics") {
        fun createRoute(postId: String) = "post/$postId/metrics"
    }
    data object Report : Screen("report")
}