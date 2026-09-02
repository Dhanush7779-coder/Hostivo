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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityDashboardScreen(
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("home") }
    var searchQuery by remember { mutableStateOf("") }
    var showAiDialog by remember { mutableStateOf(false) }

    val officerName = HostelDataStore.securityOfficerName
    val approvedPasses = HostelDataStore.gatePassRequests.filter { it.wardenApproval == "Approved" }
    val lateLogs = HostelDataStore.gatePassRequests.filter { it.isLate }

    fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    if (showAiDialog) {
        AlertDialog(
            onDismissRequest = { showAiDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF6366F1))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Security AI Guard", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("Officer $officerName: ${approvedPasses.size} active gate passes authorized for checkout today. ${lateLogs.size} late returns flagged.")
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
                userName = officerName.split(" ").firstOrNull() ?: officerName,
                subtitle = "Main Campus Gate Guard",
                actionIcon = Icons.Default.Person,
                onActionClick = { /* Profile */ },
                onNotificationsClick = { /* Alerts */ }
            )

            // 2. Search Bar
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search student roll, gate pass ID..."
            )

            // 3. Hero Action Banner: Active Passes
            ModernHeroBanner(
                title = "Main Gate Checkpoint",
                subtitle = "${approvedPasses.size} Approved passes ready for verification",
                icon = Icons.Default.Security,
                onClick = { /* Gate operations */ }
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
                        title = "Out Movement",
                        subtitle = "Record Student Exit",
                        icon = Icons.Default.DirectionsWalk,
                        containerColor = CardBlueBg,
                        iconTint = IconBlueTint,
                        onClick = { /* Record out */ },
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "In Movement",
                        subtitle = "Record Student Return",
                        icon = Icons.Default.Login,
                        containerColor = CardGreenBg,
                        iconTint = IconGreenTint,
                        onClick = { /* Record in */ },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ModernQuickCard(
                        title = "Late Night Logs",
                        subtitle = "${lateLogs.size} Violations",
                        icon = Icons.Default.AccessTime,
                        containerColor = CardPinkBg,
                        iconTint = IconPinkTint,
                        onClick = { /* Late logs */ },
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Visitor Register",
                        subtitle = "Guest Check-In",
                        icon = Icons.Default.Badge,
                        containerColor = CardPurpleBg,
                        iconTint = IconPurpleTint,
                        onClick = { /* Visitor logs */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Live Approved Gate Passes list
            if (approvedPasses.isNotEmpty()) {
                Text(
                    text = "Authorized Passes",
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
                    approvedPasses.forEach { pass ->
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
                                    Text("Pass #${pass.id} • ${pass.reason}", color = Color(0xFF64748B), fontSize = 12.sp)
                                    Text("Out: ${pass.outTime.ifEmpty { "Not Left" }} | In: ${pass.inTime.ifEmpty { "Not Returned" }}", color = Color(0xFF6366F1), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    if (pass.checkoutTime == "--:--" || pass.checkoutTime.isEmpty()) {
                                        Button(
                                            onClick = {
                                                val updated = pass.copy(checkoutTime = getCurrentTimeString())
                                                HostelDataStore.saveGatePass(updated)
                                                Toast.makeText(context, "${pass.studentName} marked OUT", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Mark Out", color = Color.White, fontSize = 11.sp)
                                        }
                                    } else if (pass.checkinTime == "--:--" || pass.checkinTime.isEmpty()) {
                                        Button(
                                            onClick = {
                                                val updated = pass.copy(checkinTime = getCurrentTimeString())
                                                HostelDataStore.saveGatePass(updated)
                                                Toast.makeText(context, "${pass.studentName} marked IN", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Mark In", color = Color.White, fontSize = 11.sp)
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

        // Floating AI Assistant button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
        ) {
            ModernFloatingAssistant(
                name = "Gate AI",
                subtitle = "Security assistant • Check",
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
                    BottomNavItem("gate", "Gate", Icons.Default.VpnKey),
                    BottomNavItem("logs", "Logs", Icons.Default.History),
                    BottomNavItem("visitors", "Visitors", Icons.Default.Badge),
                    BottomNavItem("logout", "Logout", Icons.Default.ExitToApp)
                ),
                selectedId = selectedTab,
                onSelect = { id ->
                    selectedTab = id
                    if (id == "logout") {
                        onSignOutClick()
                    }
                }
            )
        }
    }
}
