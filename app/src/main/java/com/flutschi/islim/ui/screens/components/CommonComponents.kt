package com.flutschi.islim.ui.screens.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ProfileHeader(title: String) {

    val divLen = 40

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            color = Color.Black,
            thickness = 2.dp, // Thicker divider
            modifier = Modifier
                .width(divLen.dp) // Adjust width as needed
        )
        Spacer(modifier = Modifier.width(12.dp)) // More space between divider and text
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(12.dp)) // More space between divider and text
        Divider(
            color = Color.Black,
            thickness = 2.dp, // Thicker divider
            modifier = Modifier
                .width(divLen.dp) // Adjust width as needed
        )
    }
}

