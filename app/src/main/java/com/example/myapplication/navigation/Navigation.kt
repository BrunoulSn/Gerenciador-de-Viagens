package com.example.myapplication.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.CurrentUser
import com.example.myapplication.ui.screens.AboutScreen
import com.example.myapplication.ui.screens.CreateTripScreen
import com.example.myapplication.ui.screens.ForgotPasswordScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.MenuScreen
import com.example.myapplication.ui.screens.MyTripsScreen
import com.example.myapplication.ui.screens.RegisterScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Menu : Screen("menu")
    object CreateTrip : Screen("create_trip")
    object MyTrips : Screen("my_trips")
    object About : Screen("about")
}

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNewUserClick = {
                    navController.navigate(Screen.Register.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Menu.route) {
            MenuScreen(
                onNewTripClick = {
                    navController.navigate(Screen.CreateTrip.route)
                },
                onMyTripsClick = {
                    navController.navigate(Screen.MyTrips.route)
                },
                onAboutClick = {
                    navController.navigate(Screen.About.route)
                },
                onLogoutClick = {
                    CurrentUser.userId = null
                    CurrentUser.userName = null
                    CurrentUser.userEmail = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Menu.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CreateTrip.route) {
            CreateTripScreen(
                onTripCreated = {
                    navController.navigate(Screen.MyTrips.route) {
                        popUpTo(Screen.CreateTrip.route) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MyTrips.route) {
            MyTripsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onEditTrip = {
                    // TODO: Implementar tela de edição de viagem
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

