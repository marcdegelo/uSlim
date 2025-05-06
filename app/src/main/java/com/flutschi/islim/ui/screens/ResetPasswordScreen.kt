package com.flutschi.islim.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flutschi.androidapp.R
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.ui.components.CustomInputField
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen() {
    var email by remember { mutableStateOf("flutschich@gmail.com") }
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_placeholder),
            contentDescription = "Reset Password Illustration",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Email input with Get Code button inside ---
        CustomInputField(
            label = "Enter your email",
            value = email,
            keyboardType = KeyboardType.Email,
            onValueChange = { email = it },
            trailingIcon = {
                Box(
                    modifier = Modifier.padding(start = 8.dp), // Padding inside the Box
                    contentAlignment = Alignment.CenterEnd // Align content to the end of the Box
                ) {
                    Spacer(modifier = Modifier.width(8.dp)) // Add a small spacer before the button
                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    val response = RetrofitInstance.getApi().getVerificationCode(mapOf("email" to email))
                                    if (response.isSuccessful) {
                                        message = "Verification code sent!"
                                    } else {
                                        val errorBody = response.errorBody()?.string()  // Get the error body
                                        Log.e("resetPassword", "Failed to send code: ${response.code()} - ${response.message()} - $errorBody") // Log the error
                                        message = "Failed to send code. Please try again later." // More user-friendly message
                                    }
                                } catch (e: Exception) {
                                    Log.e("resetPassword", "Exception during verification code request: ${e.localizedMessage}", e) // Log the exception with stacktrace
                                    message = "Error: ${e.localizedMessage}"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text("Get code", color = Color.Black, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Code input ---
        CustomInputField(
            label = "Enter your code",
            value = code,
            keyboardType = KeyboardType.Number,
            onValueChange = { code = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Reset Password Button ---
        Button(
            onClick = {
                scope.launch {
                    try {
                        val response = RetrofitInstance.getApi().verifyCode(mapOf("email" to email, "code" to code))
                        if (response.isSuccessful) {
                            message = "Code verified! You can now reset your password."
                        } else {
                            message = "Invalid or expired code."
                        }
                    } catch (e: Exception) {
                        message = "Error: ${e.localizedMessage}"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Reset password", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Feedback Message ---
        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Red
            )
        }
    }
}