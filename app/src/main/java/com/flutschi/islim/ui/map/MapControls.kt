package com.flutschi.islim.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flutschi.islim.ui.theme.AppColors
import com.google.android.gms.maps.model.LatLng

@Composable
fun BoxScope.MapControls( // 👈 Make it part of BoxScope
    isTracking: Boolean,
    sessionStarted: Boolean,
    startLocation: LatLng?,
    pathPoints: List<LatLng>,
    elapsedTime: Long,
    totalDistance: Double,
    pace: Double,
    calories: Double,
    sessionSteps: Int,
    onStartStopClick: () -> Unit,
    onStopClick: () -> Unit
) {
    val minutes = (elapsedTime / 1000) / 60
    val seconds = (elapsedTime / 1000) % 60

    Column(
        modifier = Modifier
            .align(Alignment.BottomCenter) // ✅ Works now because we're in BoxScope
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isTracking && startLocation == null) {
            Text("Waiting for GPS...", color = MaterialTheme.colorScheme.error, fontSize = 16.sp)
        }

        Text(text = String.format("%02d:%02d", minutes, seconds), fontSize = 32.sp)
        Text(text = "%.2f km".format(totalDistance / 1000), fontSize = 20.sp)
        Text(text = "%.2f s/km".format(pace), fontSize = 20.sp)
        Text(text = "%.1f Kcal".format(calories), fontSize = 20.sp)
        Text(text = "$sessionSteps steps", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onStartStopClick,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.gradientStart) // 🎨
            ) {
                Text(if (isTracking) "Pause" else "Start")
            }

            Button(
                onClick = onStopClick,
                enabled = sessionStarted && startLocation != null && pathPoints.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.gradientEnd) // 🎨
            ) {
                Text("Stop")
            }

        }
    }
}
