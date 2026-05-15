package com.builder.app.domain.usecase.auth

import com.builder.app.domain.model.RolUsuario
import com.builder.app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, pass: String, nombre: String, rol: RolUsuario) = 
        repository.signUp(email, pass, nombre, rol)
}
