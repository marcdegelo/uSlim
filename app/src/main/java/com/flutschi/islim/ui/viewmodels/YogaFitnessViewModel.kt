// YogaFitnessViewModel.kt
package com.flutschi.islim.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import com.flutschi.islim.models.YogaActivity

class YogaFitnessViewModel : ViewModel() {

    private val _selectedDay = MutableStateFlow("Monday")
    val selectedDay: StateFlow<String> = _selectedDay

    private val _yogaActivities = MutableStateFlow<List<YogaActivity>>(emptyList())
    val yogaActivities: StateFlow<List<YogaActivity>> = _yogaActivities

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
        loadYogaActivities(day)
    }

    private fun loadYogaActivities(day: String) {
        // 🚀 Here you could fetch from server. Right now let's hardcode an example.
        val allActivities = listOf(
            YogaActivity(1, "Grounding Breath", "Breathing exercise", "https://your.video.url/grounding.mp4", "Monday"),
            YogaActivity(2, "Head Rolls", "Relaxing the neck", "https://your.video.url/headrolls.mp4", "Monday"),
            YogaActivity(3, "Shoulder Rolls", "Loosening shoulders", "https://your.video.url/shoulderrolls.mp4", "Monday"),
            YogaActivity(4, "Side Stretch", "Open side body", "https://your.video.url/sidestretch.mp4", "Tuesday")
        )

        _yogaActivities.value = allActivities.filter { it.day.equals(day, ignoreCase = true) }
    }
}
