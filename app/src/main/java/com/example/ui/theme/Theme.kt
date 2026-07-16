package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = SlateDark,
    secondary = GoldSecondary,
    onSecondary = SlateDark,
    tertiary = GoldAccent,
    background = SlateDark,
    onBackground = TextWhite,
    surface = SlateCard,
    onSurface = TextWhite,
    surfaceVariant = SlateLight,
    onSurfaceVariant = TextMuted,
    error = ColorError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force gorgeous dark theme for high-end barbershop vibe
    dynamicColor: Boolean = false, // Disable dynamic colors to maintain premium branding
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
