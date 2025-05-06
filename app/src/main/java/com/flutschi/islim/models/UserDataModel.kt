package com.flutschi.islim.models

data class UserDataModel(
    var token: String? = null,
    var username: String? = null,
    var userID: Int? = null,
    var userXP: Int? = null,
    var stepsToday: Int = 0,

    var gender: String? = null,
    var weight: Float? = null,
    var height: String? = null,
    var age: String? = null,
    var goal: String? = null,
    var fitnessLevel: String? = null,
    var activityTime: String? = null,
    var activities: List<String> = emptyList(),
    var healthConditions: List<String> = emptyList(),
    var barriers: List<String> = emptyList(),
    var dietType: String? = null,
    var workoutStyle: String? = null
)
