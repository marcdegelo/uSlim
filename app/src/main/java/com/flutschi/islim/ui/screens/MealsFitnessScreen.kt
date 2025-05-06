package com.flutschi.islim.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.models.UserData
import com.flutschi.islim.ui.screens.components.MealPlanSection
import com.flutschi.islim.ui.screens.components.WorkoutPlanSection
import com.flutschi.islim.ui.screens.components.YogaPlanSection
import com.flutschi.islim.ui.viewmodel.MealsFitnessViewModel
import com.flutschi.islim.ui.viewmodels.YogaFitnessViewModel
import com.flutschi.islim.utils.GLOBALS
import java.time.LocalDate

@Composable
fun MealsFitnessScreen(viewModel: MealsFitnessViewModel = viewModel()) {
    val selectedDay by viewModel.selectedDay.collectAsState()
    val meals by viewModel.mealsForDay.collectAsState()

    val completedMeals = GLOBALS.GlobalAppState.completedMeals
    val completedWorkouts = GLOBALS.GlobalAppState.completedWorkouts

    LaunchedEffect(Unit) {
        val planId = determineMealPlanId()
        viewModel.loadMealPlan(planId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DaySelector(selectedDay) { viewModel.setSelectedDay(it) }

        Spacer(modifier = Modifier.height(20.dp))

        MealPlanSection(meals = meals, completedMeals = completedMeals)

        Spacer(modifier = Modifier.height(20.dp))

        WorkoutPlanSection(completedWorkouts = completedWorkouts, selectedDay = selectedDay)

        Spacer(modifier = Modifier.height(20.dp))

    }
}

@Composable
fun DaySelector(selectedDay: String, onDaySelected: (String) -> Unit) {
    val today = remember { LocalDate.now() }
    val daysRange = remember {
        (0..6).map { today.minusDays(3 - it.toLong()) }
    }

    val selectedIndex = daysRange.indexOfFirst { it.dayOfWeek.name.equals(selectedDay.uppercase(), ignoreCase = true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysRange.forEachIndexed { index, date ->
            val isSelected = index == selectedIndex
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfWeek.name.take(3), // MON, TUE...
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFFFC107) else Color.Transparent)
                        .clickable {
                            val day = date.dayOfWeek.name.lowercase()
                                .replaceFirstChar { it.uppercase() }

                            onDaySelected(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isSelected) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun determineMealPlanId(): Int {
    return when {
        UserData.goal == "Lose Weight" && UserData.dietType == "Keto" -> 1
        UserData.goal == "Gain Muscle" && UserData.dietType == "High Protein" -> 2
        UserData.goal == "Maintain" -> 3
        else -> 1
    }
}
