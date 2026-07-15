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
fun NotificationSettingsScreen(navController: NavController) {
    var likesNotif by remember { mutableStateOf(true) }
    var rtNotif by remember { mutableStateOf(true) }
    var followNotif by remember { mutableStateOf(true) }
    var dmNotif by remember { mutableStateOf(true) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(4.dp)) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text("الإشعارات", style = MaterialTheme.typography.titleMedium) }
        HorizontalDivider()
        SettingsSwitch("إعجابات", "عندما يعجب شخص ما بمنشورك", likesNotif) { likesNotif = it }
        SettingsSwitch("إعادة النشر", "عندما يعيد شخص ما نشر منشورك", rtNotif) { rtNotif = it }
        SettingsSwitch("متابَعون جدد", "عندما يتابعك شخص ما", followNotif) { followNotif = it }
        SettingsSwitch("الرسائل المباشرة", "عندما يرسل لك شخص رسالة", dmNotif) { dmNotif = it }
    }
}

@Composable
private fun SettingsSwitch(title: String, desc: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.bodyLarge); Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)) }
        Switch(checked = checked, onCheckedChange = onChecked)
    }
    HorizontalDivider()
}