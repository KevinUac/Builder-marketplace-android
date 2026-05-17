package com.builder.app.domain.repository

import com.builder.app.core.utils.Resource
import com.builder.app.domain.model.RolUsuario
import com.builder.app.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getSession(): Flow<Usuario?>
    fun login(email: String, pass: String): Flow<Resource<Usuario>>
    fun signUp(email: String, pass: String, nombre: String, rol: RolUsuario,
               telefono: String = "", fechaNacimiento: String = "", anosExperiencia: Int = 0): Flow<Resource<Usuario>>
    suspend fun logout()
    suspend fun getCurrentUser(): Usuario?
    suspend fun updateFcmToken(token: String): Resource<Unit>
    suspend fun updateProfilePhoto(photoUri: android.net.Uri): Resource<String>
}
