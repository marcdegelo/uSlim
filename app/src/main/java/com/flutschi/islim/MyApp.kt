package com.flutschi.androidapp

import android.app.Application
import android.util.Log
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.utils.GLOBALS
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import com.google.firebase.FirebaseApp

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        RetrofitInstance.init(applicationContext)
        Log.i("RetroInit", GLOBALS.API_BASE_URL)

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
            Log.d("MyApp", "🔥 Firebase initialized in Application class")

        } else {
            Log.d("MyApp", "✅ Firebase already initialized")
        }

    }
}
