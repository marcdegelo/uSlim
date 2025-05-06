package com.flutschi.islim.ui.screens.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flutschi.islim.models.SurveyViewModel
import com.flutschi.androidapp.R
import com.flutschi.islim.models.UserData
import com.flutschi.islim.ui.components.SelectableOptionCard

@Composable
fun SurveyActivityPreferenceScreen(
    navController: NavController,
) {
    val activityOptions = listOf(
        ActivityItem("Gym", R.drawable.ic_gym),
        ActivityItem("Home Workout", R.drawable.ic_home_workout),
        ActivityItem("Outdoor Activities", R.drawable.ic_outdoor),
        ActivityItem("Team Sports", R.drawable.ic_team_sports),
        ActivityItem("Yoga", R.drawable.ic_yoga),
        ActivityItem("Swimming", R.drawable.ic_swimming)
    )

    val selectedActivities = remember { mutableStateListOf<String>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Add this line

            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
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
                            .size(if (index == 9) 10.dp else 8.dp)
                            .background(
                                color = if (index == 9) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Choose your favorite type of physical activity",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "(multiple choice)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Activity options (vertical list)
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                activityOptions.forEach { item ->
                    val isSelected = item.label in selectedActivities
                    SelectableOptionCard(
                        iconRes = item.iconRes,
                        label = item.label,
                        isSelected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedActivities.remove(item.label)
                            } else {
                                selectedActivities.add(item.label)
                            }
                        }
                    )
                }
            }

            // Next button
            Button(
                onClick = {
                    UserData.activities = selectedActivities.toList()
                    navController.navigate("surveyHealthIssues")
                },
                enabled = selectedActivities.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Next", color = Color.White)
            }
        }
    }
}

data class ActivityItem(val label: String, val iconRes: Int)
