package com.builder.app.data.repository

import com.builder.app.core.utils.Resource
import com.builder.app.domain.model.Proveedor
import com.builder.app.domain.model.Resena
import com.builder.app.domain.repository.ProviderRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProviderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ProviderRepository {

    override fun getProviderProfile(uid: String): Flow<Resource<Proveedor?>> = flow {
        emit(Resource.Loading())
        try {
            val snapshot = firestore.collection("proveedores").document(uid).get().await()
            if (snapshot.exists()) {
                val proveedor = snapshot.toObject(Proveedor::class.java)
                emit(Resource.Success(proveedor))
            } else {
                emit(Resource.Success(null))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al obtener perfil de proveedor"))
        }
    }

    override fun saveProviderProfile(proveedor: Proveedor): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection("proveedores").document(proveedor.uid).set(proveedor).await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al guardar perfil de proveedor"))
        }
    }

    override fun getProvidersByCategory(categoria: String): Flow<Resource<List<Proveedor>>> = flow {
        emit(Resource.Loading())
        try {
            val query = if (categoria.isEmpty()) {
                firestore.collection("proveedores")
            } else {
                firestore.collection("proveedores").whereEqualTo("categoria", categoria)
            }
            val snapshot = query.get().await()
            val proveedores = snapshot.toObjects(Proveedor::class.java)
            emit(Resource.Success(proveedores))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al buscar proveedores"))
        }
    }

    override fun getProviderReviews(providerId: String): Flow<Resource<List<Resena>>> = flow {
        emit(Resource.Loading())
        try {
            val snapshot = firestore.collection("proveedores")
                .document(providerId)
                .collection("resenas")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            val resenas = snapshot.toObjects(Resena::class.java)
            emit(Resource.Success(resenas))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al obtener reseñas"))
        }
    }

    override fun addReview(providerId: String, resena: Resena): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val batch = firestore.batch()
            
            // 1. Añadir la reseña
            val reviewRef = firestore.collection("proveedores")
                .document(providerId)
                .collection("resenas")
                .document()
            batch.set(reviewRef, resena.copy(id = reviewRef.id, timestamp = System.currentTimeMillis()))

            // 2. Actualizar promedio en el perfil del proveedor
            val providerRef = firestore.collection("proveedores").document(providerId)
            val providerDoc = providerRef.get().await()
            val provider = providerDoc.toObject(Proveedor::class.java)
            
            provider?.let {
                val nuevoTotal = it.totalResenas + 1
                val nuevaCalificacion = ((it.calificacion * it.totalResenas) + resena.calificacion) / nuevoTotal
                batch.update(providerRef, mapOf(
                    "calificacion" to nuevaCalificacion,
                    "totalResenas" to nuevoTotal
                ))
            }

            batch.commit().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al enviar reseña"))
        }
    }

    override suspend fun uploadPortfolioImage(providerId: String, imageUri: android.net.Uri): Resource<String> {
        return try {
            val fileName = "portfolio_${System.currentTimeMillis()}.jpg"
            val imageRef = storage.reference.child("providers/$providerId/portfolio/$fileName")
            imageRef.putFile(imageUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al subir imagen")
        }
    }
}
