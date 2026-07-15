package com.adentweets.app.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.EmptyStateView
import com.adentweets.app.presentation.components.common.ErrorStateView
import com.adentweets.app.presentation.components.post.PostCard
import com.adentweets.app.presentation.components.shimmer.PostShimmerItem
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.home.viewmodel.PostDetailViewModel

@Composable
fun PostDetailScreen(
    navController: NavController,
    postId: String,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val post by viewModel.post.collectAsState()
    val replies by viewModel.replies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(postId) { viewModel.loadPost(postId) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, "Back")
            }
            Text("المنشور", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()

        if (isLoading) {
            PostShimmerItem()
            PostShimmerItem()
        } else if (error != null) {
            ErrorStateView(message = error!!, onRetry = { viewModel.loadPost(postId) })
        } else if (post != null) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    PostCard(
                        post = post!!,
                        onAvatarClick = { navController.navigate(Screen.Profile.createRoute(post!!.authorUid)) },
                        onLike = { viewModel.toggleLike(post!!) },
                        onRetweet = { viewModel.toggleRetweet(post!!) },
                        onBookmark = { viewModel.toggleBookmark(post!!) },
                        onReply = { navController.navigate("post/create?replyToPostId=${post!!.postId}") },
                        showActions = true
                    )
                }
                if (replies.isEmpty()) {
                    item {
                        EmptyStateView(
                            message = "لا توجد ردود بعد. كن أول من يرد!",
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        )
                    }
                } else {
                    items(replies, key = { it.postId }) { reply ->
                        PostCard(
                            post = reply,
                            onAvatarClick = { navController.navigate(Screen.Profile.createRoute(reply.authorUid)) },
                            onLike = { viewModel.toggleLike(reply) },
                            onReply = { navController.navigate("post/create?replyToPostId=${reply.postId}") }
                        )
                    }
                }
            }
        }
    }
}