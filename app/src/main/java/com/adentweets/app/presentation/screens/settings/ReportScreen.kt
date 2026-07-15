package com.adentweets.app.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.XButton

@Composable
fun ReportScreen(navController: NavController) {
    var selectedReason by remember { mutableStateOf("") }
    val reasons = listOf("إنه بريد مزعج", "إنه مسيء أو ضار", "يحتوي على محتوى حساس", "يهدد بالعنف", "يتحرش بشخص ما", "أخرى")
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(4.dp)) { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }; Text("الإبلاغ", style = MaterialTheme.typography.titleMedium) }
        HorizontalDivider()
        reasons.forEach { reason ->
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                RadioButton(selected = selectedReason == reason, onClick = { selectedReason = reason })
                Spacer(modifier = Modifier.width(8.dp))
                Text(reason, style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        XButton(text = "إرسال البلاغ", onClick = { navController.popBackStack() }, isFullWidth = true, enabled = selectedReason.isNotBlank(), modifier = Modifier.padding(16.dp))
    }
}