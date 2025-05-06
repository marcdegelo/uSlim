package com.flutschi.islim.ui.screens.shop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flutschi.islim.ui.screens.components.ProfileHeader
import com.flutschi.islim.ui.screens.shop.BoostLabPage
import com.flutschi.islim.ui.screens.shop.UslimPassCard
import com.flutschi.islim.ui.screens.shop.CoinVaultPage

@Composable
fun ShopScreen() {
    val tabs = listOf("BOOST LAB", "USLIM PASS", "COIN VAULT")
    var selectedTabIndex by remember { mutableStateOf(0) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Title
            ProfileHeader(title = "USLIM SHOP")

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Color.Black
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subpages
            when (selectedTabIndex) {
                0 -> BoostLabPage()
                1 -> UslimPassCard( onBuyClick = {
                    // TODO: Implement buy functionality
                })
                2 -> CoinVaultPage()
            }
        }
    }
}
