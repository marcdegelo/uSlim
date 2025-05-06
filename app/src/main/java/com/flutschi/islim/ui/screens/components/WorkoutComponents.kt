package com.flutschi.islim.ui.screens.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.api.UserProgramRequest
import com.flutschi.islim.models.UserData
import com.flutschi.islim.models.Workout
import com.flutschi.islim.models.XpAwardRequest
import com.flutschi.islim.models.WorkoutCompleteRequest
import com.flutschi.islim.ui.viewmodels.WorkoutsFitnessViewModel
import com.flutschi.islim.utils.GLOBALS
import kotlinx.coroutines.*
import org.json.JSONObject
import java.time.LocalDate

const val WALKING_GOAL_STEPS = 10000

@Composable
fun WorkoutPlanSection(completedWorkouts: SnapshotStateList<Int>, selectedDay: String
) {

    val viewModel: WorkoutsFitnessViewModel = viewModel() // ✅ Correct
    val programs by viewModel.allWorkouts.collectAsState(initial = emptyList())

    val userId = UserData.userID ?: return

    Log.i("WorkoutPlanSection", selectedDay)

    LaunchedEffect(userId, selectedDay) {
        viewModel.loadWorkoutsForUser(UserProgramRequest(userId, selectedDay))
    }

    val workouts = programs

    val walkingId = 1
    val walkingWorkout = Workout(
        icon = "🏃",
        title = "Walking - 10,000 Steps",
        id = walkingId,
        isManuallyCompletable = false,
        day = selectedDay
    )
    val combinedWorkouts = listOf(walkingWorkout) + workouts

    val steps = UserData.stepsToday ?: 0

    Log.i("Workout", steps.toString())

    LaunchedEffect(steps) {
        val walkingId = 1
        val userId = UserData.userID ?: return@LaunchedEffect

        if (steps >= WALKING_GOAL_STEPS && !completedWorkouts.contains(walkingId)) {
            markWorkoutCompleteAndAwardXP(userId, walkingId) {
                completedWorkouts.add(walkingId)
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text("WORKOUTPLAN", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))

        combinedWorkouts.forEachIndexed { index, workout ->
            val isCompleted = completedWorkouts.contains(workout.id)
            val isButtonEnabled = workout.isManuallyCompletable

            val valueText = if (workout.id == 1) {
                "${steps} / $WALKING_GOAL_STEPS Steps"
            } else {
                workout.title.substringAfter("-").trim()
            }

            WorkoutItem(
                icon = workout.icon,
                title = workout.title.substringBefore("-").trim(),
                value = valueText,
                workoutId = workout.id,
                isCompleted = isCompleted,
                onComplete = { completedWorkouts.add(workout.id) },
                isHighlighted = index == 1,
                isButtonEnabled = isButtonEnabled
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun WorkoutItem(
    icon: String,
    title: String,
    value: String,
    isHighlighted: Boolean = false,
    workoutId: Int,
    isCompleted: Boolean,
    onComplete: () -> Unit,
    isButtonEnabled: Boolean
) {
    val backgroundColor = if (isHighlighted) Color(0xFFFFF3E0) else Color(0xFFF9F9F9)
    val borderColor = if (isHighlighted) Color(0xFFFFA726) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFFFFA726),
            modifier = Modifier.padding(end = 8.dp)
        )

        CompleteWorkoutButton(
            workoutId = workoutId,
            isAlreadyCompleted = isCompleted,
            onComplete = onComplete,
            isEnabled = isButtonEnabled
        )
    }
}

@Composable
fun CompleteWorkoutButton(
    workoutId: Int,
    isAlreadyCompleted: Boolean,
    onComplete: () -> Unit,
    isEnabled: Boolean
) {
    var isLoading by remember { mutableStateOf(false) }

    val containerColor = when {
        isAlreadyCompleted -> Color(0xFF81C784) // Green
        isEnabled -> Color.LightGray                // Blue
        else -> Color.LightGray                // Disabled look
    }

    val contentColor = Color.White

    Log.i(
        "CompleteWorkoutButton",
        "Workout ID: $workoutId | isAlreadyCompleted=$isAlreadyCompleted | isLoading=$isLoading | enabled=$isEnabled"
    )

    val context = LocalContext.current
    var isCompleted by remember { mutableStateOf(false) }
    val finalCompleted = isCompleted || isAlreadyCompleted  // 🔁 computed value

    Button(
        onClick = {
            isLoading = true

            val mealInfoJson = JSONObject()
            mealInfoJson.put("workoutId", workoutId)
            mealInfoJson.put("notes", "Checking workout completion")

            val mealInfoString = mealInfoJson.toString()
            Log.i("CompleteWorkout", "Test")

            GLOBALS.cameraUploader.dispatchTakePictureIntent(
                mealInfoString,
                onUploadResult = { responseStatus ->
                    isLoading = false
                    Log.i("CompleteWorkout", "✅ Upload responseStatus: $responseStatus")
                    if (responseStatus == "success") {
                        isCompleted = true
                        onComplete()
                    } else if (responseStatus == "try_again") {
                        Toast.makeText(context, "Workout not recognized. Please try again!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        },
        enabled = true, // visually always enabled
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(50),
        modifier = Modifier.size(20.dp),
        contentPadding = PaddingValues(0.dp)
    ) {}
}


suspend fun markWorkoutCompleteAndAwardXP(userId: Int, workoutId: Int, onSuccess: () -> Unit) {
    try {
        val completeRequest = WorkoutCompleteRequest(user_id = userId, workout_id = workoutId)
        val completeResponse = RetrofitInstance.getApi().completeWorkout(completeRequest)

        if (completeResponse.isSuccessful) {
            val xpRequest = XpAwardRequest(
                user_id = userId,
                xp_amount = 15,
                source_id = workoutId,
                source_type = "workout",
                reason = "Workout Completed"
            )
            val xpResponse = RetrofitInstance.getApi().awardXp(xpRequest)

            if (xpResponse.isSuccessful) {
                withContext(Dispatchers.Main) {
                    onSuccess()
                    Log.i("WorkoutComplete", "Workout $workoutId completed and XP awarded.")
                }
            }
        } else {
            Log.e("WorkoutComplete", "Failed to complete workout $workoutId: ${completeResponse.code()}")
        }
    } catch (e: Exception) {
        Log.e("WorkoutComplete", "Error: ${e.localizedMessage}")
    }
}
