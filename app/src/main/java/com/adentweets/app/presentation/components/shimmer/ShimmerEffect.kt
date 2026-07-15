package com.adentweets.app.presentation.components.shimmer

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1200f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim - 400f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(brush)
    )
}

@Composable
fun PostShimmerItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ShimmerEffect(
            modifier = Modifier
                .size(40.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            ShimmerEffect(modifier = Modifier.size(120.dp, 16.dp))
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerEffect(modifier = Modifier.size(200.dp, 14.dp))
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerEffect(modifier = Modifier.size(180.dp, 14.dp))
            Spacer(modifier = Modifier.height(12.dp))
            ShimmerEffect(modifier = Modifier.size(300.dp, 8.dp))
        }
    }
}

@Composable
fun ProfileShimmerItem() {
    Column(modifier = Modifier.fillMaxWidth()) {
        ShimmerEffect(modifier = Modifier.fillMaxWidth().height(150.dp))
        Spacer(modifier = Modifier.height(60.dp))
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
            ShimmerEffect(modifier = Modifier.size(80.dp).clip(androidx.compose.foundation.shape.CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                ShimmerEffect(modifier = Modifier.size(150.dp, 20.dp))
                Spacer(modifier = Modifier.height(4.dp))
                ShimmerEffect(modifier = Modifier.size(100.dp, 14.dp))
            }
        }
    }
}