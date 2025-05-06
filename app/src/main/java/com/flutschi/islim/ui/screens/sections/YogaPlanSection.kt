// YogaPlanSection.kt
package com.flutschi.islim.ui.screens.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flutschi.islim.models.YogaActivity
import com.flutschi.islim.ui.viewmodels.YogaFitnessViewModel

@Composable
fun YogaPlanSection(
    selectedDay: String,
    viewModel: YogaFitnessViewModel = viewModel()
) {
    val yogaActivities by viewModel.yogaActivities.collectAsState()

    LaunchedEffect(selectedDay) {
        viewModel.setSelectedDay(selectedDay)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()  // ✅ Only fill width, NOT height
            .padding(16.dp)
    ) {
        Text("YOGA PLAN", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        yogaActivities.forEach { activity ->
            YogaActivityItem(activity)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun YogaActivityItem(activity: YogaActivity) {
    var showVideo by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(activity.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(activity.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { showVideo = true }) {
                Text("Play Video")
            }

            if (showVideo) {
                val context = LocalContext.current
                AndroidView(
                    factory = {
                        VideoView(context).apply {
                            setVideoURI(Uri.parse(activity.videoUrl))
                            setOnPreparedListener { it.start() }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
