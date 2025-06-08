package com.flutschi.islim.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.flutschi.islim.ui.components.BlackButton
import com.flutschi.islim.ui.components.CustomInputField
import com.flutschi.islim.ui.theme.AppColors
import com.flutschi.islim.utils.AuthUtils
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.flutschi.androidapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    val googleSignInClient = remember { AuthUtils.getGoogleSignInClient(context) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        coroutineScope.launch { // <--- ADD THIS LINE
            AuthUtils.handleGoogleSignInResult(
                resultIntent = result.data,
                context = context,
                navController = navController,
                coroutineScope = coroutineScope // Pass the existing scope
            )
        } // <--- AND THIS CLOSING BRACE
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sign in",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.textColor
        )

        Spacer(modifier = Modifier.height(24.dp))

        CustomInputField(
            label = "Email",
            value  = email,
            keyboardType = KeyboardType.Email,
            onValueChange  = { email = it }
        )

        CustomInputField(
            label = "Password",
            value  = password,
            keyboardType = KeyboardType.Password,
            isPassword = true,
            onValueChange  = { password = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("reset_password") } // <-- Important: your nav graph must have a reset_password route!
        ) {
            Text(
                text = "Forgot your password?",
                color = AppColors.gradientEnd,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BlackButton(
            text = "Sign In",
            onClick = {
                coroutineScope.launch {
                    loginUser(email, password, context, navController)
                }
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialLoginButton(
                iconRes = R.drawable.ic_googlelogo,
                provider = "google",
                googleSignInLauncher = { googleSignInLauncher.launch(googleSignInClient.signInIntent) }
            )

        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Don't have an account?",
            fontSize = 14.sp,
            color = AppColors.placeholderColor
        )

        TextButton(onClick = { navController.navigate("signup") }) {
            Text(
                text = "Sign up",
                color = AppColors.gradientEnd,
                fontSize = 14.sp
            )
        }
    }
}

// Helper function
suspend fun loginUser(email: String, password: String, context: Context, navController: NavController) {
    AuthUtils.loginWithEmail(email, password, context, navController)
}
