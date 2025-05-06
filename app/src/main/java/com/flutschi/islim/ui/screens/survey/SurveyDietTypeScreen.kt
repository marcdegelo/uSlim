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
fun SurveyDietTypeScreen(
    navController: NavController,
) {
    var selectedDiet by remember { mutableStateOf(UserData.dietType) }

    val dietOptions = listOf(
        DietOption("Mediterranean", R.drawable.ic_diet_mediterranean),
        DietOption("High-Protein", R.drawable.ic_diet_high_protein),
        DietOption("Keto", R.drawable.ic_diet_keto),
        DietOption("Balanced", R.drawable.ic_diet_balanced),
        DietOption("Vegetarian/Vegan", R.drawable.ic_diet_vegan),
        DietOption("Flexible/ no diet", R.drawable.ic_diet_flexible)
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
                            .size(if (index == 12) 10.dp else 8.dp)
                            .background(
                                color = if (index == 12) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Title & Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "What type of diet fits you best?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "Our expert nutritionists use this information to create meal plans tailored to your lifestyle and fitness goals.",
                    fontSize = 14.sp
                )
            }

            // Diet types as a vertical list using SelectableOptionCard
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                dietOptions.forEach { item ->
                    SelectableOptionCard(
                        iconRes = item.iconRes,
                        label = item.label,
                        isSelected = selectedDiet == item.label,
                        onClick = { selectedDiet = item.label }
                    )
                }
            }


            // Next Button
            Button(
                onClick = {
                    selectedDiet?.let {
                        UserData.dietType = it
                        navController.navigate("surveyWorkoutStyle")
                    }
                },
                enabled = selectedDiet != null,
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

data class DietOption(val label: String, val iconRes: Int)
