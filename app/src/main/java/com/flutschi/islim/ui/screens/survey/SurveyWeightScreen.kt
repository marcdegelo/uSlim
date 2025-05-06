package com.flutschi.islim.ui.screens.survey

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.flutschi.islim.models.UserData
import kotlin.math.roundToInt

@Composable
fun SurveyWeightScreen(
    navController: NavController,
) {
    var isKg by remember { mutableStateOf(true) }

    val weightRangeKg = 30..200
    val weightRangeLbs = (weightRangeKg.first * 2.20462).roundToInt()..(weightRangeKg.last * 2.20462).roundToInt()

    val tickWidthDp = 24.dp
    val tickWidthPx = with(LocalDensity.current) { tickWidthDp.toPx() }

    // Use the appropriate weight range based on the selected unit
    val currentWeightRange = if (isKg) weightRangeKg else weightRangeLbs

    // Calculate initial index for Lbs
    val initialWeightKg = 82 // Approximate initial weight in Kg
    val initialWeightLbs = (initialWeightKg * 2.20462).roundToInt()
    val initialIndex = if (isKg) weightRangeKg.indexOf(initialWeightKg) else weightRangeLbs.indexOf(initialWeightLbs)

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    // Correct: Pixels per unit depends on isKg
    val pixelsPerUnit = tickWidthPx / (if (isKg) 1f else 2.20462f)

    // Dynamic weight based on scroll
    val weight by remember(isKg, listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offsetInUnits = listState.firstVisibleItemScrollOffset / pixelsPerUnit
            (currentWeightRange.first + index + offsetInUnits).coerceIn(
                currentWeightRange.first.toFloat(),
                currentWeightRange.last.toFloat()
            )
        }
    }

    // State to hold the displayed weight (in Kg or Lbs)
    var displayedWeight by remember { mutableStateOf(weight) }

    // Update displayedWeight whenever isKg or weight changes
    LaunchedEffect(isKg, weight) {
        displayedWeight = weight
    }

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
                            .size(if (index == 3) 10.dp else 8.dp)
                            .background(
                                color = if (index == 3) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "What is your weight?",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(Color.White, RoundedCornerShape(50))
            ) {
                WeightUnitToggle("Kg", isKg) { isKg = true }
                WeightUnitToggle("Lbs", !isKg) { isKg = false }
            }

            // Use displayedWeight here
            Text(
                text = String.format("%.1f %s", displayedWeight, if (isKg) "Kg" else "Lbs"),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Ruler
            LazyRow(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(
                    horizontal = (LocalConfiguration.current.screenWidthDp.dp / 2) - (tickWidthDp / 2)
                )
            ) {
                itemsIndexed(currentWeightRange.toList()) { index, value ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(tickWidthDp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(if (value == weight.toInt()) 32.dp else 24.dp)
                                .background(if (value == weight.toInt()) Color.Red else Color.Black)
                        )
                        if (value % 5 == 0) {
                            Text(
                                text = "$value",
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Next Button
            Button(
                onClick = {
                    val finalWeight = if (isKg) weight else weight * 0.453592f
                    UserData.weight = finalWeight
                    navController.navigate("surveyHeight")
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
fun WeightUnitToggle(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                if (selected) Color(0xFFFFA726) else Color.Transparent,
                RoundedCornerShape(50)
            )
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 16.sp
        )
    }
}
