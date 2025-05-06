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
import com.flutschi.islim.ui.components.InfoBubble
import com.flutschi.islim.ui.components.SelectableOptionCard

@Composable
fun SurveyHealthIssuesScreen(
    navController: NavController,
) {
    val healthOptions = listOf(
        HealthItem("I don't have any", R.drawable.ic_nomedicalissue),
        HealthItem("Respiratory Conditions", R.drawable.ic_respiratoryconditions),
        HealthItem("Diabetes", R.drawable.ic_diabetes),
        HealthItem("Neurological Conditions", R.drawable.ic_neurological),
        HealthItem("Heart Conditions", R.drawable.ic_heartconditions),
        HealthItem("Joint issues", R.drawable.ic_jointissues),
        HealthItem("Other (please specify)", null)
    )

    val selectedConditions = remember { mutableStateListOf<String>() }

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
                            .size(if (index == 10) 10.dp else 8.dp)
                            .background(
                                color = if (index == 10) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Title + Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Do you have any health issues?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                InfoBubble(
                    text = "Understanding any health conditions helps us tailor your fitness plan to ensure safety and effectiveness."
                )
            }

            // Health options (Single-column layout)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                healthOptions.forEach { item ->
                    val isSelected = item.label in selectedConditions
                    SelectableOptionCard(
                        iconRes = item.iconRes,
                        label = item.label,
                        isSelected = isSelected,
                        onClick = {
                            if (item.label == "I don't have any") {
                                selectedConditions.clear()
                                selectedConditions.add(item.label)
                            } else {
                                if ("I don't have any" in selectedConditions) {
                                    selectedConditions.remove("I don't have any")
                                }
                                if (isSelected) selectedConditions.remove(item.label)
                                else selectedConditions.add(item.label)
                            }
                        }
                    )
                }
            }

            // Next button
            Button(
                onClick = {
                    UserData.healthConditions = selectedConditions.toList()
                    navController.navigate("surveyBarriers")
                },
                enabled = selectedConditions.isNotEmpty(),
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

data class HealthItem(val label: String, val iconRes: Int?)
