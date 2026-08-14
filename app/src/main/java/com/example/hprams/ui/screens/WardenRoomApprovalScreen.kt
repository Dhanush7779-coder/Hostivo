package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.*
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.RoomChangeRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenRoomApprovalScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()
    val context = LocalContext.current

    // Show pending room change requests scoped to Boys Warden (A&B) or Girls Warden (C&D) (Section 6)
    val pendingRequests by remember {
        derivedStateOf {
            val targetGender = if (HostelDataStore.currentWardenScope == "Girls") "Female" else "Male"
            HostelDataStore.roomChangeRequests.filter { it.status == "Pending" && it.gender == targetGender }
        }
    }

    var selectedRequestForReject by remember { mutableStateOf<RoomChangeRequest?>(null) }
    var rejectReasonText by remember { mutableStateOf("") }
    var showRejectDialog by remember { mutableStateOf(false) }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "ROOM APPROVALS",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
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
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                
                if (pendingRequests.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No pending room requests.", color = subTextColor)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(pendingRequests, key = { it.id }) { req ->
                            // Look up student fee status from database
                            val student = HostelDataStore.students.find { it.roll == req.studentRoll }
                            val feePaid = student?.feePaidStatus == "Paid"

                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(req.studentName, color = textColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Text("Hostel Hostel Type: ${if (req.gender == "Female") "Girls Hostel" else "Boys Hostel"}", color = subTextColor, fontSize = 11.sp)
                                        }
                                        Text(req.studentRoll, color = subTextColor, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Current Room", color = subTextColor)
                                        Text(req.currentRoom, color = textColor, fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Requested Room", color = subTextColor)
                                        Text(req.requestedRoom, color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontWeight = FontWeight.Bold)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Fee Payment Status", color = subTextColor)
                                        Text(
                                            text = if (feePaid) "FEES PAID" else "FEES PENDING",
                                            color = if (feePaid) Color(0xFF76DB8F) else Color(0xFFFFB4AB),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                // Approve Request
                                                req.status = "Approved"
                                                // Update student room assignment in database
                                                student?.let { s ->
                                                    s.feePaidStatus = "Paid" // mark fee paid on approval context
                                                }
                                                Toast.makeText(context, "Request Approved!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF027E3D)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Text("Approve", color = Color.White)
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                // Reject Request (Open Dialog for reason)
                                                selectedRequestForReject = req
                                                rejectReasonText = ""
                                                showRejectDialog = true
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF93000A)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                Text("Reject", color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRejectDialog && selectedRequestForReject != null) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reason for Rejection", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = rejectReasonText,
                    onValueChange = { rejectReasonText = it },
                    placeholder = { Text("Enter reason (e.g. Fees pending)") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectReasonText.isNotEmpty()) {
                            selectedRequestForReject?.let { req ->
                                req.status = "Rejected"
                                req.rejectReason = rejectReasonText
                            }
                            Toast.makeText(context, "Request Rejected!", Toast.LENGTH_SHORT).show()
                            showRejectDialog = false
                        } else {
                            Toast.makeText(context, "Please specify a reason", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                    )
                ) {
                    Text("Confirm Reject", color = if (isDark) Color(0xFF003735) else Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
