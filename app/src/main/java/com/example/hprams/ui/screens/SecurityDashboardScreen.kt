package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.*
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.GatePassRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityDashboardScreen(
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()
    val context = LocalContext.current

    // Display all approved passes (Unified Security Gate) using derivedStateOf
    val approvedPasses by remember { derivedStateOf { HostelDataStore.gatePassRequests.filter { it.wardenApproval == "Approved" } } }
    val lateLogs by remember { derivedStateOf { HostelDataStore.gatePassRequests.filter { it.isLate } } }

    var activeTab by remember { mutableStateOf("gate") } // "gate" or "profile"
    var securityName by remember { mutableStateOf("Gate Officer Kumar") }
    var showProfileEditDialog by remember { mutableStateOf(false) }

    var activePassForIn by remember { mutableStateOf<GatePassRequest?>(null) }
    var lateRemarksText by remember { mutableStateOf("") }
    var showInRemarksDialog by remember { mutableStateOf(false) }

    // Helper to get current time string
    fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    // Helper to check if current time is after 7:00 PM
    fun isCurrentTimeAfter7PM(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return hour >= 19 // 7 PM
    }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                var showDropdownMenu by remember { mutableStateOf(false) }
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "GATE SECURITY CONSOLE",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showDropdownMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "Menu Options", tint = textColor)
                            }
                            DropdownMenu(
                                expanded = showDropdownMenu,
                                onDismissRequest = { showDropdownMenu = false },
                                modifier = Modifier.background(if (isDark) Color(0xFF141C27) else Color(0xFFF3F6F6))
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit Security Profile", color = textColor) },
                                    onClick = {
                                        showDropdownMenu = false
                                        showProfileEditDialog = true
                                    },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = textColor) }
                                )
                                DropdownMenuItem(
                                    text = { Text("Logout", color = textColor) },
                                    onClick = {
                                        showDropdownMenu = false
                                        onSignOutClick()
                                    },
                                    leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = textColor) }
                                )
                            }
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
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (activeTab == "gate") {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        // Header details
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "Campus Gate Monitoring",
                                        color = textColor,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Outings: Allowed 07:00 AM - 07:00 PM. Curfew Alert: 07:00 PM.",
                                        color = subTextColor,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        // Approved Outing / Leave Passes
                        item {
                            Text(
                                "Approved Outing / Leave Passes",
                                color = textColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (approvedPasses.isEmpty()) {
                            item {
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Text("No approved gate passes active.", color = subTextColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                                }
                            }
                        } else {
                            items(approvedPasses, key = { it.id }) { pass ->
                                GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(pass.studentName, color = textColor, fontWeight = FontWeight.Bold)
                                                Text("${pass.studentRoll} (${pass.gender})", color = subTextColor, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(pass.type.uppercase(), color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                                        
                                        if (pass.type == "Leave") {
                                            Text("Place: ${pass.placeOfGoing} | Parent: ${pass.parentName} (${pass.parentPhone})", color = textColor, fontSize = 12.sp)
                                        } else {
                                            Text("Place: ${pass.placeOfGoing} | Out: ${pass.outTime} - In: ${pass.inTime}", color = textColor, fontSize = 12.sp)
                                        }

                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Column {
                                                Text("Check Out Time", color = subTextColor, style = MaterialTheme.typography.bodySmall)
                                                Text(pass.checkoutTime, color = textColor, fontWeight = FontWeight.Bold)
                                            }
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text("Check In Time", color = subTextColor, style = MaterialTheme.typography.bodySmall)
                                                Text(pass.checkinTime, color = textColor, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            if (pass.checkoutTime == "--:--") {
                                                Button(
                                                    onClick = {
                                                        pass.checkoutTime = getCurrentTimeString()
                                                        Toast.makeText(context, "Logged Out at ${pass.checkoutTime}!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Stamp Out", color = if (isDark) Color(0xFF003735) else Color.White)
                                                }
                                            }

                                            if (pass.checkoutTime != "--:--" && pass.checkinTime == "--:--") {
                                                Button(
                                                    onClick = {
                                                        activePassForIn = pass
                                                        lateRemarksText = ""
                                                        showInRemarksDialog = true
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF027E3D)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Stamp In", color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Late warnings alerts list
                        if (lateLogs.isNotEmpty()) {
                            item {
                                Text(
                                    "Late Check-In Alerts Log",
                                    color = Color(0xFFFFB4AB),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                             items(lateLogs, key = { it.id }) { log ->
                                 GlassCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(log.studentName, color = textColor, fontWeight = FontWeight.Bold)
                                            Text(log.studentRoll, color = subTextColor, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                                        }
                                        Text("Checked In Late at: ${log.checkinTime}", color = Color(0xFFFFB4AB), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Remarks: ${log.lateRemarks}", color = textColor, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Profile/Logout screen
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .background(
                                            if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                                            RoundedCornerShape(40.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(40.dp), tint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
                                }
                                Text(securityName, color = textColor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = { showProfileEditDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) Color(0xFF29FCF3).copy(alpha = 0.1f) else Color(0xFF006A66).copy(alpha = 0.1f)
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Edit Profile", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontSize = 11.sp)
                                    }
                                }
                                Text(
                                    "Duty: Main Campus Gate",
                                    color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Assigned Block Wing", color = subTextColor)
                                    Text(HostelDataStore.securityBlockAssignment, color = textColor, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Shift Schedule", color = subTextColor)
                                    Text(HostelDataStore.securityDutyTimings, color = textColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Daily attendance check-in card (Section 6)
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Daily Presence Check-In", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    "Mark your check-in once you reach your assigned wing gate for security logging.",
                                    color = subTextColor,
                                    fontSize = 11.sp
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Status:", color = textColor, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (HostelDataStore.securityIsPresentToday) Color(0xFF027E3D).copy(alpha = 0.15f) else Color(0xFF93000A).copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            if (HostelDataStore.securityIsPresentToday) "MARKED PRESENT" else "ABSENT",
                                            color = if (HostelDataStore.securityIsPresentToday) Color(0xFF76DB8F) else Color(0xFFFFB4AB),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                if (!HostelDataStore.securityIsPresentToday) {
                                    Button(
                                        onClick = {
                                            HostelDataStore.securityIsPresentToday = true
                                            Toast.makeText(context, "Checked in successfully for duty!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Mark Present Today", color = if (isDark) Color(0xFF003735) else Color.White)
                                    }
                                }
                            }
                        }

                        GlassButton(
                            text = "Log Out",
                            onClick = onSignOutClick,
                            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = if (isDark) Color(0xFF003735) else Color.White) }
                        )
                    }
                }

                // Profile Edit Dialog (Section 10)
                if (showProfileEditDialog) {
                    AlertDialog(
                        onDismissRequest = { showProfileEditDialog = false },
                        title = { Text("Edit Security Profile", color = textColor, fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = securityName,
                                    onValueChange = { securityName = it },
                                    label = { Text("Security Guard Name") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showProfileEditDialog = false
                                    Toast.makeText(context, "Security profile updated successfully!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                )
                            ) {
                                Text("Save Changes", color = if (isDark) Color(0xFF003735) else Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showProfileEditDialog = false }) {
                                Text("Cancel", color = subTextColor)
                            }
                        },
                        containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
                        shape = RoundedCornerShape(16.dp)
                    )
                }

                // Bottom tab selector for Security Dashboard
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.9f))
                        .border(
                            1.dp,
                            if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                        )
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val isGate = activeTab == "gate"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { activeTab = "gate" }
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = if (isGate) (if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)) else subTextColor)
                        Text("Gate Controls", color = if (isGate) (if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)) else subTextColor, fontSize = 10.sp)
                    }

                    val isProfile = activeTab == "profile"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { activeTab = "profile" }
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = if (isProfile) (if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)) else subTextColor)
                        Text("My Profile", color = if (isProfile) (if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)) else subTextColor, fontSize = 10.sp)
                    }
                }
            }
        }
    }

    // Stamp In Remarks Dialog
    if (showInRemarksDialog && activePassForIn != null) {
        val isLateArrival = isCurrentTimeAfter7PM()

        AlertDialog(
            onDismissRequest = { showInRemarksDialog = false },
            title = {
                Text(
                    if (isLateArrival) "Warning: Late Return Check-In" else "Student Gate Check-In",
                    color = if (isLateArrival) Color(0xFFFFB4AB) else textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        if (isLateArrival) "curfew check-in after 7:00 PM detected. Please log a late remarks description below to notify the Hostel Warden."
                        else "Enter remarks (optional) for the student's entry log:",
                        color = subTextColor,
                        fontSize = 14.sp
                    )
                    OutlinedTextField(
                        value = lateRemarksText,
                        onValueChange = { lateRemarksText = it },
                        label = { Text("Log Description / Remarks") },
                        placeholder = { Text(if (isLateArrival) "e.g. Traffic Delay, Curfew Late" else "e.g. Returned on time") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        activePassForIn?.let { pass ->
                            pass.checkinTime = getCurrentTimeString()
                            pass.lateRemarks = lateRemarksText.ifEmpty { if (isLateArrival) "Late check-in without reason" else "Normal check-in" }
                            pass.isLate = isLateArrival
                        }
                        Toast.makeText(context, "Check-in logged successfully!", Toast.LENGTH_SHORT).show()
                        showInRemarksDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLateArrival) Color(0xFF93000A) else (if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
                    )
                ) {
                    Text("Confirm Stamp", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showInRemarksDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
