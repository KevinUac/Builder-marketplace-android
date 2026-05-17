package com.builder.app.presentation.perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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

    var editingName by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var editingPassword by remember { mutableStateOf(false) }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var passwordMessage by remember { mutableStateOf<String?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }
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
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, "Volver") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground, titleContentColor = TextPrimary, navigationIconContentColor = TextPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier.size(110.dp).clip(CircleShape).background(Neutral100).clickable { showPhotoOptions = true },
                    contentAlignment = Alignment.Center
                ) {
                    val displayUri = photoUri ?: if (user?.fotoUrl?.isNotBlank() == true) Uri.parse(user!!.fotoUrl) else null
                    if (displayUri != null) {
                        AsyncImage(model = displayUri, contentDescription = "Foto de perfil", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Text((user?.nombre?.firstOrNull()?.toString() ?: "?").uppercase(), style = MaterialTheme.typography.headlineLarge, color = Accent, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Accent) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(user?.nombre ?: "", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(user?.correo ?: "", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(if (user?.rol?.name == "PROVEEDOR") "Proveedor" else "Cliente", style = MaterialTheme.typography.labelMedium, color = Accent)

            Spacer(Modifier.height(32.dp))

            // Settings list in a clean card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column {
                    ProfileRow(icon = Icons.Outlined.Person, title = "Nombre completo", value = user?.nombre ?: "", onClick = { editingName = !editingName })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = DarkBorder)
                    ProfileRow(icon = Icons.Outlined.Email, title = "Correo electrónico", value = user?.correo ?: "", onClick = {})
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = DarkBorder)
                    ProfileRow(icon = Icons.Outlined.Lock, title = "Contraseña", value = "••••••••", onClick = { editingPassword = !editingPassword })
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = DarkBorder)
                    ProfileRow(icon = Icons.Outlined.Badge, title = "Tipo de cuenta", value = if (user?.rol?.name == "PROVEEDOR") "Proveedor" else "Cliente", onClick = {})
                }
            }

            // Inline editing
            if (editingName) {
                Spacer(Modifier.height(16.dp))
                BuilderTextField(value = newName, onValueChange = { newName = it }, placeholder = "Nuevo nombre", label = "Nombre", leadingIcon = Icons.Outlined.Person)
                Spacer(Modifier.height(8.dp))
                BuilderButton(text = "Guardar nombre", onClick = { viewModel.updateName(newName); editingName = false }, modifier = Modifier.fillMaxWidth(), enabled = newName.isNotBlank())
            }

            if (editingPassword) {
                Spacer(Modifier.height(16.dp))
                BuilderTextField(value = newPassword, onValueChange = { newPassword = it }, placeholder = "Nueva contraseña", label = "Nueva contraseña", leadingIcon = Icons.Outlined.Lock, visualTransformation = PasswordVisualTransformation())
                Spacer(Modifier.height(8.dp))
                BuilderTextField(value = confirmNewPassword, onValueChange = { confirmNewPassword = it }, placeholder = "Confirmar contraseña", label = "Confirmar", leadingIcon = Icons.Outlined.Lock, visualTransformation = PasswordVisualTransformation())
                if (passwordMessage != null) {
                    Spacer(Modifier.height(4.dp))
                    Text(passwordMessage!!, style = MaterialTheme.typography.labelSmall, color = if (passwordMessage!!.contains("actualizada")) Success else Error)
                }
                Spacer(Modifier.height(8.dp))
                BuilderButton(
                    text = "Cambiar contraseña",
                    onClick = {
                        viewModel.updatePassword(newPassword) { ok, msg ->
                            passwordMessage = msg
                            if (ok) { editingPassword = false; newPassword = ""; confirmNewPassword = "" }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = newPassword.length >= 6 && newPassword == confirmNewPassword
                )
            }

            if (isLoading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(color = Accent, modifier = Modifier.size(32.dp))
            }
        }
    }

    // Photo Options Bottom Sheet
    if (showPhotoOptions) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptions = false },
            containerColor = Color.White
        ) {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text("Cambiar foto de perfil", style = MaterialTheme.typography.titleMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Neutral50, onClick = { showPhotoOptions = false; cameraLauncher.launch(tempImageUri) }) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CameraAlt, null, tint = Accent)
                        Spacer(Modifier.width(16.dp))
                        Text("Tomar foto", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Neutral50, onClick = { showPhotoOptions = false; galleryLauncher.launch("image/*") }) {
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
            Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(Modifier.size(36.dp), RoundedCornerShape(8.dp), color = Accent.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Accent, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text(value, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, fontWeight = FontWeight.Medium)
            }
            Icon(Icons.Rounded.ChevronRight, null, tint = Neutral400, modifier = Modifier.size(20.dp))
        }
    }
}
