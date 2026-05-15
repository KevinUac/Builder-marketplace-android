package com.builder.app.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.Chat
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
class ChatListViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _chatsState = MutableStateFlow<UiState<List<Chat>>>(UiState.Idle)
    val chatsState: StateFlow<UiState<List<Chat>>> = _chatsState

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId

    init {
        loadUserChats()
    }

    private fun loadUserChats() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _currentUserId.value = user?.uid
            
            user?.uid?.let { uid ->
                chatRepository.getUserChats(uid).onEach { result ->
                    when (result) {
                        is Resource.Loading -> _chatsState.value = UiState.Loading
                        is Resource.Success -> _chatsState.value = UiState.Success(result.data ?: emptyList())
                        is Resource.Error -> _chatsState.value = UiState.Error(result.message ?: "Error al cargar chats")
                    }
                }.launchIn(viewModelScope)
            } ?: run {
                _chatsState.value = UiState.Error("Usuario no autenticado")
            }
        }
    }
}
