package com.flutschi.islim.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SurveyViewModel : ViewModel() {
    var gender by mutableStateOf("")
    var userWeight: Float? = null
    var height by mutableStateOf("")
    var age by mutableStateOf("")

    var goal by mutableStateOf("")
    var fitnessLevel by mutableStateOf("")
    var activityTime by mutableStateOf("")
    var activities by mutableStateOf(listOf<String>())
    var healthConditions by mutableStateOf(listOf<String>())
    var barriers by mutableStateOf(listOf<String>())
    var dietType by mutableStateOf("")
    var workoutStyle by mutableStateOf("")
}

data class SurveyData(
    val gender: String,
    val weight: Int,
    val height: Int,
    val age: Int,
    val goal: String,
    val fitnessLevel: String,
    val activityTime: String,
    val activities: List<String>,
    val healthConditions: List<String>,
    val barriers: List<String>,
    val dietType: String,
    val workoutStyle: String,
    val xp: Int = 0
)