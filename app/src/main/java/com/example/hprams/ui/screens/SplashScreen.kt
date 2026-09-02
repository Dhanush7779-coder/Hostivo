package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.R
import com.example.hprams.ui.components.GlassBackground
import com.example.hprams.ui.components.GlassButton
import com.example.hprams.ui.components.getAppSubTextColor
import com.example.hprams.ui.components.getAppTextColor
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.theme.AccentColor

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun SplashScreen(
    onGetStartedClick: () -> Unit,
    onAutoLogin: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()
    val context = LocalContext.current
    
    // Soft Pulse Scale & Opacity Animation for logo
    val infiniteTransition = rememberInfiniteTransition(label = "Hostel Pulse Logo")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val opacity by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "opacity"
    )

    val sharedPrefs = remember { context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE) }

    var termsAccepted by remember { mutableStateOf(sharedPrefs.getBoolean("termsAccepted", true)) }
    var showTermsDialog by remember { mutableStateOf(false) }

    // Check if session is already saved
    var permissionStep by remember { mutableStateOf(3) } 
    var isAutoRedirecting by remember { mutableStateOf(true) }
    
    val auth = remember { com.google.firebase.auth.FirebaseAuth.getInstance() }
    LaunchedEffect(Unit) {
        val role = sharedPrefs.getString("loggedInRole", "") ?: ""
        val roll = sharedPrefs.getString("loggedInRoll", "") ?: ""
        
        if (role.isNotEmpty()) {
            com.example.hprams.data.HostelDataStore.currentRole = role
            com.example.hprams.data.HostelDataStore.currentStudentRoll = roll
            if (role == "Warden") {
                val matched = com.example.hprams.data.HostelDataStore.students.find { it.roll == roll }
                if (matched != null) {
                    com.example.hprams.data.HostelDataStore.currentWardenScope = if (matched.gender == "Female") "Girls" else "Boys"
                }
            }
            val destination = when (role) {
                "Warden" -> "warden_dashboard"
                "Admin" -> "admin_dashboard"
                "Security" -> "security_dashboard"
                else -> "student_dashboard"
            }
            onAutoLogin(destination)
        } else {
            isAutoRedirecting = false
            onGetStartedClick()
        }
    }
    if (permissionStep == 1) {
        AlertDialog(
            onDismissRequest = { permissionStep = 2 },
            icon = { Icon(Icons.Default.PinDrop, contentDescription = null, tint = AccentColor) },
            title = { Text("Allow Location Permission?") },
            text = { Text("Hostivo requires access to your location to verify your presence at the gate and hostel boundary.") },
            confirmButton = {
                TextButton(onClick = { permissionStep = 2 }) {
                    Text("Allow", color = AccentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { permissionStep = 2 }) {
                    Text("Deny", color = Color.Gray)
                }
            },
            containerColor = if (isDark) Color(0xFF1E222B) else Color(0xFFE0E5EC),
            titleContentColor = if (isDark) Color.White else Color(0xFF2D3133),
            textContentColor = if (isDark) Color.White else Color(0xFF2D3133)
        )
    } else if (permissionStep == 2) {
        AlertDialog(
            onDismissRequest = { 
                permissionStep = 3 
                sharedPrefs.edit().putBoolean("permissionsAsked", true).apply()
            },
            icon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AccentColor) },
            title = { Text("Allow Notifications Permission?") },
            text = { Text("Hostivo needs notification access to send you important broadcasts, warden alerts, and hostel gate pass updates.") },
            confirmButton = {
                TextButton(onClick = { 
                    permissionStep = 3 
                    sharedPrefs.edit().putBoolean("permissionsAsked", true).apply()
                }) {
                    Text("Allow", color = AccentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    permissionStep = 3 
                    sharedPrefs.edit().putBoolean("permissionsAsked", true).apply()
                }) {
                    Text("Deny", color = Color.Gray)
                }
            },
            containerColor = if (isDark) Color(0xFF1E222B) else Color(0xFFE0E5EC),
            titleContentColor = if (isDark) Color.White else Color(0xFF2D3133),
            textContentColor = if (isDark) Color.White else Color(0xFF2D3133)
        )
    }

    GlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo container with smooth pulsing/scaling effect
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nest_campus_logo),
                    contentDescription = "Hostivo Logo",
                    modifier = Modifier
                        .size(240.dp)
                        .graphicsLayer {
                            this.scaleX = scale
                            this.scaleY = scale
                            this.alpha = opacity
                        }
                )
            }

            // Bottom action area
            if (isAutoRedirecting) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentColor)
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Hostivo",
                            color = AccentColor,
                            style = MaterialTheme.typography.displayLarge,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Hostel Living, Simplified",
                            color = getAppSubTextColor(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .width(280.dp)
                                .alpha(0.8f)
                        )
                    }

                    // Terms & Conditions checkbox row moved here
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = { 
                                termsAccepted = it 
                                sharedPrefs.edit().putBoolean("termsAccepted", it).apply()
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AccentColor,
                                uncheckedColor = subTextColor,
                                checkmarkColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { 
                                termsAccepted = !termsAccepted 
                                sharedPrefs.edit().putBoolean("termsAccepted", termsAccepted).apply()
                            }
                        ) {
                            Text(
                                text = "I agree to ",
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Terms & Conditions",
                                color = AccentColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { showTermsDialog = true }
                            )
                        }
                    }

                    GlassButton(
                        text = "Get Started",
                        onClick = onGetStartedClick,
                        enabled = termsAccepted,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF1E222B) else Color.White
                            )
                        }
                    )
                }
            }
        }
    }

    // Terms and Conditions Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text(
                    text = "Terms & Conditions",
                    color = AccentColor,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. Acceptance of Terms\nBy logging in or registering with Hostivo, you agree to comply with and be bound by these Terms and Conditions.",
                        color = textColor,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = "2. User Registration & Security\nTo access student hostel features, you must provide your Roll Number, Phone Number, Father's Name, and Emergency Contact. You are responsible for ensuring details are accurate.",
                        color = textColor,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = "3. Profile Lock Policy\nWarning: Once registered, your Roll Number, Father's Name, and Phone Numbers are locked. If any correction is required, you must meet the hostel Warden in person.",
                        color = textColor,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Text(
                        text = "4. Code of Conduct\nStudents must adhere to hostel regulations, gate curfew timings, and mess rules. Digital Gate Pass requests are subject to Warden authorization.",
                        color = textColor,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        termsAccepted = true
                        sharedPrefs.edit().putBoolean("termsAccepted", true).apply()
                        showTermsDialog = false
                    }
                ) {
                    Text("I AGREE", color = AccentColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("CLOSE", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
