package com.flutschi.islim.ui.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.flutschi.islim.ui.screens.*
import com.flutschi.islim.ui.screens.survey.*
import com.flutschi.islim.snackbar.SnackbarManager
import com.flutschi.islim.ui.map.MapScreen
import com.flutschi.islim.ui.screens.shop.ShopScreen
import com.flutschi.islim.utils.GLOBALS
import com.flutschi.islim.workers.StepWorkerScheduler
import com.google.android.gms.maps.MapView
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    LaunchedEffect(true) {
        checkForUpdate(context)
        StepWorkerScheduler.scheduleMidnightWorker(context)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 🔐 Auth
        composable("signup") { SignUpScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("reset_password") { ResetPasswordScreen() }

        // 🏠 Main App
        composable("mainContainer") { MainContainer(navController) }
        // 🏠 Update
        composable("UpdateRequiredScreen") { UpdateRequiredScreen() }

        // 🏠 Map
        composable("mapScreen") { MapScreen() }

        // Shop

        composable("exchange") { ExchangeScreen() }
        composable("shop") { ShopScreen() }


        // 📋 Survey flow
        composable("surveyGender") { SurveyGenderScreen(navController) }
        composable("surveyWeight") { SurveyWeightScreen(navController) }
        composable("surveyHeight") { SurveyHeightScreen(navController) }
        composable("surveyAge") { SurveyAgeScreen(navController) }
        composable("surveyGoals") { SurveyGoalScreen(navController) }
        composable("surveyFitnessLevel") { SurveyFitnessLevelScreen(navController) }
        composable("surveyActivityTime") { SurveyActivityTimeScreen(navController) }
        composable("surveyActivityPreferences") { SurveyActivityPreferenceScreen(navController) }
        composable("surveyHealthIssues") { SurveyHealthIssuesScreen(navController) }
        composable("surveyBarriers") { SurveyBarriersScreen(navController) }
        composable("surveyDietType") { SurveyDietTypeScreen(navController) }
        composable("surveyWorkoutStyle") { SurveyWorkoutStyleScreen(navController) }
        composable("surveySubmit") { SurveySubmitScreen(navController) }
    }
}

suspend fun checkForUpdate(context: Context): Boolean {
    val currentVersionCode = GLOBALS.VERSION

    return try {
        val url = URL("${GLOBALS.API_BASE_URL}/api/version")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val stream = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(stream)
            val latestVersionCode = json.getInt("versionCode")
            val updateRequired = json.getBoolean("updateRequired")

            if (latestVersionCode > currentVersionCode && updateRequired) {
                SnackbarManager.showMessage("🚀 New version available! Please update the app.")

                // OPTIONAL: Auto open update link
                val updateUrl = json.getString("updateUrl")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)

                return true
            }
        }
        false
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

