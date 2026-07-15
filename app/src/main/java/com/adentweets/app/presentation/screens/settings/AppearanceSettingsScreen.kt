package com.adentweets.app.presentation.screens.settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AppearanceSettingsScreen(navController: NavController) {
    var selectedTheme by remember { mutableStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(4.dp)) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text("المظهر", style = MaterialTheme.typography.titleMedium) }
        HorizontalDivider()
        Text("السمة", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp))
        Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
            listOf("فاتح", "داكن", "تلقائي").forEachIndexed { i, name ->
                OutlinedButton(onClick = { selectedTheme = i }, shape = MaterialTheme.shapes.small, colors = ButtonDefaults.outlinedButtonColors(contentColor = if (selectedTheme == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground, containerColor = if (selectedTheme == i) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent), modifier = Modifier.padding(end = 8.dp)) { Text(name) }
            }
        }
    }
}