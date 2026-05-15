package com.builder.app.presentation.perfilproveedor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.EstadoServicio
import com.builder.app.domain.model.Proveedor
import com.builder.app.domain.model.Resena
import com.builder.app.domain.model.Servicio
import com.builder.app.domain.repository.AuthRepository
import com.builder.app.domain.repository.ChatRepository
import com.builder.app.domain.repository.ProviderRepository
import com.builder.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProviderProfileViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Proveedor>>(UiState.Idle)
    val uiState: StateFlow<UiState<Proveedor>> = _uiState

    private val _reviewsState = MutableStateFlow<UiState<List<Resena>>>(UiState.Idle)
    val reviewsState: StateFlow<UiState<List<Resena>>> = _reviewsState

    private val _navigateToChat = MutableSharedFlow<String>()
    val navigateToChat: SharedFlow<String> = _navigateToChat.asSharedFlow()

    fun loadProviderProfile(providerId: String) {
        providerRepository.getProviderProfile(providerId).onEach { result ->
            when (result) {
                is Resource.Loading -> _uiState.value = UiState.Loading
                is Resource.Success -> {
                    val proveedor = result.data
                    if (proveedor != null) {
                        _uiState.value = UiState.Success(proveedor)
                        loadReviews(providerId)
                    } else {
                        _uiState.value = UiState.Error("Proveedor no encontrado")
                    }
                }
                is Resource.Error -> _uiState.value = UiState.Error(result.message ?: "Error desconocido")
            }
        }.launchIn(viewModelScope)
    }

    private fun loadReviews(providerId: String) {
        providerRepository.getProviderReviews(providerId).onEach { result ->
            when (result) {
                is Resource.Loading -> _reviewsState.value = UiState.Loading
                is Resource.Success -> _reviewsState.value = UiState.Success(result.data ?: emptyList())
                is Resource.Error -> _reviewsState.value = UiState.Error(result.message ?: "Error al cargar reseñas")
            }
        }.launchIn(viewModelScope)
    }

    fun addReview(providerId: String, calificacion: Float, comentario: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser() ?: return@launch
            val resena = Resena(
                nombreCliente = currentUser.nombre,
                calificacion = calificacion,
                comentario = comentario
            )
            providerRepository.addReview(providerId, resena).collect { result ->
                if (result is Resource.Success) {
                    loadReviews(providerId)
                    // También recargar el perfil para ver la calificación actualizada
                    loadProviderProfile(providerId)
                }
            }
        }
    }

    fun contactProvider(provider: Proveedor) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser() ?: return@launch
            val result = chatRepository.getOrCreateChat(
                user1Id = currentUser.uid,
                user2Id = provider.uid,
                user1Name = currentUser.nombre,
                user2Name = provider.nombre
            )
            if (result is Resource.Success) {
                result.data?.let { chatId ->
                    _navigateToChat.emit(chatId)
                }
            }
        }
    }

    fun requestService(provider: Proveedor, descripcion: String) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser() ?: return@launch
            val servicio = Servicio(
                idCliente = currentUser.uid,
                idProveedor = provider.uid,
                nombreProveedor = provider.nombre,
                nombreCliente = currentUser.nombre,
                categoria = provider.categoria,
                estado = EstadoServicio.PENDIENTE,
                descripcion = descripcion,
                precioEstimado = provider.tarifaHora // Podría ser una estimación base
            )
            serviceRepository.createService(servicio).collect { /* handle result */ }
        }
    }
}
