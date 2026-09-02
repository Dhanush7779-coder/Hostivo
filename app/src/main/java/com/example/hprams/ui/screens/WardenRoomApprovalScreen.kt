package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.RoomChangeRequest
import com.example.hprams.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenRoomApprovalScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val pendingRequests by remember {
        derivedStateOf {
            val targetGender = if (HostelDataStore.currentWardenScope == "Girls") "Female" else "Male"
            HostelDataStore.roomChangeRequests.filter { it.status == "Pending" && it.gender == targetGender }
        }
    }

    var selectedRequestForReject by remember { mutableStateOf<RoomChangeRequest?>(null) }
    var rejectReasonText by remember { mutableStateOf("") }
    var showRejectDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ModernBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            ModernSubPageHeader(
                title = "Room Allocations & Shifts",
                subtitle = "Review and approve student room changes",
                onBackClick = onBackClick
            )

            if (pendingRequests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.DoneAll, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("All room change requests are cleared!", color = Color(0xFF64748B), fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(pendingRequests, key = { it.id }) { req ->
                        val student = HostelDataStore.students.find { it.roll == req.studentRoll }

                        ModernSectionCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEEF2FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(req.studentName, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("Roll: ${req.studentRoll} • Shift: Rm ${req.currentRoom} -> Rm ${req.requestedRoom}", color = Color(0xFF64748B), fontSize = 12.sp)
                                    }
                                }
                            }

                            Divider(color = Color(0xFFF1F5F9))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val isPaid = student?.feePaidStatus?.equals("Paid", ignoreCase = true) == true
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isPaid) Color(0xFFECFDF5) else Color(0xFFFEF3C7))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isPaid) "FEE PAID" else "FEE PENDING",
                                        color = if (isPaid) Color(0xFF10B981) else Color(0xFFD97706),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            selectedRequestForReject = req
                                            rejectReasonText = ""
                                            showRejectDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F2)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text("Reject", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            req.status = "Approved"
                                            student?.let { s ->
                                                val updatedStudent = s.copy(room = req.requestedRoom)
                                                HostelDataStore.saveStudent(updatedStudent)
                                            }
                                            HostelDataStore.saveRoomChange(req)
                                            Toast.makeText(context, "Room shift approved for ${req.studentName}!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                    ) {
                                        Text("Approve", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            title = {
                Text("Reject Room Change", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Provide a reason for rejecting the room request:", color = Color(0xFF64748B), fontSize = 13.sp)
                    HostivoTextField(
                        value = rejectReasonText,
                        onValueChange = { rejectReasonText = it },
                        label = "Reason",
                        placeholder = "e.g. Room at full capacity"
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val req = selectedRequestForReject ?: return@Button
                        req.status = "Rejected"
                        req.rejectReason = rejectReasonText
                        HostelDataStore.saveRoomChange(req)
                        Toast.makeText(context, "Room request rejected", Toast.LENGTH_SHORT).show()
                        showRejectDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E))
                ) {
                    Text("Confirm Reject", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
