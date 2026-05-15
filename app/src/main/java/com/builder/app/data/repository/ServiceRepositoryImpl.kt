package com.builder.app.data.repository

import com.builder.app.core.utils.Resource
import com.builder.app.domain.model.EstadoServicio
import com.builder.app.domain.model.Servicio
import com.builder.app.domain.repository.ServiceRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ServiceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ServiceRepository {

    override fun createService(servicio: Servicio): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val docRef = firestore.collection("servicios").document()
            firestore.collection("servicios").document(docRef.id)
                .set(servicio.copy(id = docRef.id)).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al crear servicio"))
        }
    }

    override fun getServicesForUser(userId: String, isProvider: Boolean): Flow<Resource<List<Servicio>>> = flow {
        emit(Resource.Loading())
        try {
            val field = if (isProvider) "idProveedor" else "idCliente"
            val snapshot = firestore.collection("servicios")
                .whereEqualTo(field, userId)
                .get().await()
            val servicios = snapshot.toObjects(Servicio::class.java)
                .sortedByDescending { it.fecha }
            emit(Resource.Success(servicios))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al obtener servicios"))
        }
    }

    override fun updateServiceStatus(serviceId: String, newStatus: EstadoServicio): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection("servicios").document(serviceId)
                .update("estado", newStatus).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al actualizar estado"))
        }
    }
}
