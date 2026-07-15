package com.adentweets.app.presentation.components.common

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.decodeBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.adentweets.app.core.util.Base64Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Base64 as AndroidBase64

@Composable
fun Base64ImageView(
    base64Data: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable (BoxScope.() -> Unit)? = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    },
    isCircle: Boolean = false,
    fallbackResId: Int? = null
) {
    if (base64Data.isBlank()) {
        Box(
            modifier = modifier.background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = if (isCircle) CircleShape else RoundedCornerShape(8.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            placeholder?.invoke(this)
        }
        return
    }

    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(base64Data) {
        withContext(Dispatchers.IO) {
            try {
                val bytes = AndroidBase64.decode(base64Data, AndroidBase64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                imageBitmap = bitmap?.asImageBitmap()
            } catch (e: Exception) {
                imageBitmap = null
            }
        }
    }

    val shape = if (isCircle) CircleShape else RoundedCornerShape(8.dp)
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap!!,
            contentDescription = contentDescription,
            modifier = modifier.clip(shape),
            contentScale = contentScale
        )
    } else {
        Box(
            modifier = modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            placeholder?.invoke(this)
        }
    }
}

@Composable
fun Base64AvatarView(
    base64Data: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    contentDescription: String? = null
) {
    Base64ImageView(
        base64Data = base64Data,
        modifier = modifier.size(size),
        contentDescription = contentDescription,
        isCircle = true,
        placeholder = {
            Text(
                text = "?",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                style = MaterialTheme.typography.titleMedium
            )
        }
    )
}