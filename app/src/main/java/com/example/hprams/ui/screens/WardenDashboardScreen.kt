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
import com.example.hprams.theme.AccentColor

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
    
    // Popup states
    var showComplaintsPopup by remember { mutableStateOf(false) }
    var showRoomsPopup by remember { mutableStateOf(false) }
    
    // Room allocation selector states
    var selectedRequestForAllocation by remember { mutableStateOf<com.example.hprams.data.RoomChangeRequest?>(null) }
    var selectedBlockToAllocate by remember { mutableStateOf("") }
    var selectedRoomToAllocate by remember { mutableStateOf("") }
    var showAllocationDialog by remember { mutableStateOf(false) }
    var showBlockDropdownAllocate by remember { mutableStateOf(false) }
    var showRoomDropdownAllocate by remember { mutableStateOf(false) }
    
    // Reject reason states
    var selectedRequestForReject by remember { mutableStateOf<com.example.hprams.data.RoomChangeRequest?>(null) }
    var rejectReasonText by remember { mutableStateOf("") }
    var showRejectDialog by remember { mutableStateOf(false) }

    // Dynamic warden profile lookup based on database gender
    val loggedInWarden = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    LaunchedEffect(loggedInWarden) {
        loggedInWarden?.let {
            HostelDataStore.currentWardenScope = if (it.gender == "Female") "Girls" else "Boys"
        }
    }

    var wardenProfileName by remember(loggedInWarden) {
        mutableStateOf(loggedInWarden?.name ?: if (HostelDataStore.currentWardenScope == "Girls") HostelDataStore.blockBWardenName else HostelDataStore.blockAWardenName)
    }
    var wardenProfilePhone by remember(loggedInWarden) {
        mutableStateOf(loggedInWarden?.phone ?: if (HostelDataStore.currentWardenScope == "Girls") HostelDataStore.blockBWardenPhone else HostelDataStore.blockAWardenPhone)
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
                var showDropdownMenu by remember { mutableStateOf(false) }
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
                    navigationIcon = {
                        IconButton(onClick = { showProfileEditDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Warden Profile",
                                tint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                modifier = Modifier.size(28.dp)
                            )
                        }
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
                        }
                    }
                }

                // Statistics / Metrics segmented based on active tab dashboard
                var activeTab by remember { mutableStateOf("dashboard") } // "dashboard", "complaints", "rooms", "payment", "hostel_info"

                when (activeTab) {
                    "dashboard" -> {
                        // Main Scoped Dashboard Tab (Option 5)
                        // Segregated by gender/warden scope info
                        val targetScopeText = if (HostelDataStore.currentWardenScope == "Girls") "Girls Hostel Information" else "Boys Hostel Information"
                        val totalScopedStudents = HostelDataStore.students.filter { it.gender == targetGender }.size
                        
                        Text(
                            text = targetScopeText,
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Total Students Scoped", color = subTextColor, modifier = Modifier.weight(1f))
                                    Text("$totalScopedStudents Students", color = textColor, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Pending approvals requests", color = subTextColor, modifier = Modifier.weight(1f))
                                    Text("$pendingRequestsCount Requests", color = textColor, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Pending complaints", color = subTextColor, modifier = Modifier.weight(1f))
                                    Text("$pendingComplaintsCount Complaints", color = Color(0xFFFFB4AB), fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Pending Outing Passes", color = subTextColor, modifier = Modifier.weight(1f))
                                    Text("${pendingPasses.size} Passes", color = textColor, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

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

                        // Late Check-In warnings logs
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
                    }

                    "other" -> {
                        // Other Console (Requirement 1 & 10)
                        val totalScopedStudents = HostelDataStore.students.filter { it.gender == targetGender }.size
                        Text(
                            text = "Other Console",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Total Students in Scope: $totalScopedStudents", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Divider(color = Color.White.copy(alpha = 0.1f))
                                Text("Issue Fine to Student", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                
                                OutlinedTextField(
                                    value = fineRoll,
                                    onValueChange = { fineRoll = it },
                                    label = { Text("Student Roll Number") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = fineAmount,
                                    onValueChange = { fineAmount = it },
                                    label = { Text("Fine Amount (Rs.)") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = fineReason,
                                    onValueChange = { fineReason = it },
                                    label = { Text("Reason for Fine") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                
                                Button(
                                    onClick = {
                                        if (fineRoll.isEmpty() || fineAmount.isEmpty() || fineReason.isEmpty()) {
                                            Toast.makeText(context, "Please fill in all fine details", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        val matchedStudent = HostelDataStore.students.find { it.roll == fineRoll }
                                        if (matchedStudent == null) {
                                            Toast.makeText(context, "Student Roll not found!", Toast.LENGTH_SHORT).show()
                                            return@Button
                                        }
                                        val newFine = FineItem(
                                            id = "FIN-${(1000..9999).random()}",
                                            studentRoll = fineRoll,
                                            amount = fineAmount,
                                            reason = fineReason,
                                            status = "Unpaid"
                                        )
                                        HostelDataStore.saveFine(newFine)
                                        
                                        val stdNotif = com.example.hprams.data.NotificationItem(
                                            id = "NTF-${(1000..9999).random()}",
                                            userId = fineRoll,
                                            title = "Fine Issued",
                                            message = "A fine of Rs. $fineAmount has been issued by Warden: $fineReason.",
                                            type = "FINE",
                                            timestamp = "20 Aug 2026",
                                            deepLink = "profile"
                                        )
                                        HostelDataStore.saveNotification(stdNotif, context)
                                        
                                        Toast.makeText(context, "Fine issued successfully!", Toast.LENGTH_SHORT).show()
                                        fineAmount = ""
                                        fineReason = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF93000A)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Issue Fine", color = Color.White)
                                }
                            }
                        }
                    }

                    "hostel_info" -> {
                        // Hostel Info management (Option 4)
                        Text(
                            text = "Hostel Configurations",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        GlassButton(
                            text = "Update Mess & Fee schedules",
                            onClick = { showEditDialog = true },
                            icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = if (isDark) Color(0xFF0F141C) else Color.White) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        GlassButton(
                            text = "Publish Announcement",
                            onClick = { showAnnouncementDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Custom bottom-segmented Tab Bar row for Warden Panel options
                Card(
                    modifier = Modifier.fillMaxWidth().height(65.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDark) Color(0xFF1E2836) else Color(0xFFD6DFDE)
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val activeTint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                        val normalTint = subTextColor

                        // 1. Complaints Board Popup trigger (Requirement 1)
                        IconButton(onClick = { showComplaintsPopup = true }) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Complaints Board",
                                tint = if (showComplaintsPopup) activeTint else normalTint
                            )
                        }

                        // 2. Rooms Related approvals Popup trigger (Requirement 1)
                        IconButton(onClick = { showRoomsPopup = true }) {
                            Icon(
                                imageVector = Icons.Default.MeetingRoom,
                                contentDescription = "Rooms related",
                                tint = if (showRoomsPopup) activeTint else normalTint
                            )
                        }

                        // 5. Main Dashboard (placed in the middle as requested)
                        IconButton(onClick = { activeTab = "dashboard" }) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = "Main Dashboard",
                                tint = if (activeTab == "dashboard") activeTint else normalTint
                            )
                        }

                        // 3. Other tab (Requirement 1)
                        IconButton(onClick = { activeTab = "other" }) {
                            Icon(
                                imageVector = Icons.Default.Payments,
                                contentDescription = "Other Console",
                                tint = if (activeTab == "other") activeTint else normalTint
                            )
                        }

                        // 4. Hostel Info
                        IconButton(onClick = { activeTab = "hostel_info" }) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Hostel Info",
                                tint = if (activeTab == "hostel_info") activeTint else normalTint
                            )
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
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
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

    // Complaints Popup (Requirement 1)
    if (showComplaintsPopup) {
        AlertDialog(
            onDismissRequest = { showComplaintsPopup = false },
            title = { Text("Pending Scoped Complaints", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                val pendingComplaints = HostelDataStore.complaints.filter { it.status == "Pending" && it.gender == targetGender }
                if (pendingComplaints.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("No pending complaints in your scope.", color = subTextColor)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pendingComplaints.forEach { cmp ->
                            val matchedStudent = HostelDataStore.students.find { it.name == cmp.studentName }
                            val roll = matchedStudent?.roll ?: ""
                            val roomNum = matchedStudent?.room ?: ""
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Student: ${cmp.studentName} (${roll})", color = textColor, fontWeight = FontWeight.Bold)
                                    Text("Category: ${cmp.category} | Room: ${roomNum}", color = subTextColor, fontSize = 12.sp)
                                    Text("Description: ${cmp.description}", color = textColor, fontSize = 13.sp)
                                    
                                    Button(
                                        onClick = {
                                            val updated = cmp.copy(status = "Resolved", assignedHandyman = wardenProfileName)
                                            HostelDataStore.saveComplaint(updated)
                                            Toast.makeText(context, "Complaint marked as Resolved!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Mark Resolved", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showComplaintsPopup = false }) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Room Approvals Popup (Requirement 1 & 3)
    if (showRoomsPopup) {
        AlertDialog(
            onDismissRequest = { showRoomsPopup = false },
            title = { Text("Room Change Requests", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                val pendingRoomRequests = HostelDataStore.roomChangeRequests.filter { it.status == "Pending" && it.gender == targetGender }
                if (pendingRoomRequests.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                        Text("No pending room change requests.", color = subTextColor)
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pendingRoomRequests.forEach { req ->
                            val matchedStudent = HostelDataStore.students.find { it.roll == req.studentRoll }
                            val feePaid = matchedStudent?.feePaidStatus == "Paid"
                            
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("${req.studentName} (${req.studentRoll})", color = textColor, fontWeight = FontWeight.Bold)
                                    Text("Current: ${req.currentRoom} ➜ Requested: ${req.requestedRoom}", color = subTextColor, fontSize = 12.sp)
                                    Text("Fee status: ${if (feePaid) "Paid" else "Pending"}", color = if (feePaid) Color(0xFF4CAF50) else Color(0xFFF44336), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    
                                    if (req.refundOption.isNotEmpty()) {
                                        Text("Refund Choice: ${req.refundOption}", color = Color(0xFF81C784), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        if (req.refundDetails.isNotEmpty()) {
                                            Text("Refund Account/UPI: ${req.refundDetails}", color = textColor, fontSize = 11.sp)
                                        }
                                    }
                                    
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                selectedRequestForAllocation = req
                                                selectedBlockToAllocate = matchedStudent?.block ?: (if (req.gender == "Female") "Block C" else "Block A")
                                                selectedRoomToAllocate = req.requestedRoom
                                                showAllocationDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Approve", color = Color.White, fontSize = 12.sp)
                                        }
                                        Button(
                                            onClick = {
                                                selectedRequestForReject = req
                                                rejectReasonText = ""
                                                showRejectDialog = true
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Reject", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showRoomsPopup = false }) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Allocation Dialog with Dropdowns (Requirement 3)
    if (showAllocationDialog && selectedRequestForAllocation != null) {
        val req = selectedRequestForAllocation!!
        val student = HostelDataStore.students.find { it.roll == req.studentRoll }
        
        AlertDialog(
            onDismissRequest = { showAllocationDialog = false },
            title = { Text("Allocate Room for ${req.studentName}", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Student Gender: ${req.gender}", color = subTextColor, fontSize = 12.sp)
                    Text("Requested Room: ${req.requestedRoom}", color = subTextColor, fontSize = 12.sp)

                    // Room Details Map CheatSheet
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Room Type Map & Capacity Rules", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("• Floor 1: 101-105 (4 Sharing Non-AC) | 106-107 (4 Sharing AC) | 108-110 (6 Sharing Non-AC)", color = subTextColor, fontSize = 9.sp)
                            Text("• Floor 2: 111-114 (3 Sharing Non-AC) | 115-117 (3 Sharing AC) | 118-120 (6 Sharing Non-AC)", color = subTextColor, fontSize = 9.sp)
                        }
                    }
                    
                    Text("Select Wing/Block:", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                            .clickable { showBlockDropdownAllocate = true }
                            .padding(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(selectedBlockToAllocate.ifEmpty { "Select Block" }, color = textColor)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = textColor)
                        }
                        DropdownMenu(
                            expanded = showBlockDropdownAllocate,
                            onDismissRequest = { showBlockDropdownAllocate = false }
                        ) {
                            val availableBlocks = if (req.gender == "Female") listOf("Block C", "Block D") else listOf("Block A", "Block B")
                            availableBlocks.forEach { b ->
                                DropdownMenuItem(
                                    text = { Text(b) },
                                    onClick = {
                                        selectedBlockToAllocate = b
                                        showBlockDropdownAllocate = false
                                        selectedRoomToAllocate = "" 
                                    }
                                )
                            }
                        }
                    }
                    
                    Text("Select Room Number:", color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                            .clickable { showRoomDropdownAllocate = true }
                            .padding(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(selectedRoomToAllocate.ifEmpty { "Select Room" }, color = textColor)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = textColor)
                        }
                        DropdownMenu(
                            expanded = showRoomDropdownAllocate,
                            onDismissRequest = { showRoomDropdownAllocate = false }
                        ) {
                            val availableRooms = (101..120).map { it.toString() }
                            availableRooms.forEach { r ->
                                val currentCount = HostelDataStore.students.filter { it.block == selectedBlockToAllocate && it.room == r && it.role == "Student" }.size
                                val maxCap = com.example.hprams.data.RoomRules.getRoomCapacity(r)
                                DropdownMenuItem(
                                    text = { Text("Room $r ($currentCount/$maxCap occupied)") },
                                    onClick = {
                                        selectedRoomToAllocate = r
                                        showRoomDropdownAllocate = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedBlockToAllocate.isEmpty() || selectedRoomToAllocate.isEmpty()) {
                            Toast.makeText(context, "Please select both Block and Room", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        
                        if (!com.example.hprams.data.RoomRules.isRoomAvailable(selectedBlockToAllocate, selectedRoomToAllocate)) {
                            Toast.makeText(context, "Cannot allocate room: Selected room capacity exceeded!", Toast.LENGTH_LONG).show()
                            return@Button
                        }
                        
                        val updatedReq = req.copy(status = "Approved", requestedRoom = selectedRoomToAllocate)
                        student?.let { s ->
                            val updatedStudent = s.copy(block = selectedBlockToAllocate, room = selectedRoomToAllocate)
                            HostelDataStore.saveStudent(updatedStudent)
                        }
                        HostelDataStore.saveRoomChange(updatedReq)
                        
                        val stdNotif = com.example.hprams.data.NotificationItem(
                            id = "NTF-${(1000..9999).random()}",
                            userId = req.studentRoll,
                            title = "Room Allocated",
                            message = "Your request to change room has been approved. Allocated: $selectedBlockToAllocate, Room $selectedRoomToAllocate.",
                            type = "ALLOCATION",
                            timestamp = "20 Aug 2026",
                            deepLink = "profile"
                        )
                        HostelDataStore.saveNotification(stdNotif, context)
                        
                        Toast.makeText(context, "Room allocated successfully!", Toast.LENGTH_SHORT).show()
                        showAllocationDialog = false
                        showRoomsPopup = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF027E3D))
                  ) {
                      Text("Confirm Allocation", color = Color.White)
                  }
              },
              dismissButton = {
                  TextButton(onClick = { showAllocationDialog = false }) {
                      Text("Cancel", color = subTextColor)
                  }
              },
              containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
              shape = RoundedCornerShape(16.dp)
          )
      }

      // Rejection Reason Dialog
      if (showRejectDialog && selectedRequestForReject != null) {
          AlertDialog(
              onDismissRequest = { showRejectDialog = false },
              title = { Text("Reason for Rejection", color = textColor, fontWeight = FontWeight.Bold) },
              text = {
                  OutlinedTextField(
                      value = rejectReasonText,
                      onValueChange = { rejectReasonText = it },
                      placeholder = { Text("Enter reason (e.g. Room fully booked)") },
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
                                  HostelDataStore.saveRoomChange(req)
                                  
                                  val matchedStudent = HostelDataStore.students.find { it.roll == req.studentRoll }
                                  matchedStudent?.let { s ->
                                      val stdNotif = com.example.hprams.data.NotificationItem(
                                          id = "NTF-${(1000..9999).random()}",
                                          userId = s.roll,
                                          title = "Room Allocation Rejected",
                                          message = "Your request to occupy Room ${req.requestedRoom} was rejected: ${rejectReasonText}.",
                                          type = "ALLOCATION",
                                          timestamp = "20 Aug 2026",
                                          deepLink = "profile"
                                      )
                                      HostelDataStore.saveNotification(stdNotif, context)
                                  }
                              }
                              Toast.makeText(context, "Request Rejected!", Toast.LENGTH_SHORT).show()
                              showRejectDialog = false
                              showRoomsPopup = false 
                          } else {
                              Toast.makeText(context, "Please specify a reason", Toast.LENGTH_SHORT).show()
                          }
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
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
