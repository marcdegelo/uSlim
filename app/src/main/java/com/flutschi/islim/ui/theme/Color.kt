package com.flutschi.islim.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val SurveyBackgroundColor = Color.White

object AppColors {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFFA726), Color(0xFFFF7043), Color(0xFFFF0000))
    )

    val ButtonColor = Color(0xFFFFA726)

    val gradientStart = Color(0xFFFFA726)  // Orange
    val gradientMiddle = Color(0xFFFF7043) // Deep Orange
    val gradientEnd = Color(0xFFFF0000)    // Red

    val textColor = Color.Black
    val placeholderColor = Color.Gray
    val checkboxBorder = Color(0xFFFF9800) // Checkbox border color
}