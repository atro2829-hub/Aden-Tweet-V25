package com.adentweets.app.presentation.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adentweets.app.presentation.navigation.Screen

@Composable
fun SettingsScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("الإعدادات", style = MaterialTheme.typography.titleLarge, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        HorizontalDivider()
        SettingsItem(
            title = "الحساب",
            subtitle = "معلومات الحساب، تغيير كلمة المرور",
            icon = Icons.Default.Person,
            onClick = { navController.navigate(Screen.AccountSettings.route) }
        )
        SettingsItem(
            title = "الخصوصية والسلامة",
            subtitle = "إدارة خصوصيتك",
            icon = Icons.Default.Shield,
            onClick = { navController.navigate(Screen.PrivacySettings.route) }
        )
        SettingsItem(
            title = "الإشعارات",
            subtitle = "تفضيلات الإشعارات",
            icon = Icons.Default.Notifications,
            onClick = { navController.navigate(Screen.NotificationSettings.route) }
        )
        SettingsItem(
            title = "المظهر والعرض",
            subtitle = "المظهر، السمة، حجم الخط",
            icon = Icons.Default.Palette,
            onClick = { navController.navigate(Screen.AppearanceSettings.route) }
        )
        SettingsItem(
            title = "الأمان",
            subtitle = "كلمة المرور، المصادقة الثنائية، الجلسات",
            icon = Icons.Default.Lock,
            onClick = { navController.navigate(Screen.SecuritySettings.route) }
        )
        SettingsItem(
            title = "الحسابات المحظورة",
            subtitle = "إدارة المستخدمين المحظورين",
            icon = Icons.Default.Block,
            onClick = { navController.navigate(Screen.BlockedAccounts.route) }
        )
        SettingsItem(
            title = "الحسابات المكتومة",
            subtitle = "إدارة المستخدمين المكتومين",
            icon = Icons.Default.VolumeOff,
            onClick = { navController.navigate(Screen.MutedAccounts.route) }
        )
    }
}

@Composable
private fun SettingsItem(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.bodyLarge); Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)) }
        Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f))
    }
    HorizontalDivider()
}