package com.flutschi.islim.ui.screens

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.flutschi.androidapp.R
import com.flutschi.islim.MainActivity
import com.flutschi.islim.utils.AuthUtils
import com.flutschi.islim.utils.AuthUtils.sendGoogleTokenToBackend
import com.flutschi.islim.utils.GLOBALS
import com.flutschi.islim.utils.SharedPrefManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import com.flutschi.islim.snackbar.SnackbarManager
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.flutschi.islim.ui.components.*



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val googleSignInClient = remember { AuthUtils.getGoogleSignInClient(context) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        AuthUtils.handleGoogleSignInResult(
            resultIntent = result.data,
            context = context,
            navController = navController,
            coroutineScope = coroutineScope
        )
    }

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFFA726),
            Color(0xFFFF7043),
            Color(0xFFFF0000)
        )
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Sign Up", fontSize = 26.sp, color = Color.Black, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp))

        CustomInputField(
            label = "Username",
            value = username,
            keyboardType = KeyboardType.Text,
            onValueChange = { username = it }
        )
        CustomInputField("Email", email, KeyboardType.Email, onValueChange = { email = it })
        CustomInputField("Password", password, KeyboardType.Password, isPassword = true, onValueChange = { password = it })

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Policy Checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientCheckbox(
                isChecked = isChecked,
                label = "By continuing you accept our Privacy Policy",
                onCheckedChange = { isChecked = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        BlackButton(
            text = "Sign up",
            onClick = {
                coroutineScope.launch {
                    registerUser(username, email, password, context, navController)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Or sign up with", color = Color.Black, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(12.dp))

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

        Spacer(modifier = Modifier.height(12.dp))

        val annotatedText = buildAnnotatedString {
            append("Already have an account? ")

            // Set annotation tag for clickable "Sign IN"
            pushStringAnnotation(
                tag = "SIGN_IN",
                annotation = "login" // This should match your nav graph route
            )
            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Sign IN")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "SIGN_IN", start = offset, end = offset)
                    .firstOrNull()?.let {
                        navController.navigate(it.item)
                    }
            },
            style = TextStyle(fontSize = 14.sp, color = Color.Black)
        )

    }
}

@Composable
fun SocialLoginButton(
    iconRes: Int,
    provider: String,
    googleSignInLauncher: () -> Unit
) {
    IconButton(onClick = {
        when (provider) {
            "google" -> googleSignInLauncher()
            else -> Log.e("SocialLogin", "$provider login not supported yet.")
        }
    }) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = "$provider Login",
            tint = Color.Unspecified,
            modifier = Modifier.size(40.dp)
        )
    }
}

// Function to send Sign-Up request
suspend fun registerUser(username: String, email: String, password: String, context: Context, navController: NavController) {
    AuthUtils.registerUser(username, email, password, context, navController)
}
