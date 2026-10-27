package com.example.gamecore

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object GameCoreColors {
    val Background = Color(0xFF0B0D10)
    val BackgroundSecondary = Color(0xFF101419)
    val Card = Color(0xFF151A20)
    val CardElevated = Color(0xFF1B2129)
    val Search = Color(0xFF181E25)
    val Border = Color(0xFF2A313A)
    val BorderStrong = Color(0xFF343C46)

    val Orange = Color(0xFFFF7A00)
    val OrangeLight = Color(0xFFFF963D)
    val OrangeDark = Color(0xFFD95F00)

    val TextPrimary = Color(0xFFF5F7FA)
    val TextSecondary = Color(0xFFA5ADB7)
    val TextDisabled = Color(0xFF646D78)
    val NavInactive = Color(0xFF7D8793)

    val Success = Color(0xFF35C46A)
    val Error = Color(0xFFE65353)
}

object GameCoreDimens {
    val ScreenPadding = 16.dp
    val SectionSpacing = 24.dp
    val ElementSpacing = 10.dp
    val CardRadius = 14.dp
    val ButtonRadius = 12.dp
    val SearchRadius = 12.dp
    val ButtonHeight = 48.dp
    val BottomBarHeight = 72.dp
}

object GameCoreFonts {
    val Title: FontFamily = FontFamily.SansSerif
    val Body: FontFamily = FontFamily.SansSerif
}

private val GameCoreTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = GameCoreFonts.Title,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = GameCoreFonts.Title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = GameCoreFonts.Title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = GameCoreFonts.Title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = GameCoreFonts.Body,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = GameCoreFonts.Body,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = GameCoreFonts.Body,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 17.sp
    ),
    labelLarge = TextStyle(
        fontFamily = GameCoreFonts.Body,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = GameCoreFonts.Body,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
)

private val GameCoreColorScheme = darkColorScheme(
    primary = GameCoreColors.Orange,
    onPrimary = Color.Black,
    background = GameCoreColors.Background,
    onBackground = GameCoreColors.TextPrimary,
    surface = GameCoreColors.Card,
    onSurface = GameCoreColors.TextPrimary,
    error = GameCoreColors.Error
)

@Composable
fun GameCoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GameCoreColorScheme,
        typography = GameCoreTypography,
        content = content
    )
}
