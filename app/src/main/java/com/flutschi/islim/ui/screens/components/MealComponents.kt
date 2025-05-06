package com.flutschi.islim.ui.screens.components

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.flutschi.islim.models.Meal
import com.flutschi.islim.models.MealCompleteRequest
import com.flutschi.islim.models.UserData
import com.flutschi.islim.models.XpAwardRequest
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.androidapp.R
import com.flutschi.islim.camera.CameraUploader
import com.flutschi.islim.utils.GLOBALS
import kotlinx.coroutines.*
import org.json.JSONObject
import kotlin.math.log

@Composable
fun MealPlanSection(meals: List<Meal>, completedMeals: SnapshotStateList<Int>) {
    val totalFat = meals.sumOf { it.fats.toDouble() }.toFloat()
    val totalCarbs = meals.sumOf { it.carbs.toDouble() }.toFloat()
    val totalProtein = meals.sumOf { it.protein.toDouble() }.toFloat()
    var showMeals by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFFFFC107), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("MEALPLAN", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.clickable { showMeals = !showMeals }) {
                CircularMealChart(
                    fat = totalFat to 150f,
                    carbs = totalCarbs to 230f,
                    protein = totalProtein to 600f
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                MealLegend("FAT", totalFat, 150f, Color.Red)
                MealLegend("CARBS", totalCarbs, 230f, Color(0xFFFFC107))
                MealLegend("PROTEIN", totalProtein, 600f, Color(0xFFFFA726))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        MealsDropdownStyled(meals = meals, completedMeals = completedMeals)
    }
}

@Composable
fun CircularMealChart(fat: Pair<Float, Float>, carbs: Pair<Float, Float>, protein: Pair<Float, Float>) {
    val size = 100.dp
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val thickness = 10.dp.toPx()

            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(thickness, cap = StrokeCap.Round)
            )

            drawArc(
                color = Color.Red,
                startAngle = -90f,
                sweepAngle = 360f * (fat.first / fat.second),
                useCenter = false,
                style = Stroke(thickness, cap = StrokeCap.Round)
            )

            drawArc(
                color = Color(0xFFFFC107),
                startAngle = -90f,
                sweepAngle = 360f * (carbs.first / carbs.second),
                useCenter = false,
                style = Stroke(thickness, cap = StrokeCap.Round),
                topLeft = Offset(thickness * 1.5f, thickness * 1.5f),
                size = Size(size.toPx() - thickness * 3, size.toPx() - thickness * 3)
            )

            drawArc(
                color = Color(0xFFFFA726),
                startAngle = -90f,
                sweepAngle = 360f * (protein.first / protein.second),
                useCenter = false,
                style = Stroke(thickness, cap = StrokeCap.Round),
                topLeft = Offset(thickness * 3f, thickness * 3f),
                size = Size(size.toPx() - thickness * 6, size.toPx() - thickness * 6)
            )
        }
    }
}

@Composable
fun MealLegend(label: String, current: Float, target: Float, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label ", fontWeight = FontWeight.Bold)
        Text("${current.toInt()}", color = Color.Black, fontWeight = FontWeight.SemiBold)
        Text(" of ${target.toInt()}", color = Color.Gray)
    }
}

@Composable
fun MealsDropdownStyled(meals: List<Meal>, completedMeals: SnapshotStateList<Int>) {
    val groupedMeals = meals.groupBy { it.meal_type }
    val expandedState = remember { mutableStateMapOf<String, Boolean>() }

    Column {
        groupedMeals.forEach { (mealType, mealList) ->
            val totalCalories = mealList.sumOf { it.calories }

            MealTypeCard(
                mealType = mealType.uppercase(),
                calories = totalCalories,
                isExpanded = expandedState[mealType] == true,
                onToggle = {
                    expandedState[mealType] = !(expandedState[mealType] ?: false)
                }
            )

            if (expandedState[mealType] == true) {
                mealList.forEach { meal ->
                    Spacer(modifier = Modifier.height(8.dp))
                    ExpandedMealDetail(meal = meal, completedMeals = completedMeals)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MealTypeCard(
    mealType: String,
    calories: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(30.dp))
            .background(Color(0xFFB2FF59))
            .clickable { onToggle() }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$mealType:", fontWeight = FontWeight.Bold)
            Text("$calories kcal", color = Color(0xFF009688))
        }
    }
}

@Composable
fun ExpandedMealDetail(meal: Meal, completedMeals: SnapshotStateList<Int>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color(0xFFFFC107), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("SNACK:", fontWeight = FontWeight.Bold)
            Row {
                repeat(3) {
                    Icon(
                        painter = painterResource(R.drawable.ic_placeholder),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                }
            }
            Text("${meal.calories} kcal", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Divider()

        Text(meal.title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🔴 FAT: ${meal.fats}g", modifier = Modifier.padding(end = 8.dp))
            Text("🟡 CARBS: ${meal.carbs}g", modifier = Modifier.padding(end = 8.dp))
            Text("🟣 PROTEIN: ${meal.protein}g")
        }

        Spacer(modifier = Modifier.height(8.dp))

        CompleteMealButton(
            mealId = meal.id,
            isAlreadyCompleted = completedMeals.contains(meal.id),
            onComplete = { completedMeals.add(meal.id) }
        )
    }
}

@Composable
fun CompleteMealButton(
    mealId: Int,
    isAlreadyCompleted: Boolean,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var isLoading by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    val finalCompleted = isCompleted || isAlreadyCompleted  // 🔁 computed value

    Button(
        onClick = {
            isLoading = true

            val mealInfoJson = JSONObject()
            mealInfoJson.put("mealId", mealId)
            mealInfoJson.put("notes", "Checking meal completion")

            val mealInfoString = mealInfoJson.toString()
            Log.i("CompleteMealButton", "Test")

            GLOBALS.cameraUploader.dispatchTakePictureIntent(
                mealInfoString,
                onUploadResult = { responseStatus ->
                    isLoading = false
                    Log.i("CompleteMealButton", "✅ Upload responseStatus: $responseStatus")
                    if (responseStatus == "success") {
                        isCompleted = true
                        onComplete()
                    } else if (responseStatus == "try_again") {
                        Toast.makeText(context, "Meal not recognized. Please try again!", Toast.LENGTH_SHORT).show()
                    }
                }
            )


        },
        enabled = !isLoading && !finalCompleted,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (finalCompleted) Color.LightGray else Color(0xFFFFE082),
            contentColor = if (finalCompleted) Color.DarkGray else Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = when {
                finalCompleted -> "Completed"
                isLoading -> "Uploading..."
                else -> "Complete"
            },
            fontWeight = FontWeight.Bold
        )
    }

}
