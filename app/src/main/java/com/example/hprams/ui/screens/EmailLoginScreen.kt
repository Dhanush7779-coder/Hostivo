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
import com.example.hprams.data.HostelDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailLoginScreen(
    onBackClick: () -> Unit,
    onSignInClick: (String, String, String) -> Unit, // email, password, role
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    var selectedRole by remember { mutableStateOf("Student") }
    val roles = listOf("Student", "Warden", "Admin", "Security")

    // For Student role, allow selecting which student profile to use for testing
    var selectedStudentRoll by remember { mutableStateOf("231801380001") } // Alex Vance (Female)
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
                            text = "Nest Campus",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
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
                        // Role Selector
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Select Role",
                                color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                            ) {
                                roles.forEach { role ->
                                    val isSelected = selectedRole == role
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSelected) {
                                                    if (isDark) Color(0xFF29FCF3).copy(alpha = 0.2f) else Color(0xFF006A66).copy(alpha = 0.2f)
                                                } else {
                                                    if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
                                                },
                                                RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) {
                                                    if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                } else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedRole = role }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = role,
                                            color = if (isSelected) {
                                                if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                            } else subTextColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Student selection switcher for debugging purposes
                        if (selectedRole == "Student") {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Select Testing Student Profile",
                                    color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf(
                                        "231801380001" to "Alex Vance (Female)",
                                        "231801380002" to "Dhanush Kumar (Male)"
                                    ).forEach { (roll, label) ->
                                        val isSelected = selectedStudentRoll == roll
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isSelected) {
                                                        if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                                    } else {
                                                        if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
                                                    },
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) {
                                                        if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                    } else Color.Transparent,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedStudentRoll = roll }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                color = if (isSelected) {
                                                    if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                } else subTextColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Warden scope selector for testing Boys Warden vs Girls Warden (Section 6)
                        if (selectedRole == "Warden") {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Select Warden Scope",
                                    color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf(
                                        "Boys" to "Boys (Block A & B)",
                                        "Girls" to "Girls (Block C & D)"
                                    ).forEach { (scope, label) ->
                                        val isSelected = selectedWardenScope == scope
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isSelected) {
                                                        if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                                    } else {
                                                        if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
                                                    },
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) {
                                                        if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                    } else Color.Transparent,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedWardenScope = scope }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = label,
                                                color = if (isSelected) {
                                                    if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                } else subTextColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Email Field
                        GlassTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Institutional Email",
                            placeholder = "john.doe@hprams.edu",
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
                                HostelDataStore.currentRole = selectedRole
                                if (selectedRole == "Student") {
                                    HostelDataStore.currentStudentRoll = selectedStudentRoll
                                }
                                if (selectedRole == "Warden") {
                                    HostelDataStore.currentWardenScope = selectedWardenScope
                                }
                                onSignInClick(email, password, selectedRole)
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
