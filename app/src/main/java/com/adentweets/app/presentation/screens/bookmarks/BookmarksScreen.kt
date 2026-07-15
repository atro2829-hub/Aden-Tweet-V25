package com.adentweets.app.presentation.screens.bookmarks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.EmptyStateView
import com.adentweets.app.presentation.components.post.PostCard
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.bookmarks.viewmodel.BookmarksViewModel

@Composable
fun BookmarksScreen(navController: NavController, viewModel: BookmarksViewModel = hiltViewModel()) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text("المرجعيات", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        HorizontalDivider()
        if (posts.isEmpty() && !isLoading) {
            EmptyStateView(message = "لا توجد مرجعيات بعد. احفظ المنشورات لقراءتها لاحقاً.")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(posts, key = { it.postId }) { post ->
                    PostCard(
                        post = post,
                        onPostClick = { navController.navigate(Screen.PostDetail.createRoute(post.postId)) },
                        onBookmark = { viewModel.removeBookmark(post) },
                        onLike = {},
                        onRetweet = {},
                        onReply = { navController.navigate("post/create?replyToPostId=${post.postId}") }
                    )
                }
            }
        }
    }
}