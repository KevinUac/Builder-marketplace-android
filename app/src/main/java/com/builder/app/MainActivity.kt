package com.builder.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.builder.app.core.navigation.Screen
import com.builder.app.core.ui.theme.BuilderTheme
import com.builder.app.core.ui.theme.Accent
import com.builder.app.core.ui.theme.Error
import com.builder.app.core.ui.theme.TextPrimary
import com.builder.app.core.ui.theme.TextSecondary
import com.builder.app.data.local.preferences.UserPreferences
import com.builder.app.domain.model.RolUsuario
import com.builder.app.domain.repository.AuthRepository
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var userPreferences: UserPreferences
    @Inject lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuilderTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: ""
                
                val userRole by userPreferences.userRole.collectAsState(initial = null)
                val isProvider = userRole == RolUsuario.PROVEEDOR
                val scope = rememberCoroutineScope()
                var showLogoutDialog by remember { mutableStateOf(false) }

                val showBottomBar = currentRoute.contains("Home") ||
                    currentRoute.contains("Map") ||
                    currentRoute.contains("ProviderDashboard") ||
                    currentRoute.contains("Notifications") ||
                    currentRoute.contains("ServiceHistory")

                val selectedIndex = when {
                    currentRoute.contains("Home") -> 0
                    currentRoute.contains("Map") || currentRoute.contains("ProviderDashboard") -> 1
                    currentRoute.contains("Notifications") -> 2
                    currentRoute.contains("ServiceHistory") -> 3
                    else -> 0
                }

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            com.builder.app.presentation.common.BuilderBottomBar(
                                onHomeClick = { navController.navigate(Screen.Home) { popUpTo(Screen.Home) { inclusive = true } } },
                                onMapClick = { if (isProvider) navController.navigate(Screen.ProviderDashboard) { launchSingleTop = true } else navController.navigate(Screen.Map) { launchSingleTop = true } },
                                onChatsClick = { navController.navigate(Screen.Notifications) { launchSingleTop = true } },
                                onHistoryClick = { navController.navigate(Screen.ServiceHistory) { launchSingleTop = true } },
                                onLogout = { showLogoutDialog = true },
                                selectedIndex = selectedIndex,
                                isProvider = isProvider
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
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
                                    onLogout = { showLogoutDialog = true },
                                    onNavigateToCreateProfile = {
                                        navController.navigate(Screen.CreateProfile) { launchSingleTop = true }
                                    },
                                    onNavigateToCategory = { categoryName ->
                                        navController.navigate(Screen.ProvidersByCategory(categoryName)) { launchSingleTop = true }
                                    },
                                    onNavigateToMap = {
                                        navController.navigate(Screen.Map) { launchSingleTop = true }
                                    },
                                    onNavigateToDashboard = {
                                        navController.navigate(Screen.ProviderDashboard) { launchSingleTop = true }
                                    },
                                    onNavigateToHistory = {
                                        navController.navigate(Screen.ServiceHistory) { launchSingleTop = true }
                                    },
                                    onNavigateToChats = {
                                        navController.navigate(Screen.Notifications) { launchSingleTop = true }
                                    },
                                    onNavigateToProfile = {
                                        navController.navigate(Screen.UserProfile) { launchSingleTop = true }
                                    },
                                    onNavigateToProvider = { providerId ->
                                        navController.navigate(Screen.ProviderProfile(providerId)) { launchSingleTop = true }
                                    }
                                )
                            }
                            composable<Screen.ServiceHistory> {
                                HistorialScreen(
                                    onBack = { navController.popBackStack() }
                                )
                            }
                            composable<Screen.ProviderDashboard> {
                                com.builder.app.presentation.dashboard.ProviderDashboardScreen(
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
                            composable<Screen.Notifications> {
                                ChatListScreen(
                                    onBack = { navController.popBackStack() },
                                    onChatClick = { chatId ->
                                        navController.navigate(Screen.Chat(chatId)) { launchSingleTop = true }
                                    }
                                )
                            }
                            composable<Screen.UserProfile> {
                                com.builder.app.presentation.perfil.UserProfileScreen(
                                    onBack = { navController.popBackStack() }
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

                    if (showLogoutDialog) {
                        AlertDialog(
                            onDismissRequest = { showLogoutDialog = false },
                            containerColor = Color.White,
                            shape = RoundedCornerShape(20.dp),
                            title = { Text("Cerrar sesión", color = TextPrimary, fontWeight = FontWeight.Bold) },
                            text = { Text("¿Estás seguro de que deseas salir de tu cuenta?", color = TextSecondary) },
                            confirmButton = {
                                TextButton(onClick = { 
                                    showLogoutDialog = false 
                                    scope.launch { authRepository.logout() }
                                    navController.navigate(Screen.Login) { 
                                        popUpTo(0) { inclusive = true } 
                                    }
                                }) {
                                    Text("Salir", color = Error, fontWeight = FontWeight.Bold)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showLogoutDialog = false }) {
                                    Text("Cancelar", color = Accent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
