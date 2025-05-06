package com.flutschi.islim.models

data class RouteRequest(
    val distance_km: Double,
    val duration_sec: Long,
    val calories: Double,
    val steps: Int,
    val path: List<List<Double>>,
    val start: List<Double>?,
    val end: List<Double>?
)