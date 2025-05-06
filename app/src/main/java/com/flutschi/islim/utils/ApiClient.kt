package com.flutschi.islim.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

object ApiClient {
    private val client = OkHttpClient()

    /**
     * Perform a GET request.
     * @param url The endpoint URL.
     * @return JSONObject? - Response JSON or null if failed.
     */
    suspend fun get(url: String): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).get().build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    response.body?.let { body ->
                        return@withContext JSONObject(body.string())
                    }
                } else {
                    Log.e("ApiClient", "❌ GET request failed: ${response.code}")
                }
            } catch (e: IOException) {
                Log.e("ApiClient", "❌ Error in GET request", e)
            }
            return@withContext null
        }
    }

    /**
     * Perform a POST request.
     * @param url The endpoint URL.
     * @param jsonBody The request body in JSON format.
     * @return JSONObject? - Response JSON or null if failed.
     */
    suspend fun post(url: String, jsonBody: JSONObject): JSONObject? {
        return withContext(Dispatchers.IO) {
            try {
                val body = RequestBody.create("application/json".toMediaTypeOrNull(), jsonBody.toString())
                val request = Request.Builder().url(url).post(body).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    response.body?.let { body ->
                        return@withContext JSONObject(body.string())
                    }
                } else {
                    Log.e("ApiClient", "❌ POST request failed: ${response.code}")
                }
            } catch (e: IOException) {
                Log.e("ApiClient", "❌ Error in POST request", e)
            }
            return@withContext null
        }
    }
}
