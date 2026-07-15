package com.adentweets.app.presentation.screens.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.Base64AvatarView
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.explore.viewmodel.ExploreViewModel
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun ExploreScreen(navController: NavController, viewModel: ExploreViewModel = hiltViewModel()) {
    val trending by viewModel.trending.collectAsState()
    val suggestedUsers by viewModel.suggestedUsers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("البحث في أدن توييت") },
            shape = RoundedCornerShape(24.dp),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdenBlue, cursorColor = AdenBlue),
            singleLine = true
        )
        // Search button
        XButton(
            text = "بحث",
            onClick = { if (searchQuery.isNotBlank()) navController.navigate(Screen.SearchResults.createRoute(searchQuery)) },
            isFullWidth = true,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // Trending section
            if (trending.isNotEmpty()) {
                item {
                    Text("رائج", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
                items(trending) { topic ->
                    Column(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.TrendingTopic.createRoute(topic.hashtag)) }.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(topic.displayHashtag, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, color = AdenBlue)
                        Text("${topic.postCount} منشور", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                    HorizontalDivider()
                }
            }

            // Suggested users
            if (suggestedUsers.isNotEmpty()) {
                item {
                    Text("مقترحون لك", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
                items(suggestedUsers, key = { it.uid }) { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).clickable { navController.navigate(Screen.Profile.createRoute(user.uid)) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Base64AvatarView(base64Data = user.avatarBase64, size = 44.dp, modifier = Modifier.clip(CircleShape))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(user.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                if (user.isVerified) { Spacer(modifier = Modifier.width(4.dp)); Icon(Icons.Default.Verified, null, tint = AdenBlue, modifier = Modifier.size(18.dp)) }
                            }
                            Text(user.profileUrl, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                        }
                        XButton(text = "متابعة", onClick = {}, isSmall = true)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}