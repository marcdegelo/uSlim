package com.flutschi.islim.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.models.StepRequest
import okhttp3.*
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson
import com.flutschi.islim.models.UserData
import com.flutschi.islim.models.UserDataModel

class SharedPrefManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)


    fun saveUserData(userData: UserDataModel) {
        val json = Gson().toJson(userData)
        prefs.edit().putString("user_data", json).apply()
    }

    fun getUserData(): UserDataModel? {
        val json = prefs.getString("user_data", null)
        return if (json != null) Gson().fromJson(json, UserDataModel::class.java) else null
    }

    fun saveUser(token: String, username: String?) { // Accept nullable username
        prefs.edit().putString("TOKEN", token).putString("USERNAME", username ?: "Guest").apply()
    }

    fun getUserId(): Int? {
        val id = prefs.getInt("USER_ID", -1)
        return if (id != -1) id else null
    }

    fun getUserXP(): String? {
        return prefs.getString("USER_XP", null)
    }

    fun saveUserXP(xp: Int) {
        prefs.edit().putInt("USER_XP", xp).apply()
    }

    fun saveUserId(id: Int) {
        prefs.edit().putInt("USER_ID", id).apply()
    }

    fun saveToken(token: String) { // ✅ Add this method
        prefs.edit().putString("TOKEN", token).apply()
    }

    fun getProfileImage(): String? {
        return prefs.getString("PROFILE_IMAGE_URL", null)
    }

    fun saveProfileImageUrl(url: String) {
        prefs.edit().putString("PROFILE_IMAGE_URL", url).apply()
    }

    fun getToken(): String? {
        return prefs.getString("TOKEN", null)
    }

    fun getUsername(): String? {
        return prefs.getString("USERNAME", null)
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

}

object StepStorage {
    private const val PREFS_NAME = "step_data"

    fun saveStepsToStorage(context: Context, steps: Int, userId: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = getCurrentDate()
        prefs.edit().putInt(today, steps).apply()

        // ✅ Send steps to backend
        sendStepsToBackend(context, userId, steps)
    }

    fun getStepsFromStorage(context: Context): List<Pair<String, Int>> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.all.mapNotNull {
            val date = it.key
            val steps = it.value as? Int
            if (steps != null) date to steps else null
        }.sortedBy { it.first }
    }

    fun saveInitialStepCount(context: Context, count: Float) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = getCurrentDate()
        prefs.edit()
            .putFloat("INITIAL_STEP_COUNT", count)
            .putString("INITIAL_STEP_DATE", today)
            .apply()
    }

    fun getInitialStepCount(context: Context): Float? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedDate = prefs.getString("INITIAL_STEP_DATE", null)
        val today = getCurrentDate()

        return if (savedDate == today) {
            val count = prefs.getFloat("INITIAL_STEP_COUNT", -1f)
            if (count == -1f) null else count
        } else {
            null // New day → force reset
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getTodaySteps(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val today = getCurrentDate()
        return prefs.getInt(today, 0)
    }

    private fun sendStepsToBackend(context: Context, userId: Int, steps: Int) {
        val retrofit = RetrofitInstance.getApi() // ✅ correct way
        val stepRequest = StepRequest(userId, steps)

        retrofit.sendSteps(stepRequest).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("StepStorage", "Retrofit failed: ${t.message}")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            }
        })
    }

    // 🆕 Helper functions for daily reset
    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getStoredDate(context: Context): String? {
        val prefs = context.getSharedPreferences("step_storage", Context.MODE_PRIVATE)
        return prefs.getString("last_saved_date", null)
    }

    fun saveStoredDate(context: Context, date: String) {
        val prefs = context.getSharedPreferences("step_storage", Context.MODE_PRIVATE)
        prefs.edit().putString("last_saved_date", date).apply()
    }

}
