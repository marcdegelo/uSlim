package com.flutschi.islim.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flutschi.androidapp.R
import com.flutschi.islim.ui.theme.AppColors

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.flutschi.islim.ui.screens.components.ProfileHeader
import com.flutschi.islim.utils.SharedPrefManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.io.File
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.flutschi.islim.api.RetrofitInstance
import com.flutschi.islim.models.UserData
import com.flutschi.islim.models.UserDataResponse
import com.flutschi.islim.ui.navigation.AppNavHost
import com.flutschi.islim.utils.WebhookUtils.sendDiscordMessage
import com.flutschi.islim.utils.rememberStepCounter
import com.google.firebase.FirebaseApp
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID


@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth().height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
    ) {
        Text(text, color = Color.White, fontSize = 18.sp)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomInputField(
    label: String,
    value: String,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit = { _ -> },
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFFFA726), Color(0xFFFF7043), Color(0xFFFF0000))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .background(Color.White, shape = RoundedCornerShape(32.dp))
            .border(4.dp, gradientBrush, shape = RoundedCornerShape(32.dp))
            .padding(2.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = trailingIcon, // <-- Add this
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            )
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit // Allows inserting any composable inside
) {
    Box(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp)) // Add shadow
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFFF8C00)) // Gold to Orange gradient
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp) // Inner padding
    ) {
        content() // Insert any UI inside
    }
}

@Composable
fun ActivityCard(userData: UserData, navController: NavController) {
    val liveSteps = rememberStepCounter()
    val stepsToday = if (liveSteps.value > 0) liveSteps.value else UserData.stepsToday

    val stepEntries = remember { mutableStateOf<List<Entry>>(emptyList()) }
    val avgSteps = remember { mutableStateOf(0f) }
    val userId = UserData.userID

    LaunchedEffect(userId) {
        if (userId == null) return@LaunchedEffect

        try {
            val response = RetrofitInstance.getApi().getStepData(userId)
            Log.d("ActivityCard", "Response: ${response.body()}")
            if (response.isSuccessful) {
                val steps = response.body()?.steps ?: emptyList()
                stepEntries.value = steps.map { Entry(it.day, it.count) }

                if (steps.isNotEmpty()) {
                    avgSteps.value = steps.map { it.count }.average().toFloat()
                }
            }
        } catch (e: Exception) {
            Log.e("ActivityCard", "Error fetching steps: ${e.localizedMessage}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        // Top: Date + Graph
        Column {
            Text(
                text = "Today, ${LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            LineChartView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                entries = stepEntries.value
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom: Steps & Average
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ClickableText(
                text = AnnotatedString("$stepsToday Steps"),
                onClick = {
                    navController.navigate("mapScreen")
                },
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                ),
            )

            Text(
                text = "${avgSteps.value.toInt()} AVG.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}


@Composable
fun LineChartView(modifier: Modifier = Modifier, entries: List<Entry>) {
    Log.i("lineChart", entries.isEmpty().toString())
    if (entries.isEmpty()) return

    val dataSet = LineDataSet(entries, "Step Count Trend").apply {
        color = ColorTemplate.MATERIAL_COLORS[0]
        valueTextColor = android.graphics.Color.BLACK
        valueTypeface = Typeface.DEFAULT_BOLD
        lineWidth = 2f
        setDrawCircles(false)
        setDrawValues(false)
        setDrawFilled(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }

    val lineData = LineData(dataSet)

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                this.data = lineData
                description.isEnabled = false
                legend.isEnabled = false
                axisLeft.isEnabled = false
                axisRight.isEnabled = false
                xAxis.isEnabled = false
                setTouchEnabled(false)
                setScaleEnabled(false)
                setPinchZoom(false)
                setDrawGridBackground(false)
            }
        },
        modifier = modifier
    )
}


@Composable
fun StatsBox(iconResId: Int, text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(50)) // Subtle shadow
            .background(Color.White, shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = "Icon",
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun RecommendationsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center // Ensures centering when only 2 elements
    ) {
        RecommendationBox(
            title = "ACTIVITIES CHALLENGE",
            imageResId = R.drawable.ic_sport,
            modifier = Modifier.weight(1f) // Ensures equal width
        )

        Spacer(modifier = Modifier.width(16.dp)) // Space between elements

        RecommendationBox(
            title = "HEALTHY SNACK SWAP",
            imageResId = R.drawable.ic_food,
            modifier = Modifier.weight(1f) // Ensures equal width
        )
    }
}


@Composable
fun DaySelector(selectedDay: String, onDaySelected: (String) -> Unit) {
    val today = remember { LocalDate.now() }
    val daysRange = remember {
        (0..6).map { today.minusDays(3 - it.toLong()) }
    }

    val selectedIndex = daysRange.indexOfFirst { it.dayOfWeek.name.equals(selectedDay.uppercase(), ignoreCase = true) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        daysRange.forEachIndexed { index, date ->
            val isSelected = index == selectedIndex
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfWeek.name.take(3), // MON, TUE...
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFFFFC107) else Color.Transparent)
                        .clickable {
                            val day = date.dayOfWeek.name.lowercase()
                                .replaceFirstChar { it.uppercase() }

                            onDaySelected(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        color = if (isSelected) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CustomButton(
    text: String,
    modifier: Modifier = Modifier,
    iconResId: Int? = null,
    onClick: () -> Unit
) {
    GradientButtonWithIcon(
        iconResId = iconResId,
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(52.dp)
            .border(3.dp, Color.Black, RoundedCornerShape(30.dp)),
        onClick = onClick
    )
}


@Composable
fun ProfilePicture(sharedPrefManager: SharedPrefManager, isClickable: Boolean = false) {
    val context = LocalContext.current
    var profileImageUrl by remember { mutableStateOf(sharedPrefManager.getProfileImage()) }

    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            uploadImageToFirebase(context, selectedUri) { imageUrl ->
                sharedPrefManager.saveProfileImageUrl(imageUrl)
                profileImageUrl = imageUrl
            }
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Black, CircleShape)
            .then(if (isClickable) Modifier.clickable { pickImageLauncher.launch("image/*") } else Modifier)
    ) {
        if (!profileImageUrl.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(profileImageUrl),
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            PlaceholderIcon()
        }
    }
}

fun uploadImageToFirebase(context: Context, fileUri: Uri, onSuccess: (String) -> Unit) {
    try {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "profile_pictures/${UUID.randomUUID()}.jpg"
        val fileRef = storageRef.child(fileName)

        // sendDiscordMessage("", "🚀 Uploading file: $fileName")

        val uploadTask = fileRef.putFile(fileUri)

        uploadTask
            .addOnSuccessListener {
                // sendDiscordMessage("", "✅ Upload success, waiting for download URL...")

                fileRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        // sendDiscordMessage("", "🔗 Got URL: $uri")
                        onSuccess(uri.toString())
                    }
                    .addOnFailureListener { e ->
                        sendDiscordMessage("", "❌ URL fetch failed: ${e.localizedMessage}")
                        e.printStackTrace()
                        onSuccess("")
                    }

            }
            .addOnFailureListener { e ->
                sendDiscordMessage("", "❌ Upload failed: ${e.localizedMessage}")
                e.printStackTrace()
                onSuccess("")
            }

    } catch (e: Exception) {
        sendDiscordMessage("", "💥 Exception: ${e.localizedMessage}")
        e.printStackTrace()
        onSuccess("")
    }
}

@Composable
fun InfoBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    // Outer Box that WRAPS based on text
    Box(
        modifier = modifier
            .padding(8.dp)
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        // 1. Measure text size first
        val textModifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp) // <- Tune this based on your bubble design

        // 2. Put Text first, then overlay Image BEHIND
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = textModifier,
            color = Color.Black
        )

        // 3. Image matches Text size
        Image(
            painter = painterResource(id = R.drawable.ic_descriptionbubble),
            contentDescription = "Info Bubble",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .matchParentSize() // <-- Match the size of the Box based on the Text
        )
    }
}

