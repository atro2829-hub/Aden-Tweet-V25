package com.adentweets.app.presentation.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adentweets.app.presentation.navigation.Screen
import com.adentweets.app.presentation.screens.auth.viewmodel.SplashViewModel
import com.adentweets.app.presentation.theme.AdenBlue

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    var startAnimation by remember { mutableStateOf(false) }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200),
        label = "splashFadeIn"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    LaunchedEffect(isAuthenticated) {
        when (isAuthenticated) {
            true -> navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
            false -> navController.navigate(Screen.Welcome.route) { popUpTo(Screen.Splash.route) { inclusive = true } }
            null -> { /* still loading */ }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AdenBlue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(animatedAlpha)
        ) {
            Icon(
                imageVector = Icons.Default.TravelExplore,
                contentDescription = "AdenTweet",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "AdenTweet",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}