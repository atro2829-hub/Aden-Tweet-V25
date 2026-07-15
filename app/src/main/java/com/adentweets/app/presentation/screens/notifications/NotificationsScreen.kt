package com.adentweets.app.presentation.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.domain.model.NotificationType
import com.adentweets.app.presentation.components.common.Base64AvatarView
import com.adentweets.app.presentation.components.common.EmptyStateView
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.notifications.viewmodel.NotificationsViewModel
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun NotificationsScreen(navController: NavController, viewModel: NotificationsViewModel = hiltViewModel()) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val selectedTab = remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("الإشعارات", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            if (unreadCount > 0) {
                TextButton(onClick = { viewModel.markAllAsRead() }) { Text("تحديد الكل كمقروء", color = AdenBlue) }
            }
        }
        TabRow(selectedTabIndex = selectedTab.value, containerColor = MaterialTheme.colorScheme.background, divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) }) {
            Tab(selected = selectedTab.value == 0, onClick = { selectedTab.value = 0 }, text = { Text("الكل") })
            Tab(selected = selectedTab.value == 1, onClick = { selectedTab.value = 1 }, text = { Text("الإشارات") })
        }
        val filtered = if (selectedTab.value == 1) notifications.filter { it.type == NotificationType.MENTION || it.type == NotificationType.REPLY } else notifications
        if (filtered.isEmpty()) {
            EmptyStateView(message = "لا يوجد شيء لعرضه بعد")
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.notificationId }) { notification ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).then(if (!notification.isRead) Modifier.background(AdenBlue.copy(alpha = 0.05f)) else Modifier).clickable { },
                        verticalAlignment = Alignment.Top
                    ) {
                        Base64AvatarView(base64Data = notification.fromUser?.avatarBase64 ?: "", size = 40.dp, modifier = Modifier.clip(CircleShape))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(notification.fromUser?.displayName ?: "Someone", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                if (!notification.isRead) { Spacer(modifier = Modifier.width(6.dp)); Icon(Icons.Default.Circle, "Unread", tint = AdenBlue, modifier = Modifier.size(8.dp)) }
                            }
                            Text(getNotificationText(notification), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                            Text(notification.formattedTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f), modifier = Modifier.padding(top = 2.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = when (notification.type) {
                                NotificationType.LIKE -> Icons.Default.FavoriteBorder
                                NotificationType.RETWEET -> Icons.Default.Repeat
                                NotificationType.FOLLOW -> Icons.Default.PersonAdd
                                NotificationType.REPLY -> Icons.Default.ChatBubbleOutline
                                NotificationType.MESSAGE -> Icons.Outlined.MailOutline
                                else -> Icons.Default.Notifications
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

private fun getNotificationText(notification: com.adentweets.app.domain.model.Notification): String {
    val msg = when (notification.type) {
        NotificationType.LIKE -> "liked your post"
        NotificationType.RETWEET -> "reposted your post"
        NotificationType.FOLLOW -> "followed you"
        NotificationType.REPLY -> "replied to your post"
        NotificationType.QUOTE -> "quoted your post"
        NotificationType.MENTION -> "mentioned you"
        NotificationType.MESSAGE -> "sent you a message"
    }
    return if (notification.message.isNotBlank()) notification.message else msg
}