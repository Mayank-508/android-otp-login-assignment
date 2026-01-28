package com.example.myapplication.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.viewmodel.AuthViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Scope the ViewModel to the navigation host / activity so it survives navigation
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToOtp = {
                    navController.navigate("otp")
                }
            )
        }
        composable("otp") {
            OtpScreen(
                viewModel = authViewModel,
                onNavigateToSession = {
                    // Pop everything up to login (inclusive) so back button exits app or goes to login?
                    // Requirement says "Provide Logout button".
                    // Usually you clear back stack.
                    navController.navigate("session") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("session") {
            SessionScreen(
                viewModel = authViewModel,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("session") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
