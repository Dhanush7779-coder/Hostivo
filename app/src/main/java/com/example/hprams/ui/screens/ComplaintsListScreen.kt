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
import androidx.compose.ui.draw.alpha
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsListScreen(
    onHomeClick: () -> Unit,
    onRoomsClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNewComplaintClick: () -> Unit,
    onComplaintClick: (complaintId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showInfoDialog by remember { mutableStateOf(false) }
    var showGatePassDialog by remember { mutableStateOf(false) }
    var gatePassType by remember { mutableStateOf("Outing") }

    var outDate by remember { mutableStateOf("") }
    var outTime by remember { mutableStateOf("") }
    var inDate by remember { mutableStateOf("") }
    var inTime by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var placeOfGoing by remember { mutableStateOf("") }
    var reasonText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()

    // Read current student context
    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentGender = currentStudent?.gender ?: "Female"
    val studentName = currentStudent?.name ?: "Alex Vance"
    val studentRoll = currentStudent?.roll ?: "231801380001"

    // Sync student-specific items using derivedStateOf to prevent full screen recompositions
    val studentComplaints by remember(studentName) { derivedStateOf { HostelDataStore.complaints.filter { it.studentName == studentName } } }
    val studentPasses by remember(studentRoll) { derivedStateOf { HostelDataStore.gatePassRequests.filter { it.studentRoll == studentRoll } } }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "SUPPORT & COMPLAINTS",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // Hostel Info Button
                    item {
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showInfoDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                    )
                                    Column {
                                        Text(
                                            "Hostel Information & Timings",
                                            color = textColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        Text(
                                            "Warden details, Mess schedules & Fees",
                                            color = subTextColor,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = subTextColor
                                )
                            }
                        }
                    }

                    // Action buttons (Complaint + Gate Pass)
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                GlassButton(
                                    text = "New Complaint +",
                                    onClick = onNewComplaintClick
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                GlassButton(
                                    text = "Request Gate Pass",
                                    onClick = { showGatePassDialog = true }
                                )
                            }
                        }
                    }

                    // Gate Pass requests section
                    if (studentPasses.isNotEmpty()) {
                        item {
                            Text(
                                "Your Gate Passes / Leave Requests",
                                color = textColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(studentPasses, key = { it.id }) { pass ->
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (pass.type == "Leave") Icons.Default.AirportShuttle else Icons.Default.DirectionsWalk,
                                                contentDescription = null,
                                                tint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                            )
                                            Text(
                                                pass.type.uppercase(),
                                                color = textColor,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    when (pass.wardenApproval) {
                                                        "Approved" -> Color(0xFF027E3D).copy(alpha = 0.15f)
                                                        "Rejected" -> Color(0xFF93000A).copy(alpha = 0.15f)
                                                        else -> Color(0xFFB89400).copy(alpha = 0.15f)
                                                    },
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                pass.wardenApproval.uppercase(),
                                                color = when (pass.wardenApproval) {
                                                    "Approved" -> Color(0xFF76DB8F)
                                                    "Rejected" -> Color(0xFFFFB4AB)
                                                    else -> Color(0xFFFFD43F)
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                    Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Text("Check Out Timing", color = subTextColor, style = MaterialTheme.typography.bodySmall)
                                            Text(pass.checkoutTime, color = textColor, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("Check In Timing", color = subTextColor, style = MaterialTheme.typography.bodySmall)
                                            Text(pass.checkinTime, color = textColor, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Active Tickets title
                    item {
                        Text(
                            "Your Active Complaints",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Tickets list
                    items(studentComplaints, key = { it.id }) { ticket ->
                        val isResolved = ticket.status == "RESOLVED"
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onComplaintClick(ticket.id) }
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (isResolved) Color(0xFF76DB8F) else Color(0xFFFFB4AB).copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            ticket.category,
                                            color = textColor,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        ticket.status,
                                        color = if (isResolved) Color(0xFF76DB8F) else if (ticket.status == "Assigned") Color(0xFF29FCF3) else Color(0xFFFFB4AB),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Text(ticket.title, color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(ticket.description, color = subTextColor, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.alpha(0.8f))
                                if (ticket.assignedHandyman.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Assigned to: ${ticket.assignedHandyman}", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ticket.id, color = subTextColor, style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                                    Text(ticket.date, color = subTextColor, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }

                StudentBottomBar(
                    activeTab = "support",
                    onHomeClick = onHomeClick,
                    onRoomsClick = onRoomsClick,
                    onFinanceClick = onFinanceClick,
                    onProfileClick = onProfileClick,
                    onSupportClick = {},
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }

    // Gate Pass Request Dialog
    if (showGatePassDialog) {
        val hasActivePass = studentPasses.any { it.checkinTime == "--:--" }

        AlertDialog(
            onDismissRequest = { showGatePassDialog = false },
            title = {
                Text(
                    "Request Gate Pass",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (hasActivePass) {
                        Text(
                            "You already have an active pending or approved gate pass request. You cannot submit multiple requests.",
                            color = Color(0xFFFFB4AB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    } else {
                        Text("Select the pass type:", color = subTextColor)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Outing", "Leave").forEach { type ->
                                val isSelected = gatePassType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) {
                                                if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                            } else {
                                                if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) {
                                                if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                            } else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { gatePassType = type }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        type,
                                        color = if (isSelected) {
                                            if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                        } else subTextColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (gatePassType == "Outing") {
                            Text(
                                "Note: Outing timing must be after 07:00 AM and return before 07:00 PM.",
                                color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            OutlinedTextField(
                                value = outTime,
                                onValueChange = { outTime = it },
                                label = { Text("Out Time (e.g. 08:30 AM)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = inTime,
                                onValueChange = { inTime = it },
                                label = { Text("Expected Return Time (e.g. 05:00 PM)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            OutlinedTextField(
                                value = outDate,
                                onValueChange = { outDate = it },
                                label = { Text("Out Date (e.g. 15 Aug)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = outTime,
                                onValueChange = { outTime = it },
                                label = { Text("Out Time (e.g. 09:00 AM)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = inDate,
                                onValueChange = { inDate = it },
                                label = { Text("Return Date (e.g. 18 Aug)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = inTime,
                                onValueChange = { inTime = it },
                                label = { Text("Return Time (e.g. 06:00 PM)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = parentName,
                                onValueChange = { parentName = it },
                                label = { Text("Parent/Guardian Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = parentPhone,
                                onValueChange = { parentPhone = it },
                                label = { Text("Parent Contact Number") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        OutlinedTextField(
                            value = placeOfGoing,
                            onValueChange = { placeOfGoing = it },
                            label = { Text("Place of Going") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = reasonText,
                            onValueChange = { reasonText = it },
                            label = { Text("Reason") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (!hasActivePass) {
                    Button(
                        onClick = {
                            if (gatePassType == "Outing") {
                                // Basic validation for Outing hours
                                if (outTime.isEmpty() || inTime.isEmpty() || placeOfGoing.isEmpty() || reasonText.isEmpty()) {
                                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                            } else {
                                if (outDate.isEmpty() || outTime.isEmpty() || inDate.isEmpty() || inTime.isEmpty() || parentName.isEmpty() || parentPhone.isEmpty() || placeOfGoing.isEmpty() || reasonText.isEmpty()) {
                                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                            }

                            val passId = "PASS-${(1000..9999).random()}"
                            HostelDataStore.gatePassRequests.add(
                                GatePassRequest(
                                    id = passId,
                                    studentName = studentName,
                                    studentRoll = studentRoll,
                                    type = gatePassType,
                                    gender = studentGender,
                                    wardenApproval = "Pending",
                                    outDate = outDate,
                                    outTime = outTime,
                                    inDate = inDate,
                                    inTime = inTime,
                                    parentName = parentName,
                                    parentPhone = parentPhone,
                                    placeOfGoing = placeOfGoing,
                                    reason = reasonText
                                )
                            )
                            Toast.makeText(context, "$gatePassType Request submitted successfully!", Toast.LENGTH_SHORT).show()
                            showGatePassDialog = false
                            // Reset values
                            outDate = ""
                            outTime = ""
                            inDate = ""
                            inTime = ""
                            parentName = ""
                            parentPhone = ""
                            placeOfGoing = ""
                            reasonText = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                        )
                    ) {
                        Text("Submit Request", color = if (isDark) Color(0xFF003735) else Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showGatePassDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Hostel Information Details Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text(
                    "Hostel Information",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Wardens list
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "WARDEN DIRECTORY",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                        
                        Column {
                            Text(HostelDataStore.chiefWardenName, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Chief Warden", color = subTextColor, fontSize = 12.sp)
                                Text(HostelDataStore.chiefWardenPhone, color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Column {
                            Text(HostelDataStore.blockAWardenName, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Block A Warden (Boys)", color = subTextColor, fontSize = 12.sp)
                                Text(HostelDataStore.blockAWardenPhone, color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Column {
                            Text(HostelDataStore.blockBWardenName, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Block B Warden (Girls)", color = subTextColor, fontSize = 12.sp)
                                Text(HostelDataStore.blockBWardenPhone, color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Mess Timings
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "MESS TIMINGS",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Tiffin", color = subTextColor, fontSize = 13.sp)
                            Text(HostelDataStore.tiffinTiming, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Lunch", color = subTextColor, fontSize = 13.sp)
                            Text(HostelDataStore.lunchTiming, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dinner", color = subTextColor, fontSize = 13.sp)
                            Text(HostelDataStore.dinnerTiming, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Fee Structures
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "FEE STRUCTURE",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                        Text("Non-AC Sharing Structures:", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("5 Sharing", color = subTextColor, fontSize = 12.sp)
                            Text(HostelDataStore.fee5Sharing, color = textColor, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("4 Sharing", color = subTextColor, fontSize = 12.sp)
                            Text(HostelDataStore.fee4Sharing, color = textColor, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("3 Sharing", color = subTextColor, fontSize = 12.sp)
                            Text(HostelDataStore.fee3Sharing, color = textColor, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("2 Sharing", color = subTextColor, fontSize = 12.sp)
                            Text(HostelDataStore.fee2Sharing, color = textColor, fontSize = 12.sp)
                        }

                        Text("AC Sharing Structures:", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("5 Sharing AC", color = subTextColor, fontSize = 12.sp)
                            Text(HostelDataStore.fee5SharingAC, color = textColor, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("4 Sharing AC", color = subTextColor, fontSize = 12.sp)
                            Text(HostelDataStore.fee4SharingAC, color = textColor, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("3 Sharing AC", color = subTextColor, fontSize = 12.sp)
                            Text(HostelDataStore.fee3SharingAC, color = textColor, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("2 Sharing AC", color = subTextColor, fontSize = 12.sp)
                            Text(HostelDataStore.fee2SharingAC, color = textColor, fontSize = 12.sp)
                        }
                    }

                    // Hostel General Rules & Gate Timings (Section 5)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "GENERAL RULES & CURFEW",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gate Open Time", color = subTextColor, fontSize = 12.sp)
                            Text("06:00 AM", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Gate Curfew Time", color = subTextColor, fontSize = 12.sp)
                            Text("07:00 PM (Strict curfew)", color = Color(0xFFFFB4AB), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Visiting Hours", color = subTextColor, fontSize = 12.sp)
                            Text("04:30 PM - 06:30 PM (Weekends)", color = textColor, fontSize = 12.sp)
                        }
                        Text(
                            "Rules: Outings require warden approval. Late arrivals after 7:00 PM will trigger Warden alerts and log disciplinary record details.",
                            color = subTextColor,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                    )
                ) {
                    Text("Close", color = if (isDark) Color(0xFF003735) else Color.White)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
