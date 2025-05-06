package com.flutschi.islim.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flutschi.islim.ui.components.DaySelector
import com.flutschi.islim.ui.screens.components.YogaPlanSection
import com.flutschi.islim.ui.viewmodels.YogaFitnessViewModel

@Composable
fun YogaFitnessScreen(viewModel: YogaFitnessViewModel = viewModel()) {
    val selectedDay by viewModel.selectedDay.collectAsState()

    Scaffold { paddingValues ->  // ✅ Scaffold gives bounded size!
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DaySelector(selectedDay) { viewModel.setSelectedDay(it) }

            Spacer(modifier = Modifier.height(20.dp))

            YogaPlanSection(selectedDay = selectedDay)
        }
    }
}

