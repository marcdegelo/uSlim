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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flutschi.androidapp.R
import com.flutschi.islim.models.UserData
import com.flutschi.islim.models.SurveyViewModel
import com.flutschi.islim.ui.components.SelectableOptionCard

@Composable
fun SurveyGenderScreen(
    navController: NavController,
) {
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Add this line

            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            // Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == 2) 10.dp else 8.dp)
                            .background(
                                color = if (index == 2) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            Text(
                text = "What is your gender?",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Gender affects metabolism, muscle composition, and fitness needs. This helps us personalize workout plans and nutrition recommendations for better results.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SelectableOptionCard(
                    iconRes = R.drawable.ic_male,  // your drawable
                    label = "Male",
                    isSelected = selectedGender == "Male",
                    onClick = { selectedGender = "Male" },
                    modifier = Modifier.weight(1f)  // optional for grid layout
                )
                SelectableOptionCard(
                    iconRes = R.drawable.ic_female,  // your drawable
                    label = "Female",
                    isSelected = selectedGender == "Female",
                    onClick = { selectedGender = "Female" },
                    modifier = Modifier.weight(1f)  // optional for grid layout
                )
            }

            Button(
                onClick = {
                    selectedGender?.let {
                        UserData.gender = it
                        navController.navigate("surveyWeight") // update this route if needed
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(28.dp),
                enabled = selectedGender != null
            ) {
                Text("Next", color = Color.White)
            }
        }
    }
}
