package com.builder.app.presentation.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.builder.app.core.ui.theme.*
import com.builder.app.domain.model.EstadoServicio
import com.builder.app.domain.model.Proveedor

// ═══════════════════════════════════════════════════════
// BUILDER — Component Library v2
// ═══════════════════════════════════════════════════════

// ─── PRIMARY BUTTON ──────────────────────────────────
@Composable
fun BuilderButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Accent,
            contentColor = Color.White,
            disabledContainerColor = Accent.copy(alpha = 0.3f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            if (icon != null) {
                Icon(icon, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── GHOST BUTTON ────────────────────────────────────
@Composable
fun BuilderGhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    contentColor: Color = TextPrimary
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DarkBorder),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = contentColor)
    ) {
        if (icon != null) {
            Icon(icon, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

// ─── TEXT FIELD ───────────────────────────────────────
@Composable
fun BuilderTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation =
        androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions =
        androidx.compose.foundation.text.KeyboardOptions.Default,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = singleLine,
            minLines = minLines,
            placeholder = { Text(placeholder, color = TextTertiary) },
            label = if (label != null) {{ Text(label) }} else null,
            leadingIcon = if (leadingIcon != null) {{
                Icon(leadingIcon, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            }} else null,
            trailingIcon = trailingIcon,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Accent,
                unfocusedBorderColor = DarkBorder,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Accent,
                errorBorderColor = Error,
                focusedLabelColor = Accent,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )
        if (isError && errorMessage != null) {
            Text(errorMessage, style = MaterialTheme.typography.labelSmall, color = Error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp))
        }
    }
}

// ─── PROVIDER CARD ───────────────────────────────────────
@Composable
fun BuilderProviderCard(
    proveedor: Proveedor,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val catColor = getCategoryColor(proveedor.categoria)
    Card(
        modifier = modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, DarkBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Provider photo
            BuilderAvatar(name = proveedor.nombre, size = 52.dp, imageUrl = proveedor.fotoUrl)

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        proveedor.nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (proveedor.verificado) {
                        Spacer(Modifier.width(6.dp))
                        BuilderVerifiedBadge(compact = true)
                    }
                }
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(getCategoryIcon(proveedor.categoria), null, tint = catColor, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(proveedor.categoria, style = MaterialTheme.typography.bodySmall, color = catColor)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BuilderRatingStars(rating = proveedor.calificacion, size = 13.dp)
                    Spacer(Modifier.width(6.dp))
                    Text("(${proveedor.totalResenas})", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.ThumbUp, null, tint = Success, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${proveedor.likedBy.size}", style = MaterialTheme.typography.labelSmall, color = Success)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.ThumbDown, null, tint = Error, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${proveedor.dislikedBy.size}", style = MaterialTheme.typography.labelSmall, color = Error)
                    }
                }
            }

            // Category icon + price
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(Modifier.size(36.dp), RoundedCornerShape(10.dp), color = catColor.copy(alpha = 0.12f)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(getCategoryIcon(proveedor.categoria), null, tint = catColor, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text("$${proveedor.tarifaHora}", style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold, color = Accent)
                Text("/hr", style = MaterialTheme.typography.labelSmall, color = TextTertiary)
            }
        }
    }
}

// ─── AVATAR ──────────────────────────────────────────────
@Composable
fun BuilderAvatar(
    name: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    size: Dp = 48.dp,
    showOnline: Boolean = false,
    accentColor: Color = getCategoryColor(name)
) {
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.size(size),
            shape = CircleShape,
            color = accentColor.copy(alpha = 0.15f)
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = name.take(1).uppercase(),
                        style = when {
                            size >= 80.dp -> MaterialTheme.typography.headlineLarge
                            size >= 56.dp -> MaterialTheme.typography.headlineSmall
                            size >= 40.dp -> MaterialTheme.typography.titleMedium
                            else -> MaterialTheme.typography.labelLarge
                        },
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (showOnline) {
            Box(
                modifier = Modifier
                    .size(if (size >= 48.dp) 14.dp else 10.dp)
                    .align(Alignment.BottomEnd)
                    .background(DarkSurface, CircleShape)
                    .padding(2.dp)
                    .background(OnlineGreen, CircleShape)
            )
        }
    }
}

