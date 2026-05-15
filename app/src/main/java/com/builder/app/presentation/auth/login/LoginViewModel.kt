package com.builder.app.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.Usuario
import com.builder.app.domain.usecase.auth.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<UiState<Usuario>>(UiState.Idle)
    val loginState: StateFlow<UiState<Usuario>> = _loginState

    fun login(email: String, pass: String) {
        loginUseCase(email, pass).onEach { result ->
            when (result) {
                is Resource.Loading -> _loginState.value = UiState.Loading
                is Resource.Success -> {
                    _loginState.value = UiState.Success(result.data!!)
                    updateFcmToken()
                }
                is Resource.Error -> _loginState.value = UiState.Error(result.message ?: "Error desconocido")
            }
        }.launchIn(viewModelScope)
    }

    private fun updateFcmToken() {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                updateFcmTokenUseCase(token)
            } catch (e: Exception) {
                // Silently fail or log
            }
        }
    }
}
