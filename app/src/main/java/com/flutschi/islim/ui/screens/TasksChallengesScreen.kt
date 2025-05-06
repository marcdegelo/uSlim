package com.flutschi.islim.ui.screens

import android.content.Intent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flutschi.islim.ui.components.ChallengeIcon
import com.flutschi.androidapp.R
import com.flutschi.islim.ui.components.GradientButtonWithIcon
import com.flutschi.islim.ui.components.IconWithBorder
import com.flutschi.islim.ui.screens.components.ProfileHeader

@Composable
fun TasksChallengesScreen() {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ProfileHeader("TASKS AND CHALLENGES")

        Spacer(modifier = Modifier.height(10.dp))
        Text("SELF CHALLENGES", fontSize = 14.sp)

        Spacer(modifier = Modifier.height(24.dp))

        val challenges = listOf(
            R.drawable.ic_hydration to "Hydration",
            R.drawable.ic_time_fitness to "Active Minutes",
            R.drawable.ic_food to "Healthy Snack Swap",
            R.drawable.ic_steps to "Step Count",
            R.drawable.ic_yoga to "Meditation",
            R.drawable.ic_strength to "Core Strength"
        )

        for (i in challenges.chunked(3)) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                i.forEach { (icon, label) ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            ChallengeIcon(
                                iconResId = icon,
                                label = label
                            )
                        }
                    }
                }
                // Fill remaining spaces if row has < 3 items
                repeat(3 - i.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }


        Spacer(modifier = Modifier.height(48.dp))
        ProfileHeader("BONUS TASK")
        Spacer(modifier = Modifier.height(12.dp))

        // Bonus Button
        GradientButtonWithIcon(
            iconResId = null,
            text = "INVITE FRIENDS",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp)
                .border(3.dp, Color.Black, RoundedCornerShape(30.dp)), // ⬅️ Thicker border
            onClick = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Join me on iSlim! Let's get fit and earn crypto 💪🚀\nDownload the app here: https://yourapp.link"
                    )
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }
        )
    }
}
