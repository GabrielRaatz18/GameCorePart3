package com.example.gamecore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.gamecore.GameCoreColors

private val DarkColorScheme = darkColorScheme(
    primary = GameCoreColors.Orange,
    onPrimary = Color.Black,
    background = GameCoreColors.Background,
    onBackground = GameCoreColors.TextPrimary,
    surface = GameCoreColors.Background,
    onSurface = GameCoreColors.TextPrimary,
    error = GameCoreColors.Error
)

@Composable
fun GameCoreTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
