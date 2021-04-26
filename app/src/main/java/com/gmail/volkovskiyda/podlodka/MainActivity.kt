package com.gmail.volkovskiyda.podlodka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.gmail.volkovskiyda.podlodka.ui.MainScreen
import com.gmail.volkovskiyda.podlodka.ui.SessionScreen
import com.gmail.volkovskiyda.podlodka.ui.theme.PodlodkaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as MainApp).repository

        setContent {
            PodlodkaTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "main") {
                        composable(route = "main") {
                            MainScreen(
                                repository,
                                navigateToSession = { sessionId ->
                                    navController.navigate("session/${sessionId}")
                                },
                                onFinish = { finish() }
                            )
                        }
                        composable(
                            route = "session/{sessionId}",
                            arguments = listOf(navArgument("userId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            SessionScreen(
                                repository,
                                requireNotNull(backStackEntry.arguments?.getString("sessionId"))
                            )
                        }
                    }
                }
            }
        }
    }
}
