package com.flutschi.islim.ui.screens.survey

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.models.SurveyData
import com.flutschi.islim.models.SurveyViewModel
import com.flutschi.islim.models.UserData
import com.flutschi.islim.utils.GLOBALS
import com.flutschi.islim.utils.SharedPrefManager
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun SurveySubmitScreen(
    navController: NavController,
) {
    var isLoading by remember { mutableStateOf(false) }
    var hasRequested by remember { mutableStateOf(false) }
    var responseMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Trigger the submission request
    LaunchedEffect(hasRequested) {
        if (hasRequested) {
            val result = sendSurveyToBackend(context) // ✅ pass context here
            responseMessage = result
            isLoading = false
            if (result == "Success") {
                navController.navigate("mainContainer") // Replace with your dashboard route
            }
            hasRequested = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Add this line

            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("You're all set! - ${UserData.workoutStyle}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                "Submit your data to get your personalized plan.",
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        isLoading = true
                        hasRequested = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Submit", color = Color.White)
                }
            }

            responseMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    color = if (it == "Success") Color.Green else Color.Red
                )
            }
        }
    }
}

suspend fun sendSurveyToBackend(context: Context): String = withContext(Dispatchers.IO) {
    try {
        val sharedPrefs = SharedPrefManager(context)
        val token = sharedPrefs.getToken()
        if (token == null) return@withContext "User not authenticated"

        val surveyData = SurveyData(
            gender = UserData.gender ?: "",
            weight = (UserData.weight as? Int) ?: 0,
            height = (UserData.height as? Int) ?: 0,
            age = (UserData.age as? Int) ?: 0,
            goal = UserData.goal ?: "",
            fitnessLevel = UserData.fitnessLevel ?: "",
            activityTime = UserData.activityTime ?: "",
            activities = UserData.activities ?: emptyList(),
            healthConditions = UserData.healthConditions ?: emptyList(),
            barriers = UserData.barriers ?: emptyList(),
            dietType = UserData.dietType ?: "",
            workoutStyle = UserData.workoutStyle ?: ""
        )

        val response = RetrofitInstance.getApi().submitSurvey("Bearer $token", surveyData)
        return@withContext if (response.isSuccessful) "Success" else "Error: ${response.code()}"

    } catch (e: Exception) {
        sendDiscordMessage("", "Exception: ${e.message}")
        return@withContext "Exception: ${e.message}"
    }
}
