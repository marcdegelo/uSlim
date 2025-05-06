package com.flutschi.islim.models

// --- Response Structure for Fetching a Full Program ---

data class FitnessProgramResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val levels: List<WorkoutLevel>
)

data class WorkoutLevel(
    val id: Long,
    val name: String,
    val sessionsPerWeek: Int?,
    val workouts: List<WorkoutDay>
)

data class WorkoutDay(
    val id: Long,
    val dayOfWeek: String,
    val name: String,
    val description: String?,
    val exercises: List<FitnessExercise>
)

data class FitnessExercise(
    val id: Long,
    val type: String?,
    val name: String,
    val sets: Int?,
    val reps: Int?,
    val durationSeconds: Int?,
    val notes: String?
)

// --- Request/Response for Marking Completion ---

data class WorkoutCompleteRequest(
    val workout_id: Int,
    val user_id: Int
)

data class ExerciseCompleteRequest(
    val exercise_id: Long,
    val user_id: Int
)

data class CompletedWorkoutsResponse(
    val completed_workouts: List<Long>
)

data class CompletedExercisesResponse(
    val completed_exercises: List<Long>
)

data class Workout(
    val icon: String,
    val title: String,
    val id: Int,
    val isManuallyCompletable: Boolean,
    val day: String // ← comes from WorkoutDay.dayOfWeek
)