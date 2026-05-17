package com.builder.app.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.domain.repository.AuthRepository
import com.builder.app.domain.repository.ServiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardStats(
    val totalGanancias: Double = 0.0,
    val trabajosCompletados: Int = 0,
    val serviciosPendientes: Int = 0
)

@HiltViewModel
class ProviderDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(DashboardStats())
    val stats: StateFlow<DashboardStats> = _stats.asStateFlow()

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val user = authRepository.getSession().first()
            if (user != null) {
                serviceRepository.getServicesForUser(user.uid, isProvider = true).collect { resource ->
                    if (resource is Resource.Success) {
                        val services = resource.data ?: emptyList()
                        
                        val completados = services.filter { it.estado == com.builder.app.domain.model.EstadoServicio.COMPLETADO }
                        val pendientes = services.count { it.estado == com.builder.app.domain.model.EstadoServicio.PENDIENTE }
                        
                        val ganancias = completados.sumOf { it.precioEstimado }

                        _stats.value = DashboardStats(
                            totalGanancias = ganancias,
                            trabajosCompletados = completados.size,
                            serviciosPendientes = pendientes
                        )
                    }
                }
            }
        }
    }
}
