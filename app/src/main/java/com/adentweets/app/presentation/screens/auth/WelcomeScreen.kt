package com.adentweets.app.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.adentweets.app.presentation.components.common.XButton
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun WelcomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.weight(1f))

            // Brand icon
            Icon(
                imageVector = Icons.Default.TravelExplore,
                contentDescription = "AdenTweet",
                modifier = Modifier.size(80.dp),
                tint = AdenBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AdenTweet",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "شاهد ما يحدث في العالم الآن.\nانضم إلى المحادثة.",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            XButton(
                text = "إنشاء حساب",
                onClick = { navController.navigate(Screen.Register.route) },
                isFullWidth = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            XButton(
                text = "تسجيل الدخول",
                onClick = { navController.navigate(Screen.Login.route) },
                isPrimary = false,
                isFullWidth = true
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}