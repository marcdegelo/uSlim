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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.flutschi.androidapp.R
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.ui.components.CustomInputField
import kotlinx.coroutines.launch

@Composable
fun SetNewPasswordScreen() {
    var email by remember { mutableStateOf("") } // or pass email as parameter
    var code by remember { mutableStateOf("") } // or pass code as parameter
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
            painter = painterResource(id = R.drawable.ic_placeholder), // Replace with appropriate image
            contentDescription = "Set New Password Illustration",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))


        CustomInputField(
            label = "New Password",
            value = newPassword,
            keyboardType = KeyboardType.Password,
            onValueChange = { newPassword = it },
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomInputField(
            label = "Confirm Password",
            value = confirmPassword,
            keyboardType = KeyboardType.Password,
            onValueChange = { confirmPassword = it },
        )


        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch {
                    if (newPassword == confirmPassword) {
                        try {
                            val response = RetrofitInstance.getApi().setNewPassword(
                                mapOf("email" to email, "code" to code, "newPassword" to newPassword)
                            )
                            if (response.isSuccessful) {
                                message = "Password reset successfully!"
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("setNewPassword", "Failed to reset password: ${response.code()} - ${response.message()} - $errorBody")
                                message = "Failed to reset password. Please try again later."
                            }
                        } catch (e: Exception) {
                            Log.e("setNewPassword", "Exception during password reset: ${e.localizedMessage}", e)
                            message = "Error: ${e.localizedMessage}"
                        }
                    } else {
                        message = "Passwords do not match."
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Set New Password", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier.fillMaxWidth(),
                color = if (message.startsWith("Password reset successfully!")) Color.Green else Color.Red
            )
        }
    }
}