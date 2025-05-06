package com.flutschi.islim.utils

import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import com.flutschi.islim.snackbar.SnackbarManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import com.flutschi.islim.utils.GLOBALS
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

object AuthUtils {

    suspend fun sendGoogleTokenToBackend(idToken: String, context: Context, navController: NavController): Boolean {
        return sendTokenToBackend(idToken, "google-login", context, navController)
    }

    suspend fun sendFacebookTokenToBackend(idToken: String, context: Context, navController: NavController): Boolean {
        return sendTokenToBackend(idToken, "facebook-login", context, navController)
    }

    suspend fun sendAppleTokenToBackend(idToken: String, context: Context, navController: NavController): Boolean {
        return sendTokenToBackend(idToken, "apple-login", context, navController)
    }

    private suspend fun sendTokenToBackend(idToken: String, endpoint: String, context: Context, navController: NavController): Boolean {
        return withContext(Dispatchers.IO) {
            val logID = "sendTokenToBackend"
            val url = "${GLOBALS.API_BASE_URL}/auth/$endpoint"
            val client = OkHttpClient()
            val requestBody = JSONObject().apply {
                put("id_token", idToken)
            }
            Log.i("sendTokenToBackend", url)

            try {
                val request = Request.Builder()
                    .url(url)
                    .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "{}")

                Log.i(logID, "Request JSON: $jsonResponse")

                if (response.isSuccessful) {
                    val token = jsonResponse.optString("token")
                    val user = jsonResponse.optJSONObject("user")
                    val username = user?.optString("username") ?: ""
                    val userId = user?.getInt("id")   // 👈 Must be returned by Flask

                    if (token.isNotEmpty() && username.isNotEmpty()) {
                        SharedPrefManager(context).saveUser(token, username)
                        if (userId != null) {
                            SharedPrefManager(context).saveUserId(userId)
                        }

                        withContext(Dispatchers.Main) {
                            // sendDiscordMessage("", "lOGGED IN")
                            Log.i(logID, "home")
                            navController.navigate("mainContainer") {
                                popUpTo("signup") { inclusive = true }
                            }
                        }

                        return@withContext true
                    }
                } else {
                    Log.e("AuthUtils", "Social login failed: $responseBody")
                    sendDiscordMessage("", "Social login failed: $responseBody")

                }
            } catch (e: IOException) {
                Log.e("AuthUtils", "Network error: ${e.message}")
                sendDiscordMessage("", "Network error: ${e.message}")

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("AuthUtils", "Request.Builder(): ${e.message}")
                sendDiscordMessage("", "Request.Builder(): ${e.message}")

            }


            return@withContext false
        }
    }

    suspend fun loginWithEmail(email: String, password: String, context: Context, navController: NavController) {
        withContext(Dispatchers.IO) {
            val url = "${GLOBALS.API_BASE_URL}/auth/login"
            val client = OkHttpClient()
            val requestBody = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val request = Request.Builder()
                .url(url)
                .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                Log.d("Backend Auth", "Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val token = jsonResponse.optString("token", "")

                    if (token.isNotEmpty()) {
                        SharedPrefManager(context).saveToken(token)
                        withContext(Dispatchers.Main) {
                            navController.navigate("mainContainer")
                        }
                    }
                } else {
                    Log.e("Backend Auth", "Login Failed: $responseBody")
                    sendDiscordMessage("", "Login Failed: $responseBody")

                }
            } catch (e: IOException) {
                Log.e("Backend Auth", "Error: ${e.message}")
                sendDiscordMessage("", "Error: ${e.message}")

            }
        }
    }

    suspend fun registerUser(username: String, email: String, password: String, context: Context, navController: NavController) {
        withContext(Dispatchers.IO) {
            val url = "${GLOBALS.API_BASE_URL}/auth/register"
            val client = OkHttpClient()
            val requestBody = JSONObject().apply {
                put("username", username)
                put("email", email)
                put("password", password)
            }

            val request = Request.Builder()
                .url(url)
                .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                Log.d("Backend Auth", "Register Response: $responseBody")
                sendDiscordMessage("", "Register Response: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)
                    val token = jsonResponse.optString("token", "")

                    if (token.isNotEmpty()) {
                        SharedPrefManager(context).saveToken(token)
                        withContext(Dispatchers.Main) {
                            navController.navigate("mainContainer")
                        }
                    }
                } else {
                    Log.e("Backend Auth", "Registration Failed: $responseBody")
                    sendDiscordMessage("", "Registration Failed: $responseBody")

                }
            } catch (e: IOException) {
                Log.e("Backend Auth", "Error: ${e.message}")
                sendDiscordMessage("", "Error: ${e.message}")

            }
        }
    }

    fun getGoogleSignInClient(context: Context) =
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(GLOBALS.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build()
        )

    fun handleGoogleSignInResult(
        resultIntent: android.content.Intent?,
        context: Context,
        navController: NavController,
        coroutineScope: kotlinx.coroutines.CoroutineScope
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(resultIntent)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken

            Log.d("GoogleAuth", "Google Sign-In successful. ID Token: $idToken")
            sendDiscordMessage("", "Google Sign-In successful.")

            if (idToken != null) {
                coroutineScope.launch {
                    val isSuccess = sendGoogleTokenToBackend(idToken, context, navController)
                    Log.d("GoogleAuth", "sendGoogleTokenToBackend $isSuccess")
                    sendDiscordMessage("", "sendGoogleTokenToBackend $isSuccess")

                    if (isSuccess) {
                        withContext(Dispatchers.Main) {
                            navController.navigate("mainContainer") {
                                popUpTo("signup") { inclusive = true }
                            }
                            SnackbarManager.showMessage("Welcome!")
                        }
                    }
                }
            } else {
                Log.e("GoogleAuth", "ID Token is null. Check your GoogleSignInOptions setup.")
                sendDiscordMessage("", "ID Token is null. Check your GoogleSignInOptions setup.")
            }
        } catch (e: ApiException) {
            Log.e("GoogleAuth", "Google Sign-In failed. Status Code: ${e.statusCode}", e)
            sendDiscordMessage("", "Google Sign-In failed. Status Code: ${e.statusCode}")
        } catch (e: Exception) {
            Log.e("GoogleAuth", "Unexpected error during Google Sign-In", e)
            sendDiscordMessage("", "Unexpected error during Google Sign-In")
        }
    }

}
