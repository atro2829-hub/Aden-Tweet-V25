package com.adentweets.app.presentation.screens.settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SecuritySettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(4.dp)) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text("الأمان", style = MaterialTheme.typography.titleMedium) }
        HorizontalDivider()
        SettingsNavRow("تغيير كلمة المرور", "تحديث كلمة المرور الخاصة بك")
        SettingsNavRow("المصادقة الثنائية", "إضافة طبقة أمان إضافية")
        SettingsNavRow("الحسابات المرتبطة", "جوجل، إلخ.")
    }
}

@Composable
private fun SettingsNavRow(title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.bodyLarge); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)) }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
    }
    HorizontalDivider()
}