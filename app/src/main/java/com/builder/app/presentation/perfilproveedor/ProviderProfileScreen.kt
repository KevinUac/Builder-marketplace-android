package com.builder.app.presentation.perfilproveedor

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.builder.app.core.ui.theme.*
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.Proveedor
import com.builder.app.domain.model.Resena
import com.builder.app.presentation.common.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderProfileScreen(
    providerId: String,
    viewModel: ProviderProfileViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val reviewsState by viewModel.reviewsState.collectAsState()
    var showReviewDialog by remember { mutableStateOf(false) }
    var showServiceRequestDialog by remember { mutableStateOf(false) }
    var showSuccessOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(providerId) {
        viewModel.loadProviderProfile(providerId)
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToChat.collect { chatId ->
            onNavigateToChat(chatId)
        }
    }

    // Success overlay auto-dismiss
    LaunchedEffect(showSuccessOverlay) {
        if (showSuccessOverlay) {
            delay(3000)
            showSuccessOverlay = false
        }
    }

    if (showReviewDialog) {
        AddReviewDialog(
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                viewModel.addReview(providerId, rating, comment)
                showReviewDialog = false
            }
        )
    }

    if (showServiceRequestDialog) {
        ServiceRequestDialog(
            onDismiss = { showServiceRequestDialog = false },
            onSubmit = { description ->
                (uiState as? UiState.Success)?.data?.let { provider ->
                    viewModel.requestService(provider, description)
                }
                showServiceRequestDialog = false
                showSuccessOverlay = true
            }
        )
    }

    // ─── SUCCESS OVERLAY ──────────────────
    if (showSuccessOverlay) {
        SuccessOverlay(onDismiss = { showSuccessOverlay = false })
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        // Fixed bottom action bar
        bottomBar = {
            when (val state = uiState) {
                is UiState.Success -> {
                    Surface(
                        color = Color.White,
                        tonalElevation = 0.dp,
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BuilderGhostButton(
                                text = "Chat",
                                icon = Icons.Rounded.ChatBubbleOutline,
                                onClick = { viewModel.contactProvider(state.data) },
                                modifier = Modifier.weight(1f)
                            )
                            BuilderButton(
                                text = "Contratar",
                                icon = Icons.Rounded.Handshake,
                                onClick = { showServiceRequestDialog = true },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        color = Accent,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    ProviderProfileContent(
                        proveedor = state.data,
                        reviewsState = reviewsState,
                        onAddReviewClick = { showReviewDialog = true },
                        onLikeClick = { viewModel.likeProvider(state.data.uid) },
                        onDislikeClick = { viewModel.dislikeProvider(state.data.uid) }
                    )
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProviderProfileContent(
    proveedor: Proveedor,
    reviewsState: UiState<List<Resena>>,
    onAddReviewClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ─── Profile Header ──────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            BuilderAvatar(
                name = proveedor.nombre,
                size = 80.dp,
                showOnline = true
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = proveedor.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (proveedor.verificado) {
                        Spacer(modifier = Modifier.width(8.dp))
                        BuilderVerifiedBadge()
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = proveedor.categoria,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ─── Stats Row ───────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ProfileStat(
                label = "Calificación",
                value = String.format("%.1f", proveedor.calificacion),
                icon = Icons.Rounded.Star,
                iconTint = StarGold
            )
            // Divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(DarkBorder)
            )
            ProfileStat(
                label = "Reseñas",
                value = proveedor.totalResenas.toString()
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(48.dp)
                    .background(DarkBorder)
            )
            ProfileStat(
                label = "Tarifa",
                value = "$${proveedor.tarifaHora}/h",
                valueColor = Accent
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ─── Interaction Buttons (Like / Dislike) ────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BuilderButton(
                text = "Me gusta (${proveedor.likes})",
                onClick = onLikeClick,
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.ThumbUp
            )
            BuilderGhostButton(
                text = "No me gusta (${proveedor.dislikes})",
                onClick = onDislikeClick,
                modifier = Modifier.weight(1f),
                icon = Icons.Rounded.ThumbDown
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ─── Portfolio (SINGLE section — fixed from 3x duplication) ─
        if (proveedor.portafolioUrls.isNotEmpty()) {
            BuilderSectionHeader(title = "Portafolio")
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(proveedor.portafolioUrls) { url ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(160.dp),
                        color = Neutral100
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Imagen de portafolio",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ─── Skills ──────────────────────────
        if (proveedor.habilidades.isNotEmpty()) {
            BuilderSectionHeader(title = "Habilidades")
            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.foundation.layout.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                proveedor.habilidades.forEach { habilidad ->
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = Neutral100,
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Text(
                            text = habilidad,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = Neutral300
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ─── Reviews ─────────────────────────
        BuilderSectionHeader(
            title = "Reseñas",
            actionText = "Escribir reseña",
            onAction = onAddReviewClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        when (reviewsState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    color = Accent,
                    modifier = Modifier.align(Alignment.CenterHorizontally).size(32.dp)
                )
            }
            is UiState.Success -> {
                val reviews = reviewsState.data
                if (reviews.isEmpty()) {
                    Text(
                        "Aún no hay reseñas para este proveedor.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500
                    )
                } else {
                    reviews.forEach { resena ->
                        ReviewItem(resena)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            is UiState.Error -> {
                Text(
                    "Error al cargar reseñas",
                    color = Error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            else -> {}
        }

        // Bottom spacer for the fixed action bar
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Review Item ─────────────────────────────────────
@Composable
fun ReviewItem(resena: Resena) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BuilderAvatar(
                        name = resena.nombreCliente,
                        size = 32.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        resena.nombreCliente,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
                BuilderRatingStars(rating = resena.calificacion, size = 14.dp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                resena.comentario,
                style = MaterialTheme.typography.bodyMedium,
                color = Neutral400
            )
        }
    }
}

// ─── Profile Stat ────────────────────────────────────
@Composable
fun ProfileStat(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconTint: androidx.compose.ui.graphics.Color = Accent,
    valueColor: androidx.compose.ui.graphics.Color = TextPrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Neutral500
        )
    }
}

// ─── Add Review Dialog ───────────────────────────────
@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (Float, String) -> Unit
) {
    var rating by remember { mutableStateOf(5f) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Nueva Reseña",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Tu calificación",
                    style = MaterialTheme.typography.labelMedium,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(12.dp))

                BuilderRatingStarsInteractive(
                    rating = rating,
                    onRatingChange = { rating = it },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))

                BuilderTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = "Cuéntanos tu experiencia...",
                    label = "Tu comentario",
                    singleLine = false,
                    minLines = 3
                )
            }
        },
        confirmButton = {
            BuilderButton(
                text = "Enviar",
                onClick = { onSubmit(rating, comment) },
                enabled = comment.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Neutral500)
            }
        }
    )
}

// ─── Service Request Dialog ──────────────────────────
@Composable
fun ServiceRequestDialog(
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                "Solicitar Servicio",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Describe brevemente lo que necesitas:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(16.dp))

                BuilderTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Ej. Necesito reparar una tubería con fuga...",
                    label = "Descripción",
                    singleLine = false,
                    minLines = 3
                )
            }
        },
        confirmButton = {
            BuilderButton(
                text = "Enviar Solicitud",
                onClick = { onSubmit(description) },
                enabled = description.isNotBlank()
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Neutral500)
            }
        }
    )
}

// ─── SUCCESS OVERLAY ─────────────────────────────────
@Composable
fun SuccessOverlay(onDismiss: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    // Pulse animation for the checkmark
    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = EaseInOutCubic),
            RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Full-screen semi-transparent overlay
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.7f),
        onClick = onDismiss
    ) {
        Box(contentAlignment = Alignment.Center) {
            AnimatedVisibility(
                visible = visible,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(48.dp)
                ) {
                    // Animated checkmark circle
                    Surface(
                        modifier = Modifier
                            .size(96.dp)
                            .scale(pulseScale),
                        shape = CircleShape,
                        color = Success.copy(alpha = 0.15f),
                        border = BorderStroke(2.dp, Success.copy(alpha = 0.4f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.CheckCircle,
                                "Éxito",
                                tint = Success,
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(28.dp))

                    Text(
                        "¡Solicitud Enviada!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Tu solicitud fue enviada al proveedor.\nTe notificaremos cuando responda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(28.dp))

                    BuilderButton(
                        text = "Entendido",
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}
