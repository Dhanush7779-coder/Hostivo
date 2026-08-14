package com.example.hprams.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
fun HostelApplicationScreen(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var blockPref by remember { mutableStateOf("") }
    var roomTypePref by remember { mutableStateOf("") }
    var roommatePref by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "HOSTEL APPLICATION",
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
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            "Preference Details",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        GlassTextField(
                            value = blockPref,
                            onValueChange = { blockPref = it },
                            label = "Preferred Hostel Block (e.g. Block A, B)"
                        )

                        GlassTextField(
                            value = roomTypePref,
                            onValueChange = { roomTypePref = it },
                            label = "Preferred Room Type (Single / Double Sharing)"
                        )

                        GlassTextField(
                            value = roommatePref,
                            onValueChange = { roommatePref = it },
                            label = "Preferred Roommate Roll Number (Optional)"
                        )

                        GlassTextField(
                            value = remarks,
                            onValueChange = { remarks = it },
                            label = "Any specific medical or general remarks"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        GlassButton(
                            text = "Submit Application",
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
