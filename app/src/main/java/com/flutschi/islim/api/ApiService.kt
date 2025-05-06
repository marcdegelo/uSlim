package com.flutschi.islim.api

import com.flutschi.islim.models.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

data class ApiResponse(
    val message: String
)

data class UserProgramRequest(
    val user_id: Int,
    val weekday: String
)

interface ApiService {
    @GET("api/userdata")
    suspend fun getUserData(
        @Header("Authorization") token: String
    ): Response<UserDataResponse>

    @GET("mealplans/{id}")
    suspend fun getMealPlan(@Path("id") id: Int): MealPlanResponse

    @Multipart
    @POST("upload-image")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part info: MultipartBody.Part  // <-- NEW
    ): Call<ResponseBody>

    @POST("save_steps")
    fun sendSteps(
        @Body stepData: StepRequest
    ): Call<ResponseBody>

    @GET("get_steps")
    suspend fun getStepData(@Query("user_id") userId: Int): Response<StepResponse>

    @POST("mealplans/complete-meal")
    suspend fun markMealCompleted(@Body body: MealCompleteRequest): Response<Unit>

    @POST("api/xp/award")
    suspend fun awardXp(@Body request: XpAwardRequest): Response<Unit>

    @GET("api/completed-meals/{user_id}")
    suspend fun getCompletedMeals(@Path("user_id") userId: Int): Response<CompletedMealsResponse>

    @GET("completed_workouts/{userId}")
    suspend fun getCompletedWorkouts(@Path("userId") userId: Int): Response<CompletedWorkoutsResponse>

    @POST("complete_workout")
    suspend fun completeWorkout(@Body request: WorkoutCompleteRequest): Response<ApiResponse>

    @POST("check-version")
    suspend fun checkVersion(@Body versionData: VersionRequest): Response<VersionResponse>

    @POST("api/submit_survey")
    suspend fun submitSurvey(
        @Header("Authorization") token: String,
        @Body surveyData: SurveyData
    ): retrofit2.Response<Void>

    @POST("api/save_route")
    suspend fun saveRoute(
        @Header("Authorization") token: String,
        @Body data: RouteRequest
    ): Response<ApiResponse>

    @GET("fitness_programs")
    suspend fun getFitnessPrograms(): Response<List<FitnessProgramResponse>>

    @POST("fitness_programs/by-user")
    suspend fun getUserFitnessProgram(@Body request: UserProgramRequest): Response<List<FitnessProgramResponse>>

    @POST("get_verification_code")
    suspend fun getVerificationCode(@Body body: Map<String, String>): Response<Unit>

    @POST("verify_code")
    suspend fun verifyCode(@Body body: Map<String, String>): Response<Unit>

    @POST("set_new_password")
    suspend fun setNewPassword(@Body body: Map<String, String>): Response<Unit>

}

data class VersionRequest(val version: Int)

data class VersionResponse(
    val force_update: Boolean,
    val latest_version: Int
)
