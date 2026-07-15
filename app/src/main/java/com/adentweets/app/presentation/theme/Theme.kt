package com.adentweets.app.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AdenBlue,
    onPrimary = LightOnBackground,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    secondary = DarkSecondary,
    onSecondary = DarkOnBackground,
    error = AdenRed,
    onError = LightOnBackground,
    outline = DarkBorder,
    outlineVariant = DarkDivider,
    surfaceVariant = DarkCard
)

private val LightColorScheme = lightColorScheme(
    primary = AdenBlue,
    onPrimary = LightOnBackground,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    secondary = LightSecondary,
    onSecondary = LightOnBackground,
    error = AdenRed,
    onError = LightOnBackground,
    outline = LightBorder,
    outlineVariant = LightDivider,
    surfaceVariant = LightCard
)

@Composable
fun AdenTweetTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AdenTweetTypography,
        content = content
    )
}