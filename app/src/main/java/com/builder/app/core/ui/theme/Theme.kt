package com.builder.app.core.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
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
// Material3 Color Scheme — Light Only
// ═══════════════════════════════════════════════════════

private val AppColorScheme = lightColorScheme(
    primary = Accent,
    onPrimary = Color.White,
    primaryContainer = AccentSoft,
    onPrimaryContainer = Neutral900,
    secondary = SecondaryAccent,
    onSecondary = Color.White,
    secondaryContainer = Neutral100,
    onSecondaryContainer = Neutral700,
    background = DarkBackground,
    onBackground = Neutral900,
    surface = DarkSurface,
    onSurface = Neutral900,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = Neutral600,
    outline = DarkBorder,
    outlineVariant = Neutral200,
    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainer,
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
    darkTheme: Boolean = false, // Always light
    content: @Composable () -> Unit
) {
    val builderColors = BuilderColors()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.White.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = true
            insetsController.isAppearanceLightNavigationBars = true
        }
    }

    CompositionLocalProvider(LocalBuilderColors provides builderColors) {
        MaterialTheme(
            colorScheme = AppColorScheme,
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