// ─── RATING STARS ────────────────────────────────────
@Composable
fun BuilderRatingStars(rating: Float, modifier: Modifier = Modifier, size: Dp = 16.dp) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(5) { i ->
            val fill = (rating - i).coerceIn(0f, 1f)
            Icon(
                imageVector = when {
                    fill >= 0.75f -> Icons.Rounded.Star
                    fill >= 0.25f -> Icons.Rounded.StarHalf
                    else -> Icons.Rounded.StarBorder
                },
                contentDescription = null, tint = StarGold, modifier = Modifier.size(size)
            )
        }
    }
}

// ─── INTERACTIVE RATING STARS ────────────────────────
@Composable
fun BuilderRatingStarsInteractive(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 36.dp
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { i ->
            Icon(
                imageVector = if (i < rating) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = "Estrella ${i + 1}",
                tint = if (i < rating) StarGold else TextTertiary,
                modifier = Modifier.size(size).clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onRatingChange((i + 1).toFloat()) }
            )
        }
    }
}

// ─── VERIFIED BADGE ──────────────────────────────────
@Composable
fun BuilderVerifiedBadge(modifier: Modifier = Modifier, compact: Boolean = false) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Rounded.Verified, "Verificado", tint = CategoryBlue,
            modifier = Modifier.size(if (compact) 16.dp else 20.dp))
        if (!compact) {
            Spacer(Modifier.width(4.dp))
            Text("Verificado", style = MaterialTheme.typography.labelSmall,
                color = CategoryBlue, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── CATEGORY CHIP ───────────────────────────────────
@Composable
fun BuilderCategoryChip(
    text: String,
    icon: ImageVector? = null,
    selected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = getCategoryColor(text)
) {
    val bgColor by animateColorAsState(
        if (selected) accentColor.copy(alpha = 0.15f) else DarkSurfaceElevated, label = "bg"
    )
    val contentColor by animateColorAsState(
        if (selected) accentColor else TextSecondary, label = "fg"
    )
    val borderColor by animateColorAsState(
        if (selected) accentColor.copy(alpha = 0.4f) else DarkBorder, label = "brd"
    )

    Surface(
        modifier = modifier.height(40.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, null, tint = contentColor, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
            }
            Text(text, style = MaterialTheme.typography.labelLarge, color = contentColor,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium)
        }
    }
}

// ─── STATUS PILL ─────────────────────────────────────
@Composable
fun BuilderStatusPill(estado: EstadoServicio, modifier: Modifier = Modifier) {
    val (text, color, icon) = when (estado) {
        EstadoServicio.PENDIENTE -> Triple("Pendiente", Warning, Icons.Rounded.Schedule)
        EstadoServicio.EN_PROGRESO -> Triple("En progreso", CategoryBlue, Icons.Rounded.Autorenew)
        EstadoServicio.COMPLETADO -> Triple("Completado", Success, Icons.Rounded.CheckCircle)
        EstadoServicio.CANCELADO -> Triple("Cancelado", Error, Icons.Rounded.Cancel)
    }
    Surface(modifier = modifier, shape = RoundedCornerShape(999.dp), color = color.copy(alpha = 0.12f)) {
        Row(Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
            Text(text, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── SECTION HEADER ──────────────────────────────────
@Composable
fun BuilderSectionHeader(title: String, modifier: Modifier = Modifier,
    actionText: String? = null, onAction: (() -> Unit)? = null) {
    Row(modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
        if (actionText != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(actionText, style = MaterialTheme.typography.labelLarge, color = Accent, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

// ─── EMPTY STATE ─────────────────────────────────────
@Composable
fun BuilderEmptyState(icon: ImageVector, title: String, subtitle: String,
    modifier: Modifier = Modifier, actionText: String? = null, onAction: (() -> Unit)? = null) {
    Column(modifier = modifier.fillMaxWidth().padding(48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(Modifier.size(72.dp), CircleShape, color = DarkSurfaceElevated) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = TextTertiary, modifier = Modifier.size(32.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(20.dp))
            BuilderGhostButton(text = actionText, onClick = onAction)
        }
    }
}

// ─── SHIMMER ─────────────────────────────────────────
@Composable
fun BuilderShimmer(modifier: Modifier = Modifier, shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        -300f, 300f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart), label = "x"
    )
    Box(modifier.clip(shape).background(Brush.linearGradient(
        listOf(DarkSurfaceElevated, DarkSurfaceHigh, DarkSurfaceElevated),
        start = Offset(x, 0f), end = Offset(x + 300f, 0f)
    )))
}

@Composable
fun BuilderProviderCardSkeleton(modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth(), RoundedCornerShape(16.dp),
        CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.dp, DarkBorder)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            BuilderShimmer(Modifier.size(52.dp), CircleShape)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                BuilderShimmer(Modifier.fillMaxWidth(0.6f).height(14.dp))
                Spacer(Modifier.height(8.dp))
                BuilderShimmer(Modifier.fillMaxWidth(0.4f).height(12.dp))
                Spacer(Modifier.height(8.dp))
                BuilderShimmer(Modifier.fillMaxWidth(0.3f).height(12.dp))
            }
        }
    }
}

// ─── HELPERS ─────────────────────────────────────────
fun getCategoryIcon(categoryName: String): ImageVector = when (categoryName.lowercase()) {
    "plomería" -> Icons.Rounded.Plumbing
    "electricidad" -> Icons.Rounded.ElectricalServices
    "limpieza" -> Icons.Rounded.CleaningServices
    "pintura" -> Icons.Rounded.FormatPaint
    "carpintería" -> Icons.Rounded.Handyman
    "jardinería" -> Icons.Rounded.Yard
    "albañilería" -> Icons.Rounded.Construction
    "aire acondicionado" -> Icons.Rounded.AcUnit
    else -> Icons.Rounded.Build
}

fun getCategoryColor(name: String): Color = when (name.lowercase().take(2)) {
    "pl" -> CategoryBlue
    "el" -> CategoryYellow
    "li" -> CategoryGreen
    "pi" -> CategoryPurple
    "ca" -> CategoryOrange
    "ja" -> CategoryGreen
    "al" -> CategoryRed
    "ai" -> CategoryCyan
    else -> CategoryBlue
}

// ─── BOTTOM NAV ─────────────────────────────────────
@Composable
fun BuilderBottomBar(
    onHomeClick: () -> Unit, onMapClick: () -> Unit,
    onChatsClick: () -> Unit, onHistoryClick: () -> Unit,
    onLogout: () -> Unit, selectedIndex: Int = 0,
    isProvider: Boolean = false
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 0.dp, modifier = Modifier.height(72.dp)) {
        data class NavItem(val label: String, val icon: ImageVector, val onClick: () -> Unit)
        val items = listOf(
            NavItem("Inicio", Icons.Rounded.Home, onHomeClick),
            if (isProvider) NavItem("Dashboard", Icons.Rounded.Analytics, onMapClick)
            else NavItem("Mapa", Icons.Rounded.Map, onMapClick),
            NavItem("Mensajes", Icons.Rounded.ChatBubble, onChatsClick),
            NavItem("Historial", Icons.Rounded.History, onHistoryClick),
            NavItem("Salir", Icons.Rounded.Logout, onLogout),
        )
        items.forEachIndexed { i, item ->
            NavigationBarItem(
                selected = selectedIndex == i,
                onClick = item.onClick,
                icon = { Icon(item.icon, item.label, Modifier.size(22.dp)) },
                label = { Text(item.label, style = MaterialTheme.typography.labelSmall, fontWeight = if (selectedIndex == i) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Accent, selectedTextColor = Accent,
                    unselectedIconColor = Neutral400, unselectedTextColor = Neutral400,
                    indicatorColor = AccentSoft
                )
            )
        }
    }
}
