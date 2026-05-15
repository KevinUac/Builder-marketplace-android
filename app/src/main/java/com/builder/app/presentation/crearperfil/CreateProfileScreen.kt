package com.builder.app.presentation.crearperfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.builder.app.core.ui.theme.*
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.predefinedCategories
import com.builder.app.presentation.common.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CreateProfileScreen(
    viewModel: CreateProfileViewModel = hiltViewModel(),
    onProfileCreated: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(predefinedCategories[0]) }
    var tarifa by remember { mutableStateOf("") }
    var habilidades by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var locationGranted by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(5),
        onResult = { uris -> selectedImages = uris }
    )

    // Location permission
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        onPermissionsResult = { permissions ->
            locationGranted = permissions.values.any { it }
        }
    )

    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
    }

    // Profile completeness
    val completeness = listOf(
        tarifa.isNotBlank(),
        habilidades.isNotBlank(),
        selectedImages.isNotEmpty(),
        locationPermissionState.allPermissionsGranted
    ).count { it } / 4f

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Completar Perfil", fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground, titleContentColor = TextPrimary)
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress bar
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Progreso del perfil", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Text("${(completeness * 100).toInt()}%", style = MaterialTheme.typography.labelMedium,
                        color = Accent, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { completeness },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = Accent,
                    trackColor = DarkSurfaceHigh,
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            Spacer(Modifier.height(28.dp))

            Text("Cuéntanos más sobre tus servicios",
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary, fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth())

            Spacer(Modifier.height(24.dp))

            // ─── Category Dropdown ───────────
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedCategory.name, onValueChange = {}, readOnly = true,
                    label = { Text("Categoría Principal") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    leadingIcon = {
                        Icon(getCategoryIcon(selectedCategory.name), null, tint = Accent, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Accent, unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = Accent, focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary, cursorColor = Accent
                    )
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false },
                    containerColor = DarkSurface) {
                    predefinedCategories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(getCategoryIcon(category.name), null, tint = Accent, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Text(category.name, color = TextPrimary)
                                }
                            },
                            onClick = { selectedCategory = category; expanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // ─── Tarifa ──────────────────────
            BuilderTextField(
                value = tarifa,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) tarifa = it },
                label = "Tarifa por hora ($)",
                placeholder = "Ej. 250",
                leadingIcon = Icons.Rounded.AttachMoney,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )

            Spacer(Modifier.height(16.dp))

            // ─── Habilidades ─────────────────
            BuilderTextField(
                value = habilidades,
                onValueChange = { habilidades = it },
                label = "Habilidades",
                placeholder = "Ej. Reparación, Instalación, Mantenimiento",
                leadingIcon = Icons.Rounded.Psychology,
                singleLine = false,
                minLines = 3
            )

            Spacer(Modifier.height(24.dp))

            // ─── UBICACIÓN ───────────────────
            BuilderSectionHeader(title = "Tu Ubicación")
            Spacer(Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = BorderStroke(1.dp,
                    if (locationPermissionState.allPermissionsGranted) Success.copy(alpha = 0.3f) else DarkBorder)
            ) {
                Row(
                    Modifier.padding(20.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        Modifier.size(44.dp),
                        RoundedCornerShape(12.dp),
                        color = if (locationPermissionState.allPermissionsGranted) Success.copy(alpha = 0.12f)
                               else Warning.copy(alpha = 0.12f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (locationPermissionState.allPermissionsGranted) Icons.Rounded.LocationOn
                                else Icons.Rounded.LocationOff,
                                null,
                                tint = if (locationPermissionState.allPermissionsGranted) Success else Warning,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (locationPermissionState.allPermissionsGranted) "Ubicación activada"
                            else "Ubicación no disponible",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            if (locationPermissionState.allPermissionsGranted)
                                "Tu ubicación se guardará para que los clientes te encuentren en el mapa"
                            else "Activa la ubicación para aparecer en el mapa de clientes",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                    if (!locationPermissionState.allPermissionsGranted) {
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = { locationPermissionState.launchMultiplePermissionRequest() }) {
                            Text("Activar", color = Accent, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ─── Portfolio Upload ─────────────
            BuilderSectionHeader(title = "Portafolio (máx. 5 fotos)")
            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                item {
                    Card(
                        modifier = Modifier.size(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = BorderStroke(1.dp, DarkBorder),
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Rounded.AddPhotoAlternate, "Agregar", tint = TextTertiary,
                                    modifier = Modifier.size(28.dp))
                                Spacer(Modifier.height(4.dp))
                                Text("Agregar", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                            }
                        }
                    }
                }
                items(selectedImages) { uri ->
                    Surface(Modifier.size(100.dp), RoundedCornerShape(12.dp), color = DarkSurfaceElevated) {
                        AsyncImage(uri, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }
                }
            }

            Spacer(Modifier.height(36.dp))

            // ─── Save Button ─────────────────
            BuilderButton(
                text = "Guardar y Continuar",
                icon = Icons.Rounded.ArrowForward,
                onClick = {
                    val tarifaDouble = tarifa.toDoubleOrNull() ?: 0.0
                    viewModel.saveProfile(selectedCategory.name, tarifaDouble, habilidades, selectedImages)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = tarifa.isNotBlank() && uiState !is UiState.Loading,
                isLoading = uiState is UiState.Loading
            )

            when (uiState) {
                is UiState.Success -> {
                    LaunchedEffect(Unit) { onProfileCreated() }
                }
                is UiState.Error -> {
                    Spacer(Modifier.height(16.dp))
                    Text((uiState as UiState.Error).message, color = Error,
                        style = MaterialTheme.typography.bodySmall)
                }
                else -> {}
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
