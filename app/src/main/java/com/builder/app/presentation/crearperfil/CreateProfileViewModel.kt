package com.builder.app.presentation.crearperfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.location.LocationTracker
import com.builder.app.domain.model.Proveedor
import com.builder.app.domain.repository.AuthRepository
import com.builder.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val authRepository: AuthRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val uiState: StateFlow<UiState<Unit>> = _uiState

    fun saveProfile(
        categoria: String,
        tarifa: Double,
        habilidades: String,
        imageUris: List<android.net.Uri>
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val user = authRepository.getSession().first()
            if (user == null) {
                _uiState.value = UiState.Error("Usuario no encontrado")
                return@launch
            }

            // 1. Obtener ubicación real del proveedor
            val location = locationTracker.getCurrentLocation()
            val lat = location?.latitude ?: 0.0
            val lng = location?.longitude ?: 0.0

            // 2. Subir imágenes
            val uploadedUrls = mutableListOf<String>()
            for (uri in imageUris) {
                val uploadResult = providerRepository.uploadPortfolioImage(user.uid, uri)
                if (uploadResult is Resource.Success) {
                    uploadResult.data?.let { uploadedUrls.add(it) }
                }
            }

            // 3. Guardar perfil con ubicación real
            val habilidadesList = habilidades.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            
            val proveedor = Proveedor(
                uid = user.uid,
                nombre = user.nombre,
                categoria = categoria,
                calificacion = 0f,
                totalResenas = 0,
                tarifaHora = tarifa,
                habilidades = habilidadesList,
                portafolioUrls = uploadedUrls,
                verificado = false,
                latitud = lat,
                longitud = lng
            )

            providerRepository.saveProviderProfile(proveedor).collect { result ->
                when (result) {
                    is Resource.Success -> _uiState.value = UiState.Success(Unit)
                    is Resource.Error -> _uiState.value = UiState.Error(result.message ?: "Error al guardar perfil")
                    else -> {}
                }
            }
        }
    }
}
