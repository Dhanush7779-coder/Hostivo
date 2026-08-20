package com.example.hprams.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hprams.ui.components.*
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.theme.AccentColor
import com.example.hprams.R
import com.example.hprams.data.HostelDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()
    
    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentName = currentStudent?.name ?: "Alex Vance"
    val studentRoll = currentStudent?.roll ?: "231801380001"
    val studentBlock = currentStudent?.block ?: "Block C"
    val studentRoom = currentStudent?.room ?: "104"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var showEmergencyDialog by remember { mutableStateOf(false) }
    var selectedAnnouncementTitle by remember { mutableStateOf<String?>(null) }
    var selectedAnnouncementBody by remember { mutableStateOf<String?>(null) }

    // Announcement Details Pop-up
    if (selectedAnnouncementTitle != null && selectedAnnouncementBody != null) {
        AlertDialog(
            onDismissRequest = {
                selectedAnnouncementTitle = null
                selectedAnnouncementBody = null
            },
            title = {
                Text(
                    text = selectedAnnouncementTitle!!,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },
            text = {
                Text(
                    text = selectedAnnouncementBody!!,
                    color = subTextColor
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedAnnouncementTitle = null
                        selectedAnnouncementBody = null
                    }
                ) {
                    Text("Close", color = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66))
                }
            },
            containerColor = if (isDark) Color(0xFF181F2A) else Color(0xFFF9FBFB),
            textContentColor = textColor,
            titleContentColor = textColor
        )
    }

    // Emergency Info Pop-up
    if (showEmergencyDialog) {
        AlertDialog(
            onDismissRequest = { showEmergencyDialog = false },
            title = {
                Text(
                    "Emergency Information",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Parent Name:", color = subTextColor)
                        Text("Ramesh Kumar Vance", color = textColor, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Parent Phone:", color = subTextColor)
                        Text("+91 9988776655", color = textColor, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Blood Group:", color = subTextColor)
                        Text("O+", color = textColor, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Health Issues:", color = subTextColor)
                        Text("Dust, Penicillin", color = textColor, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showEmergencyDialog = false }) {
                    Text("Close", color = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66))
                }
            },
            containerColor = if (isDark) Color(0xFF181F2A) else Color(0xFFF9FBFB),
            textContentColor = textColor,
            titleContentColor = textColor
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = if (isDark) Color(0xFF141C27) else Color(0xFFF3F6F6),
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                // Drawer Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66), CircleShape)
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=120&q=80",
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = studentName,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = studentRoll,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66),
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Upload profile image button
                    TextButton(
                        onClick = {
                            // Mock photo upload trigger action
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Upload Photo", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                Divider(color = if (isDark) Color(0xFF2C394E) else Color(0xFFB5C5C3))

                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text("Profile", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onProfileClick()
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedIconColor = subTextColor,
                        unselectedTextColor = textColor
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Emergency Info", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        showEmergencyDialog = true
                    },
                    icon = { Icon(Icons.Default.ContactPhone, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedIconColor = subTextColor,
                        unselectedTextColor = textColor
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Logout", fontWeight = FontWeight.SemiBold) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onSignOutClick()
                    },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent,
                        unselectedIconColor = subTextColor,
                        unselectedTextColor = textColor
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        GlassBackground(modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            ) {
                // Custom TopAppBar - arranged neatly to avoid merging
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu Drawer",
                                tint = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66)
                            )
                        }
                        Text(
                            text = "Hostivo",
                            color = AccentColor,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                        ) {
                            IconButton(onClick = {
                                com.example.hprams.theme.ThemeManager.themeSetting = if (isDark) "Light" else "Dark"
                            }) {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.WbSunny else Icons.Default.NightsStay,
                                    contentDescription = "Toggle Theme Preference",
                                    tint = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f))
                        ) {
                            IconButton(onClick = onNotificationsClick) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66)
                                )
                            }
                        }
                    }
                }

                // Main Content Area
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // Welcome Section
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            Text(
                                text = "Hello, $studentName",
                                color = textColor,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Here is your hostel dashboard summary.",
                                color = subTextColor,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Room Info Card (Neat clean skeuomorphic, only showing room and block)
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ROOM INFORMATION",
                                        color = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66),
                                        style = MaterialTheme.typography.labelMedium,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                Color(0xFF76DB8F).copy(alpha = 0.15f),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "ALLOCATED",
                                            color = Color(0xFF76DB8F),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Room Number", color = subTextColor, style = MaterialTheme.typography.bodySmall)
                                        Text(studentRoom, color = textColor, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Hostel Block", color = subTextColor, style = MaterialTheme.typography.bodySmall)
                                        Text(studentBlock, color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }

                    // Announcements Title
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Latest Announcements",
                                color = textColor,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "View All",
                                color = if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.clickable { onAnnouncementsClick() }
                            )
                        }
                    }

                    // Announcements List directly inside Homepage (Show subject only, click to open details popup)
                    items(2) { index ->
                        val isEmergency = index == 0
                        val announcementSubject = if (isEmergency) "Water supply maintenance block A" else "Hostel rules & curfew timing strict check"
                        val announcementBody = if (isEmergency) {
                            "Water supply in Block A will be disconnected between 2 PM to 5 PM today for routine pipe repairs."
                        } else {
                            "Curfew is strictly enforced at 10 PM. All students must present their Digital ID pass at the main gate."
                        }

                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedAnnouncementTitle = announcementSubject
                                    selectedAnnouncementBody = announcementBody
                                }
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
                                            imageVector = Icons.Default.Campaign,
                                            contentDescription = null,
                                            tint = if (isEmergency) Color(0xFFFFB4AB) else if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66)
                                        )
                                        Text(
                                            if (isEmergency) "EMERGENCY BROADCAST" else "GENERAL NOTICE",
                                            color = if (isEmergency) Color(0xFFFFB4AB) else if (isDark) Color(0xFF00DDD6) else Color(0xFF006A66),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Text("14th Aug 2026", color = subTextColor, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    text = announcementSubject,
                                    color = textColor,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Bottom Navigation Bar - profile removed from the bottom bar options
                StudentBottomBar(
                    activeTab = "home",
                    onHomeClick = {},
                    onRoomsClick = onRoomsClick,
                    onFinanceClick = onFinanceClick,
                    onProfileClick = {}, // profile bottom click does nothing
                    onSupportClick = onSupportClick,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}
