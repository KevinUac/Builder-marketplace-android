package com.builder.app.presentation.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.builder.app.core.ui.theme.*
import com.builder.app.core.utils.UiState
import com.builder.app.presentation.common.BuilderButton
import com.builder.app.presentation.common.BuilderGhostButton
import com.builder.app.presentation.common.BuilderTextField

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val loginState by viewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Header
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineLarge,
            color = Neutral50,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicia sesión en tu cuenta",
            style = MaterialTheme.typography.bodyLarge,
            color = Neutral500
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Email field
        BuilderTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "correo@ejemplo.com",
            label = "Correo electrónico",
            leadingIcon = Icons.Outlined.Email,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        BuilderTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Tu contraseña",
            label = "Contraseña",
            leadingIcon = Icons.Outlined.Lock,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff,
                        contentDescription = "Toggle password",
                        tint = Neutral500,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        // Forgot password
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { }) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.labelMedium,
                    color = Accent
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        BuilderButton(
            text = "Iniciar Sesión",
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank(),
            isLoading = loginState is UiState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = DarkBorder
            )
            Text(
                text = "  o continúa con  ",
                style = MaterialTheme.typography.labelSmall,
                color = Neutral600
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = DarkBorder
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Google button
        BuilderGhostButton(
            text = "Continuar con Google",
            onClick = { /* Google OAuth */ },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Register link
        Row(
            modifier = Modifier.padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes cuenta? ",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral500
            )
            TextButton(
                onClick = onNavigateToRegister,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Regístrate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Accent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Error state
        if (loginState is UiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (loginState as UiState.Error).message,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        // Success navigation
        if (loginState is UiState.Success) {
            LaunchedEffect(Unit) {
                onLoginSuccess()
            }
        }
    }
}
