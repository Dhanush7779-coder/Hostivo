package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.HostelDataStore
import com.example.hprams.ui.components.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailLoginScreen(
    onBackClick: () -> Unit,
    onSignInClick: (String, String, String) -> Unit, // email, password, role
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit = {},
    onGoogleSuccess: ((String, String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Google Sign-In Client configuration
    val token = "603856360970-b0pof4kakv3lugvtos3fe612mlka2p34.apps.googleusercontent.com"
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val gEmail = account?.email ?: ""
            val gDisplayName = account?.displayName ?: ""
            if (gEmail.isNotEmpty()) {
                onGoogleSuccess?.invoke(gEmail, gDisplayName)
            } else {
                Toast.makeText(context, "Google Sign-In returned empty email.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Google Sign-In failed: ${e.localizedMessage ?: "Unknown Error"}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    HostivoBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Header with H O S T I V O and underline bar
            HostivoHeader(
                title = "Welcome back",
                subtitle = "Please enter your details to access your account."
            )

            Spacer(modifier = Modifier.height(28.dp))

            // White Floating Card with Rounded Corners
            HostivoCard {
                // Email Address
                HostivoTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "name@hostivo.edu",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Password
                HostivoTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle password visibility",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                )

                // Forgot Password link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forgot password?",
                        color = Color(0xFF374151),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Sign In Button
                HostivoPrimaryButton(
                    text = "Sign In",
                    isLoading = isLoading,
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Please enter your email and password.", Toast.LENGTH_SHORT).show()
                            return@HostivoPrimaryButton
                        }
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

                // Divider: OR CONTINUE WITH
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE5E7EB)
                    )
                    Text(
                        text = "  OR CONTINUE WITH  ",
                        color = Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE5E7EB)
                    )
                }

                // Google Button (Apple login removed per specification)
                HostivoGoogleButton(
                    onClick = {
                        googleSignInClient.signOut().addOnCompleteListener {
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom prompt: New to Hostivo? Create account
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "New to Hostivo? ",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Create account",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onSignUpClick)
                )
            }
        }
    }
}
