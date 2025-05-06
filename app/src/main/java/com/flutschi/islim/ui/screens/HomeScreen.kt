package com.flutschi.islim.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp), // ⬅️ Prevents overlap with BottomNav
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home Screen")
    }
}
