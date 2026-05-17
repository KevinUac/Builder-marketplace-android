package com.builder.app.presentation.listaproveedores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.core.utils.UiState
import com.builder.app.domain.model.Proveedor
import com.builder.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProviderListViewModel @Inject constructor(
    private val providerRepository: ProviderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Proveedor>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<Proveedor>>> = _uiState

    private val _sortByRating = MutableStateFlow(false)
    val sortByRating: StateFlow<Boolean> = _sortByRating

    private var currentProviders: List<Proveedor> = emptyList()

    fun loadProviders(category: String) {
        providerRepository.getProvidersByCategory(category).onEach { result ->
            when (result) {
                is Resource.Loading -> _uiState.value = UiState.Loading
                is Resource.Success -> {
                    currentProviders = result.data ?: emptyList()
                    applySorting()
                }
                is Resource.Error -> _uiState.value = UiState.Error(result.message ?: "Error desconocido")
            }
        }.launchIn(viewModelScope)
    }

    fun toggleSort() {
        _sortByRating.value = !_sortByRating.value
        applySorting()
    }

    private fun applySorting() {
        val sortedList = if (_sortByRating.value) {
            currentProviders.sortedByDescending { it.calificacion }
        } else {
            currentProviders
        }
        _uiState.value = UiState.Success(sortedList)
    }
}
