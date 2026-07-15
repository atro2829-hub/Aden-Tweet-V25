package com.adentweets.app.presentation.components.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adentweets.app.presentation.theme.AdenBookmarkBlue
import com.adentweets.app.presentation.theme.AdenLikePink
import com.adentweets.app.presentation.theme.AdenRetweetGreen
import com.adentweets.app.presentation.theme.AdenShareBlue

@Composable
fun PostActionBar(
    replyCount: Long = 0,
    retweetCount: Long = 0,
    likeCount: Long = 0,
    viewCount: Long = 0,
    isLiked: Boolean = false,
    isRetweeted: Boolean = false,
    isBookmarked: Boolean = false,
    likeColor: Color = Color.Unspecified,
    retweetColor: Color = Color.Unspecified,
    bookmarkColor: Color = Color.Unspecified,
    onReply: () -> Unit = {},
    onRetweet: () -> Unit = {},
    onLike: () -> Unit = {},
    onBookmark: () -> Unit = {},
    onShare: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reply
        ActionButton(
            icon = Icons.Outlined.ChatBubbleOutline,
            count = replyCount,
            onClick = onReply,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
        )

        // Retweet
        ActionButton(
            icon = Icons.Default.Repeat,
            count = retweetCount,
            onClick = onRetweet,
            tint = retweetColor,
            modifier = Modifier.weight(1f)
        )

        // Like
        ActionButton(
            icon = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
            count = likeCount,
            onClick = onLike,
            tint = likeColor,
            modifier = Modifier.weight(1f)
        )

        // Views
        if (viewCount > 0) {
            ActionButton(
                icon = Icons.Outlined.BarChart,
                count = viewCount,
                onClick = {},
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.weight(1f)
            )
        }

        // Bookmark
        ActionButton(
            icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
            count = 0,
            onClick = onBookmark,
            tint = bookmarkColor,
            modifier = Modifier.weight(1f)
        )

        // Share
        IconButton(
            onClick = onShare,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "مشاركة",
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    count: Long,
    onClick: () -> Unit,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(36.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        if (count > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatCount(count),
                style = MaterialTheme.typography.labelSmall,
                color = tint
            )
        }
    }
}

fun formatCount(count: Long): String {
    return when {
        count >= 1_000_000 -> "%.1fM".format(count / 1_000_000.0)
        count >= 1_000 -> "%.1fK".format(count / 1_000.0)
        else -> count.toString()
    }
}