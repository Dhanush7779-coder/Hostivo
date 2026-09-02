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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var selectedTab by remember { mutableStateOf("home") }
    var searchQuery by remember { mutableStateOf("") }
    var showAiDialog by remember { mutableStateOf(false) }

    val wardenName = HostelDataStore.chiefWardenName
    val wardenScope = HostelDataStore.currentWardenScope
    val targetGender = if (wardenScope == "Girls") "Female" else "Male"

    val pendingPasses = HostelDataStore.gatePassRequests.filter { it.wardenApproval == "Pending" && it.gender == targetGender }
    val pendingRooms = HostelDataStore.roomChangeRequests.filter { it.status == "Pending" && it.gender == targetGender }
    val pendingComplaints = HostelDataStore.complaints.filter { it.status == "Pending" && it.gender == targetGender }

    if (showAiDialog) {
        AlertDialog(
            onDismissRequest = { showAiDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF6366F1))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Warden AI Assistant", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("Warden $wardenName: You have ${pendingPasses.size} pending gate passes and ${pendingRooms.size} pending room changes in $wardenScope Hostel.")
            },
            confirmButton = {
                Button(
                    onClick = { showAiDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) {
                    Text("Dismiss", color = Color.White)
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
                onActionClick = { /* Profile */ },
                onNotificationsClick = { onComplaintsClick() }
            )

            // 2. Search Bar
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search student pass, room change, tickets..."
            )

            // 3. Hero Action Banner: Gate Passes
            ModernHeroBanner(
                title = "Gate Passes (${pendingPasses.size} Pending)",
                subtitle = if (pendingPasses.isEmpty()) "No active outpass requests" else "Tap to review and approve gate passes",
                icon = Icons.Default.VpnKey,
                onClick = { /* View gate passes */ }
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
                        subtitle = "Issue Fine / Penalty",
                        icon = Icons.Default.Gavel,
                        containerColor = CardPinkBg,
                        iconTint = IconPinkTint,
                        onClick = { /* Fines dialog */ },
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Broadcaster",
                        subtitle = "Post Hostel Notice",
                        icon = Icons.Default.Campaign,
                        containerColor = CardPurpleBg,
                        iconTint = IconPurpleTint,
                        onClick = { /* Notices */ },
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

        // Floating AI Assistant button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
        ) {
            ModernFloatingAssistant(
                name = "Warden AI",
                subtitle = "Hostel supervisor • Assist",
                onClick = { showAiDialog = true }
            )
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
                        "logout" -> onSignOutClick()
                    }
                }
            )
        }
    }
}
