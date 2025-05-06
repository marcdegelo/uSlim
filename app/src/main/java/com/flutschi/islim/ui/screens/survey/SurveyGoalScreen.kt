package com.flutschi.islim.ui.screens.survey

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.flutschi.androidapp.R
import com.flutschi.islim.models.UserData

@Composable
fun SurveyGoalScreen(
    navController: NavController,
) {
    var selectedGoal by remember { mutableStateOf<String?>(UserData.goal) }

    val goalOptions = listOf(
        GoalItem(
            "Lose Weight",
            R.drawable.ic_goal_lose,
            "Focus on fat loss and achieving a leaner physique."
        ),
        GoalItem(
            "Build Muscle",
            R.drawable.ic_goal_muscle,
            "Gain strength and muscle mass with targeted training."
        ),
        GoalItem(
            "Mind-Body Wellness",
            R.drawable.ic_yoga,
            "Reduce stress, improve focus, and enhance overall well-being through mindful movement like yoga, meditation, and breathing exercises."
        ),
        GoalItem(
            "Maintain Fitness",
            R.drawable.ic_goal_maintain,
            "Stay active and healthy with balanced workouts."
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                            .size(if (index == 6) 10.dp else 8.dp)
                            .background(
                                color = if (index == 6) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Title
            Text(
                text = "What is your primary fitness goal?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Goal Options List
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                goalOptions.forEach { item ->
                    SelectableOptionCard(
                        iconRes = item.iconRes,
                        label = item.title,
                        description = item.description,
                        isSelected = selectedGoal == item.title,
                        onClick = { selectedGoal = item.title }
                    )
                }
            }

            // Next button
            Button(
                onClick = {
                    selectedGoal?.let {
                        UserData.goal = it
                        navController.navigate("surveyFitnessLevel")
                    }
                },
                enabled = selectedGoal != null,
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

@Composable
fun SelectableOptionCard(
    iconRes: Int,
    label: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val bgColor = if (isSelected) Color(0xFFB8FFB8) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else null,
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { showDialog = true }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info),
                    contentDescription = "Info about $label",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK")
                }
            },
            title = { Text(text = label) },
            text = { Text(text = description) }
        )
    }
}

data class GoalItem(
    val title: String,
    val iconRes: Int,
    val description: String
)
