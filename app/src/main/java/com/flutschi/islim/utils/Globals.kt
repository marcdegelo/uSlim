package com.flutschi.islim.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.flutschi.islim.camera.CameraUploader
import com.flutschi.islim.models.UserData

object GLOBALS {

    const val PYTHON_BASE_URL = "https://flutscherino.eu.pythonanywhere.com/uSlim/"
    const val DEBUG_URL = "https://188.83.22.160:8080/uSlim/"
    var API_BASE_URL = DEBUG_URL

    const val DISCORD_URL = "https://discord.com/api/webhooks/1351878592623153202/WOkqYWYlb1K4WmlGVCssNXnx9SC8WO_MaJESkeT8QtIdknce6y8jmhXtFq7Tx5l_lsIq"
    const val GOOGLE_CLIENT_ID = "393366931388-hutniuobiks7vlhoee4ujho4k7pbb768.apps.googleusercontent.com" // https://console.cloud.google.com/apis/credentials?inv=1&invt=AbqWyQ&project=islim-c39e0

    const val DEV_ANDROID_ID = "e2edbee1c35dcdb5"

    const val APPSTART = "mainContainer" // surveySubmit // surveyGender // mainContainer
    const val APPSTARTSIGNUP = "mainContainer" // signUp

    const val VERSION = 1

    object GlobalAppState {
        val completedMeals = mutableStateListOf<Int>()
        val completedWorkouts = mutableStateListOf<Int>()
    }

    fun initBaseUrl(context: Context) {
        val deviceId = getDeviceId(context)
        API_BASE_URL = if (deviceId == DEV_ANDROID_ID) DEBUG_URL else PYTHON_BASE_URL
        API_BASE_URL = DEBUG_URL

        Log.i("GLOBALS", "Device ID: $deviceId")
        Log.i("GLOBALS", "Using API_BASE_URL: $API_BASE_URL")
    }

    lateinit var cameraUploader: CameraUploader

}
