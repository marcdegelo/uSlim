package com.flutschi.islim.workers

import android.content.Context
import androidx.work.*
import java.util.*
import java.util.concurrent.TimeUnit

object StepWorkerScheduler {

    fun scheduleMidnightWorker(context: Context) {
        val currentTimeMillis = System.currentTimeMillis()
        val now = Calendar.getInstance()
        val midnight = now.clone() as Calendar
        midnight.set(Calendar.HOUR_OF_DAY, 0)
        midnight.set(Calendar.MINUTE, 5)
        midnight.set(Calendar.SECOND, 0)

        if (midnight.timeInMillis < currentTimeMillis) {
            midnight.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = midnight.timeInMillis - currentTimeMillis

        val workRequest = OneTimeWorkRequestBuilder<MidnightStepSaverWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("MidnightStepSaver")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "MidnightStepSaverWork",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}
