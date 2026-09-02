package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.AnnouncementItem
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.StudentProfile
import com.example.hprams.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onSignOutClick: () -> Unit,
    onAccountsClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onSecurityShiftsClick: () -> Unit,
    onReportsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("home") }
    var searchQuery by remember { mutableStateOf("") }

    val adminName = HostelDataStore.adminName
    val pendingStudents = HostelDataStore.students.filter { it.approvalStatus != "Approved" }
    val totalStudents = HostelDataStore.students.size
    val unpaidFines = HostelDataStore.fines.filter { it.status == "Unpaid" }.size

    // Modals and dialog states
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var showStudentBioDialog by remember { mutableStateOf<StudentProfile?>(null) }
    var showAdminProfileDialog by remember { mutableStateOf(false) }

    // Broadcast Form State
    var broadcastTarget by remember { mutableStateOf("All Students & Staff") }
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastContent by remember { mutableStateOf("") }
    var broadcastCategory by remember { mutableStateOf("ADMIN NOTICE") }

    // Filtered search results
    val searchResults = remember(searchQuery, HostelDataStore.students.size) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val q = searchQuery.trim().lowercase()
            HostelDataStore.students.filter {
                it.name.lowercase().contains(q) ||
                it.roll.lowercase().contains(q) ||
                it.email.lowercase().contains(q) ||
                it.room.lowercase().contains(q) ||
                it.block.lowercase().contains(q)
            }
        }
    }

    // -------------------------------------------------------------
    // DIALOG: BROADCAST / ANNOUNCEMENT COMPOSER
    // -------------------------------------------------------------
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF6366F1))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Announcement", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Target Audience:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF334155))
                    val targets = listOf(
                        "All Students & Staff",
                        "All Students",
                        "All Staff",
                        "Boys Hostel Only",
                        "Girls Hostel Only"
                    )
                    targets.forEach { target ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { broadcastTarget = target }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (broadcastTarget == target),
                                onClick = { broadcastTarget = target },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6366F1))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(target, fontSize = 13.sp, color = Color(0xFF1E293B))
                        }
                    }

                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Announcement Title") },
                        placeholder = { Text("e.g. Campus Curfew / Mess Timings") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = broadcastContent,
                        onValueChange = { broadcastContent = it },
                        label = { Text("Detailed Notice / Message") },
                        placeholder = { Text("Write your message here...") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitle.isBlank() || broadcastContent.isBlank()) {
                            Toast.makeText(context, "Please enter title and content.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newAnnouncement = AnnouncementItem(
                            id = "ANN-${(1000..9999).random()}",
                            title = "[$broadcastTarget] $broadcastTitle",
                            category = broadcastCategory,
                            date = "Today",
                            content = broadcastContent,
                            targetHostel = when (broadcastTarget) {
                                "Boys Hostel Only" -> "Boys"
                                "Girls Hostel Only" -> "Girls"
                                else -> "All"
                            }
                        )
                        HostelDataStore.saveAnnouncement(newAnnouncement)
                        Toast.makeText(context, "Broadcast sent to $broadcastTarget!", Toast.LENGTH_LONG).show()
                        broadcastTitle = ""
                        broadcastContent = ""
                        showBroadcastDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Send Broadcast", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // -------------------------------------------------------------
    // DIALOG: STUDENT BIO & FULL DETAILS POPUP
    // -------------------------------------------------------------
    if (showStudentBioDialog != null) {
        val s = showStudentBioDialog!!
        val studentComplaints = HostelDataStore.complaints.filter { it.studentName.equals(s.name, ignoreCase = true) }
        val studentFines = HostelDataStore.fines.filter { it.studentRoll == s.roll }
        AlertDialog(
            onDismissRequest = { showStudentBioDialog = null },
            title = {
                Column {
                    Text(s.name, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 18.sp)
                    Text("Roll: ${s.roll} • ${s.role}", color = Color(0xFF6366F1), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Personal & Hostel Bio", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                            Text("Email: ${s.email}", fontSize = 12.sp, color = Color(0xFF334155))
                            Text("Phone: ${s.phone}", fontSize = 12.sp, color = Color(0xFF334155))
                            Text("Gender: ${s.gender} • DOB: ${s.dob.ifEmpty { "N/A" }}", fontSize = 12.sp, color = Color(0xFF334155))
                            Text("Allocated: ${s.block}, Room ${s.room}", fontSize = 12.sp, color = Color(0xFF334155))
                            Text("Father: ${s.fatherName} (${s.emergencyPhone})", fontSize = 12.sp, color = Color(0xFF334155))
                            Text("Approval: ${s.approvalStatus}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (s.approvalStatus == "Approved") Color(0xFF10B981) else Color(0xFFF59E0B))
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Fee & Finance Status", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                            Text("Hostel Fee Status: ${s.feePaidStatus}", fontSize = 12.sp, color = if (s.feePaidStatus == "Paid") Color(0xFF10B981) else Color(0xFFEF4444), fontWeight = FontWeight.SemiBold)
                            Text("Payment Gateway: ${s.paymentStatus}", fontSize = 12.sp, color = Color(0xFF475569))
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Discipline & Complaints", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                            Text("Raised Complaints: ${studentComplaints.size}", fontSize = 12.sp, color = Color(0xFF475569))
                            Text("Assigned Fines: ${studentFines.size} (Total ₹${studentFines.sumOf { it.amount.toIntOrNull() ?: 0 }})", fontSize = 12.sp, color = Color(0xFF475569))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStudentBioDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // -------------------------------------------------------------
    // DIALOG: ADMIN PROFILE
    // -------------------------------------------------------------
    if (showAdminProfileDialog) {
        AlertDialog(
            onDismissRequest = { showAdminProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color(0xFF6366F1))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Administrator Profile", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Name: $adminName", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Text("Role: Super Administrator", color = Color(0xFF475569))
                    Text("Email: ammananasanju@gmail.com", color = Color(0xFF475569))
                    Text("Access Scope: Complete Campus Hostels (Boys & Girls)", color = Color(0xFF475569))
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAdminProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("OK", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAdminProfileDialog = false
                    onSignOutClick()
                }) {
                    Text("Sign Out", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // -------------------------------------------------------------
    // MAIN LAYOUT
    // -------------------------------------------------------------
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ModernBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 85.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Top Greeting Header with Notification Bell & Profile
            ModernTopGreeting(
                userName = adminName.split(" ").firstOrNull() ?: adminName,
                subtitle = "Administrator Control Center",
                actionIcon = Icons.Default.Person,
                onActionClick = { showAdminProfileDialog = true },
                onNotificationsClick = { showBroadcastDialog = true }
            )

            // 2. Search Bar for instant Student Bio lookup
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search student roll, name, email or room..."
            )

            // Live Search Results Dropdown Preview
            if (searchResults.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Search Results (${searchResults.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6366F1))
                        searchResults.forEach { student ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { showStudentBioDialog = student }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(student.name, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                                    Text("${student.roll} • ${student.block}, Room ${student.room}", color = Color(0xFF64748B), fontSize = 12.sp)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
                            }
                        }
                    }
                }
            }

            // 3. Hero Action Banner: Account Approvals
            ModernHeroBanner(
                title = "Account Approvals (${pendingStudents.size} Pending)",
                subtitle = if (pendingStudents.isEmpty()) "All campus accounts are approved." else "Tap to open Directory & approve registrations",
                icon = Icons.Default.VerifiedUser,
                onClick = onAccountsClick
            )

            // 4. Quick Access Header
            Text(
                text = "Quick Access",
                color = Color(0xFF0F172A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // 5. 2x2 Grid of Quick Access Cards (Dedicated Navigation)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ModernQuickCard(
                        title = "Accounts",
                        subtitle = "$totalStudents Registered",
                        icon = Icons.Default.People,
                        containerColor = CardBlueBg,
                        iconTint = IconBlueTint,
                        onClick = onAccountsClick,
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Broadcasts",
                        subtitle = "Targeted Notices",
                        icon = Icons.Default.Campaign,
                        containerColor = CardGreenBg,
                        iconTint = IconGreenTint,
                        onClick = { showBroadcastDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ModernQuickCard(
                        title = "Finance",
                        subtitle = "Collections & Dues",
                        icon = Icons.Default.Payments,
                        containerColor = CardPinkBg,
                        iconTint = IconPinkTint,
                        onClick = onFinanceClick,
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Gate Shifts",
                        subtitle = "Security Allotment",
                        icon = Icons.Default.Security,
                        containerColor = CardPurpleBg,
                        iconTint = IconPurpleTint,
                        onClick = onSecurityShiftsClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Modern Bottom Nav Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            ModernBottomNavBar(
                items = listOf(
                    BottomNavItem("home", "Home", Icons.Default.Home),
                    BottomNavItem("accounts", "Accounts", Icons.Default.People),
                    BottomNavItem("finance", "Finance", Icons.Default.Payments),
                    BottomNavItem("security", "Security", Icons.Default.Security)
                ),
                selectedId = selectedTab,
                onSelect = { id ->
                    selectedTab = id
                    when (id) {
                        "accounts" -> onAccountsClick()
                        "finance" -> onFinanceClick()
                        "security" -> onSecurityShiftsClick()
                    }
                }
            )
        }
    }
}
