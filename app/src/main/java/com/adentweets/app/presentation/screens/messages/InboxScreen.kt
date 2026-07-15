package com.adentweets.app.presentation.screens.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.Base64AvatarView
import com.adentweets.app.presentation.components.common.EmptyStateView
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.messages.viewmodel.InboxViewModel

@Composable
fun InboxScreen(navController: NavController, viewModel: InboxViewModel = hiltViewModel()) {
    val conversations by viewModel.conversations.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("الرسائل", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            IconButton(onClick = { navController.navigate(Screen.NewMessage.route) }) { Icon(Icons.Default.Edit, "رسالة جديدة") }
        }
        HorizontalDivider()
        if (conversations.isEmpty()) {
            EmptyStateView(message = "لا توجد محادثات بعد", actionLabel = "ابدأ محادثة جديدة", onAction = { navController.navigate(Screen.NewMessage.route) })
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(conversations, key = { it.conversationId }) { conversation ->
                    val otherUser = conversation.participantDetails.values.firstOrNull()
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Conversation.createRoute(conversation.conversationId)) }.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Base64AvatarView(base64Data = otherUser?.avatarBase64 ?: "", size = 52.dp, modifier = Modifier.clip(CircleShape))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(otherUser?.displayName ?: "Unknown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                Text(conversation.formattedLastMessageTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                            }
                            Text(conversation.lastMessage.ifBlank { "ابدأ محادثة" }, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                        if (conversation.getUnreadCountForUser("") > 0) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) { Text("${conversation.getUnreadCountForUser("")}") }
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}