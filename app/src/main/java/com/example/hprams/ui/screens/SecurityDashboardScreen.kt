package com.example.hprams.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
    BackHandler { (context as? Activity)?.finish() }

    var selectedTab by remember { mutableStateOf("home") }
    var searchQuery by remember { mutableStateOf("") }

    val officerName = HostelDataStore.securityOfficerName
    val approvedPasses = HostelDataStore.gatePassRequests.filter { it.wardenApproval == "Approved" }
    val lateLogs = HostelDataStore.gatePassRequests.filter { it.isLate }

    fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    val isSearching = searchQuery.isNotBlank()
    val displayedPasses = if (!isSearching) approvedPasses else {
        approvedPasses.filter {
            it.studentName.contains(searchQuery, true) ||
            it.studentRoll.contains(searchQuery, true) ||
            it.reason.contains(searchQuery, true)
        }
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
                userName = officerName,
                subtitle = "Main Security Post • Checkpoint",
                actionIcon = Icons.Default.Security,
                onActionClick = { /* Profile */ },
                onNotificationsClick = { /* Alerts */ }
            )

            // 2. Search Bar
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search student roll, name, pass ID..."
            )

            // 3. Hero Action Banner: Security Scanner
            ModernHeroBanner(
                title = "QR Gate Scanner",
                subtitle = "${approvedPasses.size} approved passes ready for verification",
                icon = Icons.Default.QrCodeScanner,
                onClick = { Toast.makeText(context, "Ready to scan gate pass QR code", Toast.LENGTH_SHORT).show() }
            )

            // 4. Quick Access Header
            Text(
                text = "Shift Overview",
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
                        title = "Out Students",
                        subtitle = "${approvedPasses.count { it.checkoutTime.isNotEmpty() && it.checkinTime.isEmpty() }} Outside",
                        icon = Icons.Default.DirectionsWalk,
                        containerColor = CardBlueBg,
                        iconTint = IconBlueTint,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "In Campus",
                        subtitle = "${approvedPasses.count { it.checkinTime.isNotEmpty() }} Returned",
                        icon = Icons.Default.Login,
                        containerColor = CardGreenBg,
                        iconTint = IconGreenTint,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ModernQuickCard(
                        title = "Curfew Logs",
                        subtitle = "${lateLogs.size} Curfew Violations",
                        icon = Icons.Default.HistoryToggleOff,
                        containerColor = CardPinkBg,
                        iconTint = IconPinkTint,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Visitors",
                        subtitle = "Gate Entry Registry",
                        icon = Icons.Default.Badge,
                        containerColor = CardPurpleBg,
                        iconTint = IconPurpleTint,
                        onClick = { },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Live Check-in / Check-out Registry Section
            Text(
                text = "Active Gate Approvals (${displayedPasses.size})",
                color = Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            if (displayedPasses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No gate passes found.", color = Color(0xFF94A3B8), fontSize = 13.sp)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    displayedPasses.forEach { pass ->
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
                                    Text("Roll: ${pass.studentRoll} • ${pass.reason}", color = Color(0xFF64748B), fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text("OUT: ${pass.checkoutTime.ifEmpty { "--:--" }}", fontSize = 11.sp, color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
                                        Text("IN: ${pass.checkinTime.ifEmpty { "--:--" }}", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (pass.checkoutTime == "--:--" || pass.checkoutTime.isEmpty()) {
                                        Button(
                                            onClick = {
                                                val updated = pass.copy(checkoutTime = getCurrentTimeString())
                                                HostelDataStore.saveGatePass(updated)
                                                Toast.makeText(context, "${pass.studentName} marked OUT", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)),
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
