package com.example.hprams.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import com.example.hprams.data.HostelDataStore
import com.example.hprams.ui.components.*

@Composable
fun StudentDashboardScreen(
    onSignOutClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onRoomsClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAnnouncementsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { (context as? Activity)?.finish() }

    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentName = currentStudent?.name ?: "C.Venkat"
    val studentRoll = currentStudent?.roll ?: "231801380007"
    val studentBlock = currentStudent?.block ?: "Block A"
    val studentRoom = currentStudent?.room ?: "101"

    var selectedTab by remember { mutableStateOf("home") }
    var searchQuery by remember { mutableStateOf("") }
    val isSearching = searchQuery.isNotBlank()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ModernBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 85.dp) // Space for bottom nav bar
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Top Greeting Header
            ModernTopGreeting(
                userName = studentName.split(" ").firstOrNull() ?: studentName,
                subtitle = "Ready for your campus day?",
                actionIcon = Icons.Default.Person,
                onActionClick = { onProfileClick() },
                onNotificationsClick = { onNotificationsClick() }
            )

            // 2. Search Bar
            ModernSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search room, gate pass, complaints, fees..."
            )

            // Interactive Search Results
            if (isSearching) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Quick Navigation", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                    
                    val shortcuts = listOf(
                        Triple("Room Details & Roommates", "View $studentBlock Room $studentRoom", onRoomsClick),
                        Triple("Fee Portal & Receipts", "Check semester fees and dues", onFinanceClick),
                        Triple("Gate Pass & Complaints", "Request gate pass or log ticket", onSupportClick),
                        Triple("Community & Notices", "View hostel announcements", onAnnouncementsClick),
                        Triple("Student Profile", "Edit contact & profile photo", onProfileClick)
                    ).filter { it.first.contains(searchQuery, true) || it.second.contains(searchQuery, true) }

                    if (shortcuts.isEmpty()) {
                        Text("No matching actions found. Try searching for 'room', 'fee', 'pass', 'notice'...", fontSize = 12.sp, color = Color(0xFF94A3B8))
                    } else {
                        shortcuts.forEach { (title, sub, action) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { action() },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
                                        Text(sub, fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
                                }
                            }
                        }
                    }
                }
            }

            // 3. Hero Action Banner
            ModernHeroBanner(
                title = "Hostel Room & Amenities",
                subtitle = "Assigned: $studentBlock • Room $studentRoom",
                icon = Icons.Default.Bed,
                onClick = { onRoomsClick() }
            )

            // 4. Quick Access Section Header
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
                // Row 1: Announcements / Community & Gate Pass
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ModernQuickCard(
                        title = "Community",
                        subtitle = "Notices & Feeds",
                        icon = Icons.Default.Groups,
                        containerColor = CardGreenBg,
                        iconTint = IconGreenTint,
                        onClick = onAnnouncementsClick,
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Gate Pass",
                        subtitle = "Request & History",
                        icon = Icons.Default.ConfirmationNumber,
                        containerColor = CardBlueBg,
                        iconTint = IconBlueTint,
                        onClick = onSupportClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Row 2: Fee Payment & Support/Complaints
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ModernQuickCard(
                        title = "Fee Portal",
                        subtitle = "Receipts & Dues",
                        icon = Icons.Default.AccountBalanceWallet,
                        containerColor = CardPinkBg,
                        iconTint = IconPinkTint,
                        onClick = onFinanceClick,
                        modifier = Modifier.weight(1f)
                    )
                    ModernQuickCard(
                        title = "Support",
                        subtitle = "Raise Complaint",
                        icon = Icons.Default.SupportAgent,
                        containerColor = CardPurpleBg,
                        iconTint = IconPurpleTint,
                        onClick = onSupportClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Modern Bottom Nav Bar (Profile removed, accessible via top header)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            ModernBottomNavBar(
                items = listOf(
                    BottomNavItem("home", "Home", Icons.Default.Home),
                    BottomNavItem("rooms", "Rooms", Icons.Default.Bed),
                    BottomNavItem("finance", "Finance", Icons.Default.AccountBalanceWallet),
                    BottomNavItem("community", "Community", Icons.Default.Groups)
                ),
                selectedId = selectedTab,
                onSelect = { id ->
                    selectedTab = id
                    when (id) {
                        "rooms" -> onRoomsClick()
                        "finance" -> onFinanceClick()
                        "community" -> onAnnouncementsClick()
                    }
                }
            )
        }
    }
}
