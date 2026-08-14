package com.example.hprams.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.GlassBackground
import com.example.hprams.ui.components.GlassButton
import com.example.hprams.ui.components.GlassCard
import com.example.hprams.ui.components.GlassTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewComplaintScreen(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "NEW COMPLAINT",
                            color = Color(0xFF29FCF3),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = "Category (Electrical, Plumbing, Wi-Fi)"
                        )

                        GlassTextField(
                            value = roomNumber,
                            onValueChange = { roomNumber = it },
                            label = "Room Number"
                        )

                        GlassTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Detailed Description"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        GlassButton(
                            text = "Submit Ticket",
                            onClick = onSubmitClick,
                            icon = {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color(0xFF003735))
                            }
                        )
                    }
                }
            }
        }
    }
}
