package com.builder.app.domain.usecase.auth

import com.builder.app.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String) = repository.updateFcmToken(token)
}
