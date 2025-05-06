package com.flutschi.islim.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            "WATER_REMINDER" -> {
                Log.d("AlarmReceiver", "🚰 Time to hydrate!")
                // TODO: Show a hydration notification
            }
            "EXERCISE_REMINDER" -> {
                Log.d("AlarmReceiver", "🏋️ Time to move!")
                // TODO: Show a fitness reminder
            }
            "RESET_STEPS" -> {
                Log.d("AlarmReceiver", "🧼 Resetting step data for the day")
                // TODO: Trigger daily step rollover
            }
            "TEST_ALERT" -> {
                Log.d("AlarmReceiver", "⏰ 5-minute alarm triggered!")
                sendDiscordMessage("", "Test Alert")
                NotificationUtils.showNotification(
                    context = context,
                    title = "🚨 iSlim Reminder",
                    message = "Time to move, drink water, or check your progress!"
                )
            }
        }
    }
}
