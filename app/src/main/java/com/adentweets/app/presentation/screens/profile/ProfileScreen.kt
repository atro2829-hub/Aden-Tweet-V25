package com.adentweets.app.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.Base64ImageView
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.components.post.PostCard
import com.adentweets.app.presentation.components.shimmer.PostShimmerItem
import com.adentweets.app.presentation.components.shimmer.ProfileShimmerItem
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.profile.viewmodel.ProfileViewModel
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun ProfileScreen(
    navController: NavController,
    uid: String,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val posts by viewModel.posts.collectAsState()

    LaunchedEffect(uid) { viewModel.loadProfile(uid) }

    if (isLoading) {
        ProfileShimmerItem()
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        // Banner
        item {
            Base64ImageView(
                base64Data = user?.bannerBase64 ?: "",
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                placeholder = {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant))
                }
            )
        }

        // Avatar + action buttons
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-40).dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Base64ImageView(
                    base64Data = user?.avatarBase64 ?: "",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
                    contentDescription = "Avatar",
                    isCircle = true
                )
                Row {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, "المزيد")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    XButton(
                        text = if (viewModel.isFollowing.collectAsState().value) "متابَع" else "متابعة",
                        onClick = { viewModel.toggleFollow(uid) },
                        isPrimary = !viewModel.isFollowing.collectAsState().value,
                        isSmall = true
                    )
                }
            }
        }

        // User info
        item {
            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user?.displayName ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (user?.isVerified == true) {
                        Icon(Icons.Default.CheckCircle, "حساب موثق", tint = AdenBlue, modifier = Modifier.size(20.dp).padding(start = 4.dp))
                    }
                }
                Text(text = user?.profileUrl ?: "", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                if (!user?.bio.isNullOrBlank()) {
                    Text(text = user!!.bio, style = MaterialTheme.typography.bodyLarge)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                    if (!user?.location.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, "Location", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = user!!.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    if (!user?.website.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Link, "Website", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = user!!.website, style = MaterialTheme.typography.bodySmall, color = AdenBlue)
                        }
                    }
                }
                Text(
                    text = "انضم في ${user?.formattedJoinDate ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Text(
                        text = "${user?.followingCount ?: 0} ",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(text = "متابَع ", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${user?.followerCount ?: 0} ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "متابِع", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) }
            ) {
                listOf("المنشورات", "الردود", "الوسائط", "الإعجابات").forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { viewModel.selectedTab.value = index }, text = { Text(title) })
                }
            }
        }

        // Posts
        if (posts.isEmpty() && !isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("لا توجد منشورات بعد", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                }
            }
        } else {
            items(posts, key = { it.postId }) { post ->
                PostCard(
                    post = post,
                    onPostClick = { navController.navigate(Screen.PostDetail.createRoute(post.postId)) },
                    onLike = { viewModel.toggleLike(post) },
                    onBookmark = { viewModel.toggleBookmark(post) },
                    onReply = { navController.navigate("post/create?replyToPostId=${post.postId}") }
                )
            }
        }
    }
}