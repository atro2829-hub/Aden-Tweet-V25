package com.adentweets.app.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PostMetricsScreen(navController: NavController, postId: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(4.dp)) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text("إحصائيات المنشور", style = MaterialTheme.typography.titleMedium) }
        HorizontalDivider()
        MetricRow("مشاهدات", "1,234")
        MetricRow("تفاعلات", "56")
        MetricRow("تفاصيل موسّعة", "23")
        MetricRow("نقرات على الروابط", "12")
        MetricRow("مشاهدات الوسائط", "89")
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
    }
    HorizontalDivider()
}