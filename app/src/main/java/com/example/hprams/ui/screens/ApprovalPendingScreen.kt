package com.example.hprams.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.*

@Composable
fun ApprovalPendingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HostivoBackground(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding(),
            contentAlignment = Alignment.Center
        ) {
            HostivoCard(modifier = Modifier.widthIn(max = 400.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = "Pending Approval",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(56.dp)
                    )

                    Text(
                        text = "Approval Pending",
                        color = Color(0xFF111827),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Your account registration has been submitted and is currently awaiting approval by the Administrator (C. Venkat Dhanush).",
                        color = Color(0xFF4B5563),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "Once approved, you will be able to sign in immediately using your registered email and password.",
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    HostivoPrimaryButton(
                        text = "Back to Sign In",
                        onClick = onBackClick
                    )
                }
            }
        }
    }
}
