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
import androidx.compose.material.icons.filled.Build
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
import androidx.compose.ui.draw.clip
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.ComplaintTicket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenComplaintsBoardScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()
    val context = LocalContext.current

    // Show complaints scoped to Boys Warden (A&B) or Girls Warden (C&D) (Section 6)
    val tickets by remember {
        derivedStateOf {
            val targetGender = if (HostelDataStore.currentWardenScope == "Girls") "Female" else "Male"
            HostelDataStore.complaints.filter { it.gender == targetGender }
        }
    }

    var selectedTicketForAssign by remember { mutableStateOf<ComplaintTicket?>(null) }
    var selectedHandyman by remember { mutableStateOf("") }
    var showAssignDialog by remember { mutableStateOf(false) }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "COMPLAINTS BOARD",
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
                
                if (tickets.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text("No complaints logged.", color = subTextColor)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(tickets, key = { it.id }) { ticket ->
                            val isResolved = ticket.status == "RESOLVED"
                            val isAssigned = ticket.status == "Assigned"

                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(ticket.studentName, color = textColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("Hostel Hostel Type: ${if (ticket.gender == "Female") "Girls Hostel" else "Boys Hostel"}", color = subTextColor, fontSize = 11.sp)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    when (ticket.status) {
                                                        "RESOLVED" -> Color(0xFF027E3D).copy(alpha = 0.15f)
                                                        "Assigned" -> Color(0xFF005B5C).copy(alpha = 0.15f)
                                                        else -> Color(0xFF93000A).copy(alpha = 0.15f)
                                                    },
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                ticket.status.uppercase(),
                                                color = when (ticket.status) {
                                                    "RESOLVED" -> Color(0xFF76DB8F)
                                                    "Assigned" -> Color(0xFF29FCF3)
                                                    else -> Color(0xFFFFB4AB)
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                    Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                                    Text("Category: ${ticket.category}", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(ticket.title, color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(ticket.description, color = subTextColor, style = MaterialTheme.typography.bodyMedium)
                                    
                                    if (ticket.assignedHandyman.isNotEmpty()) {
                                        Text("Assigned to: ${ticket.assignedHandyman}", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (ticket.status == "Pending") {
                                            Button(
                                                onClick = {
                                                    selectedTicketForAssign = ticket
                                                    selectedHandyman = ""
                                                    showAssignDialog = true
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                ),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Build, contentDescription = null, tint = if (isDark) Color(0xFF003735) else Color.White, modifier = Modifier.size(16.dp))
                                                    Text("Assign Handyman", color = if (isDark) Color(0xFF003735) else Color.White)
                                                }
                                            }
                                        } else if (ticket.status == "Assigned") {
                                            Button(
                                                onClick = {
                                                    val updatedTicket = ticket.copy(status = "RESOLVED")
                                                    HostelDataStore.saveComplaint(updatedTicket)
                                                    
                                                    val matchedStudent = HostelDataStore.students.find { it.name == ticket.studentName }
                                                    matchedStudent?.let { std ->
                                                        val notification = com.example.hprams.data.NotificationItem(
                                                            id = "NTF-${(1000..9999).random()}",
                                                            userId = std.roll,
                                                            title = "Complaint Status: Resolved",
                                                            message = "Your complaint '${ticket.category}' has been marked as RESOLVED.",
                                                            type = "COMPLAINT",
                                                            timestamp = "18 Aug 2026",
                                                            deepLink = "complaints_list"
                                                        )
                                                        HostelDataStore.saveNotification(notification, context)
                                                    }
                                                    Toast.makeText(context, "Complaint Ticket Resolved!", Toast.LENGTH_SHORT).show()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF027E3D)
                                                ),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Mark Resolved", color = Color.White)
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

    if (showAssignDialog && selectedTicketForAssign != null) {
        AlertDialog(
            onDismissRequest = { showAssignDialog = false },
            title = { Text("Assign Handyman", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a handyman to resolve this issue:", color = subTextColor)
                    HostelDataStore.handymen.forEach { name ->
                        val isSelected = selectedHandyman == name
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) {
                                        if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                    } else {
                                        if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)
                                    }
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) {
                                        if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                    } else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedHandyman = name }
                                .padding(12.dp)
                        ) {
                            Text(name, color = textColor, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedHandyman.isNotEmpty()) {
                              selectedTicketForAssign?.let { ticket ->
                                  val updatedTicket = ticket.copy(
                                      status = "Assigned",
                                      assignedHandyman = selectedHandyman
                                  )
                                  HostelDataStore.saveComplaint(updatedTicket)
                                  
                                  val matchedStudent = HostelDataStore.students.find { it.name == ticket.studentName }
                                  matchedStudent?.let { std ->
                                      val notification = com.example.hprams.data.NotificationItem(
                                          id = "NTF-${(1000..9999).random()}",
                                          userId = std.roll,
                                          title = "Complaint Status: Assigned",
                                          message = "Your complaint '${ticket.category}' has been assigned to ${selectedHandyman}.",
                                          type = "COMPLAINT",
                                          timestamp = "18 Aug 2026",
                                          deepLink = "complaints_list"
                                      )
                                      HostelDataStore.saveNotification(notification, context)
                                  }
                              }
                            Toast.makeText(context, "Handyman assigned successfully!", Toast.LENGTH_SHORT).show()
                            showAssignDialog = false
                        } else {
                            Toast.makeText(context, "Please select a handyman", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                    )
                ) {
                    Text("Confirm Assign", color = if (isDark) Color(0xFF003735) else Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
