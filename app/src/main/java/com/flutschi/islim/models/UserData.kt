package com.flutschi.islim.models

object UserData {
    var token: String? = null
    var username: String? = null
    var userID: Int? = null
    var userXP: Int? = null

    var stepsToday: Int = 0

    var gender: String? = null
    var weight: Float? = null
    var height: String? = null
    var age: String? = null
    var goal: String? = null
    var fitnessLevel: String? = null
    var activityTime: String? = null
    var activities: List<String> = emptyList()
    var healthConditions: List<String> = emptyList()
    var barriers: List<String> = emptyList()
    var dietType: String? = null
    var workoutStyle: String? = null

    fun isSurveyComplete(): Boolean {
        return !gender.isNullOrBlank() &&
                weight != null &&
                !height.isNullOrBlank() &&
                !age.isNullOrBlank() &&
                !goal.isNullOrBlank() &&
                !fitnessLevel.isNullOrBlank() &&
                !activityTime.isNullOrBlank() &&
                activities.isNotEmpty() &&
                healthConditions.isNotEmpty() &&
                barriers.isNotEmpty() &&
                !dietType.isNullOrBlank() &&
                !workoutStyle.isNullOrBlank()
    }

    fun load(from: UserDataResponse) {
        gender = from.gender
        weight = from.weight
        height = from.height
        age = from.age
        goal = from.goal
        fitnessLevel = from.fitnessLevel
        activityTime = from.activityTime
        activities = from.activities ?: emptyList()
        healthConditions = from.healthConditions ?: emptyList()
        barriers = from.barriers ?: emptyList()
        dietType = from.dietType
        workoutStyle = from.workoutStyle
        userID = from.id
        userXP = from.xp
    }

    fun toModel(): UserDataModel {
        return UserDataModel(
            token = token,
            username = username,
            userID = userID,
            userXP = userXP,
            stepsToday = stepsToday,
            gender = gender,
            weight = weight,
            height = height,
            age = age,
            goal = goal,
            fitnessLevel = fitnessLevel,
            activityTime = activityTime,
            activities = activities,
            healthConditions = healthConditions,
            barriers = barriers,
            dietType = dietType,
            workoutStyle = workoutStyle
        )
    }

    fun fromModel(model: UserDataModel) {
        token = model.token
        username = model.username
        userID = model.userID
        userXP = model.userXP
        stepsToday = model.stepsToday
        gender = model.gender
        weight = model.weight
        height = model.height
        age = model.age
        goal = model.goal
        fitnessLevel = model.fitnessLevel
        activityTime = model.activityTime
        activities = model.activities
        healthConditions = model.healthConditions
        barriers = model.barriers
        dietType = model.dietType
        workoutStyle = model.workoutStyle
    }


}
