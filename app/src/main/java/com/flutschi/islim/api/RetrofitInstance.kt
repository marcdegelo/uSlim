package com.flutschi.islim.api

import android.content.Context
import android.util.Log
import com.flutschi.islim.utils.GLOBALS
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object RetrofitInstance {
    private var retrofit: Retrofit? = null
    private var api: ApiService? = null

    fun init(context: Context) {
        GLOBALS.initBaseUrl(context)

        val baseUrl = GLOBALS.API_BASE_URL

        Log.i("retrofit init", "Base URL: $baseUrl")

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = createUnsafeOkHttpClient()
            .addInterceptor(logging)
            .retryOnConnectionFailure(false)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit!!.create(ApiService::class.java)
    }

    fun getApi(): ApiService {
        return api ?: throw IllegalStateException("Retrofit not initialized. Call RetrofitInstance.init(context) first.")
    }

    private fun createUnsafeOkHttpClient(): OkHttpClient.Builder {
        try {
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

        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}
