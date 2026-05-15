package com.builder.app.presentation.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.builder.app.core.ui.theme.*
import com.builder.app.domain.model.RolUsuario
import com.builder.app.domain.model.predefinedCategories
import com.builder.app.presentation.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onNavigateToCreateProfile: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToChats: () -> Unit = {}
) {
    val user by viewModel.userSession.collectAsState()
    val needsProfile by viewModel.needsProfile.collectAsState()

    LaunchedEffect(needsProfile) {
        if (needsProfile) onNavigateToCreateProfile()
    }

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            BuilderBottomBar(
                onHomeClick = {},
                onMapClick = onNavigateToMap,
                onChatsClick = onNavigateToChats,
                onHistoryClick = onNavigateToHistory,
                onLogout = { viewModel.logout(); onLogout() },
                selectedIndex = 0
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            user?.let { currentUser ->
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    // ─── Header ─────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp)
                            .padding(top = 20.dp, bottom = 24.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Hola, ${currentUser.nombre.split(" ").first()} 👋",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    if (currentUser.rol == RolUsuario.CLIENTE)
                                        "¿Qué servicio necesitas hoy?"
                                    else "Tu panel de actividad",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                            BuilderAvatar(name = currentUser.nombre, size = 44.dp)
                        }
                    }

                    if (currentUser.rol == RolUsuario.CLIENTE) {
                        ClientHomeContent(onCategoryClick = onNavigateToCategory)
                    } else {
                        ProviderHomeContent(
                            onNavigateToChats = onNavigateToChats,
                            onNavigateToHistory = onNavigateToHistory
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                }
            } ?: run {
                CircularProgressIndicator(color = Accent, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

// ─── CLIENT HOME ─────────────────────────────────────
@Composable
fun ClientHomeContent(onCategoryClick: (String) -> Unit) {
    Column(Modifier.padding(horizontal = 24.dp)) {
        // Search
        BuilderTextField(
            value = "",
            onValueChange = {},
            placeholder = "Buscar servicios...",
            leadingIcon = Icons.Rounded.Search
        )

        Spacer(Modifier.height(32.dp))
        BuilderSectionHeader(title = "Servicios")
    }

    Spacer(Modifier.height(16.dp))

    // Category grid
    Column(Modifier.padding(horizontal = 24.dp)) {
        predefinedCategories.chunked(2).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { category ->
                    CategoryCard(
                        name = category.name,
                        icon = getCategoryIcon(category.name),
                        color = getCategoryColor(category.name),
                        onClick = { onCategoryClick(category.name) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun CategoryCard(
    name: String, icon: ImageVector, color: Color,
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Surface(Modifier.size(38.dp), RoundedCornerShape(10.dp), color = color.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Text(name, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── PROVIDER HOME (with Messages & Quick Actions) ──
@Composable
fun ProviderHomeContent(
    onNavigateToChats: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column(Modifier.padding(horizontal = 24.dp)) {
        // Active badge
        Card(
            Modifier.fillMaxWidth(), RoundedCornerShape(16.dp),
            CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            border = androidx.compose.foundation.BorderStroke(1.dp, Success.copy(alpha = 0.25f))
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(44.dp), RoundedCornerShape(12.dp), color = Success.copy(alpha = 0.12f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = Success, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Tu Perfil está Activo", style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Recibirás solicitudes de clientes",
                        style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Quick actions for providers
        BuilderSectionHeader(title = "Acciones rápidas")
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // MESSAGES
            QuickActionCard(
                label = "Mensajes",
                icon = Icons.Rounded.ChatBubble,
                color = CategoryBlue,
                onClick = onNavigateToChats,
                modifier = Modifier.weight(1f)
            )
            // HISTORY / REQUESTS
            QuickActionCard(
                label = "Solicitudes",
                icon = Icons.Rounded.Assignment,
                color = CategoryOrange,
                onClick = onNavigateToHistory,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(28.dp))

        // Stats
        BuilderSectionHeader(title = "Resumen")
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Trabajos", "0", Icons.Rounded.WorkHistory, CategoryBlue, Modifier.weight(1f))
            StatCard("Ganancias", "$0", Icons.Rounded.AttachMoney, CategoryGreen, Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Calificación", "—", Icons.Rounded.Star, StarGold, Modifier.weight(1f))
            StatCard("Vistas", "0", Icons.Rounded.Visibility, CategoryPurple, Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionCard(
    label: String, icon: ImageVector, color: Color,
    onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(88.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        onClick = onClick
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Surface(Modifier.size(36.dp), RoundedCornerShape(10.dp), color = color.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Text(label, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier, RoundedCornerShape(16.dp),
        CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column(Modifier.padding(20.dp)) {
            Surface(Modifier.size(36.dp), RoundedCornerShape(10.dp), color = color.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

// ─── BOTTOM NAV (with Messages tab) ─────────────────
@Composable
fun BuilderBottomBar(
    onHomeClick: () -> Unit, onMapClick: () -> Unit,
    onChatsClick: () -> Unit, onHistoryClick: () -> Unit,
    onLogout: () -> Unit, selectedIndex: Int = 0
) {
    Surface(color = DarkSurface, border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp, modifier = Modifier.height(72.dp)) {
            data class NavItem(val label: String, val icon: ImageVector, val onClick: () -> Unit)
            val items = listOf(
                NavItem("Inicio", Icons.Rounded.Home, onHomeClick),
                NavItem("Mapa", Icons.Rounded.Map, onMapClick),
                NavItem("Mensajes", Icons.Rounded.ChatBubble, onChatsClick),
                NavItem("Historial", Icons.Rounded.History, onHistoryClick),
                NavItem("Salir", Icons.Rounded.Logout, onLogout),
            )
            items.forEachIndexed { i, item ->
                NavigationBarItem(
                    selected = selectedIndex == i,
                    onClick = item.onClick,
                    icon = { Icon(item.icon, item.label, Modifier.size(22.dp)) },
                    label = { Text(item.label, style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selectedIndex == i) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Accent, selectedTextColor = Accent,
                        unselectedIconColor = TextTertiary, unselectedTextColor = TextTertiary,
                        indicatorColor = Accent.copy(alpha = 0.1f)
                    )
                )
            }
        }
    }
}
