package com.adentweets.app.presentation.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.Base64ImageView
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.components.common.XTextField
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun EditProfileScreen(navController: NavController) {
    var displayName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }

    val avatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { }
    val bannerPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, "Back") }
            Text("تعديل الملف الشخصي", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            XButton(text = "حفظ", onClick = { navController.popBackStack() }, isSmall = true)
        }
        HorizontalDivider()
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            // Banner
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp).background(MaterialTheme.colorScheme.surfaceVariant).clickable { bannerPicker.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {
                IconButton(modifier = Modifier.padding(8.dp).background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f), CircleShape), onClick = { bannerPicker.launch("image/*") }) {
                    Icon(Icons.Default.CameraAlt, "تغيير صورة الغلاف")
                }
            }
            // Avatar
            Box(
                modifier = Modifier.offset(y = (-30).dp).padding(start = 16.dp).size(80.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant).clickable { avatarPicker.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {
                Icon(Icons.Default.CameraAlt, "تغيير الصورة الشخصية", modifier = Modifier.size(20.dp).background(AdenBlue, CircleShape).padding(4.dp).clip(CircleShape))
            }
            Spacer(modifier = Modifier.height(24.dp))
            XTextField(value = displayName, onValueChange = { displayName = it }, placeholder = "الاسم المعروض", modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))
            XTextField(value = bio, onValueChange = { if (it.length <= 160) bio = it }, placeholder = "النبذة التعريفية", maxLines = 4, isSingleLine = false, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))
            XTextField(value = location, onValueChange = { location = it }, placeholder = "الموقع", modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(12.dp))
            XTextField(value = website, onValueChange = { website = it }, placeholder = "الموقع الإلكتروني", modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}