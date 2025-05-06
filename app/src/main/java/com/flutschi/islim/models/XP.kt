package com.flutschi.islim.models

data class XpAwardRequest(
    val user_id: Int,
    val xp_amount: Int,
    val source_id: Int,
    val source_type: String,
    val reason: String
)
