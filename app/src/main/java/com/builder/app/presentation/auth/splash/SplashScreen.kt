package com.builder.app.presentation.auth.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.builder.app.core.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // Animations
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "alpha"
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
        label = "scale"
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 400, easing = EaseOutCubic),
        label = "tagline_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1500)
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is SplashNavigation.NavigateToHome -> onNavigateToHome()
                is SplashNavigation.NavigateToRoleSelection -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.builder.app.R.drawable.logo_builder),
                contentDescription = "Builder Logo",
                modifier = Modifier.width(220.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Tu servicio, a un toque",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                modifier = Modifier.alpha(taglineAlpha)
            )
        }

        // Bottom subtle accent line
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .width(40.dp)
                .height(3.dp)
                .alpha(taglineAlpha)
                .background(
                    color = Accent,
                    shape = MaterialTheme.shapes.extraLarge
                )
        )
    }
}
