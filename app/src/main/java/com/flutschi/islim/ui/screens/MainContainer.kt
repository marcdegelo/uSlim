package com.flutschi.islim.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.flutschi.islim.ui.screens.shop.ShopScreen
import com.flutschi.islim.utils.SharedPrefManager
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage


@Composable
fun MainContainer(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    var profileImageUrl by remember { mutableStateOf(sharedPrefManager.getProfileImage()) }

    LaunchedEffect(Unit) {
        try {
            // sendDiscordMessage("App gestartet", "App gestartet.")
            profileImageUrl = sharedPrefManager.getProfileImage() // Load profile image on launch
        } catch (e: Exception) {
            sendDiscordMessage("", "💥 Fehler in MainContainer: ${e.message}")
        }
    }

    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { MainScreen(navController) }
            composable("tasks") { TasksChallengesScreen() }
            composable("exchange") { ExchangeScreen() }
            composable("shop") { ShopScreen() }
            composable("meals") { MealsFitnessScreen() }
            composable("profile") { ProfileScreen(navController) }
        }
    }
}