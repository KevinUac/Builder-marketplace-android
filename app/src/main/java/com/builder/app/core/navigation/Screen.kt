package com.builder.app.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Splash : Screen

    @Serializable
    data object RoleSelection : Screen

    @Serializable
    data object Login : Screen

    @Serializable
    data object Register : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data class ProvidersByCategory(val category: String) : Screen

    @Serializable
    data object Search : Screen

    @Serializable
    data object Map : Screen

    @Serializable
    data class ProviderProfile(val providerId: String) : Screen

    @Serializable
    data class Chat(val chatId: String) : Screen

    @Serializable
    data object Review : Screen

    @Serializable
    data object ServiceHistory : Screen

    @Serializable
    data object ProviderDashboard : Screen

    @Serializable
    data object CreateProfile : Screen

    @Serializable
    data object Notifications : Screen

    @Serializable
    data object Settings : Screen

    @Serializable
    data object UserProfile : Screen
}
