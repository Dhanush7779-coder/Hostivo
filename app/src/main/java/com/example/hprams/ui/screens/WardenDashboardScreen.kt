package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.*
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.GatePassRequest
import com.example.hprams.data.FineItem
import com.example.hprams.data.AnnouncementItem
import com.example.hprams.theme.isAppDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenDashboardScreen(
    onSignOutClick: () -> Unit,
    onAllocationsClick: () -> Unit,
    onComplaintsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showFineDialog by remember { mutableStateOf(false) }
    var showAnnouncementDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()

    var showProfileEditDialog by remember { mutableStateOf(false) }
    var wardenProfileName by remember {
        mutableStateOf(
            if (HostelDataStore.currentWardenScope == "Girls") HostelDataStore.blockBWardenName
            else HostelDataStore.blockAWardenName
        )
    }
    var wardenProfilePhone by remember {
        mutableStateOf(
            if (HostelDataStore.currentWardenScope == "Girls") HostelDataStore.blockBWardenPhone
            else HostelDataStore.blockAWardenPhone
        )
    }

    val targetGender = if (HostelDataStore.currentWardenScope == "Girls") "Female" else "Male"

    // Scoped counts using derivedStateOf to avoid lag (Section 6)
    val pendingRequestsCount by remember(targetGender) { derivedStateOf { HostelDataStore.roomChangeRequests.filter { it.status == "Pending" && it.gender == targetGender }.size } }
    val pendingComplaintsCount by remember(targetGender) { derivedStateOf { HostelDataStore.complaints.filter { it.status == "Pending" && it.gender == targetGender }.size } }
    val pendingPasses by remember(targetGender) { derivedStateOf { HostelDataStore.gatePassRequests.filter { it.wardenApproval == "Pending" && it.gender == targetGender } } }
    
    // late logs warning alerts using derivedStateOf
    val latePassAlerts by remember(targetGender) { derivedStateOf { HostelDataStore.gatePassRequests.filter { it.isLate && it.gender == targetGender } } }

    // Forms
    var fineRoll by remember { mutableStateOf("231801380001") }
    var fineAmount by remember { mutableStateOf("") }
    var fineReason by remember { mutableStateOf("") }

    var annTitle by remember { mutableStateOf("") }
    var annCategory by remember { mutableStateOf("GENERAL NOTICE") }
    var annContent by remember { mutableStateOf("") }

    var chiefWardenName by remember { mutableStateOf(HostelDataStore.chiefWardenName) }
    var chiefWardenPhone by remember { mutableStateOf(HostelDataStore.chiefWardenPhone) }
    var blockAWardenName by remember { mutableStateOf(HostelDataStore.blockAWardenName) }
    var blockAWardenPhone by remember { mutableStateOf(HostelDataStore.blockAWardenPhone) }
    var blockBWardenName by remember { mutableStateOf(HostelDataStore.blockBWardenName) }
    var blockBWardenPhone by remember { mutableStateOf(HostelDataStore.blockBWardenPhone) }
    var tiffinTiming by remember { mutableStateOf(HostelDataStore.tiffinTiming) }
    var lunchTiming by remember { mutableStateOf(HostelDataStore.lunchTiming) }
    var dinnerTiming by remember { mutableStateOf(HostelDataStore.dinnerTiming) }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "WARDEN PANEL",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = onSignOutClick) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = textColor)
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Welcome Card
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Welcome, Hostel Warden",
                            color = textColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Scope: ${HostelDataStore.currentWardenScope} Hostel Portal (Block ${if (HostelDataStore.currentWardenScope == "Girls") "C & D" else "A & B"})",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Warden: $wardenProfileName", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Phone: $wardenProfilePhone", color = subTextColor, fontSize = 11.sp)
                            }
                            Button(
                                onClick = { showProfileEditDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Edit Profile", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Statistics
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Room Requests", color = subTextColor, style = MaterialTheme.typography.labelSmall)
                            Text("$pendingRequestsCount Pending", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Complaints", color = subTextColor, style = MaterialTheme.typography.labelSmall)
                            Text("$pendingComplaintsCount Pending", color = Color(0xFFFFB4AB), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Action Buttons
                GlassButton(
                    text = "Room Requests approvals",
                    onClick = onAllocationsClick,
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = if (isDark) Color(0xFF003735) else Color.White) }
                )

                GlassButton(
                    text = "Complaints Board",
                    onClick = onComplaintsClick,
                    icon = { Icon(Icons.Default.Assignment, contentDescription = null, tint = if (isDark) Color(0xFF003735) else Color.White) }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        GlassButton(
                            text = "Issue Fine",
                            onClick = { showFineDialog = true }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        GlassButton(
                            text = "Announcement",
                            onClick = { showAnnouncementDialog = true }
                        )
                    }
                }

                GlassButton(
                    text = "Edit Hostel Info & Fees",
                    onClick = { showEditDialog = true },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = if (isDark) Color(0xFF003735) else Color.White) }
                )

                // Gate Pass approval requests list
                Text(
                    "Outing / Leave Gate Passes Pending",
                    color = textColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                if (pendingPasses.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("No pending passes at this time.", color = subTextColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                } else {
                    pendingPasses.forEach { pass ->
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
                                    Text("Reason: ${pass.reason}", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Place: ${pass.placeOfGoing} | Leave: ${pass.outDate} - ${pass.inDate} | Parent: ${pass.parentName} (${pass.parentPhone})", color = textColor, fontSize = 12.sp)
                                } else {
                                    Text("Reason: ${pass.reason}", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("Place: ${pass.placeOfGoing} | Outing time: ${pass.outTime} - ${pass.inTime}", color = textColor, fontSize = 12.sp)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            pass.wardenApproval = "Approved"
                                            Toast.makeText(context, "Pass Approved!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF027E3D)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Approve", color = Color.White)
                                    }
                                    Button(
                                        onClick = {
                                            pass.wardenApproval = "Rejected"
                                            Toast.makeText(context, "Pass Rejected!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF93000A)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Reject", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                // Late Arrivals Alerts warning logs
                if (latePassAlerts.isNotEmpty()) {
                    Text(
                        "Late Check-In Alerts Log",
                        color = Color(0xFFFFB4AB),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    latePassAlerts.forEach { log ->
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

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Edit configs dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update Configs & Fees", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Hostel Fee Structure Settings", fontWeight = FontWeight.Bold, color = textColor)
                    OutlinedTextField(value = HostelDataStore.fee5Sharing, onValueChange = { HostelDataStore.fee5Sharing = it }, label = { Text("5 Sharing Fee") })
                    OutlinedTextField(value = HostelDataStore.fee4Sharing, onValueChange = { HostelDataStore.fee4Sharing = it }, label = { Text("4 Sharing Fee") })
                    OutlinedTextField(value = HostelDataStore.fee3Sharing, onValueChange = { HostelDataStore.fee3Sharing = it }, label = { Text("3 Sharing Fee") })
                    OutlinedTextField(value = HostelDataStore.fee2Sharing, onValueChange = { HostelDataStore.fee2Sharing = it }, label = { Text("2 Sharing Fee") })
                    
                    OutlinedTextField(value = HostelDataStore.fee5SharingAC, onValueChange = { HostelDataStore.fee5SharingAC = it }, label = { Text("5 Sharing AC Fee") })
                    OutlinedTextField(value = HostelDataStore.fee2SharingAC, onValueChange = { HostelDataStore.fee2SharingAC = it }, label = { Text("2 Sharing AC Fee") })

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Warden Details", fontWeight = FontWeight.Bold, color = textColor)
                    OutlinedTextField(value = chiefWardenName, onValueChange = { chiefWardenName = it }, label = { Text("Chief Warden Name") })
                    OutlinedTextField(value = chiefWardenPhone, onValueChange = { chiefWardenPhone = it }, label = { Text("Chief Warden Phone") })
                    OutlinedTextField(value = blockAWardenName, onValueChange = { blockAWardenName = it }, label = { Text("Block A Warden") })
                    OutlinedTextField(value = blockAWardenPhone, onValueChange = { blockAWardenPhone = it }, label = { Text("Block A Phone") })
                    OutlinedTextField(value = blockBWardenName, onValueChange = { blockBWardenName = it }, label = { Text("Block B Warden") })
                    OutlinedTextField(value = blockBWardenPhone, onValueChange = { blockBWardenPhone = it }, label = { Text("Block B Phone") })

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mess Schedules", fontWeight = FontWeight.Bold, color = textColor)
                    OutlinedTextField(value = tiffinTiming, onValueChange = { tiffinTiming = it }, label = { Text("Tiffin Timing") })
                    OutlinedTextField(value = lunchTiming, onValueChange = { lunchTiming = it }, label = { Text("Lunch Timing") })
                    OutlinedTextField(value = dinnerTiming, onValueChange = { dinnerTiming = it }, label = { Text("Dinner Timing") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        HostelDataStore.chiefWardenName = chiefWardenName
                        HostelDataStore.chiefWardenPhone = chiefWardenPhone
                        HostelDataStore.blockAWardenName = blockAWardenName
                        HostelDataStore.blockAWardenPhone = blockAWardenPhone
                        HostelDataStore.blockBWardenName = blockBWardenName
                        HostelDataStore.blockBWardenPhone = blockBWardenPhone
                        HostelDataStore.tiffinTiming = tiffinTiming
                        HostelDataStore.lunchTiming = lunchTiming
                        HostelDataStore.dinnerTiming = dinnerTiming
                        Toast.makeText(context, "Configurations saved!", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
                ) {
                    Text("Save Changes", color = if (isDark) Color(0xFF003735) else Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Issue fine dialog
    if (showFineDialog) {
        AlertDialog(
            onDismissRequest = { showFineDialog = false },
            title = { Text("Issue Fine to Student", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Student Roll Number:", color = subTextColor)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("231801380001" to "Alex", "231801380002" to "Dhanush").forEach { (roll, name) ->
                            val isSelected = fineRoll == roll
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) {
                                            if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                        } else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, if (isSelected) Color(0xFF29FCF3) else subTextColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { fineRoll = roll }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$name ($roll)", color = textColor, fontSize = 12.sp)
                            }
                        }
                    }
                    OutlinedTextField(value = fineAmount, onValueChange = { fineAmount = it }, label = { Text("Fine Amount (e.g. Rs. 500)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = fineReason, onValueChange = { fineReason = it }, label = { Text("Reason") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fineAmount.isNotEmpty() && fineReason.isNotEmpty()) {
                            HostelDataStore.fines.add(
                                FineItem(
                                    id = "FINE-${(100..999).random()}",
                                    studentRoll = fineRoll,
                                    amount = fineAmount,
                                    reason = fineReason,
                                    status = "Unpaid"
                                )
                            )
                            Toast.makeText(context, "Fine issued successfully!", Toast.LENGTH_SHORT).show()
                            showFineDialog = false
                        } else {
                            Toast.makeText(context, "Please fill in all details", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
                ) {
                    Text("Issue Fine", color = if (isDark) Color(0xFF003735) else Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFineDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Publish Announcement Dialog
    if (showAnnouncementDialog) {
        AlertDialog(
            onDismissRequest = { showAnnouncementDialog = false },
            title = { Text("Publish Announcement", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = annTitle, onValueChange = { annTitle = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = annContent, onValueChange = { annContent = it }, label = { Text("Content Description") }, modifier = Modifier.fillMaxWidth())
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("GENERAL NOTICE", "EMERGENCY BROADCAST").forEach { category ->
                            val isSelected = annCategory == category
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) {
                                            if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                        } else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .border(1.dp, if (isSelected) Color(0xFF29FCF3) else subTextColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable { annCategory = category }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(category, color = textColor, fontSize = 10.sp, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (annTitle.isNotEmpty() && annContent.isNotEmpty()) {
                            HostelDataStore.announcements.add(
                                AnnouncementItem(
                                    id = "ANN-${(100..999).random()}",
                                    title = annTitle,
                                    category = annCategory,
                                    date = "14 Aug 2026",
                                    content = annContent,
                                    targetHostel = "All"
                                )
                            )
                            Toast.makeText(context, "Announcement Published!", Toast.LENGTH_SHORT).show()
                            showAnnouncementDialog = false
                        } else {
                            Toast.makeText(context, "Please fill in title and content", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
                ) {
                    Text("Publish", color = if (isDark) Color(0xFF003735) else Color.White)
                }
            },
             dismissButton = {
                 TextButton(onClick = { showAnnouncementDialog = false }) {
                     Text("Cancel", color = subTextColor)
                 }
             },
             containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
             shape = RoundedCornerShape(16.dp)
         )
     }

     // Warden profile edit dialog (Section 10)
     if (showProfileEditDialog) {
         AlertDialog(
             onDismissRequest = { showProfileEditDialog = false },
             title = { Text("Edit Warden Profile", color = textColor, fontWeight = FontWeight.Bold) },
             text = {
                 Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                     OutlinedTextField(
                         value = wardenProfileName,
                         onValueChange = { wardenProfileName = it },
                         label = { Text("Warden Name") },
                         modifier = Modifier.fillMaxWidth()
                     )
                     OutlinedTextField(
                         value = wardenProfilePhone,
                         onValueChange = { wardenProfilePhone = it },
                         label = { Text("Warden Contact Phone") },
                         modifier = Modifier.fillMaxWidth()
                     )
                 }
             },
             confirmButton = {
                 Button(
                     onClick = {
                         if (HostelDataStore.currentWardenScope == "Girls") {
                             HostelDataStore.blockBWardenName = wardenProfileName
                             HostelDataStore.blockBWardenPhone = wardenProfilePhone
                         } else {
                             HostelDataStore.blockAWardenName = wardenProfileName
                             HostelDataStore.blockAWardenPhone = wardenProfilePhone
                         }
                         showProfileEditDialog = false
                         Toast.makeText(context, "Warden profile updated successfully!", Toast.LENGTH_SHORT).show()
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
 }
