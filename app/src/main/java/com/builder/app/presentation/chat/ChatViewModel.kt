package com.builder.app.presentation.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.Mensaje
import com.builder.app.domain.repository.AuthRepository
import com.builder.app.domain.repository.ChatRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _messagesState = MutableStateFlow<UiState<List<Mensaje>>>(UiState.Idle)
    val messagesState: StateFlow<UiState<List<Mensaje>>> = _messagesState

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    init {
        viewModelScope.launch {
            _currentUserId.value = authRepository.getCurrentUser()?.uid
        }
    }

    fun loadMessages(chatId: String) {
        chatRepository.getMessages(chatId).onEach { result ->
            when (result) {
                is Resource.Loading -> _messagesState.value = UiState.Loading
                is Resource.Success -> _messagesState.value = UiState.Success(result.data ?: emptyList())
                is Resource.Error -> _messagesState.value = UiState.Error(result.message ?: "Error al cargar mensajes")
            }
        }.launchIn(viewModelScope)
    }

    fun sendMessage(chatId: String, text: String) {
        val userId = _currentUserId.value ?: return
        if (text.isBlank()) return
        val mensaje = Mensaje(idRemitente = userId, texto = text, tipo = "text", timestamp = System.currentTimeMillis())
        viewModelScope.launch { chatRepository.sendMessage(chatId, mensaje) }
    }

    fun sendImage(chatId: String, imageUri: Uri) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            try {
                val fileName = "chat_${chatId}_${System.currentTimeMillis()}.jpg"
                val ref = storage.reference.child("chats/$chatId/$fileName")
                ref.putFile(imageUri).await()
                val url = ref.downloadUrl.await().toString()
                val mensaje = Mensaje(idRemitente = userId, texto = "📷 Foto", tipo = "image", imageUrl = url, timestamp = System.currentTimeMillis())
                chatRepository.sendMessage(chatId, mensaje)
            } catch (_: Exception) { }
        }
    }

    fun sendLocation(chatId: String, lat: Double, lng: Double) {
        val userId = _currentUserId.value ?: return
        val mensaje = Mensaje(idRemitente = userId, texto = "📍 Ubicación", tipo = "location", latitude = lat, longitude = lng, timestamp = System.currentTimeMillis())
        viewModelScope.launch { chatRepository.sendMessage(chatId, mensaje) }
    }
}

