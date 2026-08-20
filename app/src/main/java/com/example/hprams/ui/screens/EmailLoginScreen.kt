package com.example.hprams.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.hprams.R
import com.example.hprams.ui.components.*
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.theme.AccentColor
import com.example.hprams.data.HostelDataStore

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailLoginScreen(
    onBackClick: () -> Unit,
    onSignInClick: (String, String, String) -> Unit, // email, password, role
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var selectedRole by remember { mutableStateOf("Student") }
    val roles = listOf("Student", "Warden", "Admin", "Security")

    // For Student role, allow selecting which student profile to use for testing
    var selectedStudentRoll by remember { mutableStateOf("231801380007") } // C venkat Dhanush (Male)
    var selectedWardenScope by remember { mutableStateOf("Boys") } // Boys or Girls

    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()

    GlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding()
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 450.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nest_campus_logo),
                            contentDescription = "Nest Campus Logo",
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Hostivo",
                            color = AccentColor,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Sign in to manage your hostel profile",
                            color = subTextColor,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        // Email Field
                        GlassTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Institutional Email",
                            placeholder = "john.doe@hostivo.edu",
                            leadingIcon = Icons.Default.Mail,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        // Password Field
                        GlassTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            placeholder = "••••••••",
                            leadingIcon = Icons.Default.Lock,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = "Toggle password visibility",
                                        tint = subTextColor
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Submit
                        GlassButton(
                            text = "Sign In",
                            onClick = {
                                val trimmedEmail = email.trim().lowercase()
                                val matchedStudent = HostelDataStore.students.find { it.email.lowercase() == trimmedEmail }
                                val resolvedRole = when {
                                    trimmedEmail == "ammananasanju@gmail.com" -> {
                                        HostelDataStore.adminName = "C.Venkat Dhanush"
                                        "Admin"
                                    }
                                    matchedStudent != null -> {
                                        HostelDataStore.currentStudentRoll = matchedStudent.roll
                                        matchedStudent.role
                                    }
                                    trimmedEmail == "ramprasad@hostivo.edu" -> {
                                        HostelDataStore.currentWardenScope = "Boys"
                                        "Warden"
                                    }
                                    trimmedEmail == "ramesh@hostivo.edu" -> {
                                        HostelDataStore.securityOfficerName = "Ramesh Kumar"
                                        "Security"
                                    }
                                    else -> "Student"
                                }
                                HostelDataStore.currentRole = resolvedRole
                                onSignInClick(email, password, resolvedRole)
                            }
                        )

                        // Toggle Register
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Don't have an account? ",
                                color = subTextColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Sign Up",
                                color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable(onClick = onSignUpClick)
                            )
                        }
                    }
                }
            }
        }
    }
}
