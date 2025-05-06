package com.flutschi.islim.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.flutschi.islim.utils.SharedPrefManager
import com.flutschi.islim.utils.StepStorage

class MidnightStepSaverWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val sharedPref = SharedPrefManager(context)
        val userId = sharedPref.getUserId() ?: return Result.failure()

        val steps = StepStorage.getTodaySteps(context)
        StepStorage.saveStepsToStorage(context, steps, userId)

        // 🔁 Schedule again for next day
        StepWorkerScheduler.scheduleMidnightWorker(context)

        return Result.success()
    }
}
