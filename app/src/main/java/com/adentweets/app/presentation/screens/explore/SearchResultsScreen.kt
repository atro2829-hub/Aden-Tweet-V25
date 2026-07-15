package com.adentweets.app.presentation.screens.explore

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.EmptyStateView
import com.adentweets.app.presentation.components.post.PostCard
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.explore.viewmodel.ExploreViewModel

@Composable
fun SearchResultsScreen(navController: NavController, query: String, viewModel: ExploreViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("نتائج البحث عن \"$query\"", style = MaterialTheme.typography.titleMedium)
        }
        HorizontalDivider()
        EmptyStateView(message = "لم يتم العثور على نتائج لـ '$query'", modifier = Modifier.fillMaxSize())
    }
}