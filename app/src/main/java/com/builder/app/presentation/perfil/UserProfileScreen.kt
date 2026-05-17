package com.builder.app.presentation.perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.builder.app.core.ui.theme.*
import com.builder.app.presentation.common.BuilderButton
import com.builder.app.presentation.common.BuilderTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val user by viewModel.userSession.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var sheetState by remember { mutableStateOf<ProfileEditSheet>(ProfileEditSheet.None) }
    var newName by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordMessage by remember { mutableStateOf<String?>(null) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val tempImageFile = remember { java.io.File(context.cacheDir, "profile_edit_photo.jpg") }
    val tempImageUri = remember {
        androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.provider", tempImageFile)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (uri != null) { photoUri = uri; viewModel.updatePhoto(uri) } }
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success -> if (success) { photoUri = tempImageUri; viewModel.updatePhoto(tempImageUri) } }
    )

    LaunchedEffect(user) { if (newName.isBlank()) newName = user?.nombre ?: "" }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "Volver") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White, titleContentColor = TextPrimary, navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // ─── Premium Avatar ─────────────────────────────────────────────────────────────
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Neutral50)
                        .clickable { sheetState = ProfileEditSheet.PhotoOptions },
                    contentAlignment = Alignment.Center
                ) {
                    val displayUri = photoUri ?: if (user?.fotoUrl?.isNotBlank() == true) Uri.parse(user!!.fotoUrl) else null
                    if (displayUri != null) {
                        AsyncImage(model = displayUri, contentDescription = "Foto de perfil", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Text((user?.nombre?.firstOrNull()?.toString() ?: "?").uppercase(), style = MaterialTheme.typography.displaySmall, color = Accent, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    modifier = Modifier.size(36.dp).offset(x = (-4).dp, y = (-4).dp),
                    shape = CircleShape,
                    color = Accent,
                    border = BorderStroke(3.dp, Color.White),
                    onClick = { sheetState = ProfileEditSheet.PhotoOptions }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(user?.nombre ?: "Cargando...", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Accent.copy(alpha = 0.1f)
            ) {
                Text(
                    text = if (user?.rol?.name == "PROVEEDOR") "Perfil de Proveedor" else "Perfil de Cliente",
                    style = MaterialTheme.typography.labelMedium,
                    color = Accent,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            // ─── Settings Groups ─────────────────────────────────────────────────────────────
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text("Cuenta", style = MaterialTheme.typography.titleSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                SettingsCard {
                    ProfileRow(icon = Icons.Outlined.Person, title = "Nombre completo", value = user?.nombre ?: "", onClick = { newName = user?.nombre ?: ""; sheetState = ProfileEditSheet.EditName })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = DarkBorder)
                    ProfileRow(icon = Icons.Outlined.Email, title = "Correo electrónico", value = user?.correo ?: "", onClick = {})
                }

                Spacer(Modifier.height(24.dp))
                Text("Seguridad", style = MaterialTheme.typography.titleSmall, color = TextSecondary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                SettingsCard {
                    ProfileRow(icon = Icons.Outlined.Lock, title = "Contraseña", value = "••••••••", onClick = { newPassword = ""; confirmNewPassword = ""; passwordMessage = null; sheetState = ProfileEditSheet.EditPassword })
                }
            }

            if (isLoading) {
                Spacer(Modifier.height(32.dp))
                CircularProgressIndicator(color = Accent, modifier = Modifier.size(32.dp))
            }

            Spacer(Modifier.height(48.dp))
        }
    }

    // ─── Bottom Sheets for Modals ──────────────────────────────────────────────────
    if (sheetState != ProfileEditSheet.None) {
        ModalBottomSheet(
            onDismissRequest = { sheetState = ProfileEditSheet.None },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 8.dp).padding(bottom = 32.dp).imePadding()) {
                when (sheetState) {
                    ProfileEditSheet.PhotoOptions -> {
                        Text("Actualizar foto", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(24.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Surface(modifier = Modifier.weight(1f).height(100.dp), shape = RoundedCornerShape(16.dp), color = Neutral50, onClick = { sheetState = ProfileEditSheet.None; cameraLauncher.launch(tempImageUri) }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Outlined.CameraAlt, null, tint = Accent, modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("Cámara", color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f).height(100.dp), shape = RoundedCornerShape(16.dp), color = Neutral50, onClick = { sheetState = ProfileEditSheet.None; galleryLauncher.launch("image/*") }) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(Icons.Outlined.PhotoLibrary, null, tint = Accent, modifier = Modifier.size(32.dp))
                                    Spacer(Modifier.height(8.dp))
                                    Text("Galería", color = TextPrimary, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                    ProfileEditSheet.EditName -> {
                        Text("Cambiar nombre", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Ingresa tu nuevo nombre completo.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(Modifier.height(24.dp))
                        BuilderTextField(value = newName, onValueChange = { newName = it }, placeholder = "Tu nombre", label = "Nombre completo", leadingIcon = Icons.Outlined.Person)
                        Spacer(Modifier.height(24.dp))
                        BuilderButton(text = "Guardar cambios", onClick = { viewModel.updateName(newName); sheetState = ProfileEditSheet.None }, modifier = Modifier.fillMaxWidth(), enabled = newName.isNotBlank() && newName != user?.nombre)
                    }
                    ProfileEditSheet.EditPassword -> {
                        Text("Actualizar contraseña", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("Asegúrate de usar al menos 6 caracteres.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(Modifier.height(24.dp))
                        BuilderTextField(value = newPassword, onValueChange = { newPassword = it }, placeholder = "Nueva contraseña", label = "Nueva contraseña", leadingIcon = Icons.Outlined.Lock, visualTransformation = PasswordVisualTransformation())
                        Spacer(Modifier.height(16.dp))
                        BuilderTextField(value = confirmNewPassword, onValueChange = { confirmNewPassword = it }, placeholder = "Confirmar", label = "Confirmar contraseña", leadingIcon = Icons.Outlined.Lock, visualTransformation = PasswordVisualTransformation())
                        if (passwordMessage != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(passwordMessage!!, style = MaterialTheme.typography.labelSmall, color = if (passwordMessage!!.contains("actualizada")) Success else Error)
                        }
                        Spacer(Modifier.height(24.dp))
                        BuilderButton(
                            text = "Cambiar contraseña",
                            onClick = {
                                viewModel.updatePassword(newPassword) { ok, msg ->
                                    passwordMessage = msg
                                    if (ok) { sheetState = ProfileEditSheet.None }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = newPassword.length >= 6 && newPassword == confirmNewPassword
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

enum class ProfileEditSheet { None, PhotoOptions, EditName, EditPassword }

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Neutral50),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, DarkBorder)
    ) {
        Column(content = content)
    }
}

@Composable
private fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(Modifier.size(40.dp), RoundedCornerShape(10.dp), color = Color.White, border = BorderStroke(1.dp, DarkBorder)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Medium)
            }
            if (title != "Correo electrónico") {
                Icon(Icons.Rounded.ChevronRight, null, tint = Neutral400, modifier = Modifier.size(20.dp))
            }
        }
    }
}
