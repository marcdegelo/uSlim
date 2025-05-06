package com.flutschi.islim.ui.screens

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.flutschi.androidapp.R
import com.flutschi.islim.MainActivity
import com.flutschi.islim.models.UserData
import com.flutschi.islim.ui.components.ActivityCard
import com.flutschi.islim.ui.components.GradientButtonWithIcon
import com.flutschi.islim.ui.components.IconWithBorder
import com.flutschi.islim.ui.components.ProfilePicture
import com.flutschi.islim.ui.components.RecommendationsRow
import com.flutschi.islim.utils.SharedPrefManager
import com.flutschi.islim.utils.StepStorage
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import com.flutschi.islim.utils.rememberStepCounter


@Composable
fun MainScreen(navController: NavController) {

    val context = LocalContext.current

    val sharedPrefManager = SharedPrefManager(context)
    val username = sharedPrefManager.getUsername() ?: "Guest"  // Fallback to "Guest" if username is null

    val activity = context as? MainActivity

    // Wait until UserData is populated
    val isUserDataReady = remember { derivedStateOf { UserData.weight != null } }
    Log.d("MainScreen", "UserData.weight = ${UserData.weight}")

    if (!isUserDataReady.value) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()) // Enables scrolling
            .padding(16.dp)
    ) {
        // Header Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("WELCOME BACK...", fontSize = 14.sp, color = Color.Gray)
                Text(username, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_notifications),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(12.dp))
                // Dynamic Profile Picture
                ProfilePicture(sharedPrefManager)
            }
        }

        // ACTIVITY SECTION
        Text("ACTIVITY", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        ActivityCard(UserData, navController)

        Spacer(modifier = Modifier.height(24.dp))

        val liveSteps = rememberStepCounter()
        val stepsToday = if (liveSteps.value > 0) liveSteps.value else UserData.stepsToday

        val stepLengthInMeters = 0.7
        val kilometers = String.format("%.2f", stepsToday * stepLengthInMeters / 1000.0).toDouble()
        val kcal = String.format("%.2f", stepsToday * UserData.weight!! * 0.0005).toDouble()


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // Adjust horizontal padding
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GradientButtonWithIcon(
                iconResId = R.drawable.ic_steps, // Replace with correct icon
                text = "${kilometers} km",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp)) // Space between buttons

            GradientButtonWithIcon(
                iconResId = R.drawable.ic_kcal, // Replace with correct icon
                text = "${kcal} kcal",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // RECOMMENDATIONS SECTION
        Text("RECOMMENDATIONS", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        RecommendationsRow()

    }
}

// Scanners Icon Composable
@Composable
fun ScannerIcon(iconRes: Int, description: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = description,
            modifier = Modifier.size(50.dp),
            tint = Color.Black
        )
        Text(description, fontSize = 12.sp, color = Color.Black)
    }
}

// Activity Stats (km, kcal)
@Composable
fun ActivityStat(value: String, iconRes: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, fontSize = 16.sp, color = Color.Black)
    }
}

// Recommendation Cards
@Composable
fun RecommendationCard(iconRes: Int, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(40.dp),
                tint = Color.Black
            )
            Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

