package com.flutschi.islim.models

data class MealPlanResponse(
    val id: Int,
    val name: String,
    val diet_type: String,
    val days: List<MealDay>
)

data class MealDay(
    val day: String,
    val meals: List<Meal>
)

data class Meal(
    val id: Int,
    val meal_type: String,
    val title: String,
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fats: Float
)

data class MealCompleteRequest(
    val meal_id: Int,
    val user_id: Int // or String if needed
)

data class CompletedMealsResponse(
    val completed_meals: List<Int>
)
