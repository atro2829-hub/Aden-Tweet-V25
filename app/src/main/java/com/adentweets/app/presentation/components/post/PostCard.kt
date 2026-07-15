package com.adentweets.app.presentation.components.post

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adentweets.app.domain.model.MediaType
import com.adentweets.app.domain.model.Post
import com.adentweets.app.presentation.components.common.Base64AvatarView
import com.adentweets.app.presentation.components.common.Base64ImageView
import com.adentweets.app.presentation.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostCard(
    post: Post,
    onPostClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onLike: () -> Unit = {},
    onRetweet: () -> Unit = {},
    onReply: () -> Unit = {},
    onBookmark: () -> Unit = {},
    onShare: () -> Unit = {},
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier,
    showActions: Boolean = true
) {
    val author = post.author
    val likeColor by animateColorAsState(
        if (post.isLikedByCurrentUser) AdenLikePink else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        label = "like_color"
    )
    val retweetColor by animateColorAsState(
        if (post.isRetweetedByCurrentUser) AdenRetweetGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        label = "rt_color"
    )
    val bookmarkColor by animateColorAsState(
        if (post.isBookmarkedByCurrentUser) AdenBookmarkBlue else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
        label = "bm_color"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onPostClick,
                onLongClick = onLongPress
            )
            .padding(horizontal = 16.dp)
    ) {
        if (post.isRetweetedByCurrentUser) {
            Row(
                modifier = Modifier.padding(start = 8.dp, top = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Reposted",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "You reposted",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        }

        Row(modifier = Modifier.padding(top = 12.dp)) {
            // Avatar
            Base64AvatarView(
                base64Data = author?.avatarBase64 ?: "",
                size = 40.dp,
                modifier = Modifier.clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .then(
                                Modifier.padding(end = 4.dp)
                            )
                    ) {
                        Text(
                            text = author?.displayName ?: "Unknown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (author?.isVerified == true) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = AdenBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = author?.profileUrl ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = " · ${post.formattedDate}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                    IconButton(onClick = onLongPress, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "More",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Content
                if (post.content.isNotBlank()) {
                    val annotatedContent = buildAnnotatedString {
                        val parts = post.content.split("(?=#|@)".toRegex())
                        for (part in parts) {
                            when {
                                part.startsWith("#") -> {
                                    val tagEnd = part.indexOfAny(charArrayOf(' ', '\n', ',', '.', '!', '?', ':', ';'))
                                    val tag = if (tagEnd >= 0) part.substring(0, tagEnd) else part
                                    withStyle(SpanStyle(color = AdenBlue, fontWeight = FontWeight.Medium)) {
                                        append(tag)
                                    }
                                    if (tagEnd >= 0) append(part.substring(tagEnd))
                                }
                                part.startsWith("@") -> {
                                    val mentionEnd = part.indexOfAny(charArrayOf(' ', '\n', ',', '.', '!', '?', ':', ';'))
                                    val mention = if (mentionEnd >= 0) part.substring(0, mentionEnd) else part
                                    withStyle(SpanStyle(color = AdenBlue, fontWeight = FontWeight.Medium)) {
                                        append(mention)
                                    }
                                    if (mentionEnd >= 0) append(part.substring(mentionEnd))
                                }
                                else -> append(part)
                            }
                        }
                    }
                    Text(
                        text = annotatedContent,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Media
                if (post.hasMedia && post.mediaItems.firstOrNull()?.isImage == true) {
                    val mediaCount = post.mediaDisplayCount
                    when {
                        mediaCount == 1 -> {
                            Base64ImageView(
                                base64Data = post.mediaItems[0].base64Data,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        }
                        mediaCount >= 2 -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(180.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                post.mediaItems.take(2).forEach { media ->
                                    Base64ImageView(
                                        base64Data = media.base64Data,
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(16.dp)),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                    )
                                }
                            }
                        }
                        mediaCount >= 3 -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(260.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    post.mediaItems.take(2).forEach { media ->
                                        Base64ImageView(
                                            base64Data = media.base64Data,
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(16.dp)),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    post.mediaItems.drop(2).take(2).forEach { media ->
                                        Base64ImageView(
                                            base64Data = media.base64Data,
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(16.dp)),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Video indicator
                if (post.hasVideo) {
                    val videoMedia = post.mediaItems.first { it.isVideo }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        if (videoMedia.hasThumbnail) {
                            Base64ImageView(
                                base64Data = videoMedia.thumbnailBase64,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play video",
                                    tint = Color.White,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                }

                // Actions
                if (showActions) {
                    PostActionBar(
                        replyCount = post.replyCount,
                        retweetCount = post.retweetCount,
                        likeCount = post.likeCount,
                        viewCount = post.viewCount,
                        isLiked = post.isLikedByCurrentUser,
                        isRetweeted = post.isRetweetedByCurrentUser,
                        isBookmarked = post.isBookmarkedByCurrentUser,
                        likeColor = likeColor,
                        retweetColor = retweetColor,
                        bookmarkColor = bookmarkColor,
                        onReply = onReply,
                        onRetweet = onRetweet,
                        onLike = onLike,
                        onBookmark = onBookmark,
                        onShare = onShare
                    )
                }
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}