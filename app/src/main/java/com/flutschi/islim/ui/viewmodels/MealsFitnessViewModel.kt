package com.flutschi.islim.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.models.Meal
import com.flutschi.islim.models.MealPlanResponse
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MealsFitnessViewModel : ViewModel() {

    private val _mealPlan = MutableStateFlow<MealPlanResponse?>(null)
    val mealPlan: StateFlow<MealPlanResponse?> = _mealPlan

    private val _selectedDay = MutableStateFlow("Monday")
    val selectedDay: StateFlow<String> = _selectedDay

    private val _mealsForDay = MutableStateFlow<List<Meal>>(emptyList())
    val mealsForDay: StateFlow<List<Meal>> = _mealsForDay

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
        updateMealsForDay(day)
    }

    private fun updateMealsForDay(day: String) {
        val meals = mealPlan.value
            ?.days
            ?.firstOrNull { it.day.equals(day, ignoreCase = true) }
            ?.meals
            ?: emptyList()

        _mealsForDay.value = meals
    }

    fun loadMealPlan(planId: Int) {
        viewModelScope.launch {
            try {
                // on DEV, this is slow!!
                val response = RetrofitInstance.getApi().getMealPlan(planId)
                _mealPlan.value = response
                updateMealsForDay(_selectedDay.value)
            } catch (e: Exception) {
                sendDiscordMessage("", e.message ?: "Unknown error")
                e.printStackTrace()
            }
        }
    }

    fun getMealsForSelectedDay(): List<Meal> {
        return _mealPlan.value?.days?.firstOrNull { it.day.equals(_selectedDay.value, ignoreCase = true) }?.meals ?: emptyList()
    }
}
