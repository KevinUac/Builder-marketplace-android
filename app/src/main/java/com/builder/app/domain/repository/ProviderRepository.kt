package com.builder.app.domain.repository

import com.builder.app.core.utils.Resource
import com.builder.app.domain.model.Proveedor
import com.builder.app.domain.model.Resena
import kotlinx.coroutines.flow.Flow

interface ProviderRepository {
    fun getProviderProfile(uid: String): Flow<Resource<Proveedor?>>
    fun saveProviderProfile(proveedor: Proveedor): Flow<Resource<Unit>>
    fun getProvidersByCategory(categoria: String): Flow<Resource<List<Proveedor>>>
    fun getAllProviders(): Flow<Resource<List<Proveedor>>>
    fun getProviderReviews(providerId: String): Flow<Resource<List<Resena>>>
    fun addReview(providerId: String, resena: Resena): Flow<Resource<Unit>>
    suspend fun uploadPortfolioImage(providerId: String, imageUri: android.net.Uri): Resource<String>
}
