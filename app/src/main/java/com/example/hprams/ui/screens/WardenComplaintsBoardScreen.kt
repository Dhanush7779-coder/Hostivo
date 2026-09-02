package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.hprams.data.ComplaintTicket
import com.example.hprams.data.HostelDataStore
import com.example.hprams.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenComplaintsBoardScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val tickets by remember {
        derivedStateOf {
            val targetGender = if (HostelDataStore.currentWardenScope == "Girls") "Female" else "Male"
            HostelDataStore.complaints.filter { it.gender == targetGender }
        }
    }

    var selectedTicketForAssign by remember { mutableStateOf<ComplaintTicket?>(null) }
    var selectedHandyman by remember { mutableStateOf("") }
    var showAssignDialog by remember { mutableStateOf(false) }

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
                title = "Complaints Board",
                subtitle = "Manage student tickets & handyman assignments",
                onBackClick = onBackClick
            )

            if (tickets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.TaskAlt, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No complaints logged for this hostel.", color = Color(0xFF64748B), fontSize = 14.sp)
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
                    items(tickets, key = { it.id }) { ticket ->
                        val isResolved = ticket.status.equals("Resolved", ignoreCase = true)
                        val isAssigned = ticket.status.equals("In Progress", ignoreCase = true) || ticket.assignedHandyman.isNotEmpty()

                        ModernSectionCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFAF5FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(ticket.title, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${ticket.studentName} • ${ticket.category} • ${ticket.date}", color = Color(0xFF64748B), fontSize = 12.sp)
                                    }
                                }

                                val statusColor = if (isResolved) Color(0xFF10B981) else if (isAssigned) Color(0xFF6366F1) else Color(0xFFD97706)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(statusColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = ticket.status.uppercase(),
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            if (ticket.description.isNotEmpty()) {
                                Text(
                                    text = ticket.description,
                                    color = Color(0xFF475569),
                                    fontSize = 13.sp
                                )
                            }

                            Divider(color = Color(0xFFF1F5F9))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (ticket.assignedHandyman.isNotEmpty()) {
                                    Text(
                                        text = "Assigned: ${ticket.assignedHandyman}",
                                        color = Color(0xFF6366F1),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                } else {
                                    Text(
                                        text = "Unassigned",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (!isResolved) {
                                        Button(
                                            onClick = {
                                                selectedTicketForAssign = ticket
                                                selectedHandyman = ticket.assignedHandyman.ifEmpty { "Electrician - Ramesh" }
                                                showAssignDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF)),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Assign", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                ticket.status = "Resolved"
                                                HostelDataStore.saveComplaint(ticket)
                                                Toast.makeText(context, "Complaint marked as Resolved!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("Resolve", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

    if (showAssignDialog && selectedTicketForAssign != null) {
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = {
                Text("Assign Handyman", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a staff member to resolve this issue:", color = Color(0xFF64748B), fontSize = 13.sp)
                    listOf(
                        "Electrician - Ramesh (+91 9848011223)",
                        "Plumber - Suresh (+91 9848022334)",
                        "Carpenter - Mohan (+91 9848033445)",
                        "Housekeeping - Lakshmi (+91 9848044556)"
                    ).forEach { worker ->
                        val isSel = selectedHandyman == worker
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(0xFFEEF2FF) else Color(0xFFF8FAFC))
                                .border(1.dp, if (isSel) Color(0xFF6366F1) else Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                .clickable { selectedHandyman = worker }
                                .padding(12.dp)
                        ) {
                            Text(
                                text = worker,
                                color = if (isSel) Color(0xFF6366F1) else Color(0xFF0F172A),
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ticket = selectedTicketForAssign ?: return@Button
                        ticket.assignedHandyman = selectedHandyman.substringBefore(" (")
                        ticket.status = "In Progress"
                        HostelDataStore.saveComplaint(ticket)
                        Toast.makeText(context, "Assigned to ${ticket.assignedHandyman}", Toast.LENGTH_SHORT).show()
                        showAssignDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("Assign", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
