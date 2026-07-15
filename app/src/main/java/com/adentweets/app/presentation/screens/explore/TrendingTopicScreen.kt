package com.adentweets.app.presentation.screens.explore

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.EmptyStateView

@Composable
fun TrendingTopicScreen(navController: NavController, hashtag: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("رائج #$hashtag", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()
        EmptyStateView(message = "لا توجد منشورات بوسم #$hashtag بعد")
    }
}