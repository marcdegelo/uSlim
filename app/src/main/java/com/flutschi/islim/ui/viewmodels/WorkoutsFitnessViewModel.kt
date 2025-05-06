package com.flutschi.islim.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.api.UserProgramRequest
import com.flutschi.islim.models.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorkoutsFitnessViewModel : ViewModel() {

    private val _selectedDay = MutableStateFlow("Monday")
    val selectedDay: StateFlow<String> = _selectedDay

    private val _allWorkouts = MutableStateFlow<List<Workout>>(emptyList())
    val allWorkouts: StateFlow<List<Workout>> = _allWorkouts

    private val _workoutsForDay = MutableStateFlow<List<Workout>>(emptyList())
    val workoutsForDay: StateFlow<List<Workout>> = _workoutsForDay

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
        updateWorkoutsForDay(day)
    }

    private fun updateWorkoutsForDay(day: String) {
        val filtered = _allWorkouts.value.filter {
            it.day.equals(day, ignoreCase = true)
        }
        _workoutsForDay.value = filtered
    }

    fun loadWorkoutsForUser(request: UserProgramRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.getApi().getUserFitnessProgram(request)
                if (response.isSuccessful) {
                    val programs = response.body() ?: emptyList()

                    val allWorkouts = programs.flatMap { program ->
                        program.levels.flatMap { level ->
                            level.workouts.map { workoutDay ->
                                Workout(
                                    icon = "🔥",
                                    title = workoutDay.name,
                                    id = workoutDay.id.toInt(),
                                    isManuallyCompletable = true,
                                    day = workoutDay.dayOfWeek // assuming this exists
                                )
                            }
                        }
                    }

                    _allWorkouts.value = allWorkouts
                    updateWorkoutsForDay(request.weekday)
                    Log.i("WorkoutsViewModel", "✅ Loaded ${allWorkouts.size} workouts")
                } else {
                    Log.e("WorkoutsViewModel", "❌ Failed with code ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("WorkoutsViewModel", "💥 Exception: ${e.message}", e)
            }
        }
    }


    fun getWorkoutsForSelectedDay(): List<Workout> {
        return _allWorkouts.value.filter {
            it.day.equals(_selectedDay.value, ignoreCase = true)
        }
    }
}
