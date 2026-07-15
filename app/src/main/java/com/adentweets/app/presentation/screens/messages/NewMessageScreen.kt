package com.adentweets.app.presentation.screens.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
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
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.messages.viewmodel.NewMessageViewModel
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun NewMessageScreen(navController: NavController, viewModel: NewMessageViewModel = hiltViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("رسالة جديدة", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("البحث عن أشخاص") },
            shape = RoundedCornerShape(24.dp),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AdenBlue, cursorColor = AdenBlue)
        )

        Button(
            onClick = { viewModel.searchUsers() },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            enabled = searchQuery.isNotBlank()
        ) { Text("بحث") }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Results
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(searchResults, key = { it.uid }) { user ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Conversation.createRoute(user.uid)) }.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Base64AvatarView(base64Data = user.avatarBase64, size = 44.dp, modifier = Modifier.clip(CircleShape))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.displayName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Text(user.profileUrl, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                    }
                }
                HorizontalDivider()
            }
        }
    }
}