package com.builder.app.presentation.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.builder.app.core.ui.theme.*
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.Chat
import com.builder.app.domain.model.Mensaje
import com.builder.app.presentation.common.BuilderAvatar
import com.builder.app.presentation.common.BuilderEmptyState
import java.text.SimpleDateFormat
import java.util.*

// ═══════════════════════════════════════════════════════
// Chat Screen
// ═══════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    viewModel: ChatViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val messagesState by viewModel.messagesState.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        viewModel.loadMessages(chatId)
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Surface(
                color = DarkSurface,
                border = BorderStroke(0.dp, Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = Neutral50
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    BuilderAvatar(
                        name = "Chat",
                        size = 40.dp,
                        showOnline = true
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Chat",
                            style = MaterialTheme.typography.titleMedium,
                            color = Neutral50,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "En línea",
                            style = MaterialTheme.typography.labelSmall,
                            color = OnlineGreen
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Attach button
                    IconButton(
                        onClick = {},
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Rounded.AttachFile,
                            contentDescription = "Adjuntar",
                            tint = Neutral500,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Message input
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = DarkSurfaceElevated
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = {
                                Text(
                                    "Escribe un mensaje...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Neutral600
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Accent,
                                focusedTextColor = Neutral50,
                                unfocusedTextColor = Neutral50
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            singleLine = false,
                            maxLines = 4
                        )
                    }

                    // Send button
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = if (messageText.isNotBlank()) Accent else DarkSurfaceHigh,
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(chatId, messageText)
                                messageText = ""
                            }
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Rounded.Send,
                                contentDescription = "Enviar",
                                tint = if (messageText.isNotBlank()) Color.White else Neutral600,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = messagesState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        color = Accent,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        BuilderEmptyState(
                            icon = Icons.Rounded.ChatBubbleOutline,
                            title = "Sin mensajes",
                            subtitle = "Envía el primer mensaje para iniciar la conversación",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        MessageList(
                            messages = state.data,
                            currentUserId = currentUserId ?: ""
                        )
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
fun MessageList(messages: List<Mensaje>, currentUserId: String) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(messages) { mensaje ->
            MessageBubble(
                mensaje = mensaje,
                isMe = mensaje.idRemitente == currentUserId
            )
        }
    }
}

@Composable
fun MessageBubble(mensaje: Mensaje, isMe: Boolean) {
    val alignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = if (isMe) Primary else DarkSurfaceElevated,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = mensaje.texto,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isMe) Neutral50 else Neutral300
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(mensaje.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral600,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════
// Chat List Screen
// ═══════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val chatsState by viewModel.chatsState.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mis Mensajes",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = Neutral50
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = Neutral50
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = chatsState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(
                        color = Accent,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        BuilderEmptyState(
                            icon = Icons.Rounded.Forum,
                            title = "Sin conversaciones",
                            subtitle = "No tienes conversaciones activas",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.data) { chat ->
                                ChatItem(
                                    chat = chat,
                                    currentUserId = currentUserId ?: "",
                                    onClick = { onChatClick(chat.id) }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 24.dp),
                                    thickness = 0.5.dp,
                                    color = DarkBorder
                                )
                            }
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
fun ChatItem(chat: Chat, currentUserId: String, onClick: () -> Unit) {
    val otherParticipantName = chat.nombresParticipantes.entries
        .find { it.key != currentUserId }?.value ?: "Usuario"

    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val timeText = sdf.format(Date(chat.timestamp))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BuilderAvatar(
                name = otherParticipantName,
                size = 48.dp,
                showOnline = true
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = otherParticipantName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Neutral50
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = chat.ultimoMensaje,
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = timeText,
                style = MaterialTheme.typography.labelSmall,
                color = Neutral600
            )
        }
    }
}
