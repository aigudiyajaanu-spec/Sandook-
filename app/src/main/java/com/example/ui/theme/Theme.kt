package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color.White,
    primaryContainer = DeepForest,
    onPrimaryContainer = PremiumGold,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = MintGreen,
    tertiary = TrueGold,
    onTertiary = DeepForest,
    background = DarkBackground,
    onBackground = DarkGray,
    surface = DarkSurface,
    onSurface = DarkGray,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = SoftGray,
    error = Color(0xFFB00020),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color.White,
    primaryContainer = DeepForest,
    onPrimaryContainer = PremiumGold,
    secondary = EmeraldGreen,
    onSecondary = Color.White,
    secondaryContainer = LightSurface,
    onSecondaryContainer = MintGreen,
    tertiary = TrueGold,
    onTertiary = DeepForest,
    background = LightBackground,
    onBackground = DarkGray,
    surface = LightSurface,
    onSurface = DarkGray,
    surfaceVariant = LightBorder,
    onSurfaceVariant = SoftGray,
    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun SandookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Both configurations deliver the sleek light-first off-white and deep green visual design theme of the Sleek Interface.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
