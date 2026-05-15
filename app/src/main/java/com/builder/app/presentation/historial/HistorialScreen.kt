package com.builder.app.presentation.historial

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.builder.app.core.ui.theme.*
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.EstadoServicio
import com.builder.app.domain.model.Servicio
import com.builder.app.presentation.common.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isProvider by viewModel.isProvider.collectAsState()

    val tabs = listOf("Todos", "Pendientes", "En Proceso", "Completados", "Cancelados")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            if (isProvider) "Mis Solicitudes" else "Mis Servicios",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Rounded.ArrowBack, "Volver", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkBackground, titleContentColor = TextPrimary
                    )
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 24.dp,
                    containerColor = DarkBackground,
                    contentColor = TextPrimary,
                    divider = {},
                    indicator = {},
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTab == index
                        Tab(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(index) },
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(999.dp),
                                color = if (isSelected) Accent else DarkSurfaceElevated,
                                border = if (!isSelected) BorderStroke(1.dp, DarkBorder) else null
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(color = Accent, modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Success -> {
                    val servicios = state.data
                    if (servicios.isEmpty()) {
                        BuilderEmptyState(
                            icon = Icons.Rounded.WorkHistory,
                            title = "Sin servicios",
                            subtitle = if (isProvider) "Aún no tienes solicitudes recibidas"
                                       else "Aún no has solicitado ningún servicio",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(servicios) { servicio ->
                                ServicioItem(
                                    servicio = servicio,
                                    isProvider = isProvider,
                                    onStatusUpdate = { newStatus ->
                                        viewModel.updateServiceStatus(servicio.id, newStatus)
                                    }
                                )
                            }
                            item { Spacer(Modifier.height(24.dp)) }
                        }
                    }
                }
                is UiState.Error -> {
                    BuilderEmptyState(
                        icon = Icons.Rounded.ErrorOutline,
                        title = "Error",
                        subtitle = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ServicioItem(
    servicio: Servicio,
    isProvider: Boolean,
    onStatusUpdate: (EstadoServicio) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy · HH:mm", Locale.getDefault())
    val fechaStr = dateFormat.format(Date(servicio.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            // Header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        Modifier.size(40.dp),
                        RoundedCornerShape(12.dp),
                        color = DarkSurfaceHigh
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                getCategoryIcon(servicio.categoria),
                                null, tint = Accent, modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(servicio.categoria, style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(fechaStr, style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                    }
                }
                BuilderStatusPill(estado = servicio.estado)
            }

            Spacer(Modifier.height(16.dp))

            // Show the OTHER party's info (provider sees client, client sees provider)
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(
                        if (isProvider) "Cliente" else "Proveedor",
                        style = MaterialTheme.typography.labelSmall, color = TextTertiary
                    )
                    Text(
                        if (isProvider) servicio.nombreCliente else servicio.nombreProveedor,
                        style = MaterialTheme.typography.bodyMedium, color = TextSecondary
                    )
                }
            }

            if (servicio.descripcion.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(servicio.descripcion, style = MaterialTheme.typography.bodySmall, color = TextTertiary)
            }

            if (servicio.precioEstimado > 0) {
                Spacer(Modifier.height(12.dp))
                Text("$${servicio.precioEstimado}", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = Accent)
            }

            // Action buttons — ONLY for providers on pending services
            if (isProvider && servicio.estado == EstadoServicio.PENDIENTE) {
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    BuilderButton(
                        text = "Aceptar",
                        onClick = { onStatusUpdate(EstadoServicio.EN_PROGRESO) },
                        modifier = Modifier.weight(1f)
                    )
                    BuilderGhostButton(
                        text = "Rechazar",
                        onClick = { onStatusUpdate(EstadoServicio.CANCELADO) },
                        modifier = Modifier.weight(1f),
                        contentColor = Error
                    )
                }
            } else if (isProvider && servicio.estado == EstadoServicio.EN_PROGRESO) {
                Spacer(Modifier.height(16.dp))
                BuilderButton(
                    text = "Marcar Completado",
                    icon = Icons.Rounded.CheckCircle,
                    onClick = { onStatusUpdate(EstadoServicio.COMPLETADO) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // For CLIENT — show waiting status on pending
            if (!isProvider && servicio.estado == EstadoServicio.PENDIENTE) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Warning,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Esperando respuesta del proveedor...",
                        style = MaterialTheme.typography.labelMedium,
                        color = Warning
                    )
                }
            }
        }
    }
}
