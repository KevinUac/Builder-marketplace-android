package com.builder.app.domain.usecase.auth

import com.builder.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String, pass: String) = repository.login(email, pass)
}
