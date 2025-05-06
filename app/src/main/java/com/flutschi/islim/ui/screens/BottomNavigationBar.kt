package com.flutschi.islim.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.flutschi.androidapp.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", R.drawable.ic_home, "Home"),
        BottomNavItem("tasks", R.drawable.ic_tasks, "Tasks"),
        BottomNavItem("exchange", R.drawable.ic_rewards, "Rewards"),
        BottomNavItem("shop", R.drawable.ic_uslimpass, "Shop"),
        BottomNavItem("meals", R.drawable.ic_meals, "Meals"),
        BottomNavItem("profile", R.drawable.ic_profile, "Profile")
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Column {
        Divider(
            thickness = 2.dp, // Thicker line
            color = Color.LightGray // or Color.Black depending on design
        )

        NavigationBar(
            containerColor = Color.White, // or Color.Transparent
            modifier = Modifier.height(80.dp)
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.iconRes),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { }, // Keep empty if you want icon-only nav bar
                    selected = currentRoute == item.route,
                    onClick = { navController.navigate(item.route) }
                )
            }
        }
    }

}

data class BottomNavItem(val route: String, val iconRes: Int, val label: String)
