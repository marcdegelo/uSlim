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
fun SurveyFitnessLevelScreen(
    navController: NavController,
) {
    var selectedLevel by remember { mutableStateOf<String?>(null) }

    val options = listOf(
        FitnessLevelItem("Beginner", "New to fitness or returning after a long break.", R.drawable.ic_beginer),
        FitnessLevelItem("Intermediate", "Regularly active with some experience in workouts.", R.drawable.ic_intermediate),
        FitnessLevelItem("Advanced", "Consistently training with high endurance and strength.", R.drawable.ic_advanced)
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
                            .size(if (index == 7) 10.dp else 8.dp)
                            .background(
                                color = if (index == 7) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Title
            Text(
                text = "What is your fitness level?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Explanation
            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Understanding your current fitness level helps us create a workout plan that matches your abilities.",
                    fontSize = 14.sp
                )
            }

            // Options
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                options.forEach { item ->
                    FitnessLevelOptionCard(
                        item = item,
                        isSelected = selectedLevel == item.label,
                        onClick = { selectedLevel = item.label }
                    )
                }
            }

            // Next Button
            Button(
                onClick = {
                    selectedLevel?.let {
                        UserData.fitnessLevel = it
                        navController.navigate("surveyActivityTime") // update route
                    }
                },
                enabled = selectedLevel != null,
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
fun FitnessLevelOptionCard(
    item: FitnessLevelItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
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
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                tint = Color.Unspecified,
                modifier = Modifier.size(128.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(item.label, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.description, fontSize = 12.sp)
            }
        }
    }
}

data class FitnessLevelItem(val label: String, val description: String, val iconRes: Int)
