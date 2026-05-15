package com.builder.app.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.Mensaje
import com.builder.app.domain.repository.AuthRepository
import com.builder.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
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

        val mensaje = Mensaje(
            idRemitente = userId,
            texto = text,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            chatRepository.sendMessage(chatId, mensaje)
        }
    }
}
