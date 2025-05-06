package com.flutschi.islim.ui.screens

import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.flutschi.islim.ui.screens.components.ProfileHeader
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun GoalsScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ProfileHeader("Goals")

        Spacer(modifier = Modifier.height(16.dp))

        // Line Chart Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ComposeColor.White)
                .border(4.dp, ComposeColor.Gray, RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            LineChartView(modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Bar Chart Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ComposeColor.White)
                .border(4.dp, ComposeColor.Gray, RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            BarChartView(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LineChartView(modifier: Modifier = Modifier) {
    val entries = listOf(
        Entry(0f, 110f),
        Entry(1f, 105f),
        Entry(2f, 100f),
        Entry(3f, 96f),
        Entry(4f, 92f),
        Entry(5f, 88f),
        Entry(6f, 85f),
        Entry(7f, 82f),
        Entry(8f, 80f),
        Entry(9f, 78f),
        Entry(10f, 76.8f),
    )
    val dataSet = LineDataSet(entries, "Weight Loss Trend").apply {
        color = ColorTemplate.MATERIAL_COLORS[0]
        valueTextColor = Color.BLACK
        valueTypeface = Typeface.DEFAULT_BOLD
        lineWidth = 2f
        circleRadius = 4f
        setCircleColor(ColorTemplate.MATERIAL_COLORS[0])
    }
    val lineData = LineData(dataSet)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                data = lineData
                description = Description().apply { text = "Weight Progress" }
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        },
        modifier = modifier
    )
}

@Composable
fun BarChartView(modifier: Modifier = Modifier) {
    val barEntries = listOf(
        BarEntry(0f, 3.2f),
        BarEntry(1f, 1.8f),
        BarEntry(2f, 4.5f),
        BarEntry(3f, 2.3f),
        BarEntry(4f, 5.0f),
        BarEntry(5f, 3.8f),
        BarEntry(6f, 2.6f),
    )
    val barDataSet = BarDataSet(barEntries, "Monthly Weight Loss").apply {
        colors = ColorTemplate.MATERIAL_COLORS.toList()
        valueTextColor = Color.BLACK
        valueTypeface = Typeface.DEFAULT_BOLD
    }
    val barData = BarData(barDataSet)

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                data = barData
                description = Description().apply { text = "Monthly Overview" }
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        },
        modifier = modifier
    )
}
