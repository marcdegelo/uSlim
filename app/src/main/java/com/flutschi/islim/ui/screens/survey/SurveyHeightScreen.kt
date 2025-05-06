package com.flutschi.islim.ui.screens.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.flutschi.islim.models.UserData
import com.flutschi.islim.models.SurveyViewModel
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun SurveyHeightScreen(
    navController: NavController,
) {
    var isCm by remember { mutableStateOf(true) }

    val heightRange = 140..260
    val tickHeightDp = 32.dp
    val tickHeightPx = with(LocalDensity.current) { tickHeightDp.toPx() }
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = heightRange.indexOf(180)) // Try this

    val height by remember {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset / tickHeightPx
            (heightRange.first + index + offset).coerceIn(heightRange.first.toFloat(), heightRange.last.toFloat())
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
                            .size(if (index == 4) 10.dp else 8.dp)
                            .background(
                                color = if (index == 4) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Question
            Text(
                text = "What is your height ?",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Cm / Ft toggle
            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color.White, RoundedCornerShape(50))
            ) {
                HeightUnitToggle("Cm", isCm) { isCm = true }
                HeightUnitToggle("Ft", !isCm) { isCm = false }
            }

            // Live height display
            Text(
                text = if (isCm) "${height.toInt()} cm" else cmToFeetInches(height.toInt()),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            val lazyColumnHeight = 500.dp
            val tickHeight = 2.dp

            // Scrollable vertical ruler
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(
                    vertical = (lazyColumnHeight / 2) - (tickHeight / 2)
                ),
                ) {
                items(heightRange.count()) { i ->
                    val value = heightRange.first + i
                    val isSelected = value == height.toInt()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(2.dp)
                                .background(if (isSelected) Color.Red else Color.Black)
                        )
                        Spacer(Modifier.width(8.dp))
                        if (value % 5 == 0) {
                            Text(
                                text = "$value cm",
                                fontSize = 12.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Next Button
            Button(
                onClick = {
                    val finalHeight = if (isCm) height else feetInchesToCm(height.toInt())
                    UserData.height = finalHeight.toInt().toString()
                    navController.navigate("surveyAge")
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

@Composable
fun HeightUnitToggle(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                if (selected) Color(0xFFFFA726) else Color.Transparent,
                RoundedCornerShape(50)
            )
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(text = label, fontSize = 16.sp, color = Color.Black)
    }
}

// Convert CM to ft/in
fun cmToFeetInches(cm: Int): String {
    val totalInches = (cm / 2.54).toInt()
    val feet = totalInches / 12
    val inches = totalInches % 12
    return "${feet}ft ${inches}in"
}

// Convert placeholder back (optional — not fully implemented)
fun feetInchesToCm(cmValue: Int): Float {
    return cmValue.toFloat() // just keeping it safe for now
}
