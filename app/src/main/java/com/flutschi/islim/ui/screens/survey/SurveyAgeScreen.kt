package com.flutschi.islim.ui.screens.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flutschi.islim.models.SurveyViewModel
import com.flutschi.islim.models.UserData

@Composable
fun SurveyAgeScreen(
    navController: NavController,
) {
    val yearRange = 1920..2030
    val initialYear = 2000
    val itemHeightDp = 48.dp
    val itemHeightPx = with(LocalDensity.current) { itemHeightDp.toPx() }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = yearRange.indexOf(initialYear)
    )

    val selectedYear by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset / itemHeightPx
            (yearRange.first + index + offset).toInt().coerceIn(yearRange.first, yearRange.last)
        }
    }

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
                            .size(if (index == 5) 10.dp else 8.dp)
                            .background(
                                color = if (index == 5) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Question
            Text(
                text = "What year were you born?",
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
                    text = "Age affects metabolism, muscle recovery, and overall fitness needs. Knowing your birth year helps us tailor the perfect plan for you.",
                    fontSize = 14.sp
                )
            }
// Selected Year Display
            Text(
                text = "$selectedYear",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

// Scrollable Year Picker
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(itemHeightDp * 5), // 5 items visible
                contentPadding = PaddingValues(vertical = itemHeightDp * 2), // center the selected year
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(yearRange.count()) { i ->
                    val year = yearRange.first + i
                    val isSelected = year == selectedYear

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeightDp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = year.toString(),
                            color = if (isSelected) Color.Black else Color.DarkGray,
                            fontSize = if (isSelected) 24.sp else 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }


            // Next Button
            Button(
                onClick = {
                    UserData.age = selectedYear.toString()
                    navController.navigate("surveyGoals")
                },
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
