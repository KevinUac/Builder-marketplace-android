package com.builder.app.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.builder.app.core.utils.Resource
import com.builder.app.domain.model.Usuario
import com.builder.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val userSession: StateFlow<Usuario?> = authRepository.getSession()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _photoUpdated = MutableStateFlow(false)
    val photoUpdated = _photoUpdated.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun updatePhoto(uri: android.net.Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.updateProfilePhoto(uri)
            if (result is Resource.Success) {
                // Save photo URL locally for persistence
                (authRepository as? com.builder.app.data.repository.AuthRepositoryImpl)
                    ?.savePhotoLocally(result.data ?: "")
            }
            _isLoading.value = false
            _photoUpdated.value = true
        }
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val user = authRepository.getCurrentUser()
            if (user != null) {
                // Update Firestore
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("usuarios").document(user.uid)
                    .update("nombre", newName)
            }
            _isLoading.value = false
        }
    }

    fun updatePassword(newPassword: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                firebaseUser?.updatePassword(newPassword)
                    ?.addOnSuccessListener { onResult(true, "Contraseña actualizada") }
                    ?.addOnFailureListener { onResult(false, it.message ?: "Error") }
                    ?: onResult(false, "No hay sesión activa")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Error desconocido")
            }
        }
    }
}