@Composable
fun SelectableOptionCard(
    iconRes: Int?,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) Color(0xFFB8FFB8) else Color.White
    val borderStroke = if (isSelected) BorderStroke(1.5.dp, Color.Black) else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        border = if (isSelected) BorderStroke(2.dp, Color.Black) else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFC9F76F) else Color(0xFFFFF3E0)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            if (iconRes != null) {
                Box(
                    modifier = Modifier
                        .size(48.dp), // Fixed space for the icon
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = label,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(48.dp) // Keep layout size
                            .graphicsLayer(
                                scaleX = 1.8f,
                                scaleY = 1.8f
                            )
                    )

                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(label, fontSize = 18.sp)
        }

    }
}

@Composable
fun ChallengeIcon(iconResId: Int, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Gradient ring with transparent center
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .background(Color.Transparent, shape = CircleShape)
                .border(
                    width = 4.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFFEB3B), Color(0xFFFF9800)), // yellow to orange
                    ),
                    shape = CircleShape
                )
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label.uppercase(),
            color = Color(0xFFFF9800),
        )
    }
}



@Composable
fun PlaceholderIcon() {
    Icon(
        painter = painterResource(id = R.drawable.ic_placeholder), // Use a default icon
        contentDescription = "Default Profile Picture",
        tint = Color.Gray,
        modifier = Modifier.size(40.dp)
    )
}


@Composable
fun RecommendationBox(title: String, imageResId: Int, modifier: Modifier) {
    Column(
        modifier = modifier
            .border(2.dp, Color.Black, RoundedCornerShape(20.dp)) // Border
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = title,
            modifier = Modifier
                .size(100.dp) // Adjust image size
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun GradientButtonWithIcon(
    iconResId: Int? = null, // nullable
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}

) {
    Row(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(50))
            .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                ),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable { onClick() },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center

    ) {
        // Left Icon if provided
        iconResId?.let {
            Icon(
                painter = painterResource(id = it),
                contentDescription = "Icon",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        // Text
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun IconWithBorder(
    fixedBorderIcon: Int,
    dynamicInnerIcon: Int,
    picSize: Int = 100,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clickable(onClick = onClick),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = fixedBorderIcon),
            contentDescription = "Border Icon",
            tint = Color.Black,
            modifier = Modifier.size(100.dp)
        )

        Icon(
            painter = painterResource(id = dynamicInnerIcon),
            contentDescription = "Inner Icon",
            tint = Color(0xFFFFA500),
            modifier = Modifier.size(40.dp)
        )
    }
}


@Composable
fun GradientCheckbox(
    isChecked: Boolean,
    label: String, // ✅ Now text is dynamic
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, AppColors.gradientBrush, CircleShape) // Gradient border
        ) {
            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = AppColors.gradientMiddle,
                    uncheckedColor = AppColors.checkboxBorder,
                    checkmarkColor = AppColors.gradientEnd
                ),
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        ClickableText(
            text = AnnotatedString(label),
            onClick = { /* TODO: Handle navigation to Privacy Policy */ },
            modifier = Modifier.padding(start = 4.dp),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = AppColors.textColor,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

@Composable
fun BlackButton(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        shape = RoundedCornerShape(50.dp), // ✅ Rounded corners
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
        Text(
            text = text,
            color = Color.White, // ✅ White text
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}