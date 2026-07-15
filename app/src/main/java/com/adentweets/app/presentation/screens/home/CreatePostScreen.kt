package com.adentweets.app.presentation.screens.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.core.util.Constants
import com.adentweets.app.presentation.components.common.Base64ImageView
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.theme.AdenBlue
import com.adentweets.app.presentation.screens.home.viewmodel.CreatePostViewModel

@Composable
fun CreatePostScreen(
    navController: NavController,
    replyToPostId: String,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val content by viewModel.content.collectAsState()
    val selectedImages by viewModel.selectedImageUris.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val isPosted by viewModel.isPosted.collectAsState()
    val characterCount = viewModel.characterCount

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.addImage(it) } }

    LaunchedEffect(isPosted) {
        if (isPosted) {
            viewModel.clear()
            navController.popBackStack()
        }
    }

    LaunchedEffect(replyToPostId) {
        if (replyToPostId.isNotBlank()) {
            // Could load reply post info here
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Close, contentDescription = "Cancel")
            }
            Spacer(modifier = Modifier.weight(1f))
            XButton(
                text = "نشر",
                onClick = { viewModel.createPost(replyToPostId) },
                isLoading = isUploading,
                enabled = viewModel.isPostEnabled
            )
        }

        // Content area
        Row(modifier = Modifier.padding(horizontal = 16.dp).weight(1f)) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { if (it.length <= Constants.MAX_POST_CHARS) viewModel.updateContent(it) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                    placeholder = { Text("ماذا يحدث؟!", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                // Character counter
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Media thumbnails
                    if (selectedImages.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(selectedImages) { index, uri ->
                                Box(modifier = Modifier.size(80.dp)) {
                                    Base64ImageView(
                                        base64Data = "",
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeImage(index) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f), CircleShape)
                                    ) {
                                        Icon(Icons.Default.Close, "", modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Circular progress indicator
                    val progress = characterCount.toFloat() / Constants.MAX_POST_CHARS
                    val counterColor = when {
                        progress > 1f -> MaterialTheme.colorScheme.error
                        progress > 0.9f -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    }
                    Text(
                        text = "${Constants.MAX_POST_CHARS - characterCount}",
                        color = counterColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        // Bottom toolbar
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { imagePicker.launch("image/*") }) {
                Icon(Icons.Outlined.Image, "Add image", tint = AdenBlue)
            }
            IconButton(onClick = { /* GIF picker */ }) {
                Icon(Icons.Outlined.Tag, "GIF", tint = AdenBlue)
            }
            IconButton(onClick = { /* Emoji picker */ }) {
                Icon(Icons.Outlined.EmojiEmotions, "Emoji", tint = AdenBlue)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (characterCount > Constants.MAX_POST_CHARS) {
                Text("!", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun rememberGalleryLauncher(onResult: (Uri?) -> Unit) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { onResult(it) }