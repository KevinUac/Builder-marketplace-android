package com.builder.app.data.repository

import com.builder.app.core.utils.Resource
import com.builder.app.data.local.preferences.UserPreferences
import com.builder.app.domain.model.RolUsuario
import com.builder.app.domain.model.Usuario
import com.builder.app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: com.google.firebase.storage.FirebaseStorage,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override fun getSession(): Flow<Usuario?> {
        return combine(
            userPreferences.userUid,
            userPreferences.userRole,
            userPreferences.userName,
            userPreferences.userPhotoUrl
        ) { uid, role, name, photoUrl ->
            if (uid != null && role != null) {
                Usuario(uid, name ?: "", auth.currentUser?.email ?: "", role, fotoUrl = photoUrl ?: "")
            } else {
                null
            }
        }
    }

    override fun login(email: String, pass: String): Flow<Resource<Usuario>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("ID de usuario no encontrado")
            
            val doc = firestore.collection("usuarios").document(uid).get().await()
            val rolString = doc.getString("rol") ?: "CLIENTE"
            val rol = RolUsuario.valueOf(rolString)
            val nombre = doc.getString("nombre") ?: ""
            
            val usuario = Usuario(
                uid = uid,
                nombre = nombre,
                correo = doc.getString("correo") ?: "",
                rol = rol,
                fotoUrl = doc.getString("fotoUrl") ?: ""
            )
            val fotoUrl = doc.getString("fotoUrl") ?: ""
            userPreferences.saveUserSession(uid, rol, nombre, fotoUrl)
            emit(Resource.Success(usuario))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al iniciar sesión"))
        }
    }

    override fun signUp(
        email: String,
        pass: String,
        nombre: String,
        rol: RolUsuario,
        telefono: String,
        fechaNacimiento: String,
        anosExperiencia: Int
    ): Flow<Resource<Usuario>> = flow {
        emit(Resource.Loading())
        try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val uid = result.user?.uid ?: throw Exception("Error al crear usuario")
            
            val usuario = Usuario(uid, nombre, email, rol)
            val userData = hashMapOf<String, Any>(
                "uid" to uid,
                "nombre" to nombre,
                "correo" to email,
                "rol" to rol.name,
                "fotoUrl" to ""
            )
            if (rol == RolUsuario.PROVEEDOR) {
                userData["telefono"] = telefono
                userData["fechaNacimiento"] = fechaNacimiento
                userData["anosExperiencia"] = anosExperiencia
            }
            firestore.collection("usuarios").document(uid).set(userData).await()
            
            userPreferences.saveUserSession(uid, rol, nombre)
            emit(Resource.Success(usuario))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Error al registrarse"))
        }
    }

    override suspend fun logout() {
        auth.signOut()
        userPreferences.clear()
    }

    override suspend fun getCurrentUser(): Usuario? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = firestore.collection("usuarios").document(uid).get().await()
            val rolString = doc.getString("rol") ?: "CLIENTE"
            val rol = RolUsuario.valueOf(rolString)
            Usuario(
                uid = uid,
                nombre = doc.getString("nombre") ?: "",
                correo = doc.getString("correo") ?: "",
                rol = rol,
                fotoUrl = doc.getString("fotoUrl") ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateFcmToken(token: String): Resource<Unit> {
        val uid = auth.currentUser?.uid ?: return Resource.Error("Usuario no autenticado")
        return try {
            firestore.collection("usuarios").document(uid)
                .update("fcmToken", token).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al actualizar token")
        }
    }

    override suspend fun updateProfilePhoto(photoUri: android.net.Uri): Resource<String> {
        val uid = auth.currentUser?.uid ?: return Resource.Error("Usuario no autenticado")
        return try {
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val imageRef = storage.reference.child("users/$uid/profile/$fileName")
            imageRef.putFile(photoUri).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()
            
            val userDoc = firestore.collection("usuarios").document(uid).get().await()
            val rolString = userDoc.getString("rol") ?: "CLIENTE"
            
            val batch = firestore.batch()
            batch.update(firestore.collection("usuarios").document(uid), "fotoUrl", downloadUrl)
            
            if (rolString == "PROVEEDOR") {
                batch.update(firestore.collection("proveedores").document(uid), "fotoUrl", downloadUrl)
            }
            
            batch.commit().await()
            
            Resource.Success(downloadUrl)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al subir foto")
        }
    }

    suspend fun savePhotoLocally(url: String) {
        userPreferences.savePhotoUrl(url)
    }
}
