package com.flutschi.islim.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flutschi.androidapp.R
import com.flutschi.islim.ui.components.GradientButtonWithIcon

@Composable
fun ExchangeScreen() {
    var swapFromAmount by remember { mutableStateOf("1000") }
    var swapToAmount by remember { mutableStateOf("0.01") }
    var topUpAmount by remember { mutableStateOf("100") }
    var receivedTokenAmount by remember { mutableStateOf("1000") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("— SWAP SYSTEM —", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))
        SwapSection(
            swapFromAmount = swapFromAmount,
            swapToAmount = swapToAmount,
            onSwapFromChange = { swapFromAmount = it },
            onSwapToChange = { swapToAmount = it }
        )

        Spacer(modifier = Modifier.height(40.dp))
        Text("— TOP UP YOUR BALANCE —", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))
        TopUpSection(
            topUpAmount = topUpAmount,
            receivedTokenAmount = receivedTokenAmount,
            onTopUpChange = { topUpAmount = it },
            onReceivedTokenChange = { receivedTokenAmount = it }
        )
    }
}

@Composable
fun SwapSection(
    swapFromAmount: String,
    swapToAmount: String,
    onSwapFromChange: (String) -> Unit,
    onSwapToChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        TokenInputField(value = swapFromAmount, onValueChange = onSwapFromChange, placeholder = "$1000")
        Text("➝", fontSize = 24.sp)
        TokenInputField(value = swapToAmount, onValueChange = onSwapToChange, placeholder = "0.01")
    }

    Spacer(modifier = Modifier.height(20.dp))
    GradientButtonWithIcon(
        iconResId = null,
        text = "CONFIRM SWAP",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(52.dp)
            .border(3.dp, Color.Black, RoundedCornerShape(30.dp))
    )
}

@Composable
fun TopUpSection(
    topUpAmount: String,
    receivedTokenAmount: String,
    onTopUpChange: (String) -> Unit,
    onReceivedTokenChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        TokenInputField(value = topUpAmount, onValueChange = onTopUpChange, placeholder = "$100")
        Text("➝", fontSize = 24.sp)
        TokenInputField(value = receivedTokenAmount, onValueChange = onReceivedTokenChange, placeholder = "$1000")
    }

    Spacer(modifier = Modifier.height(12.dp))
    PaymentIconsRow()

    Spacer(modifier = Modifier.height(16.dp))
    GradientButtonWithIcon(
        iconResId = null,
        text = "TOP UP BALANCE",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(52.dp)
            .border(3.dp, Color.Black, RoundedCornerShape(30.dp))
    )
}

@Composable
fun TokenInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        modifier = Modifier
            .width(100.dp)
            .height(56.dp)
    )
}

@Composable
fun PaymentIconsRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(R.drawable.ic_stripe, R.drawable.ic_other_payment, R.drawable.ic_card, R.drawable.ic_google).forEach {
            Icon(
                painter = painterResource(id = it),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = Color.Unspecified // prevent default coloring and show real image colors

            )
        }
    }
}
