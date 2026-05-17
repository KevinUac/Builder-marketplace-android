package com.builder.app.presentation.inicio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.builder.app.core.ui.theme.*
import com.builder.app.domain.model.Proveedor
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
    onNavigateToDashboard: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToChats: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToProvider: (String) -> Unit = {}
) {
    val userSession by viewModel.userSession.collectAsState()
    val needsProfile by viewModel.needsProfile.collectAsState()
    val providerCounts by viewModel.providerCounts.collectAsState()
    val allProviders by viewModel.allProviders.collectAsState()
    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        if (needsProfile) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Rounded.AccountCircle, null, tint = Accent, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(16.dp))
                Text("Completa tu perfil", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text("Necesitamos más detalles para activar tu cuenta de proveedor.",
                    style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                Spacer(Modifier.height(32.dp))
                BuilderButton("Ir al Perfil", onClick = onNavigateToCreateProfile, modifier = Modifier.fillMaxWidth())
            }
        } else {
            userSession?.let { currentUser ->
                val isProv = currentUser.rol == RolUsuario.PROVEEDOR
                Scaffold(
                    containerColor = DarkBackground
                ) { padding ->
                    if (currentUser.rol == RolUsuario.CLIENTE) {
                        ClientHomeContent(
                            currentUser = currentUser,
                            allProviders = allProviders,
                            onNavigateToProfile = onNavigateToProfile,
                            onNavigateToProvider = onNavigateToProvider,
                            modifier = Modifier.padding(padding)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(padding),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            item {
                                // Header
                                Column(
                                    modifier = Modifier.fillMaxWidth().statusBarsPadding()
                                        .padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 24.dp)
                                ) {
                                    Image(
                                        painter = androidx.compose.ui.res.painterResource(id = com.builder.app.R.drawable.logo_builder),
                                        contentDescription = "Builder Logo",
                                        modifier = Modifier.height(28.dp),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column {
                                            Text("Hola, ${currentUser.nombre.split(" ").first()} 👋", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                                            Spacer(Modifier.height(4.dp))
                                            Text("Tu panel de actividad", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                                        }
                                        Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = Neutral50, onClick = onNavigateToProfile) {
                                            BuilderAvatar(name = currentUser.nombre, size = 44.dp, imageUrl = currentUser.fotoUrl)
                                        }
                                    }
                                }
                            }
                            item {
                                ProviderHomeContent(onNavigateToChats = onNavigateToChats, onNavigateToHistory = onNavigateToHistory)
                            }
                            item { Spacer(Modifier.height(32.dp)) }
                        }
                    }
                }
            } ?: run {
                CircularProgressIndicator(color = Accent, modifier = Modifier.align(Alignment.Center))
            }
        }
    }

}

// ═══════════════════════════════════════════════════════
// CLIENT HOME — Tabs + Providers
// ═══════════════════════════════════════════════════════

