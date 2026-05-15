package com.builder.app.presentation.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.EstadoServicio
import com.builder.app.domain.model.RolUsuario
import com.builder.app.domain.model.Servicio
import com.builder.app.domain.repository.AuthRepository
import com.builder.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val serviceRepository: ServiceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _servicesResource = MutableStateFlow<Resource<List<Servicio>>>(Resource.Loading())
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    // Expose whether the current user is a provider
    private val _isProvider = MutableStateFlow(false)
    val isProvider: StateFlow<Boolean> = _isProvider

    val uiState: StateFlow<UiState<List<Servicio>>> = combine(
        _servicesResource,
        _selectedTab
    ) { resource, tabIndex ->
        when (resource) {
            is Resource.Loading -> UiState.Loading
            is Resource.Error -> UiState.Error(resource.message ?: "Error al cargar servicios")
            is Resource.Success -> {
                val allServices = resource.data ?: emptyList()
                val filtered = when (tabIndex) {
                    1 -> allServices.filter { it.estado == EstadoServicio.PENDIENTE }
                    2 -> allServices.filter { it.estado == EstadoServicio.EN_PROGRESO }
                    3 -> allServices.filter { it.estado == EstadoServicio.COMPLETADO }
                    4 -> allServices.filter { it.estado == EstadoServicio.CANCELADO }
                    else -> allServices
                }
                UiState.Success(filtered)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Idle)

    init {
        loadServices()
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun loadServices() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser() ?: return@launch
            val isProvider = user.rol == RolUsuario.PROVEEDOR
            _isProvider.value = isProvider
            
            serviceRepository.getServicesForUser(user.uid, isProvider).collect { result ->
                _servicesResource.value = result
            }
        }
    }

    fun updateServiceStatus(serviceId: String, newStatus: EstadoServicio) {
        serviceRepository.updateServiceStatus(serviceId, newStatus).onEach { result ->
            if (result is Resource.Success) {
                loadServices()
            }
        }.launchIn(viewModelScope)
    }
}
