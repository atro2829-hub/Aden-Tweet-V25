package com.adentweets.app.presentation.screens.settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AccountSettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text("الحساب", style = MaterialTheme.typography.titleMedium) }
        HorizontalDivider()
        SettingsTextField("البريد الإلكتروني", "user@example.com")
        SettingsTextField("الهاتف", "غير محدد")
        SettingsTextField("اسم المستخدم", "@username")
    }
}

@Composable
private fun SettingsTextField(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Column(modifier = Modifier.weight(1f)) { Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)); Text(value, style = MaterialTheme.typography.bodyLarge) }
    }
    HorizontalDivider()
}