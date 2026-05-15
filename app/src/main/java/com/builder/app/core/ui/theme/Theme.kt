package com.builder.app.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════
// BUILDER — Extended Colors
// ═══════════════════════════════════════════════════════

data class BuilderColors(
    val accent: Color = Accent,
    val accentSoft: Color = AccentSoft,
    val secondaryAccent: Color = SecondaryAccent,
    val success: Color = Success,
    val warning: Color = Warning,
    val starGold: Color = StarGold,
    val onlineGreen: Color = OnlineGreen,
    val surfaceElevated: Color = DarkSurfaceElevated,
    val surfaceHigh: Color = DarkSurfaceHigh,
    val border: Color = DarkBorder,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textTertiary: Color = TextTertiary,
)

val LocalBuilderColors = staticCompositionLocalOf { BuilderColors() }

// ═══════════════════════════════════════════════════════
// Material3 Color Schemes
// ═══════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = DarkSurfaceElevated,
    onPrimaryContainer = TextPrimary,
    secondary = SecondaryAccent,
    onSecondary = Color.White,
    secondaryContainer = DarkSurfaceHigh,
    onSecondaryContainer = Neutral300,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFFFFA4AB),
    inverseSurface = Neutral50,
    inverseOnSurface = Neutral900,
    surfaceTint = Accent,
)

private val LightColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0E6),
    onPrimaryContainer = Color(0xFF4A0010),
    secondary = Primary,
    onSecondary = Color.White,
    secondaryContainer = Neutral100,
    onSecondaryContainer = Neutral700,
    background = LightBackground,
    onBackground = Neutral900,
    surface = LightSurface,
    onSurface = Neutral900,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = Neutral600,
    outline = LightBorder,
    outlineVariant = Neutral200,
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    inverseSurface = Neutral800,
    inverseOnSurface = Neutral100,
    surfaceTint = Accent,
)

// ═══════════════════════════════════════════════════════
// Theme Composable
// ═══════════════════════════════════════════════════════

@Composable
fun BuilderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val builderColors = if (darkTheme) {
        BuilderColors()
    } else {
        BuilderColors(
            surfaceElevated = LightSurfaceElevated,
            surfaceHigh = Neutral200,
            border = LightBorder,
            textPrimary = Neutral900,
            textSecondary = Neutral600,
            textTertiary = Neutral500,
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalBuilderColors provides builderColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

object BuilderTheme {
    val colors: BuilderColors
        @Composable
        get() = LocalBuilderColors.current
}
