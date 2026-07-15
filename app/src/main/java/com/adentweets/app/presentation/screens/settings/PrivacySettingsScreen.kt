package com.adentweets.app.presentation.screens.settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PrivacySettingsScreen(navController: NavController) {
    var isPrivate by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(4.dp)) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text("الخصوصية والأمان", style = MaterialTheme.typography.titleMedium) }
        HorizontalDivider()
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { Text("حساب خاص", style = MaterialTheme.typography.bodyLarge); Text("فقط المتابعين المعتمدين يمكنهم رؤية منشوراتك", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)) }
            Switch(checked = isPrivate, onCheckedChange = { isPrivate = it })
        }
        HorizontalDivider()
    }
}