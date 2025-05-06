package com.flutschi.islim.models

data class StepEntry(
    val day: Float,
    val count: Float
)

data class StepResponse(
    val steps: List<StepEntry>
)
