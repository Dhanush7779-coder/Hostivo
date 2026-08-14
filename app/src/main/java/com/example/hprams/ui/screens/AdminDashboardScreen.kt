package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.*
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.AnnouncementItem
import com.example.hprams.theme.isAppDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onSignOutClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onReportsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()

    var activeTab by remember { mutableStateOf("controls") } // "controls", "security", "finance", "allocations"
    var showEditDialog by remember { mutableStateOf(false) }

    // Forms/State
    var chiefWardenName by remember { mutableStateOf(HostelDataStore.chiefWardenName) }
    var chiefWardenPhone by remember { mutableStateOf(HostelDataStore.chiefWardenPhone) }
    var blockAWardenName by remember { mutableStateOf(HostelDataStore.blockAWardenName) }
    var blockAWardenPhone by remember { mutableStateOf(HostelDataStore.blockAWardenPhone) }
    var blockBWardenName by remember { mutableStateOf(HostelDataStore.blockBWardenName) }
    var blockBWardenPhone by remember { mutableStateOf(HostelDataStore.blockBWardenPhone) }

    var tiffinTiming by remember { mutableStateOf(HostelDataStore.tiffinTiming) }
    var lunchTiming by remember { mutableStateOf(HostelDataStore.lunchTiming) }
    var dinnerTiming by remember { mutableStateOf(HostelDataStore.dinnerTiming) }

    // Security assignments (Section 6)
    var securityBlock by remember { mutableStateOf(HostelDataStore.securityBlockAssignment) }
    var securityDutyHours by remember { mutableStateOf(HostelDataStore.securityDutyTimings) }
    var showBlockDropdown by remember { mutableStateOf(false) }

    // Admin profile settings (Section 10)
    var adminName by remember { mutableStateOf("System Admin") }
    var adminPhone by remember { mutableStateOf("+91 9900990099") }
    var showProfileEditDialog by remember { mutableStateOf(false) }

    // Announcement forms
    var announcementTitle by remember { mutableStateOf("") }
    var announcementContent by remember { mutableStateOf("") }
    var targetHostelGroup by remember { mutableStateOf("All") } // "All", "Boys", "Girls"
    var showAnnounceDropdown by remember { mutableStateOf(false) }

    // Scoped metrics derived state
    val totalStudentsCount = HostelDataStore.students.size
    val activeRoomRequestsCount = HostelDataStore.roomChangeRequests.filter { it.status == "Pending" }.size
    val unpaidFinesCount = HostelDataStore.fines.filter { it.status == "Unpaid" }.size

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "ADMIN PORTAL",
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Horizontal scrollable Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "controls" to "Controls",
                        "security" to "Security Roster",
                        "finance" to "Finance",
                        "allocations" to "Allocations"
                    ).forEach { (tabId, label) ->
                        val isActive = activeTab == tabId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isActive) {
                                        if (isDark) Color(0xFF29FCF3).copy(alpha = 0.2f) else Color(0xFF006A66).copy(alpha = 0.2f)
                                    } else {
                                        if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
                                    }
                                )
                                .clickable { activeTab = tabId }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isActive) {
                                    if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                } else subTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    when (activeTab) {
                        "controls" -> {
                            // Admin Profile Details (Section 10)
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Admin Profile: $adminName", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Contact Phone: $adminPhone", color = subTextColor, fontSize = 12.sp)
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

                            // Quick Status Summary
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Students", color = subTextColor, style = MaterialTheme.typography.labelSmall)
                                        Text("$totalStudentsCount", color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Change Reqs", color = subTextColor, style = MaterialTheme.typography.labelSmall)
                                        Text("$activeRoomRequestsCount", color = if (activeRoomRequestsCount > 0) Color(0xFFFFB4AB) else textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Unpaid Fines", color = subTextColor, style = MaterialTheme.typography.labelSmall)
                                        Text("$unpaidFinesCount", color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            // Info details button
                            GlassButton(
                                text = "Edit Hostel Information",
                                onClick = { showEditDialog = true },
                                icon = { Icon(Icons.Default.Edit, contentDescription = null, tint = if (isDark) Color(0xFF003735) else Color.White) }
                            )

                            // Post Announcement card
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "Hostel Announcement Broadcast",
                                        color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    OutlinedTextField(
                                        value = announcementTitle,
                                        onValueChange = { announcementTitle = it },
                                        label = { Text("Announcement Title") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = announcementContent,
                                        onValueChange = { announcementContent = it },
                                        label = { Text("Details & Information text") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 2
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Target Wing: ", color = textColor, fontSize = 13.sp)
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { showAnnounceDropdown = true }
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(targetHostelGroup, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Icon(Icons.Default.ExpandMore, contentDescription = null, tint = subTextColor, modifier = Modifier.size(16.dp))
                                            }
                                            DropdownMenu(
                                                expanded = showAnnounceDropdown,
                                                onDismissRequest = { showAnnounceDropdown = false },
                                                modifier = Modifier.background(if (isDark) Color(0xFF101415) else Color(0xFFE3EAE9))
                                            ) {
                                                listOf("All", "Boys", "Girls").forEach { gp ->
                                                    DropdownMenuItem(
                                                        text = { Text(gp, color = textColor) },
                                                        onClick = {
                                                            targetHostelGroup = gp
                                                            showAnnounceDropdown = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            if (announcementTitle.isEmpty() || announcementContent.isEmpty()) {
                                                Toast.makeText(context, "Fields cannot be blank", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            HostelDataStore.announcements.add(
                                                AnnouncementItem(
                                                    id = "ANN-${(100..999).random()}",
                                                    title = announcementTitle,
                                                    category = "ADMIN BROADCAST",
                                                    date = "Today",
                                                    content = announcementContent,
                                                    targetHostel = targetHostelGroup
                                                )
                                            )
                                            Toast.makeText(context, "Announcement published!", Toast.LENGTH_SHORT).show()
                                            announcementTitle = ""
                                            announcementContent = ""
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Publish Announcement", color = if (isDark) Color(0xFF003735) else Color.White)
                                    }
                                }
                            }
                        }

                        "security" -> {
                            // Security duty assignment settings (Section 6)
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "Security Guard Duty Roster",
                                        color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))

                                    Text("Assign Security Guard to Block Wing:", color = textColor, fontSize = 12.sp)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { showBlockDropdown = true }
                                            .padding(horizontal = 12.dp, vertical = 8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(securityBlock, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Icon(Icons.Default.ExpandMore, contentDescription = null, tint = subTextColor, modifier = Modifier.size(16.dp))
                                        }
                                        DropdownMenu(
                                            expanded = showBlockDropdown,
                                            onDismissRequest = { showBlockDropdown = false },
                                            modifier = Modifier.background(if (isDark) Color(0xFF101415) else Color(0xFFE3EAE9))
                                        ) {
                                            listOf("Block A & B", "Block C & D").forEach { blk ->
                                                DropdownMenuItem(
                                                    text = { Text(blk, color = textColor) },
                                                    onClick = {
                                                        securityBlock = blk
                                                        showBlockDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = securityDutyHours,
                                        onValueChange = { securityDutyHours = it },
                                        label = { Text("Shift Timings (e.g. 08:00 AM - 08:00 PM)") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    // Presence indicator
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Daily Presence Status:", color = textColor, fontSize = 13.sp)
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (HostelDataStore.securityIsPresentToday) Color(0xFF027E3D).copy(alpha = 0.15f) else Color(0xFF93000A).copy(alpha = 0.15f),
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                if (HostelDataStore.securityIsPresentToday) "MARKED PRESENT" else "ABSENT/NOT IN GATE",
                                                color = if (HostelDataStore.securityIsPresentToday) Color(0xFF76DB8F) else Color(0xFFFFB4AB),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            HostelDataStore.securityBlockAssignment = securityBlock
                                            HostelDataStore.securityDutyTimings = securityDutyHours
                                            Toast.makeText(context, "Security Roster updated successfully!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Update Guard Roster", color = if (isDark) Color(0xFF003735) else Color.White)
                                    }
                                }
                            }
                        }

                        "finance" -> {
                            // Analytics occupancy rate and fees collection trends (Section 6)
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "Collections & Analytics Summary",
                                        color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Total Rooms Occupancy Rate", color = subTextColor, fontSize = 13.sp)
                                        Text("78% (Capacity: 300)", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Hostel Fees Collected", color = subTextColor, fontSize = 13.sp)
                                        Text("Rs. 6,80,000", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Pending Fee Collection Balance", color = subTextColor, fontSize = 13.sp)
                                        Text("Rs. 2,10,000", color = Color(0xFFFFB4AB), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Active Refund Requests", color = subTextColor, fontSize = 13.sp)
                                        Text("1 Outstanding", color = Color(0xFFFFD43F), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }

                            // Quick Finance Actions
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Finance Quick Operations", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                Toast.makeText(context, "Payment collection reminder notifications broadcasted to all pending accounts!", Toast.LENGTH_LONG).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Send Reminders", color = if (isDark) Color(0xFF003735) else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = {
                                                Toast.makeText(context, "Mock Refund Request of Rs. 20,000 approved & released!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF027E3D)
                                            ),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Approve Refund", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        "allocations" -> {
                            // Active list of room assignments (Section 6)
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text(
                                        "Student Hostel Allocation Board",
                                        color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))

                                    HostelDataStore.students.forEach { std ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(
                                                    if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(std.name, color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(std.roll, color = subTextColor, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Allocated: ${std.room} (${std.block})", color = subTextColor, fontSize = 12.sp)
                                                Text(
                                                    "Fees: ${std.feePaidStatus}",
                                                    color = if (std.feePaidStatus == "Paid") Color(0xFF76DB8F) else Color(0xFFFFB4AB),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(
                    "Update Hostel Info",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Warden Details", fontWeight = FontWeight.Bold, color = textColor)
                    OutlinedTextField(
                        value = chiefWardenName,
                        onValueChange = { chiefWardenName = it },
                        label = { Text("Chief Warden Name") }
                    )
                    OutlinedTextField(
                        value = chiefWardenPhone,
                        onValueChange = { chiefWardenPhone = it },
                        label = { Text("Chief Warden Phone") }
                    )
                    OutlinedTextField(
                        value = blockAWardenName,
                        onValueChange = { blockAWardenName = it },
                        label = { Text("Block A Warden") }
                    )
                    OutlinedTextField(
                        value = blockAWardenPhone,
                        onValueChange = { blockAWardenPhone = it },
                        label = { Text("Block A Phone") }
                    )
                    OutlinedTextField(
                        value = blockBWardenName,
                        onValueChange = { blockBWardenName = it },
                        label = { Text("Block B Warden") }
                    )
                    OutlinedTextField(
                        value = blockBWardenPhone,
                        onValueChange = { blockBWardenPhone = it },
                        label = { Text("Block B Phone") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Mess Schedules", fontWeight = FontWeight.Bold, color = textColor)
                    OutlinedTextField(
                        value = tiffinTiming,
                        onValueChange = { tiffinTiming = it },
                        label = { Text("Tiffin Timing") }
                    )
                    OutlinedTextField(
                        value = lunchTiming,
                        onValueChange = { lunchTiming = it },
                        label = { Text("Lunch Timing") }
                    )
                    OutlinedTextField(
                        value = dinnerTiming,
                        onValueChange = { dinnerTiming = it },
                        label = { Text("Dinner Timing") }
                    )
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
                        Toast.makeText(context, "Hostel Information updated successfully!", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                    )
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

    // Admin profile edit dialog (Section 10)
    if (showProfileEditDialog) {
        AlertDialog(
            onDismissRequest = { showProfileEditDialog = false },
            title = { Text("Edit Admin Profile", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = adminName,
                        onValueChange = { adminName = it },
                        label = { Text("Administrator Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = adminPhone,
                        onValueChange = { adminPhone = it },
                        label = { Text("Contact Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showProfileEditDialog = false
                        Toast.makeText(context, "Admin profile updated successfully!", Toast.LENGTH_SHORT).show()
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
