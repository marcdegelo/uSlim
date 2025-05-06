package com.flutschi.islim.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.flutschi.islim.models.UserData

@Composable
fun rememberStepCounter(): State<Int> {
    val context = LocalContext.current
    val steps = remember { mutableStateOf(0) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val stepCount = intent?.getIntExtra("steps", 0) ?: 0
                steps.value = stepCount
                UserData.stepsToday = stepCount
            }
        }

        val filter = IntentFilter("STEP_UPDATE")
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    return steps
}

