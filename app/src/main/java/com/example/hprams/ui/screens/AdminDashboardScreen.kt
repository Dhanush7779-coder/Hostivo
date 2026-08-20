package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.*
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.AnnouncementItem
import com.example.hprams.data.StudentProfile
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.theme.AccentColor

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

fun downloadPaymentsCsv(context: android.content.Context, payments: List<com.example.hprams.data.PaymentItem>, students: List<StudentProfile>) {
    try {
        val csvBuilder = StringBuilder()
        csvBuilder.append("Payment ID,Student ID,Student Name,Amount,Method,Type,Status,Reference,Date,Verified By,Verified At\n")
        payments.forEach { pay ->
            val stud = students.find { it.roll == pay.studentId }
            val studName = stud?.name ?: "Unknown"
            csvBuilder.append("${pay.paymentId},${pay.studentId},\"$studName\",${pay.amount},${pay.paymentMethod},${pay.paymentType},${pay.paymentStatus},${pay.paymentReference},${pay.paymentDate},${pay.verifiedBy},${pay.verifiedAt}\n")
        }
        val fileContents = csvBuilder.toString()
        val filename = "payments_report_${System.currentTimeMillis()}.csv"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(fileContents.toByteArray())
                }
                Toast.makeText(context, "CSV Report saved to Downloads folder!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to create file in Downloads", Toast.LENGTH_SHORT).show()
            }
        } else {
            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(path, filename)
            file.writeText(fileContents)
            Toast.makeText(context, "CSV Report saved to: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to download CSV: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

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

    var activeTab by remember { mutableStateOf("controls") } // "controls", "security", "finance", "allocations", "accounts"
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

    // Security assignments
    var securityBlock by remember { mutableStateOf(HostelDataStore.securityBlockAssignment) }
    var securityDutyHours by remember { mutableStateOf(HostelDataStore.securityDutyTimings) }
    var securityOfficerName by remember { mutableStateOf(HostelDataStore.securityOfficerName) }
    var showBlockDropdown by remember { mutableStateOf(false) }

    // Admin profile settings
    var adminName by remember { mutableStateOf(HostelDataStore.adminName) }
    var adminPhone by remember { mutableStateOf("9492409574") }
    var showProfileEditDialog by remember { mutableStateOf(false) }

    // Announcement forms
    var announcementTitle by remember { mutableStateOf("") }
    var announcementContent by remember { mutableStateOf("") }
    var targetHostelGroup by remember { mutableStateOf("All") }
    var showAnnounceDropdown by remember { mutableStateOf(false) }

    // Account editing state
    var selectedStudentForEdit by remember { mutableStateOf<StudentProfile?>(null) }
    var showAccountEditDialog by remember { mutableStateOf(false) }

    // Account form fields
    var editName by remember { mutableStateOf("") }
    var editRoll by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editFather by remember { mutableStateOf("") }
    var editEmergency by remember { mutableStateOf("") }
    var editBlock by remember { mutableStateOf("") }
    var editRoom by remember { mutableStateOf("") }
    var editRole by remember { mutableStateOf("Student") }
    var editApproval by remember { mutableStateOf("Pending") }
    var editGender by remember { mutableStateOf("Male") }
    var editDob by remember { mutableStateOf("") }

    // Account creation state
    var showCreateAccountDialog by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    var createRoll by remember { mutableStateOf("") }
    var createEmail by remember { mutableStateOf("") }
    var createPhone by remember { mutableStateOf("") }
    var createPassword by remember { mutableStateOf("Welcome@123") }
    var createFather by remember { mutableStateOf("") }
    var createEmergency by remember { mutableStateOf("") }
    var createBlock by remember { mutableStateOf("Block A") }
    var createRoom by remember { mutableStateOf("101") }
    var createRole by remember { mutableStateOf("Student") }
    var createGender by remember { mutableStateOf("Male") }
    var createDob by remember { mutableStateOf("") }

    val totalStudentsCount = HostelDataStore.students.size
    val activeRoomRequestsCount = HostelDataStore.roomChangeRequests.filter { it.status == "Pending" }.size
    val unpaidFinesCount = HostelDataStore.fines.filter { it.status == "Unpaid" }.size

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                var showDropdownMenu by remember { mutableStateOf(false) }
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
                                    text = { Text("Edit Admin Profile", color = textColor) },
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tab Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "controls" to "Controls",
                        "accounts" to "Accounts & Approvals",
                        "security" to "Security Roster",
                        "finance" to "Finance"
                    ).forEach { (tabId, label) ->
                        val isActive = activeTab == tabId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isActive) {
                                        if (isDark) Color(0xFF29FCF3).copy(alpha = 0.2f) else Color(0xFF006A66).copy(alpha = 0.2f)
                                    } else {
                                        if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
                                    }
                                )
                                .clickable { activeTab = tabId }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isActive) {
                                    if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                } else subTextColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
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
                            // Admin Profile Details
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Admin: $adminName", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Contact Phone: $adminPhone", color = subTextColor, fontSize = 12.sp)
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
                                        Text("Total Accounts", color = subTextColor, style = MaterialTheme.typography.labelSmall)
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
                                                listOf("All", "Students", "Staff").forEach { gp ->
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
                                            val newAnn = AnnouncementItem(
                                                id = "ANN-${(100..999).random()}",
                                                title = announcementTitle,
                                                category = "GENERAL BROADCAST",
                                                date = "18 Aug 2026",
                                                content = announcementContent,
                                                targetHostel = targetHostelGroup
                                            )
                                            HostelDataStore.saveAnnouncement(newAnn)

                                            val notification = com.example.hprams.data.NotificationItem(
                                                id = "NTF-${(1000..9999).random()}",
                                                userId = "All",
                                                title = "New Announcement: ${newAnn.title}",
                                                message = newAnn.content,
                                                type = "ANNOUNCEMENT",
                                                timestamp = "18 Aug 2026",
                                                deepLink = "announcements"
                                            )
                                            HostelDataStore.saveNotification(notification, context)
                                            
                                            announcementTitle = ""
                                            announcementContent = ""
                                            Toast.makeText(context, "Announcement broadcasted successfully!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Broadcast Announcement", color = if (isDark) Color(0xFF003735) else Color.White)
                                    }
                                }
                            }
                        }

                        "accounts" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Manage User Accounts & Approvals",
                                    color = AccentColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Button(
                                    onClick = {
                                        createName = ""
                                        createRoll = ""
                                        createEmail = ""
                                        createPhone = ""
                                        createFather = ""
                                        createEmergency = ""
                                        createDob = ""
                                        showCreateAccountDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add User", color = Color.White, fontSize = 12.sp)
                                }
                            }
                            if (HostelDataStore.students.isEmpty()) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No registered accounts found in database.", color = subTextColor)
                                }
                            } else {
                                HostelDataStore.students.forEach { profile ->
                                    val isApproved = profile.approvalStatus == "Approved"
                                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(profile.name, color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    Text("Role: ${profile.role}", color = AccentColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (isApproved) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFF44336).copy(alpha = 0.2f),
                                                                RoundedCornerShape(6.dp)
                                                            )
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            profile.approvalStatus,
                                                            color = if (isApproved) Color(0xFF81C784) else Color(0xFFE57373),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }

                                            Divider(color = Color.White.copy(alpha = 0.1f))

                                            Text("Email: ${profile.email}", color = subTextColor, fontSize = 12.sp)
                                            Text("Phone: ${profile.phone}", color = subTextColor, fontSize = 12.sp)
                                            Text("Gender: ${profile.gender} | DOB: ${profile.dob.ifEmpty { "Not Specified" }}", color = subTextColor, fontSize = 12.sp)
                                            if (profile.role == "Student") {
                                                Text("Roll: ${profile.roll}", color = subTextColor, fontSize = 12.sp)
                                                Text("Father's Name: ${profile.fatherName}", color = subTextColor, fontSize = 12.sp)
                                                Text("Emergency: ${profile.emergencyPhone}", color = subTextColor, fontSize = 12.sp)
                                                Text("Room: ${profile.block}, Room ${profile.room}", color = subTextColor, fontSize = 12.sp)
                                                
                                                // Display Payment status indicator/notification
                                                val hasPaid = profile.feePaidStatus == "Paid"
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("Fee Paid Status:", color = subTextColor, fontSize = 12.sp)
                                                    Box(
                                                        modifier = Modifier
                                                            .background(
                                                                if (hasPaid) Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFF44336).copy(alpha = 0.2f),
                                                                RoundedCornerShape(6.dp)
                                                            )
                                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                                    ) {
                                                        Text(
                                                            if (hasPaid) "PAID" else "NOT PAID (Pending)",
                                                            color = if (hasPaid) Color(0xFF81C784) else Color(0xFFE57373),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                if (!isApproved) {
                                                    Button(
                                                        onClick = {
                                                            val updated = profile.copy(approvalStatus = "Approved")
                                                            HostelDataStore.saveStudent(updated)
                                                            Toast.makeText(context, "Account Approved!", Toast.LENGTH_SHORT).show()
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Text("Approve", color = Color.White)
                                                    }
                                                }
                                                Button(
                                                    onClick = {
                                                        selectedStudentForEdit = profile
                                                        editName = profile.name
                                                        editRoll = profile.roll
                                                        editEmail = profile.email
                                                        editPhone = profile.phone
                                                        editFather = profile.fatherName
                                                        editEmergency = profile.emergencyPhone
                                                        editBlock = profile.block
                                                        editRoom = profile.room
                                                        editRole = profile.role
                                                        editApproval = profile.approvalStatus
                                                        editGender = profile.gender
                                                        editDob = profile.dob
                                                        showAccountEditDialog = true
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Edit Info", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
                                                }
                                                Button(
                                                    onClick = {
                                                        val db = com.google.firebase.database.FirebaseDatabase.getInstance().reference
                                                        db.child("students").child(profile.roll).removeValue()
                                                        Toast.makeText(context, "Account deleted successfully!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Delete", color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        "security" -> {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Security Assignments", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    OutlinedTextField(
                                        value = securityOfficerName,
                                        onValueChange = { securityOfficerName = it },
                                        label = { Text("Security Officer Name / Person") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = securityDutyHours,
                                        onValueChange = { securityDutyHours = it },
                                        label = { Text("Duty Shift Timings") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text("Currently Assigned to: $securityBlock", color = textColor, fontSize = 13.sp)
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                            .clickable { showBlockDropdown = true }
                                            .padding(12.dp)
                                    ) {
                                        Text(securityBlock, color = textColor, fontWeight = FontWeight.SemiBold)
                                        DropdownMenu(
                                            expanded = showBlockDropdown,
                                            onDismissRequest = { showBlockDropdown = false },
                                            modifier = Modifier.background(if (isDark) Color(0xFF101415) else Color(0xFFE3EAE9))
                                        ) {
                                            listOf("Block A & B", "Block C & D", "Main Gate Entrance").forEach { b ->
                                                DropdownMenuItem(
                                                    text = { Text(b, color = textColor) },
                                                    onClick = {
                                                        securityBlock = b
                                                        showBlockDropdown = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    Button(
                                        onClick = {
                                            HostelDataStore.securityBlockAssignment = securityBlock
                                            HostelDataStore.securityDutyTimings = securityDutyHours
                                            HostelDataStore.securityOfficerName = securityOfficerName
                                            Toast.makeText(context, "Security duty roster updated successfully", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Update Security Roster", color = if (isDark) Color(0xFF003735) else Color.White)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Mess & Campus Timings", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    OutlinedTextField(value = tiffinTiming, onValueChange = { tiffinTiming = it }, label = { Text("Tiffin Timings") }, modifier = Modifier.fillMaxWidth())
                                    OutlinedTextField(value = lunchTiming, onValueChange = { lunchTiming = it }, label = { Text("Lunch Timings") }, modifier = Modifier.fillMaxWidth())
                                    OutlinedTextField(value = dinnerTiming, onValueChange = { dinnerTiming = it }, label = { Text("Dinner Timings") }, modifier = Modifier.fillMaxWidth())
                                    Button(
                                        onClick = {
                                            HostelDataStore.tiffinTiming = tiffinTiming
                                            HostelDataStore.lunchTiming = lunchTiming
                                            HostelDataStore.dinnerTiming = dinnerTiming
                                            Toast.makeText(context, "Mess and campus timings updated successfully", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Save Timings", color = if (isDark) Color(0xFF003735) else Color.White)
                                    }
                                }
                            }
                        }

                        "finance" -> {
                            // Dashboard statistics inside the fees section (Requirement 10)
                            val totalPaidAmt = HostelDataStore.payments.filter { it.paymentStatus == "PAID" }.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
                            val totalPaidCount = HostelDataStore.students.filter { it.role == "Student" && it.feePaidStatus == "Paid" }.size
                            val totalUnderVerification = HostelDataStore.payments.filter { it.paymentStatus == "UNDER_VERIFICATION" }.size
                            val totalUnpaidCount = HostelDataStore.students.filter { it.role == "Student" && it.feePaidStatus != "Paid" }.size

                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Text("Finance Dashboard & Summary", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))) {
                                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Total Collected", color = subTextColor, fontSize = 10.sp)
                                                Text("Rs. ${totalPaidAmt.toInt()}", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                        }
                                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))) {
                                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Paid Students", color = subTextColor, fontSize = 10.sp)
                                                Text("$totalPaidCount", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                        }
                                    }
                                    
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))) {
                                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Verification Pending", color = subTextColor, fontSize = 10.sp)
                                                Text("$totalUnderVerification", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                        }
                                        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))) {
                                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text("Unpaid / Pending", color = subTextColor, fontSize = 10.sp)
                                                Text("$totalUnpaidCount", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            downloadPaymentsCsv(context, HostelDataStore.payments, HostelDataStore.students)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Download CSV Report", color = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Pending Fee Receipt Verification", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            val pendingPayments = HostelDataStore.payments.filter { it.paymentStatus == "UNDER_VERIFICATION" }
                            if (pendingPayments.isEmpty()) {
                                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                                    Text("No pending receipts to verify.", color = subTextColor)
                                }
                            } else {
                                pendingPayments.forEach { payment ->
                                    val matchedStudent = HostelDataStore.students.find { it.roll == payment.studentId }
                                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("Student: ${matchedStudent?.name ?: "Unknown"} (${payment.studentId})", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("Amount: Rs. ${payment.amount}", color = textColor, fontSize = 13.sp)
                                            Text("Method: ${payment.paymentMethod}", color = subTextColor, fontSize = 12.sp)
                                            Text("Ref UTR: ${payment.paymentReference}", color = subTextColor, fontSize = 12.sp)
                                            Text("Receipt Link: ${payment.receiptUrl}", color = AccentColor, fontSize = 12.sp, modifier = Modifier.clickable {
                                                Toast.makeText(context, "Opening receipt link...", Toast.LENGTH_SHORT).show()
                                            })
                                            
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Button(
                                                    onClick = {
                                                        val updatedPayment = payment.copy(paymentStatus = "PAID", verifiedBy = "Admin C.Venkat Dhanush", verifiedAt = "18 Aug 2026")
                                                        HostelDataStore.savePayment(updatedPayment)
                                                        
                                                        matchedStudent?.let {
                                                            val updatedStudent = it.copy(feePaidStatus = "Paid", paymentStatus = "Paid", approvalStatus = "Approved")
                                                            HostelDataStore.saveStudent(updatedStudent)
                                                            
                                                            val stdNotif = com.example.hprams.data.NotificationItem(
                                                                id = "NTF-${(1000..9999).random()}",
                                                                userId = it.roll,
                                                                title = "Hostel Fee Approved",
                                                                message = "Your hostel fee payment has been successfully verified. You are now PAID.",
                                                                type = "PAYMENT",
                                                                timestamp = "18 Aug 2026",
                                                                deepLink = "finance_dashboard"
                                                            )
                                                            HostelDataStore.saveNotification(stdNotif, context)

                                                            val wardenNotif = com.example.hprams.data.NotificationItem(
                                                                id = "NTF-${(1000..9999).random()}",
                                                                userId = "Warden",
                                                                title = "Fee Paid: ${it.name}",
                                                                message = "${it.name} (${it.roll}) has paid the hostel fees and is eligible for room allocation.",
                                                                type = "ALLOCATION",
                                                                timestamp = "18 Aug 2026",
                                                                deepLink = "warden_dashboard"
                                                            )
                                                            HostelDataStore.saveNotification(wardenNotif, context)
                                                        }
                                                        Toast.makeText(context, "Payment approved!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Approve", color = Color.White)
                                                }
                                                Button(
                                                    onClick = {
                                                        val updatedPayment = payment.copy(paymentStatus = "REJECTED", verifiedBy = "Admin C.Venkat Dhanush", verifiedAt = "18 Aug 2026", rejectionReason = "Invalid receipt image")
                                                        HostelDataStore.savePayment(updatedPayment)
                                                        
                                                        matchedStudent?.let {
                                                            val updatedStudent = it.copy(feePaidStatus = "Rejected", paymentStatus = "Rejected")
                                                            HostelDataStore.saveStudent(updatedStudent)
                                                            
                                                            val stdNotif = com.example.hprams.data.NotificationItem(
                                                                id = "NTF-${(1000..9999).random()}",
                                                                userId = it.roll,
                                                                title = "Hostel Fee Rejected",
                                                                message = "Your fee payment receipt was rejected by Admin: Invalid receipt image.",
                                                                type = "PAYMENT",
                                                                timestamp = "18 Aug 2026",
                                                                deepLink = "finance_dashboard"
                                                            )
                                                            HostelDataStore.saveNotification(stdNotif, context)
                                                        }
                                                        Toast.makeText(context, "Payment rejected!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text("Reject", color = Color.White)
                                                }
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

    // Hostel information details dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text("Edit Hostel Information", color = textColor, fontWeight = FontWeight.Bold)
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
                    OutlinedTextField(value = chiefWardenName, onValueChange = { chiefWardenName = it }, label = { Text("Chief Warden Name") })
                    OutlinedTextField(value = chiefWardenPhone, onValueChange = { chiefWardenPhone = it }, label = { Text("Chief Warden Phone") })
                    OutlinedTextField(value = blockAWardenName, onValueChange = { blockAWardenName = it }, label = { Text("Block A Warden") })
                    OutlinedTextField(value = blockAWardenPhone, onValueChange = { blockAWardenPhone = it }, label = { Text("Block A Phone") })
                    OutlinedTextField(value = blockBWardenName, onValueChange = { blockBWardenName = it }, label = { Text("Block B Warden") })
                    OutlinedTextField(value = blockBWardenPhone, onValueChange = { blockBWardenPhone = it }, label = { Text("Block B Phone") })
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
                        Toast.makeText(context, "Hostel Information updated successfully!", Toast.LENGTH_SHORT).show()
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

    // Admin profile edit dialog
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
                        HostelDataStore.adminName = adminName
                        showProfileEditDialog = false
                        Toast.makeText(context, "Admin profile updated successfully!", Toast.LENGTH_SHORT).show()
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

    // Account Edit Dialog
    if (showAccountEditDialog && selectedStudentForEdit != null) {
        AlertDialog(
            onDismissRequest = { showAccountEditDialog = false },
            title = { Text("Edit Account Info", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Contact Phone") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (editRole == "Student") {
                        OutlinedTextField(
                            value = editRoll,
                            onValueChange = { editRoll = it },
                            label = { Text("Roll Number") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editFather,
                            onValueChange = { editFather = it },
                            label = { Text("Father's Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editEmergency,
                            onValueChange = { editEmergency = it },
                            label = { Text("Emergency Phone") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editBlock,
                            onValueChange = { editBlock = it },
                            label = { Text("Wing/Block") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editRoom,
                            onValueChange = { editRoom = it },
                            label = { Text("Room Assignment") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Role selection drop option
                    Text("Role Assignment: $editRole", color = textColor, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Student", "Warden", "Security").forEach { r ->
                            val isSelected = editRole == r
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) AccentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .clickable { editRole = r }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(r, color = if (isSelected) AccentColor else subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Approval status selector
                    Text("Approval Status: $editApproval", color = textColor, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Approved", "Pending").forEach { ap ->
                            val isSelected = editApproval == ap
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) AccentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .clickable { editApproval = ap }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(ap, color = if (isSelected) AccentColor else subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = editDob,
                        onValueChange = { editDob = it },
                        label = { Text("Date of Birth (DD/MM/YYYY)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(if (editRole == "Warden") "Warden Scope (Gender): $editGender" else "Gender: $editGender", color = textColor, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Male", "Female").forEach { g ->
                            val isSel = editGender == g
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) AccentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .clickable { editGender = g }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (editRole == "Warden") (if (g == "Male") "Boys Warden" else "Girls Warden") else g,
                                    color = if (isSel) AccentColor else subTextColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val original = selectedStudentForEdit!!
                        
                        // Room capacity check (Requirement 5)
                        if (editRoom != original.room && editRoom.isNotEmpty() && editRole == "Student") {
                            val targetBlock = editBlock.ifEmpty { original.block }
                            if (!com.example.hprams.data.RoomRules.isRoomAvailable(targetBlock, editRoom)) {
                                Toast.makeText(context, "Cannot allocate room: Room capacity exceeded!", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                        }

                        val updated = original.copy(
                            name = editName,
                            roll = editRoll,
                            phone = editPhone,
                            fatherName = editFather,
                            emergencyPhone = editEmergency,
                            block = editBlock,
                            room = editRoom,
                            role = editRole,
                            approvalStatus = editApproval,
                            gender = editGender,
                            dob = editDob
                        )
                        // If Roll number changed, we delete old database key and write new one
                        if (original.roll != updated.roll && original.roll.isNotEmpty()) {
                            // Write new details first
                            HostelDataStore.saveStudent(updated)
                            // Remove old key using Firebase reference helper
                            val db = com.google.firebase.database.FirebaseDatabase.getInstance().reference
                            db.child("students").child(original.roll).removeValue()
                        } else {
                            HostelDataStore.saveStudent(updated)
                        }
                        showAccountEditDialog = false
                        Toast.makeText(context, "Account updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
                ) {
                    Text("Save Info", color = if (isDark) Color(0xFF003735) else Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccountEditDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Manual user account creation dialog (Requirement 10)
    if (showCreateAccountDialog) {
        AlertDialog(
            onDismissRequest = { showCreateAccountDialog = false },
            title = { Text("Create New User Account", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(value = createName, onValueChange = { createName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = createEmail, onValueChange = { createEmail = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = createPhone, onValueChange = { createPhone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = createPassword, onValueChange = { createPassword = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = createDob, onValueChange = { createDob = it }, label = { Text("Date of Birth (DD/MM/YYYY)") }, modifier = Modifier.fillMaxWidth())

                    val genderLabel = if (createRole == "Warden") "Warden Scope:" else "Gender:"
                    val currentGenderSelectionText = if (createRole == "Warden") (if (createGender == "Male") "Boys Warden" else "Girls Warden") else createGender
                    Text("$genderLabel $currentGenderSelectionText", color = textColor, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Male", "Female").forEach { g ->
                            val isSel = createGender == g
                            val optionText = if (createRole == "Warden") (if (g == "Male") "Boys Warden" else "Girls Warden") else g
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) AccentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .clickable { createGender = g }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(optionText, color = if (isSel) AccentColor else subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Text("Role Assignment: $createRole", color = textColor, fontSize = 13.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Student", "Warden", "Security").forEach { r ->
                            val isSel = createRole == r
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) AccentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                    .clickable { createRole = r }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(r, color = if (isSel) AccentColor else subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (createRole == "Student") {
                        OutlinedTextField(value = createRoll, onValueChange = { createRoll = it }, label = { Text("Roll Number") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = createFather, onValueChange = { createFather = it }, label = { Text("Father's Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = createEmergency, onValueChange = { createEmergency = it }, label = { Text("Emergency Phone") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = createBlock, onValueChange = { createBlock = it }, label = { Text("Wing/Block") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = createRoom, onValueChange = { createRoom = it }, label = { Text("Room Assignment") }, modifier = Modifier.fillMaxWidth())
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (createName.isEmpty() || createEmail.isEmpty() || createPhone.isEmpty()) {
                            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val resolvedRoll = if (createRole == "Student") createRoll else "STAFF-${(1000..9999).random()}"
                        if (createRole == "Student") {
                            if (resolvedRoll.isEmpty()) {
                                Toast.makeText(context, "Roll number is required for students", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            // Capacity check (Requirement 5)
                            if (!com.example.hprams.data.RoomRules.isRoomAvailable(createBlock, createRoom)) {
                                Toast.makeText(context, "Cannot allocate room: Room capacity exceeded!", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                        }

                        val newProfile = StudentProfile(
                            roll = resolvedRoll,
                            name = createName,
                            email = createEmail,
                            phone = createPhone,
                            gender = createGender,
                            block = createBlock,
                            room = createRoom,
                            feePaidStatus = "Pending",
                            paymentStatus = "Pending",
                            approvalStatus = "Approved",
                            fatherName = createFather,
                            emergencyPhone = createEmergency,
                            role = createRole,
                            dob = createDob
                        )
                        HostelDataStore.saveStudent(newProfile)
                        showCreateAccountDialog = false
                        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66))
                ) {
                    Text("Create Account", color = if (isDark) Color(0xFF003735) else Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAccountDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
