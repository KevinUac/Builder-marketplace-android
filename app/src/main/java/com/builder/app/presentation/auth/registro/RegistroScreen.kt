package com.builder.app.presentation.auth.registro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import com.builder.app.domain.model.RolUsuario
import com.builder.app.presentation.common.BuilderButton
import com.builder.app.presentation.common.BuilderCategoryChip
import com.builder.app.presentation.common.BuilderTextField

@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = hiltViewModel(),
    onRegistroSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rol by remember { mutableStateOf(RolUsuario.CLIENTE) }

    val registroState by viewModel.registroState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Header
        Text(
            text = "Crea tu cuenta",
            style = MaterialTheme.typography.headlineLarge,
            color = Neutral50,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Únete a la comunidad BUILDER",
            style = MaterialTheme.typography.bodyLarge,
            color = Neutral500
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Name field
        BuilderTextField(
            value = nombre,
            onValueChange = { nombre = it },
            placeholder = "Tu nombre completo",
            label = "Nombre completo",
            leadingIcon = Icons.Outlined.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            placeholder = "Mínimo 6 caracteres",
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

        Spacer(modifier = Modifier.height(24.dp))

        // Role selector
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "¿Cuál es tu perfil?",
                style = MaterialTheme.typography.titleSmall,
                color = Neutral400,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BuilderCategoryChip(
                    text = "Cliente",
                    selected = rol == RolUsuario.CLIENTE,
                    onClick = { rol = RolUsuario.CLIENTE },
                    modifier = Modifier.weight(1f)
                )
                BuilderCategoryChip(
                    text = "Proveedor",
                    selected = rol == RolUsuario.PROVEEDOR,
                    onClick = { rol = RolUsuario.PROVEEDOR },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Register button
        BuilderButton(
            text = "Crear Cuenta",
            onClick = { viewModel.registrar(email, password, nombre, rol) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotBlank() && password.isNotBlank() && nombre.isNotBlank(),
            isLoading = registroState is UiState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login link
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(
                text = "¿Ya tienes cuenta? ",
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral500
            )
            TextButton(
                onClick = onNavigateToLogin,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Inicia sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Accent,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Error state
        if (registroState is UiState.Error) {
            Text(
                text = (registroState as UiState.Error).message,
                color = Error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        // Success navigation
        if (registroState is UiState.Success) {
            LaunchedEffect(Unit) {
                onRegistroSuccess()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
