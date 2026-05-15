package com.builder.app.presentation.auth.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.RolUsuario
import com.builder.app.domain.model.Usuario
import com.builder.app.domain.usecase.auth.RegisterUseCase
import com.builder.app.domain.usecase.auth.UpdateFcmTokenUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) : ViewModel() {

    private val _registroState = MutableStateFlow<UiState<Usuario>>(UiState.Idle)
    val registroState: StateFlow<UiState<Usuario>> = _registroState

    fun registrar(email: String, pass: String, nombre: String, rol: RolUsuario) {
        registerUseCase(email, pass, nombre, rol).onEach { result ->
            when (result) {
                is Resource.Loading -> _registroState.value = UiState.Loading
                is Resource.Success -> {
                    _registroState.value = UiState.Success(result.data!!)
                    updateFcmToken()
                }
                is Resource.Error -> _registroState.value = UiState.Error(result.message ?: "Error desconocido")
            }
        }.launchIn(viewModelScope)
    }

    private fun updateFcmToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                updateFcmTokenUseCase(token)
            } catch (e: Exception) {
                // Silently fail
            }
        }
    }
}
