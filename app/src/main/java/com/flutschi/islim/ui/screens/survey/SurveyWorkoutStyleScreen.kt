package com.flutschi.islim.ui.screens.survey

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.flutschi.islim.models.SurveyViewModel
import com.flutschi.androidapp.R
import com.flutschi.islim.models.UserData
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage

@Composable
fun SurveyWorkoutStyleScreen(
    navController: NavController,
) {
    var selectedStyle by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val options = listOf(
        WorkoutStyleItem("Solo", R.drawable.ic_placeholder),
        WorkoutStyleItem("Group Workouts", R.drawable.ic_placeholder)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Add this line
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Progress Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(6) { index ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (index == 13) 10.dp else 8.dp)
                            .background(
                                color = if (index == 13) Color.Black else Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Title
            Text(
                text = "Do you prefer solo workouts or group classes?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Info bubble + Invite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Get even more motivated by inviting friends to join you in group workouts! Track each other's progress, cheer each other on, and challenge your friends to friendly competitions!",
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_placeholder),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(36.dp)
                    )
                    Button(
                        onClick = {
                            Toast.makeText(context, "Invite Friends feature coming soon!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Invite friends", fontSize = 12.sp, color = Color.White)
                    }
                }
            }

            // Options
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                options.forEach { item ->
                    val isSelected = item.label == selectedStyle
                    WorkoutStyleOptionCard(
                        item = item,
                        isSelected = isSelected,
                        onClick = { selectedStyle = item.label }
                    )
                }
            }

            // Next Button
            Button(
                onClick = {
                    selectedStyle?.let {
                        UserData.workoutStyle = it
                        sendDiscordMessage("", it.toString())
                        navController.navigate("surveySubmit") // Or wherever next
                    }
                },
                enabled = selectedStyle != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text("Next", color = Color.White)
            }
        }
    }
}

@Composable
fun WorkoutStyleOptionCard(
    item: WorkoutStyleItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFFB8FFB8) else Color.White
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else null,
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.label,
                tint = Color.Unspecified,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(item.label, fontWeight = FontWeight.Bold)
        }
    }
}

data class WorkoutStyleItem(val label: String, val iconRes: Int)
