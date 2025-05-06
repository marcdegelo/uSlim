package com.flutschi.islim.backend

import android.util.Log
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.models.RouteRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

suspend fun sendRouteToBackend(
    token: String,
    totalDistance: Double,
    elapsedTime: Long,
    calories: Double,
    steps: Int,
    path: List<LatLng>,
    start: LatLng?,
    end: LatLng?
): Boolean {
    val api = RetrofitInstance.getApi()

    val route = RouteRequest(
        distance_km = totalDistance / 1000,
        duration_sec = elapsedTime / 1000,
        calories = calories,
        steps = steps,
        path = path.map { listOf(it.latitude, it.longitude) },
        start = start?.let { listOf(it.latitude, it.longitude) },
        end = end?.let { listOf(it.latitude, it.longitude) }
    )

    Log.i("RoutePayload", "Sending route with token: Bearer $token")

    return withContext(Dispatchers.IO) {
        try {
            val response = api.saveRoute("Bearer $token", route)
            if (response.isSuccessful) {
                Log.i("RouteUpload", "Route successfully uploaded.")
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("RouteUpload", "Failed to upload route. Code: ${response.code()}, Error: $errorBody")
                false
            }
        } catch (e: IOException) {
            Log.e("RouteUpload", "IO Exception", e)
            false
        } catch (e: HttpException) {
            Log.e("RouteUpload", "HTTP Exception", e)
            false
        } catch (e: Exception) {
            Log.e("RouteUpload", "Unexpected Exception", e)
            false
        }
    }
}
