package com.example.hprams.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.AnnouncementItem
import com.example.hprams.data.FineItem
import com.example.hprams.data.HostelDataStore
import com.example.hprams.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenDashboardScreen(
    onSignOutClick: () -> Unit,
    onAllocationsClick: () -> Unit,
    onComplaintsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { (context as? Activity)?.finish() }

    var selectedTab by remember { mutableStateOf("home") }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog states
    var showWardenProfileDialog by remember { mutableStateOf(false) }
    var showFinesDialog by remember { mutableStateOf(false) }
    var showNewFineDialog by remember { mutableStateOf(false) }
    var showNoticeDialog by remember { mutableStateOf(false) }

    // New Fine Form state
    var fineStudentRoll by remember { mutableStateOf("") }
    var fineReason by remember { mutableStateOf("") }
    var fineAmount by remember { mutableStateOf("500") }

    // New Notice Form state
    var noticeTitle by remember { mutableStateOf("") }
    var noticeCategory by remember { mutableStateOf("MESS") }
    var noticeContent by remember { mutableStateOf("") }

    val wardenName = HostelDataStore.chiefWardenName
    val wardenScope = HostelDataStore.currentWardenScope
    val targetGender = if (wardenScope == "Girls") "Female" else "Male"

    val pendingPasses = HostelDataStore.gatePassRequests.filter { it.wardenApproval == "Pending" && it.gender == targetGender }
    val pendingRooms = HostelDataStore.roomChangeRequests.filter { it.status == "Pending" && it.gender == targetGender }
    val pendingComplaints = HostelDataStore.complaints.filter { it.status == "Pending" && it.gender == targetGender }
    val activeFines = HostelDataStore.fines

    // Interactive Search filtering
    val isSearching = searchQuery.isNotBlank()
    val searchResultsPasses = remember(searchQuery, pendingPasses.size) {
        if (!isSearching) emptyList()
        else pendingPasses.filter { it.studentName.contains(searchQuery, true) || it.studentRoll.contains(searchQuery, true) || it.reason.contains(searchQuery, true) }
    }
    val searchResultsComplaints = remember(searchQuery, pendingComplaints.size) {
        if (!isSearching) emptyList()
        else pendingComplaints.filter { it.studentName.contains(searchQuery, true) || it.title.contains(searchQuery, true) || it.category.contains(searchQuery, true) }
    }

    // -------------------------------------------------------------
    // WARDEN PROFILE DIALOG
    // -------------------------------------------------------------
    if (showWardenProfileDialog) {
        AlertDialog(
            onDismissRequest = { showWardenProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(wardenName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                        Text("Chief Warden • $wardenScope Hostel", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Jurisdiction", color = Color(0xFF64748B), fontSize = 13.sp)
                        Text("$wardenScope Hostel Blocks", color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Hostel Capacity", color = Color(0xFF64748B), fontSize = 13.sp)
                        Text("120 Rooms Active", color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Office Timings", color = Color(0xFF64748B), fontSize = 13.sp)
                        Text("08:00 AM - 09:30 PM", color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Helpline", color = Color(0xFF64748B), fontSize = 13.sp)
                        Text("+91 94400 12345", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showWardenProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // -------------------------------------------------------------
    // DISCIPLINE & FINES DIALOG
    // -------------------------------------------------------------
    if (showFinesDialog) {
        AlertDialog(
            onDismissRequest = { showFinesDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Hostel Fines & Discipline", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A))
                    Button(
                        onClick = { showNewFineDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("+ Issue Fine", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (activeFines.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp), contentAlignment = Alignment.Center) {
                            Text("No discipline fines issued.", color = Color(0xFF94A3B8), fontSize = 13.sp)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(activeFines) { fine ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(fine.reason, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                            Text("Student Roll: ${fine.studentRoll}", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text("₹${fine.amount}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFF43F5E))
                                            Text(fine.status, fontSize = 10.sp, color = if (fine.status == "Paid") Color(0xFF10B981) else Color(0xFFD97706), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFinesDialog = false }) {
                    Text("Close", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // -------------------------------------------------------------
    // ISSUE NEW FINE FORM DIALOG
    // -------------------------------------------------------------
    if (showNewFineDialog) {
        AlertDialog(
            onDismissRequest = { showNewFineDialog = false },
            title = {
                Text("Issue Disciplinary Fine", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A))
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HostivoTextField(
                        value = fineStudentRoll,
                        onValueChange = { fineStudentRoll = it },
                        label = "Student Roll Number",
                        placeholder = "e.g. 231801380007"
                    )

                    HostivoTextField(
                        value = fineReason,
                        onValueChange = { fineReason = it },
                        label = "Violation Reason",
                        placeholder = "e.g. Curfew breach / Unauthorized appliance"
                    )

                    HostivoTextField(
                        value = fineAmount,
                        onValueChange = { fineAmount = it },
                        label = "Fine Amount (₹)",
                        placeholder = "500",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (fineStudentRoll.isBlank() || fineReason.isBlank() || fineAmount.isBlank()) {
                            Toast.makeText(context, "Please enter Roll Number, Reason, and Amount.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newFine = FineItem(
                            id = "FINE-${(1000..9999).random()}",
                            studentRoll = fineStudentRoll.trim(),
                            reason = fineReason.trim(),
                            amount = fineAmount.trim(),
                            status = "Unpaid"
                        )
                        HostelDataStore.saveFine(newFine)
                        Toast.makeText(context, "Fine of ₹${newFine.amount} issued to ${newFine.studentRoll}!", Toast.LENGTH_LONG).show()
                        fineStudentRoll = ""
                        fineReason = ""
                        fineAmount = "500"
                        showNewFineDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Issue Fine", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFineDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // -------------------------------------------------------------
    // POST NOTICE / BROADCASTER DIALOG
    // -------------------------------------------------------------
    if (showNoticeDialog) {
        AlertDialog(
            onDismissRequest = { showNoticeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF6366F1))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Post Hostel Notice", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A))
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HostivoTextField(
                        value = noticeTitle,
                        onValueChange = { noticeTitle = it },
                        label = "Notice Title",
                        placeholder = "e.g. Special Dinner Tonight / Maintenance Notice"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("CATEGORY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("MESS", "MAINTENANCE", "RULES", "EVENT").forEach { cat ->
                                val isSel = noticeCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSel) Color(0xFF6366F1) else Color(0xFFF1F5F9))
                                        .clickable { noticeCategory = cat }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(cat, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else Color(0xFF64748B))
                                }
                            }
                        }
                    }

                    HostivoTextField(
                        value = noticeContent,
                        onValueChange = { noticeContent = it },
                        label = "Detailed Notice Content",
                        placeholder = "Type notice message..."
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noticeTitle.isBlank() || noticeContent.isBlank()) {
                            Toast.makeText(context, "Please enter title and content.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newAnnouncement = AnnouncementItem(
                            id = "ANN-${(1000..9999).random()}",
                            title = "[$wardenScope Hostel] ${noticeTitle.trim()}",
                            category = noticeCategory,
                            date = "Today",
                            content = noticeContent.trim(),
                            targetHostel = if (wardenScope == "Girls") "Girls" else "Boys"
                        )
                        HostelDataStore.saveAnnouncement(newAnnouncement)
                        Toast.makeText(context, "Notice posted to all residents!", Toast.LENGTH_SHORT).show()
                        noticeTitle = ""
                        noticeContent = ""
                        showNoticeDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Publish Notice", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoticeDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

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

            // 1. Top Greeting Header
            ModernTopGreeting(
                userName = wardenName.split(" ").firstOrNull() ?: wardenName,
                subtitle = "Warden Console • $wardenScope Hostel",
                actionIcon = Icons.Default.Person,
                onActionClick = { showWardenProfileDialog = true },
                onNotificationsClick = { onComplaintsClick() }
            )

            // 2. Search Bar
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search student pass, room change, tickets..."
            )

            // Search results display if query entered
            if (isSearching) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Search Results for \"$searchQuery\"", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                    if (searchResultsPasses.isEmpty() && searchResultsComplaints.isEmpty()) {
                        Text("No matching gate passes or complaints found.", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    }
                    searchResultsPasses.forEach { pass ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Gate Pass: ${pass.studentName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${pass.reason} • ${pass.outDate}", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (pass.wardenApproval == "Approved") Color(0xFFECFDF5) else Color(0xFFFFFBEB))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(pass.wardenApproval, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (pass.wardenApproval == "Approved") Color(0xFF10B981) else Color(0xFFD97706))
                                }
                            }
                        }
                    }
                }
            }

            // 3. Hero Action Banner: Gate Passes
            ModernHeroBanner(
                title = "Gate Passes (${pendingPasses.size} Pending)",
                subtitle = if (pendingPasses.isEmpty()) "All outpass requests reviewed" else "Tap to review and approve gate passes",
                icon = Icons.Default.VpnKey,
                onClick = { /* Gate pass section below */ }
            )

            // 4. Quick Access Header
            Text(
                text = "Quick Access",
                color = Color(0xFF0F172A),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // 5. 2x2 Grid of Quick Access Cards
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
                        title = "Room Requests",
                        subtitle = "${pendingRooms.size} Pending",
                        icon = Icons.Default.MeetingRoom,
                        containerColor = CardBlueBg,
                        iconTint = IconBlueTint,
                        onClick = onAllocationsClick,
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Complaints",
                        subtitle = "${pendingComplaints.size} Active",
                        icon = Icons.Default.ReportProblem,
                        containerColor = CardGreenBg,
                        iconTint = IconGreenTint,
                        onClick = onComplaintsClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ModernQuickCard(
                        title = "Discipline & Fines",
                        subtitle = "${activeFines.size} Fines Listed",
                        icon = Icons.Default.Gavel,
                        containerColor = CardPinkBg,
                        iconTint = IconPinkTint,
                        onClick = { showFinesDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Broadcaster",
                        subtitle = "Post Hostel Notice",
                        icon = Icons.Default.Campaign,
                        containerColor = CardPurpleBg,
                        iconTint = IconPurpleTint,
                        onClick = { showNoticeDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Pending Gate Passes Section
            if (pendingPasses.isNotEmpty()) {
                Text(
                    text = "Pending Gate Passes",
                    color = Color(0xFF0F172A),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    pendingPasses.forEach { pass ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(pass.studentName, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                                    Text("Reason: ${pass.reason} • ${pass.outDate} to ${pass.inDate}", color = Color(0xFF64748B), fontSize = 12.sp)
                                }
                                Button(
                                    onClick = {
                                        val updated = pass.copy(wardenApproval = "Approved")
                                        HostelDataStore.saveGatePass(updated)
                                        Toast.makeText(context, "Pass Approved for ${pass.studentName}!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Approve", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
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
                    BottomNavItem("rooms", "Rooms", Icons.Default.MeetingRoom),
                    BottomNavItem("complaints", "Tickets", Icons.Default.SupportAgent),
                    BottomNavItem("notices", "Notices", Icons.Default.Campaign),
                    BottomNavItem("logout", "Logout", Icons.Default.ExitToApp)
                ),
                selectedId = selectedTab,
                onSelect = { id ->
                    selectedTab = id
                    when (id) {
                        "rooms" -> onAllocationsClick()
                        "complaints" -> onComplaintsClick()
                        "notices" -> showNoticeDialog = true
                        "logout" -> onSignOutClick()
                    }
                }
            )
        }
    }
}
