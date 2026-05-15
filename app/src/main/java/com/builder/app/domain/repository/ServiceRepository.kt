package com.builder.app.domain.repository

import com.builder.app.core.utils.Resource
import com.builder.app.domain.model.EstadoServicio
import com.builder.app.domain.model.Servicio
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun createService(servicio: Servicio): Flow<Resource<Unit>>
    fun getServicesForUser(userId: String, isProvider: Boolean): Flow<Resource<List<Servicio>>>
    fun updateServiceStatus(serviceId: String, newStatus: EstadoServicio): Flow<Resource<Unit>>
}
