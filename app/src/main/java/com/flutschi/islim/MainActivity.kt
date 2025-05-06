package com.flutschi.islim

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.api.VersionRequest
import com.flutschi.islim.camera.CameraUploader
import com.flutschi.islim.models.UserData
import com.flutschi.islim.models.UserDataResponse
import com.flutschi.islim.ui.navigation.AppNavHost
import com.flutschi.islim.utils.*
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import kotlinx.coroutines.*
import androidx.navigation.compose.rememberNavController
import com.flutschi.islim.utils.GLOBALS
import com.flutschi.islim.utils.GLOBALS.DEBUG_URL
import com.flutschi.islim.utils.GLOBALS.DEV_ANDROID_ID

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*

class MainActivity : ComponentActivity(), OnMapReadyCallback {

    private val REQUIRED_PERMISSIONS = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
        add(Manifest.permission.CAMERA)
        add(Manifest.permission.ACCESS_FINE_LOCATION)
    }.toTypedArray()


    lateinit var cameraUploader: CameraUploader

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var onPermissionsGranted: (() -> Unit)? = null

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val deniedPermissions = permissions.filterValues { !it }.keys

        if (deniedPermissions.isNotEmpty()) {
            Log.e("Permissions", "❌ Missing permissions: $deniedPermissions")
            sendDiscordMessage("", "❌ Missing permissions: $deniedPermissions")
            Toast.makeText(this, "Permissions required for step tracking", Toast.LENGTH_LONG).show()
        } else {
            Log.d("Permissions", "✅ All permissions granted!")
            onPermissionsGranted?.invoke()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = MapView(this)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        cameraUploader = CameraUploader(this)
        cameraUploader.registerCameraLauncher()
        GLOBALS.cameraUploader = cameraUploader

        try {
            val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val sharedPrefManager = SharedPrefManager(this)

            setContent {
                val context = this@MainActivity
                val navController = rememberNavController()
                val sharedPrefs = remember { SharedPrefManager(context) }

                var isLoading by remember { mutableStateOf(true) }
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    try {
                        val token = sharedPrefs.getToken()
                        Log.i("MainActivity", "🔑 Retrieved token: $token")

                        val currentVersion = 1
                        val versionResponse = try {
                            RetrofitInstance.getApi().checkVersion(VersionRequest(currentVersion)).body()
                        } catch (e: Exception) {
                            Log.i("Version Check", "💥 Version check exception: ${e.message}")
                            null
                        }

                        if (versionResponse?.force_update == true && currentVersion < versionResponse.latest_version) {
                            startDestination = "UpdateRequiredScreen"
                            isLoading = false
                            return@LaunchedEffect
                        }

                        if ((!sharedPrefs.isLoggedIn() || token.isNullOrBlank())) {
                            Log.i("Token Check", "❌ Token is null or user is not logged in.")
                            startDestination = GLOBALS.APPSTARTSIGNUP
                        } else {
                            sharedPrefs.saveToken(token)
                            val userData = getUserDataFromBackend(this@MainActivity, token)

                            if (userData != null) {
                                Log.i("UserData", "✅ User data loaded successfully: ${userData}")
                                UserData.load(userData)
                                sharedPrefs.saveUserData(UserData.toModel())

                                userData.id?.let { userId ->
                                    loadCompletedData(userId)

                                    StepStorage.getTodaySteps(context).also { stepsToday ->
                                        if (stepsToday > 0) {
                                            StepStorage.saveStepsToStorage(context, stepsToday, userId)
                                            StepStorage.saveStoredDate(context, StepStorage.getTodayDate())
                                        }
                                        UserData.stepsToday = stepsToday
                                    }

                                    onPermissionsGranted = {
                                        val stepIntent = Intent(context, StepCounterService::class.java).apply {
                                            putExtra("user_id", userId)
                                        }
                                        ContextCompat.startForegroundService(context, stepIntent)
                                    }

                                    if (hasAllPermissions()) {
                                        onPermissionsGranted?.invoke()
                                    } else {
                                        requestPermissionsLauncher.launch(REQUIRED_PERMISSIONS)
                                    }
                                    startDestination = if (UserData.isSurveyComplete()) "mainContainer" else GLOBALS.APPSTART
                                } ?: run {
                                    startDestination = GLOBALS.APPSTART
                                }
                            } else {
                                Log.i("MainActivity", "❌ Invalid token or user data is null. Redirecting to signup.")
                                startDestination = GLOBALS.APPSTART
                            }
                        }
                    } catch (e: Exception) {
                        // sendDiscordMessage("", "💥 Startup error: ${e.message}")
                        Log.i("MainActivity", "<> Startup error: ${e.message}")
                        startDestination = GLOBALS.APPSTART
                    }

                    isLoading = false
                }

                // isLoading = true
                Log.i("MainActivity", "✅ isLoading set to: $isLoading")

                if (isLoading || startDestination == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column {
                        // startDestination = "login" // <-- if you want to start with another screen
                        AppNavHost(navController, startDestination!!)
                        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())
                    }
                }
            }

        } catch (e: Exception) {
            sendDiscordMessage("", "Fehler beim Starten der App: ${e.message}")
            Log.e("MainActivity", "Fehler beim Starten der App: ${e.message}")
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Beispiel: Marker hinzufügen
        val location = LatLng(37.7749, -122.4194) // San Francisco
        googleMap.addMarker(MarkerOptions().position(location).title("San Francisco"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private fun hasAllPermissions(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}


suspend fun getUserDataFromBackend(context: android.content.Context, token: String): UserDataResponse? {
    return try {
        val response = RetrofitInstance.getApi().getUserData("Bearer $token")
        if (response.isSuccessful) {
            response.body()
        } else null
    } catch (e: Exception) {
        val deviceId = getDeviceId(context)
        if (deviceId == DEV_ANDROID_ID) {
            Log.d("getUserDataFromBackend", "🔄 DEBUG-Modus aktiv: API_BASE_URL auf PYTHON_BASE_URL geändert.")
            GLOBALS.API_BASE_URL = GLOBALS.PYTHON_BASE_URL
        } else {
            Log.d("getUserDataFromBackend", "🔄 DEBUG-Modus nicht aktiv: API_BASE_URL bleibt unverändert.")
        }
        sendDiscordMessage("", "💥 Exception fetching user data: ${e.message}")
        null
    }
}

suspend fun loadCompletedData(userId: Int) {
    try {
        val workoutsResponse = RetrofitInstance.getApi().getCompletedWorkouts(userId)
        val mealsResponse = RetrofitInstance.getApi().getCompletedMeals(userId)

        withContext(Dispatchers.Main) {
            if (workoutsResponse.isSuccessful) {
                GLOBALS.GlobalAppState.completedWorkouts.clear()
                GLOBALS.GlobalAppState.completedWorkouts.addAll(
                    workoutsResponse.body()?.completed_workouts?.map { it.toInt() } ?: emptyList()
                )
            } else {
                Log.e("MainActivity", "❌ Failed loading workouts: ${workoutsResponse.code()}")
            }

            if (mealsResponse.isSuccessful) {
                GLOBALS.GlobalAppState.completedMeals.clear()
                GLOBALS.GlobalAppState.completedMeals.addAll(mealsResponse.body()?.completed_meals ?: emptyList())
            } else {
                Log.e("MainActivity", "❌ Failed loading meals: ${mealsResponse.code()}")
            }
        }
    } catch (e: Exception) {
        Log.e("MainActivity", "💥 Error loading completed data: ${e.localizedMessage}")
    }
}
