package com.muhouling.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.muhouling.app.MuhoulingApp
import com.muhouling.app.data.api.RetrofitClient
import com.muhouling.app.data.repository.AuthRepository
import com.muhouling.app.data.repository.ContactRepository
import com.muhouling.app.data.repository.SleepRepository
import com.muhouling.app.ui.contacts.ContactsScreen
import com.muhouling.app.ui.history.HistoryScreen
import com.muhouling.app.ui.home.HomeScreen
import com.muhouling.app.ui.login.LoginScreen
import com.muhouling.app.ui.settings.SettingsScreen
import com.muhouling.app.ui.sleep.SleepModeScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object SleepMode : Screen("sleep_mode")
    object Contacts : Screen("contacts")
    object History : Screen("history")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val app = context.applicationContext as MuhoulingApp
    val prefsStore = app.prefsStore

    val authRepository = remember { AuthRepository(RetrofitClient.api, prefsStore) }
    val sleepRepository = remember { SleepRepository(RetrofitClient.api, prefsStore) }
    val contactRepository = remember { ContactRepository(RetrofitClient.api, prefsStore) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                sleepRepository = sleepRepository,
                onCheckIn = {
                    navController.navigate(Screen.SleepMode.route)
                },
                onNavigateToContacts = {
                    navController.navigate(Screen.Contacts.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.SleepMode.route) {
            SleepModeScreen(
                sleepRepository = sleepRepository,
                onExit = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Contacts.route) {
            ContactsScreen(
                contactRepository = contactRepository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                sleepRepository = sleepRepository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                sleepRepository = sleepRepository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
