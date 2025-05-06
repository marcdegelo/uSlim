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
fun SurveyBarriersScreen(
    navController: NavController,
) {
    val barrierOptions = listOf(
        BarrierItem("Lack of motivation", R.drawable.ic_barrier_motivation),
        BarrierItem("Physical Limitations", R.drawable.ic_barrier_physical),
        BarrierItem("Time constraints", R.drawable.ic_barrier_time),
        BarrierItem("Limited knowledge", R.drawable.ic_barrier_knowledge),
        BarrierItem("Financial constraints", R.drawable.ic_barrier_financial),
        BarrierItem("No hold backs !", R.drawable.ic_barrier_none)
    )

    val selectedBarriers = remember { mutableStateListOf<String>() }

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
                            .size(if (index == 11) 10.dp else 8.dp)
                            .background(
                                color = if (index == 11) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Title & Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "What’s holding you back?",
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
                    text = "Understanding your challenges helps us provide you with better support and guidance.",
                    fontSize = 14.sp
                )
            }

            // Options in a column using SelectableOptionCard
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                barrierOptions.forEach { item ->
                    val isSelected = item.label in selectedBarriers
                    SelectableOptionCard(
                        iconRes = item.iconRes,
                        label = item.label,
                        isSelected = isSelected,
                        onClick = {
                            if (item.label == "No hold backs !") {
                                selectedBarriers.clear()
                                selectedBarriers.add(item.label)
                            } else {
                                if ("No hold backs !" in selectedBarriers) {
                                    selectedBarriers.remove("No hold backs !")
                                }
                                if (isSelected) selectedBarriers.remove(item.label)
                                else selectedBarriers.add(item.label)
                            }
                        }
                    )
                }
            }

            // Next Button
            Button(
                onClick = {
                    UserData.barriers = selectedBarriers.toList()
                    navController.navigate("surveyDietType")
                },
                enabled = selectedBarriers.isNotEmpty(),
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

data class BarrierItem(val label: String, val iconRes: Int)
