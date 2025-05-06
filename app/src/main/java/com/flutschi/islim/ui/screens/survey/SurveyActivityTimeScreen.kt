package com.flutschi.islim.ui.screens.survey

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flutschi.islim.models.SurveyViewModel
import com.flutschi.androidapp.R
import com.flutschi.islim.models.UserData

@Composable
fun SurveyActivityTimeScreen(
    navController: NavController,
) {
    var selectedTime by remember { mutableStateOf<String?>(null) }

    val timeOptions = listOf(
        "0–3 hours",
        "4–6 hours",
        "6–9 hours",
        "10+ hours"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Add this line

            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Progress Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (index == 8) 10.dp else 8.dp)
                            .background(
                                color = if (index == 8) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Title
            Text(
                text = "How much time do you have available for physical activities (weekly)?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Info Card
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Your available time plays a key role in designing a workout plan that fits your schedule.",
                    fontSize = 14.sp
                )
            }

            // Clock Icon (optional)
            Icon(
                painter = painterResource(id = R.drawable.ic_time_fitness), // ⬅️ Add your asset
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(100.dp)
            )

            // Time Options Grid
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                timeOptions.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { option ->
                            val isSelected = selectedTime == option
                            val borderModifier = if (isSelected) {
                                Modifier.border(BorderStroke(2.dp, Color.Black), shape = RoundedCornerShape(50))
                            } else {
                                Modifier
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .then(borderModifier) // ✅ conditional border here
                                    .clickable { selectedTime = option }
                                    .background(
                                        if (isSelected) Color(0xFFB8FFB8) else Color.White,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // Next Button
            Button(
                onClick = {
                    selectedTime?.let {
                        UserData.activityTime = it
                        navController.navigate("surveyActivityPreferences") // update to your next screen
                    }
                },
                enabled = selectedTime != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Next", color = Color.White)
            }
        }
    }
}
