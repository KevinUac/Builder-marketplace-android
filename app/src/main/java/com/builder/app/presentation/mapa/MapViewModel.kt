package com.builder.app.presentation.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.domain.location.LocationTracker
import com.builder.app.domain.model.Proveedor
import com.builder.app.domain.repository.ProviderRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val locationTracker: LocationTracker
) : ViewModel() {

    private val _providers = MutableStateFlow<List<Proveedor>>(emptyList())
    val providers: StateFlow<List<Proveedor>> = _providers

    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation = _userLocation.asStateFlow()

    init {
        loadAllProviders()
        refreshUserLocation()
    }

    fun refreshUserLocation() {
        viewModelScope.launch {
            locationTracker.getCurrentLocation()?.let { location ->
                _userLocation.value = LatLng(location.latitude, location.longitude)
            }
        }
    }

    private fun loadAllProviders() {
        providerRepository.getProvidersByCategory("").onEach { result ->
            if (result is Resource.Success) {
                _providers.value = result.data ?: emptyList()
            }
        }.launchIn(viewModelScope)
    }
}
