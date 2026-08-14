package com.example.hprams.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.GlassBackground
import com.example.hprams.ui.components.GlassButton
import com.example.hprams.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestDashboardScreen(
    onSignOutClick: () -> Unit,
    onHostelInfoClick: () -> Unit,
    onAppStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "GUEST DASHBOARD",
                            color = Color(0xFF29FCF3),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = onSignOutClick) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Welcome, Guest",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Explore hostel rooms and track your allocation application.",
                            color = Color(0xFFB9CAC8),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                GlassButton(
                    text = "Hostel Information",
                    onClick = onHostelInfoClick,
                    icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF003735)) }
                )

                GlassButton(
                    text = "Application Status",
                    onClick = onAppStatusClick,
                    icon = { Icon(Icons.Default.List, contentDescription = null, tint = Color(0xFF003735)) }
                )
            }
        }
    }
}
