package com.builder.app.presentation.listaproveedores

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.builder.app.core.utils.UiState
import com.builder.app.presentation.common.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderListScreen(
    category: String,
    viewModel: ProviderListViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onProviderClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(category) {
        viewModel.loadProviders(category)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(category, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
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
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            val isSorted by viewModel.sortByRating.collectAsState()

            // Sort Filter Chip
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                FilterChip(
                    selected = isSorted,
                    onClick = { viewModel.toggleSort() },
                    label = { Text("Mejor Calificados") },
                    leadingIcon = { Icon(Icons.Rounded.Star, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Neutral50,
                        labelColor = Neutral400,
                        iconColor = Neutral400,
                        selectedContainerColor = Accent.copy(alpha = 0.2f),
                        selectedLabelColor = Accent,
                        selectedLeadingIconColor = Accent
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSorted,
                        borderColor = DarkBorder,
                        selectedBorderColor = Accent
                    )
                )
            }

            Box(Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is UiState.Loading -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(4) { BuilderProviderCardSkeleton() }
                        }
                    }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        BuilderEmptyState(
                            icon = Icons.Rounded.SearchOff,
                            title = "Sin proveedores",
                            subtitle = "No hay proveedores disponibles en $category",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data) { proveedor ->
                                BuilderProviderCard(
                                    proveedor = proveedor,
                                    onClick = { onProviderClick(proveedor.uid) }
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
}
