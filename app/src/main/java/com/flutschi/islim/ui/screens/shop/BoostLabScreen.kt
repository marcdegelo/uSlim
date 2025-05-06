package com.flutschi.islim.ui.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flutschi.androidapp.R
import com.flutschi.islim.ui.components.InfoBubble

@Composable
fun BoostLabPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top Section: Double XP
        ShopItemSection(
            sectionTitle = "Double XP ⚡",
            items = listOf(
                ShopItem("1 DAY", "200 SLIMS", R.drawable.ic_placeholder),
                ShopItem("3 DAYS", "500 SLIMS", R.drawable.ic_placeholder),
                ShopItem("7 DAYS", "1,000 SLIMS", R.drawable.ic_placeholder)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bottom Section: Tier Unlock Token
        ShopItemSection(
            sectionTitle = "Tier Unlock Token 🔒",
            items = listOf(
                ShopItem("x1", "500 SLIMS", R.drawable.ic_placeholder),
                ShopItem("x2", "1,000 SLIMS", R.drawable.ic_placeholder),
                ShopItem("x3", "1,500 SLIMS", R.drawable.ic_placeholder)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom info text
        InfoBubble(
            text = "Use this token to instantly unlock any one tier of the Uslim Pass. Perfect for users who may be stuck on a specific task or missed a day — keep your progress flowing without losing momentum."
        )
    }
}

// --- Reusable section ---

@Composable
fun ShopItemSection(
    sectionTitle: String,
    items: List<ShopItem>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BlackRibbonTitle(title = sectionTitle)

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items.size) { index ->
                ShopItemCard(item = items[index])
            }
        }
    }
}

// --- Single Card ---
@Composable
fun ShopItemCard(item: ShopItem) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Icon
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = item.title,
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Price
            Text(
                text = item.price,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // BUY Button
            Button(
                onClick = {
                    // TODO: handle buying this item
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .height(32.dp)
                    .width(80.dp)
            ) {
                Text(
                    text = "BUY",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun BlackRibbonTitle(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black, shape = RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}


// --- Model ---

data class ShopItem(
    val title: String,
    val price: String,
    val iconRes: Int
)
