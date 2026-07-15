package com.adentweets.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adentweets.app.presentation.screens.auth.*
import com.adentweets.app.presentation.screens.bookmarks.BookmarksScreen
import com.adentweets.app.presentation.screens.explore.ExploreScreen
import com.adentweets.app.presentation.screens.explore.SearchResultsScreen
import com.adentweets.app.presentation.screens.explore.TrendingTopicScreen
import com.adentweets.app.presentation.screens.home.CreatePostScreen
import com.adentweets.app.presentation.screens.home.PostMetricsScreen
import com.adentweets.app.presentation.screens.home.HomeScreen
import com.adentweets.app.presentation.screens.home.PostDetailScreen
import com.adentweets.app.presentation.screens.home.ThreadViewScreen
import com.adentweets.app.presentation.screens.messages.ConversationScreen
import com.adentweets.app.presentation.screens.messages.InboxScreen
import com.adentweets.app.presentation.screens.messages.NewMessageScreen
import com.adentweets.app.presentation.screens.notifications.NotificationsScreen
import com.adentweets.app.presentation.screens.profile.*
import com.adentweets.app.presentation.screens.settings.*
import com.adentweets.app.presentation.theme.AdenBlue
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdenTweetNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isAuthScreen = currentRoute in listOf(
        Screen.Splash.route, Screen.Welcome.route, Screen.Login.route,
        Screen.Register.route, Screen.ForgotPassword.route, Screen.PhoneVerify.route
    )
    val showBottomBar = !isAuthScreen && currentRoute !in listOf(
        Screen.CreatePost.route, Screen.Conversation.route, Screen.FullImageViewer.route,
        Screen.VideoPlayer.route, Screen.NewMessage.route
    )
    val isSubScreen = currentRoute in listOf(
        Screen.EditProfile.route, Screen.PostDetail.route, Screen.ThreadView.route,
        Screen.PostMetrics.route, Screen.Followers.route, Screen.Following.route,
        Screen.SearchResults.route, Screen.TrendingTopic.route, Screen.Settings.route,
        Screen.AccountSettings.route, Screen.PrivacySettings.route,
        Screen.NotificationSettings.route, Screen.AppearanceSettings.route,
        Screen.SecuritySettings.route, Screen.BlockedAccounts.route,
        Screen.MutedAccounts.route, Screen.Report.route, Screen.Bookmarks.route
    ) || (currentRoute?.startsWith("post/") == true && currentRoute != Screen.Home.route)

    Scaffold(
        topBar = {
            if (isSubScreen && !isAuthScreen && showBottomBar) {
                TopAppBar(
                    title = {
                        val title = when {
                            currentRoute == Screen.EditProfile.route -> "تعديل الملف الشخصي"
                            currentRoute == Screen.Settings.route -> "الإعدادات"
                            currentRoute == Screen.AccountSettings.route -> "الحساب"
                            currentRoute == Screen.PrivacySettings.route -> "الخصوصية والسلامة"
                            currentRoute == Screen.NotificationSettings.route -> "إعدادات الإشعارات"
                            currentRoute == Screen.AppearanceSettings.route -> "المظهر والعرض"
                            currentRoute == Screen.SecuritySettings.route -> "الأمان"
                            currentRoute == Screen.BlockedAccounts.route -> "الحسابات المحظورة"
                            currentRoute == Screen.MutedAccounts.route -> "الحسابات المكتومة"
                            currentRoute == Screen.Bookmarks.route -> "المرجعيات"
                            currentRoute?.startsWith("search?") == true -> "البحث"
                            currentRoute?.startsWith("trend/") == true -> "رائج"
                            else -> ""
                        }
                        if (title.isNotEmpty()) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = when {
                            item.route == Screen.Profile.route -> currentRoute?.startsWith("profile/") == true
                            else -> currentRoute == item.route
                        }
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label,
                                    tint = if (selected) AdenBlue else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    color = if (selected) AdenBlue else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            },
                            selected = selected,
                            onClick = {
                                if (item.route == Screen.Profile.route) {
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@NavigationBarItem
                                    val targetRoute = Screen.Profile.createRoute(uid)
                                    if (currentRoute != targetRoute) {
                                        navController.navigate(targetRoute) {
                                            popUpTo(Screen.Home.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                } else if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = AdenBlue.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (showBottomBar && !isSubScreen) {
                SmallFloatingActionButton(
                    onClick = { navController.navigate(Screen.CreatePost.route) },
                    containerColor = AdenBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Create, contentDescription = "إنشاء منشور", modifier = Modifier.size(24.dp))
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth screens
            composable(Screen.Splash.route) { SplashScreen(navController) }
            composable(Screen.Welcome.route) { WelcomeScreen(navController) }
            composable(Screen.Login.route) { LoginScreen(navController) }
            composable(Screen.Register.route) { RegisterScreen(navController) }
            composable(Screen.ForgotPassword.route) { ForgotPasswordScreen(navController) }
            composable(Screen.PhoneVerify.route) { PhoneVerifyScreen(navController) }

            // Main screens
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(
                route = "post/create?replyToPostId={replyToPostId}",
                arguments = listOf(navArgument("replyToPostId") { type = NavType.StringType; defaultValue = "" })
            ) { CreatePostScreen(navController, it.arguments?.getString("replyToPostId") ?: "") }
            composable(
                route = "post/{postId}",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { PostDetailScreen(navController, it.arguments?.getString("postId") ?: "") }
            composable(
                route = "post/{postId}/thread",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { ThreadViewScreen(navController, it.arguments?.getString("postId") ?: "") }

            // Profile
            composable(
                route = "profile/{uid}",
                arguments = listOf(navArgument("uid") { type = NavType.StringType })
            ) { backStackEntry ->
                val uid = backStackEntry.arguments?.getString("uid") ?: ""
                ProfileScreen(navController, uid)
            }
            composable(Screen.EditProfile.route) { EditProfileScreen(navController) }
            composable(
                route = "profile/{uid}/followers",
                arguments = listOf(navArgument("uid") { type = NavType.StringType })
            ) { FollowersScreen(navController, it.arguments?.getString("uid") ?: "") }
            composable(
                route = "profile/{uid}/following",
                arguments = listOf(navArgument("uid") { type = NavType.StringType })
            ) { FollowingScreen(navController, it.arguments?.getString("uid") ?: "") }

            // Explore
            composable(Screen.Explore.route) { ExploreScreen(navController) }
            composable(
                route = "search?q={query}",
                arguments = listOf(navArgument("q") { type = NavType.StringType })
            ) { SearchResultsScreen(navController, it.arguments?.getString("q") ?: "") }
            composable(
                route = "trend/{hashtag}",
                arguments = listOf(navArgument("hashtag") { type = NavType.StringType })
            ) { TrendingTopicScreen(navController, it.arguments?.getString("hashtag") ?: "") }

            // Notifications
            composable(Screen.Notifications.route) { NotificationsScreen(navController) }

            // Messages
            composable(Screen.Messages.route) { InboxScreen(navController) }
            composable(
                route = "messages/{conversationId}",
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { ConversationScreen(navController, it.arguments?.getString("conversationId") ?: "") }
            composable(Screen.NewMessage.route) { NewMessageScreen(navController) }

            // Bookmarks
            composable(Screen.Bookmarks.route) { BookmarksScreen(navController) }

            // Settings
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.AccountSettings.route) { AccountSettingsScreen(navController) }
            composable(Screen.PrivacySettings.route) { PrivacySettingsScreen(navController) }
            composable(Screen.NotificationSettings.route) { NotificationSettingsScreen(navController) }
            composable(Screen.AppearanceSettings.route) { AppearanceSettingsScreen(navController) }
            composable(Screen.SecuritySettings.route) { SecuritySettingsScreen(navController) }
            composable(Screen.BlockedAccounts.route) { BlockedAccountsScreen(navController) }
            composable(Screen.MutedAccounts.route) { MutedAccountsScreen(navController) }

            // Media
            composable(Screen.FullImageViewer.route) { com.adentweets.app.presentation.screens.media.FullImageViewer(navController) }
            composable(Screen.VideoPlayer.route) { com.adentweets.app.presentation.screens.media.VideoPlayerScreen(navController) }

            // Other
            composable(
                route = "post/{postId}/metrics",
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { PostMetricsScreen(navController, it.arguments?.getString("postId") ?: "") }
            composable(Screen.Report.route) { ReportScreen(navController) }
        }
    }
}