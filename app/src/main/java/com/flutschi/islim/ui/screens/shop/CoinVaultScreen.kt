package com.flutschi.islim.ui.screens.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flutschi.androidapp.R

@Composable
fun CoinVaultPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(coinVaultItems.size) { index ->
                CoinVaultItemCard(item = coinVaultItems[index])
            }
        }
    }
}

// --- Reusable Card ---

@Composable
fun CoinVaultItemCard(item: CoinVaultItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = item.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .width(140.dp)
            .height(220.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Title
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )

            // Icon
            Icon(
                painter = painterResource(id = item.iconRes),
                contentDescription = "Coins",
                tint = Color.Yellow,
                modifier = Modifier.size(48.dp)
            )

            // Slims Amount
            Text(
                text = "${item.slims} SLIMS",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.White
            )

            // Price at the bottom
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = item.price,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// --- Model for items ---

data class CoinVaultItem(
    val title: String,
    val slims: Int,
    val price: String,
    val iconRes: Int,
    val backgroundColor: Color
)

// --- Dummy Items ---

val coinVaultItems = listOf(
    CoinVaultItem("STARTER PACK", 100, "$0.99", R.drawable.ic_placeholder, Color(0xFFBDBDBD)),
    CoinVaultItem("GRIND PACK", 500, "$4.99", R.drawable.ic_placeholder, Color(0xFF7E57C2)),
    CoinVaultItem("MUSCLE PACK", 1000, "$9.99", R.drawable.ic_placeholder, Color(0xFF29B6F6)),
    CoinVaultItem("HERO PACK", 2500, "$19.99", R.drawable.ic_placeholder, Color(0xFFFFA726)),
    CoinVaultItem("TITAN PACK", 5000, "$29.99", R.drawable.ic_placeholder, Color(0xFFFF7043)),
    CoinVaultItem("GOD PACK", 10000, "$49.99", R.drawable.ic_placeholder, Color(0xFFFF5722))
)
