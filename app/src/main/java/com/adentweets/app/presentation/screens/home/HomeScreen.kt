package com.adentweets.app.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.EmptyStateView
import com.adentweets.app.presentation.components.common.ErrorStateView
import com.adentweets.app.presentation.components.post.PostCard
import com.adentweets.app.presentation.components.shimmer.PostShimmerItem
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.home.viewmodel.HomeViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val listState = rememberLazyListState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)

    // Infinite scroll
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem?.index ?: 0 >= layoutInfo.totalItemsCount - 3
        }.collect { nearEnd ->
            if (nearEnd && hasMore) viewModel.loadMore()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with tabs
        Column {
            Text(
                text = "الرئيسية",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                indicator = { tabPositions ->
                    if (selectedTab < tabPositions.size) {
                        val tabPos = tabPositions[selectedTab]
                        Box(
                            Modifier
                                .offset(x = tabPos.left)
                                .width(tabPos.width)
                                .height(2.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                },
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { viewModel.switchTab(0) },
                    text = { Text("لك", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { viewModel.switchTab(1) },
                    text = { Text("المتابَعون", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // Content
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                error != null && posts.isEmpty() -> ErrorStateView(
                    message = error!!,
                    onRetry = { viewModel.loadFeed() }
                )
                posts.isEmpty() && !isLoading -> EmptyStateView(
                    message = "لا يوجد شيء هنا بعد. تابع أشخاصاً لرؤية منشوراتهم.",
                    actionLabel = "استكشاف",
                    onAction = { navController.navigate(Screen.Explore.route) }
                )
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (isLoading && posts.isEmpty()) {
                            items(5) { PostShimmerItem() }
                        }
                        items(posts, key = { it.postId }) { post ->
                            PostCard(
                                post = post,
                                onPostClick = { navController.navigate(Screen.PostDetail.createRoute(post.postId)) },
                                onAvatarClick = { navController.navigate(Screen.Profile.createRoute(post.authorUid)) },
                                onLike = { viewModel.toggleLike(post) },
                                onRetweet = { viewModel.toggleRetweet(post) },
                                onBookmark = { viewModel.toggleBookmark(post) },
                                onReply = { navController.navigate("post/create?replyToPostId=${post.postId}") },
                                onShare = { /* System share sheet */ }
                            )
                        }
                        if (isLoading && posts.isNotEmpty()) {
                            item { PostShimmerItem() }
                        }
                    }
                }
            }
        }
    }
}