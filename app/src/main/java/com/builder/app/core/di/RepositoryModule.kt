package com.builder.app.core.di

import com.builder.app.data.repository.AuthRepositoryImpl
import com.builder.app.data.repository.ChatRepositoryImpl
import com.builder.app.data.repository.ProviderRepositoryImpl
import com.builder.app.data.repository.ServiceRepositoryImpl
import com.builder.app.domain.repository.AuthRepository
import com.builder.app.domain.repository.ChatRepository
import com.builder.app.domain.repository.ProviderRepository
import com.builder.app.domain.repository.ServiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProviderRepository(
        providerRepositoryImpl: ProviderRepositoryImpl
    ): ProviderRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindServiceRepository(
        serviceRepositoryImpl: ServiceRepositoryImpl
    ): ServiceRepository
}
