package com.flutschi.islim.models

data class UserDataResponse(
    val gender: String?,
    val weight: Float?,
    val height: String?,
    val age: String?,
    val goal: String?,
    val fitnessLevel: String?,
    val activityTime: String?,
    val activities: List<String>?,
    val healthConditions: List<String>?,
    val barriers: List<String>?,
    val dietType: String?,
    val workoutStyle: String?,
    val id: Int?,
    val xp: Int?,
    val phoneNumber: String?,
)
