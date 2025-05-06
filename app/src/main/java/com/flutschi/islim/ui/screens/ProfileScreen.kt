package com.flutschi.islim.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.flutschi.androidapp.R
import com.flutschi.islim.ui.components.CustomButton
import com.flutschi.islim.ui.components.ProfilePicture
import com.flutschi.islim.ui.screens.components.ProfileHeader
import com.flutschi.islim.utils.SharedPrefManager

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }

    val userData = sharedPrefManager.getUserData()
    val username = userData?.username ?: "Guest"
    val xp = userData?.userXP ?: 0
    val xpGoal = 1000
    val profileImageUri = sharedPrefManager.getProfileImage()

    Log.i("Profile", xp.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileHeader("Profile")

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            ProfilePicture(
                sharedPrefManager = sharedPrefManager,
                isClickable = true,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "@$username",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_verified),
                        contentDescription = "Verified",
                        tint = Color.Blue,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "ID: 839894234",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                XPBar(currentXP = xp, goalXP = xpGoal)

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        OverviewSection()

        Spacer(modifier = Modifier.height(16.dp))

        SettingsHeader()

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CustomButton(text = "Stats") { navController.navigate("stats") }
            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(text = "Goals") { navController.navigate("goals") }
            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(text = "Notifications") { navController.navigate("youtubePlayer/DHfRfU3XUEo") }
            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(text = "Guide") { navController.navigate("guide") }
            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(text = "Logout") {
                sharedPrefManager.logout()
                navController.navigate("signup") {
                    popUpTo("mainContainer") { inclusive = true }
                }
            }
        }

    }
}

@Composable
fun XPBar(currentXP: Int, goalXP: Int) {
    val progress = currentXP / goalXP.toFloat()
    val barHeight = 24.dp
    val capsuleWidth = 48.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(barHeight)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFE0E0E0))
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(50))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFFFFA500), Color(0xFFFFD580))
                    )
                )
        )

        Box(
            modifier = Modifier
                .width(capsuleWidth)
                .height(barHeight)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFFFA500))
                .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$currentXP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Text(
            text = "$currentXP / $goalXP XP",
            fontSize = 11.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 8.dp)
        )
    }
}

@Composable
fun OverviewSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "OVERVIEW",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF8F8F8),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 12.dp, horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewItem(value = "34", label = "WORKOUTS")
                OverviewItem(value = "2U", label = "EARNED")
                OverviewItem(value = "-3.4 kg", label = "VOLUME")
                OverviewItem(value = "74h", label = "TIME")
            }
        }
    }
}

@Composable
fun OverviewItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFA500)
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SettingsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = "Settings",
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "SETTINGS",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileButton(text: String, navController: NavController, route: String? = null, onClick: (() -> Unit)? = null) {
    Button(
        onClick = {
            if (onClick != null) {
                onClick()
            } else if (route != null) {
                navController.navigate(route)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = text)
    }
}
