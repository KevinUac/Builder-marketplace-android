package com.builder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.builder.app.core.navigation.Screen
import com.builder.app.core.ui.theme.BuilderTheme
import com.builder.app.presentation.auth.login.LoginScreen
import com.builder.app.presentation.auth.registro.RegistroScreen
import com.builder.app.presentation.auth.seleccionrol.SeleccionRolScreen
import com.builder.app.presentation.auth.splash.SplashScreen
import com.builder.app.presentation.chat.ChatListScreen
import com.builder.app.presentation.chat.ChatScreen
import com.builder.app.presentation.crearperfil.CreateProfileScreen
import com.builder.app.presentation.historial.HistorialScreen
import com.builder.app.presentation.inicio.HomeScreen
import com.builder.app.presentation.listaproveedores.ProviderListScreen
import com.builder.app.presentation.mapa.MapScreen
import com.builder.app.presentation.perfilproveedor.ProviderProfileScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuilderTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Splash
                ) {
                    composable<Screen.Splash> {
                        SplashScreen(
                            onNavigateToLogin = {
                                navController.navigate(Screen.RoleSelection) {
                                    popUpTo(Screen.Splash) { inclusive = true }
                                }
                            },
                            onNavigateToHome = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Splash) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable<Screen.RoleSelection> {
                        SeleccionRolScreen(
                            onRolSelected = { rol ->
                                navController.navigate(Screen.Login)
                            }
                        )
                    }
                    composable<Screen.Login> {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Login) { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register)
                            }
                        )
                    }
                    composable<Screen.Register> {
                        RegistroScreen(
                            onRegistroSuccess = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.Register) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login)
                            }
                        )
                    }
                    composable<Screen.Home> {
                        HomeScreen(
                            onLogout = {
                                navController.navigate(Screen.Login) {
                                    popUpTo(Screen.Home) { inclusive = true }
                                }
                            },
                            onNavigateToCreateProfile = {
                                navController.navigate(Screen.CreateProfile)
                            },
                            onNavigateToCategory = { categoryName ->
                                navController.navigate(Screen.ProvidersByCategory(categoryName))
                            },
                            onNavigateToMap = {
                                navController.navigate(Screen.Map)
                            },
                            onNavigateToHistory = {
                                navController.navigate(Screen.ServiceHistory)
                            },
                            onNavigateToChats = {
                                navController.navigate(Screen.Notifications)
                            }
                        )
                    }
                    composable<Screen.ServiceHistory> {
                        HistorialScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<Screen.Map> {
                        MapScreen(
                            onBack = { navController.popBackStack() },
                            onProviderClick = { providerId ->
                                navController.navigate(Screen.ProviderProfile(providerId))
                            }
                        )
                    }
                    composable<Screen.ProvidersByCategory> { backStackEntry ->
                        val route: Screen.ProvidersByCategory = backStackEntry.toRoute()
                        ProviderListScreen(
                            category = route.category,
                            onBack = { navController.popBackStack() },
                            onProviderClick = { providerId ->
                                navController.navigate(Screen.ProviderProfile(providerId))
                            }
                        )
                    }
                    composable<Screen.ProviderProfile> { backStackEntry ->
                        val route: Screen.ProviderProfile = backStackEntry.toRoute()
                        ProviderProfileScreen(
                            providerId = route.providerId,
                            onBack = { navController.popBackStack() },
                            onNavigateToChat = { chatId ->
                                navController.navigate(Screen.Chat(chatId))
                            }
                        )
                    }
                    composable<Screen.Chat> { backStackEntry ->
                        val route: Screen.Chat = backStackEntry.toRoute()
                        ChatScreen(
                            chatId = route.chatId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable<Screen.Notifications> { // Usaremos Notifications como ruta para el listado de chats por ahora o añadiremos una nueva
                        ChatListScreen(
                            onBack = { navController.popBackStack() },
                            onChatClick = { chatId ->
                                navController.navigate(Screen.Chat(chatId))
                            }
                        )
                    }
                    composable<Screen.CreateProfile> {
                        CreateProfileScreen(
                            onProfileCreated = {
                                navController.navigate(Screen.Home) {
                                    popUpTo(Screen.CreateProfile) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
