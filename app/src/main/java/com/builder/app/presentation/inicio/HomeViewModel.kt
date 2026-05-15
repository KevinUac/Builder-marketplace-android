package com.builder.app.presentation.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.domain.model.RolUsuario
import com.builder.app.domain.model.Usuario
import com.builder.app.domain.repository.AuthRepository
import com.builder.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val providerRepository: ProviderRepository
) : ViewModel() {

    val userSession: StateFlow<Usuario?> = authRepository.getSession()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _needsProfile = MutableStateFlow(false)
    val needsProfile = _needsProfile.asStateFlow()

    init {
        checkProviderProfile()
    }

    private fun checkProviderProfile() {
        viewModelScope.launch {
            val user = authRepository.getSession().first()
            if (user?.rol == RolUsuario.PROVEEDOR) {
                providerRepository.getProviderProfile(user.uid).collect { resource ->
                    if (resource is com.builder.app.core.utils.Resource.Success && resource.data == null) {
                        _needsProfile.value = true
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
