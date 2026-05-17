package com.builder.app.presentation.auth.registro

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.builder.app.core.ui.theme.*
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.RolUsuario
import com.builder.app.presentation.common.BuilderButton
import com.builder.app.presentation.common.BuilderCategoryChip
import com.builder.app.presentation.common.BuilderTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = hiltViewModel(),
    onRegistroSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var rol by remember { mutableStateOf(RolUsuario.CLIENTE) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }

    // Provider-specific fields
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var anosExperiencia by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val registroState by viewModel.registroState.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    // Stable camera URI using rememberSaveable
    val tempImageFile = remember {
        java.io.File(context.cacheDir, "builder_camera_photo.jpg")
    }
    val tempImageUri = remember {
        androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", tempImageFile)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) photoUri = uri }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) photoUri = tempImageUri }
    )

    // Validations
    val isEmailValid = email.isBlank() || android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    val hasMinLength = password.length >= 6
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasNumber = password.any { it.isDigit() }
    val isPasswordValid = password.isBlank() || hasMinLength
    val doPasswordsMatch = confirmPassword.isBlank() || password == confirmPassword
    val isFormComplete = email.isNotBlank() && isEmailValid &&
            password.isNotBlank() && hasMinLength &&
            confirmPassword.isNotBlank() && doPasswordsMatch &&
            nombre.isNotBlank()

    // Phone auto-format: XXX XXX XXXX
    fun formatPhone(raw: String): String {
        val digits = raw.filter { it.isDigit() }.take(10)
        return buildString {
            digits.forEachIndexed { i, c ->
                if (i == 3 || i == 6) append(' ')
                append(c)
            }
        }
    }

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = DarkSurfaceElevated,
            icon = {
                Surface(Modifier.size(64.dp), CircleShape, color = Success.copy(alpha = 0.15f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = Success, modifier = Modifier.size(36.dp))
                    }
                }
            },
            title = { Text("¡Cuenta creada!", color = TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            text = { Text("Tu cuenta se ha creado exitosamente.\nInicia sesión para continuar.", color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
            confirmButton = {
                BuilderButton(
                    text = "Iniciar Sesión",
                    onClick = { showSuccessDialog = false; onNavigateToLogin() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }

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
        Spacer(modifier = Modifier.height(40.dp))

        Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Únete a la comunidad BUILDER", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(modifier = Modifier.height(32.dp))

        // Profile Photo Picker
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(DarkSurfaceElevated)
                .clickable { showPhotoOptions = true },
            contentAlignment = Alignment.Center
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(Icons.Outlined.AddAPhoto, contentDescription = "Añadir foto", tint = TextSecondary, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Añadir foto de perfil", style = MaterialTheme.typography.labelSmall, color = TextSecondary)

        // Photo options bottom sheet
        if (showPhotoOptions) {
            ModalBottomSheet(
                onDismissRequest = { showPhotoOptions = false },
                containerColor = Color.White,
                dragHandle = null
            ) {
                Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text("Selecciona una opción", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Neutral50,
                        onClick = { showPhotoOptions = false; cameraLauncher.launch(tempImageUri) }
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CameraAlt, null, tint = Accent)
                            Spacer(Modifier.width(16.dp))
                            Text("Tomar foto", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Neutral50,
                        onClick = { showPhotoOptions = false; galleryLauncher.launch("image/*") }
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.PhotoLibrary, null, tint = Accent)
                            Spacer(Modifier.width(16.dp))
                            Text("Elegir de galería", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Role selector
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BuilderCategoryChip(text = "Soy Cliente", selected = rol == RolUsuario.CLIENTE, onClick = { rol = RolUsuario.CLIENTE }, modifier = Modifier.weight(1f))
            BuilderCategoryChip(text = "Soy Proveedor", selected = rol == RolUsuario.PROVEEDOR, onClick = { rol = RolUsuario.PROVEEDOR }, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Name
        BuilderTextField(value = nombre, onValueChange = { nombre = it }, placeholder = "Tu nombre completo", label = "Nombre completo", leadingIcon = Icons.Outlined.Person)

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        BuilderTextField(
            value = email, onValueChange = { email = it }, placeholder = "correo@ejemplo.com", label = "Correo electrónico", leadingIcon = Icons.Outlined.Email,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Email)
        )
        AnimatedVisibility(visible = !isEmailValid) {
            Text("Ingresa un correo electrónico válido", color = Error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        BuilderTextField(
            value = password, onValueChange = { password = it }, placeholder = "Mínimo 6 caracteres", label = "Contraseña", leadingIcon = Icons.Outlined.Lock,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = if (passwordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff, contentDescription = "Toggle password", tint = Neutral500, modifier = Modifier.size(20.dp))
                }
            }
        )

        // Password requirements checklist
        AnimatedVisibility(visible = password.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(10.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(Modifier.padding(12.dp)) {
                    PasswordRequirement("Mínimo 6 caracteres", hasMinLength)
                    PasswordRequirement("Al menos una mayúscula", hasUpperCase)
                    PasswordRequirement("Al menos un número", hasNumber)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        BuilderTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "Repite tu contraseña", label = "Confirmar Contraseña", leadingIcon = Icons.Outlined.Lock,
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = if (confirmPasswordVisible) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff, contentDescription = "Toggle confirm password", tint = Neutral500, modifier = Modifier.size(20.dp))
                }
            }
        )
        AnimatedVisibility(visible = !doPasswordsMatch) {
            Text("Las contraseñas no coinciden", color = Error, style = MaterialTheme.typography.labelSmall, modifier = Modifier.fillMaxWidth().padding(top = 4.dp, start = 16.dp))
        }

        // ─── Provider extra fields ─────────────────
        AnimatedVisibility(visible = rol == RolUsuario.PROVEEDOR) {
            Column {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Información de proveedor", style = MaterialTheme.typography.titleSmall, color = Accent, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Phone
                BuilderTextField(
                    value = telefono,
                    onValueChange = { raw ->
                        val digits = raw.filter { it.isDigit() }.take(10)
                        telefono = formatPhone(digits)
                    },
                    placeholder = "981 197 9815",
                    label = "Teléfono",
                    leadingIcon = Icons.Outlined.Phone,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date of birth
                BuilderTextField(
                    value = fechaNacimiento,
                    onValueChange = { raw ->
                        val digits = raw.filter { it.isDigit() }.take(8)
                        fechaNacimiento = buildString {
                            digits.forEachIndexed { i, c ->
                                if (i == 2 || i == 4) append('/')
                                append(c)
                            }
                        }
                    },
                    placeholder = "DD/MM/AAAA",
                    label = "Fecha de nacimiento",
                    leadingIcon = Icons.Outlined.CalendarMonth,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Years of experience
                BuilderTextField(
                    value = anosExperiencia,
                    onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) anosExperiencia = it },
                    placeholder = "Ej. 5",
                    label = "Años de experiencia",
                    leadingIcon = Icons.Outlined.WorkHistory,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Register button
        BuilderButton(
            text = "Crear Cuenta",
            onClick = {
                viewModel.registrar(
                    email, password, nombre, rol, photoUri,
                    telefono.filter { it.isDigit() },
                    fechaNacimiento,
                    anosExperiencia.toIntOrNull() ?: 0
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isFormComplete,
            isLoading = registroState is UiState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Login link
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 32.dp)) {
            Text("¿Ya tienes cuenta? ", style = MaterialTheme.typography.bodyMedium, color = Neutral500)
            TextButton(onClick = onNavigateToLogin, contentPadding = PaddingValues(0.dp)) {
                Text("Inicia sesión", style = MaterialTheme.typography.bodyMedium, color = Accent, fontWeight = FontWeight.SemiBold)
            }
        }

        // Error state
        if (registroState is UiState.Error) {
            Surface(
                color = Error.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Error.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.ErrorOutline, contentDescription = null, tint = Error)
                    Spacer(Modifier.width(12.dp))
                    Text((registroState as UiState.Error).message, color = Error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        // Success → show dialog instead of auto-navigating
        if (registroState is UiState.Success) {
            LaunchedEffect(Unit) { showSuccessDialog = true }
        }
    }
}

@Composable
private fun PasswordRequirement(text: String, met: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (met) Icons.Rounded.CheckCircle else Icons.Rounded.Cancel,
            null,
            tint = if (met) Success else Neutral600,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.labelSmall, color = if (met) Success else Neutral500)
    }
}