@Composable
fun ClientHomeContent(
    currentUser: com.builder.app.domain.model.Usuario,
    allProviders: List<Proveedor>,
    onNavigateToProfile: () -> Unit,
    onNavigateToProvider: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(predefinedCategories.first().name) }
    val filteredProviders = allProviders.filter { it.categoria == selectedCategory }

    Column(modifier.fillMaxSize()) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth().statusBarsPadding()
                .padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 16.dp)
        ) {
            Image(
                painter = androidx.compose.ui.res.painterResource(id = com.builder.app.R.drawable.logo_builder),
                contentDescription = "Builder Logo",
                modifier = Modifier.height(28.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
            Spacer(Modifier.height(16.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Hola, ${currentUser.nombre.split(" ").first()} 👋", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("¿Qué servicio necesitas hoy?", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
                Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = Neutral50, onClick = onNavigateToProfile) {
                    BuilderAvatar(name = currentUser.nombre, size = 44.dp, imageUrl = currentUser.fotoUrl)
                }
            }
        }

        // Horizontal category tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(predefinedCategories) { category ->
                val isSelected = category.name == selectedCategory
                Column(
                    modifier = Modifier
                        .clickable { selectedCategory = category.name }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) TextPrimary else TextSecondary
                    )
                    Spacer(Modifier.height(6.dp))
                    if (isSelected) {
                        Box(Modifier.width(40.dp).height(2.dp).background(Accent, RoundedCornerShape(1.dp)))
                    } else {
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }
        }

        HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)

        // Provider list for selected category
        if (filteredProviders.isEmpty()) {
            Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.SearchOff, null, tint = Neutral400, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Sin proveedores en $selectedCategory", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredProviders) { proveedor ->
                    ProviderHomeCard(proveedor = proveedor, onClick = { onNavigateToProvider(proveedor.uid) })
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
// PROVIDER HOME CARD — Matching Figma design
// ═══════════════════════════════════════════════════════

@Composable
fun ProviderHomeCard(proveedor: Proveedor, onClick: () -> Unit) {
    val catColor = getCategoryColor(proveedor.categoria)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                // Provider photo
                BuilderAvatar(name = proveedor.nombre, size = 72.dp, imageUrl = proveedor.fotoUrl)

                Spacer(Modifier.width(14.dp))

                Column(Modifier.weight(1f)) {
                    Text(proveedor.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    if (proveedor.anosExperiencia > 0) {
                        Text("${proveedor.anosExperiencia} Años", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    } else {
                        Text("Nuevo", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Accent)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(proveedor.categoria, style = MaterialTheme.typography.bodySmall, color = catColor)
                }

                // Profession icon
                Surface(Modifier.size(44.dp), RoundedCornerShape(10.dp), color = catColor.copy(alpha = 0.1f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(getCategoryIcon(proveedor.categoria), null, tint = catColor, modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bottom row: stars + likes/dislikes + contratar
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                BuilderRatingStars(rating = proveedor.calificacion, size = 18.dp)

                Spacer(Modifier.width(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.ThumbUp, null, tint = Neutral600, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${proveedor.likedBy.size}", style = MaterialTheme.typography.bodySmall, color = Neutral600)
                }

                Spacer(Modifier.width(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.ThumbDown, null, tint = Neutral600, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("${proveedor.dislikedBy.size}", style = MaterialTheme.typography.bodySmall, color = Neutral600)
                }

                Spacer(Modifier.weight(1f))

                TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)) {
                    Text("Contratar", color = Accent, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
// PROVIDER HOME (Dashboard)
// ═══════════════════════════════════════════════════════

@Composable
fun ProviderHomeContent(
    onNavigateToChats: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    Column(Modifier.padding(horizontal = 24.dp)) {
        Card(
            Modifier.fillMaxWidth(), RoundedCornerShape(16.dp),
            CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(Modifier.size(44.dp), RoundedCornerShape(12.dp), color = Success.copy(alpha = 0.12f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.CheckCircle, null, tint = Success, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Tu Perfil está Activo", style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Recibirás solicitudes de clientes", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        BuilderSectionHeader(title = "Acciones rápidas")
        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard("Mensajes", Icons.Rounded.ChatBubble, CategoryBlue, onNavigateToChats, Modifier.weight(1f))
            QuickActionCard("Solicitudes", Icons.Rounded.Assignment, CategoryOrange, onNavigateToHistory, Modifier.weight(1f))
        }

        Spacer(Modifier.height(28.dp))
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
fun QuickActionCard(label: String, icon: ImageVector, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(88.dp), shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        onClick = onClick
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Surface(Modifier.size(36.dp), RoundedCornerShape(10.dp), color = color.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            }
            Text(label, style = MaterialTheme.typography.titleSmall, color = TextPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier, RoundedCornerShape(16.dp),
        CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Surface(Modifier.size(36.dp), RoundedCornerShape(10.dp), color = color.copy(alpha = 0.12f)) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = color, modifier = Modifier.size(20.dp)) }
            }
            Spacer(Modifier.height(14.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

