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
import com.adentweets.app.presentation.components.post.PostCard
import com.adentweets.app.presentation.screens.home.viewmodel.PostDetailViewModel

@Composable
fun ThreadViewScreen(
    navController: NavController,
    postId: String,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val post by viewModel.post.collectAsState()
    val replies by viewModel.replies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(postId) { viewModel.loadPost(postId) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("المحادثة", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            post?.let { p ->
                item {
                    PostCard(
                        post = p,
                        onLike = { viewModel.toggleLike(p) },
                        onRetweet = { viewModel.toggleRetweet(p) },
                        onBookmark = { viewModel.toggleBookmark(p) },
                        showActions = true
                    )
                }
            }
            items(replies, key = { it.postId }) { reply ->
                PostCard(
                    post = reply,
                    onLike = { viewModel.toggleLike(reply) },
                    showActions = true
                )
            }
        }
    }
}