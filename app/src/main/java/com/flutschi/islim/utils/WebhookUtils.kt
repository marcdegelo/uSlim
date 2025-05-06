package com.flutschi.islim.utils

import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

object WebhookUtils {

    private val client: OkHttpClient by lazy { createUnsafeClient() }

    private fun createUnsafeClient(): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    fun sendDiscordMessage(webhookUrl: String, message: String) {
        val jsonPayload = """{"content": "$message"}"""
        val mediaType = "application/json".toMediaType()
        val requestBody = RequestBody.create(mediaType, jsonPayload)

        val finalWebhookUrl = webhookUrl.takeIf { it.isNotEmpty() } ?: GLOBALS.DISCORD_URL

        val request = Request.Builder()
            .url(finalWebhookUrl)
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WebhookUtils", "Failed to send message: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.d("WebhookUtils", "Message sent successfully: $responseBody")
                } else {
                    Log.e("WebhookUtils", "Failed with status: ${response.code} - Response: $responseBody")
                }
            }
        })
    }

    fun sendWebhookMessage(webhookUrl: String, payload: Map<String, String>, callback: (Boolean) -> Unit) {
        val jsonObject = JSONObject()
        for ((key, value) in payload) {
            jsonObject.put(key, value)
        }

        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())

        val request = Request.Builder()
            .url(webhookUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("WebhookUtils", "Webhook request failed: ${e.message}")
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.d("WebhookUtils", "Webhook request successful: ${response.body?.string()}")
                    callback(true)
                } else {
                    Log.e("WebhookUtils", "Webhook request failed with status: ${response.code}")
                    callback(false)
                }
            }
        })
    }
}
