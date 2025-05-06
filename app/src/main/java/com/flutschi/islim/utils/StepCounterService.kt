package com.flutschi.islim.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class StepCounterService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var initialStepCount: Float? = null
    private var intentUserId: Int? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("StepCounterService", "✅ Service created")

        // 🔔 Setup Foreground Notification
        val channelId = "step_counter_channel"
        val channelName = "Step Counter"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Step Counter Active")
            .setContentText("Tracking your steps in the background.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            Log.e("StepCounterService", "🚫 No Step Counter sensor available!")
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intentUserId = intent?.getIntExtra("user_id", -1)?.takeIf { it != -1 }
        Log.d("StepCounterService", "📥 Received userId in intent: $intentUserId")
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {

            val currentDate = StepStorage.getTodayDate()
            val storedDate = StepStorage.getStoredDate(this)

            // Check if the day has changed
            if (currentDate != storedDate) {
                Log.d("StepCounterService", "📆 New day detected: $currentDate")
                initialStepCount = event.values[0]
                StepStorage.saveInitialStepCount(this, initialStepCount!!)
                StepStorage.saveStoredDate(this, currentDate)
            }

            if (initialStepCount == null) {
                initialStepCount = StepStorage.getInitialStepCount(this)
                if (initialStepCount == null) {
                    initialStepCount = event.values[0]
                    StepStorage.saveInitialStepCount(this, initialStepCount!!)
                    StepStorage.saveStoredDate(this, currentDate)
                }
            }

            val stepsToday = maxOf(0, (event.values[0] - (initialStepCount ?: 0f)).toInt())

            val userID = intentUserId ?: SharedPrefManager(this).getUserId()

            userID.takeIf { it != -1 }?.let {
                StepStorage.saveStepsToStorage(this, stepsToday, it)
            } ?: Log.i("saveSteps", "❌ User ID is invalid (null or -1), skipping save")

            val intent = Intent("STEP_UPDATE")
            intent.putExtra("steps", stepsToday)
            sendBroadcast(intent)

        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        Log.d("StepCounterService", "🛑 Service destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

