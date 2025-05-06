package com.flutschi.islim.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.flutschi.islim.location.LocationTracker
import com.flutschi.islim.utils.SharedPrefManager
import com.flutschi.islim.utils.StepStorage
import com.flutschi.islim.backend.sendRouteToBackend
import com.flutschi.islim.ui.screens.MapControls
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.*

@Composable
fun MapScreen() {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = remember { MapView(context) }

    val locationTracker = remember { LocationTracker(context) }

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    var isTracking by remember { mutableStateOf(false) }
    var pathPoints by remember { mutableStateOf(listOf<LatLng>()) }
    var totalDistance by remember { mutableStateOf(0.0) }
    var elapsedTime by remember { mutableStateOf(0L) }
    var permissionGranted by remember { mutableStateOf(false) }
    var sessionStarted by remember { mutableStateOf(false) }

    var startLocation by remember { mutableStateOf<LatLng?>(null) }
    var endLocation by remember { mutableStateOf<LatLng?>(null) }

    val sharedPrefManager = remember { SharedPrefManager(context) }
    val token = sharedPrefManager.getToken()

    var stepsAtStart by remember { mutableStateOf(0) }
    var sessionSteps by remember { mutableStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> permissionGranted = granted }
    )

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    DisposableEffect(mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) = mapView.onCreate(null)
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isTracking) {
        while (isTracking) {
            delay(1000)
            elapsedTime += 1000
        }
    }

    LaunchedEffect(isTracking) {
        if (isTracking) {
            locationTracker.startLocationUpdates { location ->
                val newPoint = LatLng(location.latitude, location.longitude)

                if (pathPoints.isNotEmpty()) {
                    val last = pathPoints.last()
                    val result = FloatArray(1)
                    Location.distanceBetween(
                        last.latitude, last.longitude,
                        newPoint.latitude, newPoint.longitude,
                        result
                    )
                    totalDistance += result[0]
                }

                pathPoints = pathPoints + newPoint

                if (startLocation == null) {
                    startLocation = newPoint
                }
            }
        } else {
            locationTracker.stopLocationUpdates()
        }
    }

    val minutes = (elapsedTime / 1000) / 60
    val seconds = (elapsedTime / 1000) % 60
    val pace = if (totalDistance > 0) elapsedTime / 1000 / (totalDistance / 1000) else 0.0
    val calories = totalDistance * 0.05

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = {
            mapView.apply {
                getMapAsync { map ->
                    googleMap = map
                    if (ActivityCompat.checkSelfPermission(
                            context, Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        map.isMyLocationEnabled = true
                    }
                }
            }
        }, modifier = Modifier.fillMaxSize())

        LaunchedEffect(pathPoints) {
            if (googleMap != null && pathPoints.size >= 2) {
                googleMap!!.clear()
                googleMap!!.addPolyline(
                    PolylineOptions().addAll(pathPoints).color(android.graphics.Color.BLUE).width(10f)
                )
                googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(pathPoints.last(), 17f))
            }
        }

        MapControls(
            isTracking = isTracking,
            sessionStarted = sessionStarted,
            startLocation = startLocation,
            pathPoints = pathPoints,
            elapsedTime = elapsedTime,
            totalDistance = totalDistance,
            pace = pace,
            calories = calories,
            sessionSteps = sessionSteps,
            onStartStopClick = {
                isTracking = !isTracking
                if (isTracking) {
                    sessionStarted = true
                    stepsAtStart = StepStorage.getTodaySteps(context)
                    if (pathPoints.isNotEmpty()) {
                        startLocation = pathPoints.first()
                    }
                }
            },
            onStopClick = {
                isTracking = false
                sessionStarted = false
                endLocation = pathPoints.lastOrNull()
                sessionSteps = StepStorage.getTodaySteps(context) - stepsAtStart

                CoroutineScope(Dispatchers.Main).launch {
                    if (token == null) {
                        Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (startLocation == null || pathPoints.isEmpty()) {
                        Toast.makeText(context, "Start location not yet available", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    val success = sendRouteToBackend(
                        token = token,
                        totalDistance = totalDistance,
                        elapsedTime = elapsedTime,
                        calories = calories,
                        steps = sessionSteps,
                        path = pathPoints,
                        start = startLocation,
                        end = endLocation
                    )

                    Toast.makeText(context, if (success) "Route saved!" else "Failed to save route", Toast.LENGTH_SHORT).show()

                    pathPoints = emptyList()
                    totalDistance = 0.0
                    elapsedTime = 0L
                    startLocation = null
                    endLocation = null
                    sessionSteps = 0
                    stepsAtStart = 0
                }
            }
        )
    }
}
