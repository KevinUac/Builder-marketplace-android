package com.builder.app.presentation.mapa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.builder.app.core.ui.theme.*
import com.builder.app.presentation.common.BuilderEmptyState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import androidx.compose.ui.platform.LocalContext
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import android.content.Intent
import android.net.Uri
import com.builder.app.domain.model.Proveedor
import com.builder.app.presentation.common.BuilderAvatar
import com.builder.app.presentation.common.BuilderButton
import com.builder.app.presentation.common.BuilderRatingStars

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onProviderClick: (String) -> Unit
) {
    val providers by viewModel.providers.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        onPermissionsResult = { permissions ->
            if (permissions.values.any { it }) {
                viewModel.refreshUserLocation()
            }
        }
    )

    var selectedProvider by remember { mutableStateOf<Proveedor?>(null) }

    LaunchedEffect(Unit) {
        locationPermissionState.launchMultiplePermissionRequest()
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(19.4326, -99.1332), 12f) // CDMX default
    }

    // Center on user location when available
    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 14f))
        }
    }

    // Filter providers with real coordinates (lat/lng != 0)
    val validProviders = providers.filter { it.latitud != 0.0 && it.longitud != 0.0 }

    val context = LocalContext.current
    val isKeyMissing = remember {
        try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val key = appInfo.metaData?.getString("com.google.android.geo.API_KEY")
            key == null || key == "YOUR_API_KEY_HERE"
        } catch (e: Exception) {
            false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = locationPermissionState.allPermissionsGranted
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = false
            )
        ) {
            validProviders.forEach { provider ->
                Marker(
                    state = MarkerState(position = LatLng(provider.latitud, provider.longitud)),
                    title = provider.nombre,
                    snippet = "${provider.categoria} · $${provider.tarifaHora}/h",
                    onClick = {
                        selectedProvider = provider
                        false
                    },
                    onInfoWindowClick = {
                        onProviderClick(provider.uid)
                    }
                )
            }
        }

        // Floating back button
        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 12.dp)
                .align(Alignment.TopStart)
                .size(44.dp),
            shape = CircleShape,
            color = DarkSurface.copy(alpha = 0.92f),
            onClick = onBack
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.ArrowBack, "Volver", tint = TextPrimary, modifier = Modifier.size(22.dp))
            }
        }

        // Provider count badge
        Surface(
            modifier = Modifier
                .statusBarsPadding()
                .padding(end = 16.dp, top = 12.dp)
                .align(Alignment.TopEnd),
            shape = RoundedCornerShape(999.dp),
            color = DarkSurface.copy(alpha = 0.92f)
        ) {
            Row(
                Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Person, null, tint = Accent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "${validProviders.size} proveedores",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Empty state overlay if no providers with coordinates
        if (validProviders.isEmpty() && providers.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface.copy(alpha = 0.95f)
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.LocationOff, null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Los proveedores aún no han compartido su ubicación",
                        style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        // Missing API Key Warning
        if (isKeyMissing) {
            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Error.copy(alpha = 0.9f)
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Warning, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Falta configurar Google Maps",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Reemplaza 'YOUR_API_KEY_HERE' en AndroidManifest.xml con una API Key válida de Google Cloud para ver el mapa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // My location FAB
        FloatingActionButton(
            onClick = { viewModel.refreshUserLocation() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp)
                .navigationBarsPadding(),
            containerColor = DarkSurface,
            contentColor = Accent,
            shape = CircleShape
        ) {
            Icon(Icons.Rounded.MyLocation, "Mi ubicación", modifier = Modifier.size(24.dp))
        }
    }

    // Provider Details Bottom Sheet
    if (selectedProvider != null) {
        val provider = selectedProvider!!
        ModalBottomSheet(
            onDismissRequest = { selectedProvider = null },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    BuilderAvatar(name = provider.nombre, size = 64.dp, imageUrl = provider.fotoUrl)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(provider.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(provider.categoria, style = MaterialTheme.typography.bodyMedium, color = Accent, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BuilderRatingStars(rating = provider.calificacion, size = 16.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("$${provider.tarifaHora}/h", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Navigation Button
                    BuilderButton(
                        text = "Cómo llegar",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${provider.latitud},${provider.longitud}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    // Profile Button
                    Surface(
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Neutral50,
                        onClick = {
                            selectedProvider = null
                            onProviderClick(provider.uid)
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Ver Perfil", color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
