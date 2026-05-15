package com.builder.app.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════════
// BUILDER — Shape Tokens
// 8dp chips, 12dp inputs, 16dp cards, 999dp pills
// ═══════════════════════════════════════════════════════

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),   // Chips, small badges
    small = RoundedCornerShape(8.dp),         // Status pills
    medium = RoundedCornerShape(12.dp),       // Inputs, smaller cards
    large = RoundedCornerShape(16.dp),        // Cards, dialogs
    extraLarge = RoundedCornerShape(999.dp)   // Pill buttons, FABs
)
