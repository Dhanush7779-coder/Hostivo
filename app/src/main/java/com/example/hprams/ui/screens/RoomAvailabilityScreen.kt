package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PublishedWithChanges
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
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.RoomChangeRequest

data class RoomItem(
    val number: String,
    val block: String, // "Block A", "Block B", "Block C", "Block D"
    val type: String,
    val floor: String, // "1", "2", "3", "4"
    val isAvailable: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomAvailabilityScreen(
    onHomeClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSupportClick: () -> Unit,
    onApplyClick: (roomId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()

    // Get current student profile
    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentGender = currentStudent?.gender ?: "Female"
    val studentName = currentStudent?.name ?: "Alex Vance"
    val studentRoll = currentStudent?.roll ?: "231801380001"
    val currentRoomNum = currentStudent?.room ?: "402-B"

    // Gender-based wing allocation constraints (Boys = A&B, Girls = C&D)
    val availableBlocks = if (studentGender == "Male") {
        listOf("Block A", "Block B")
    } else {
        listOf("Block C", "Block D")
    }

    var selectedBlock by remember { mutableStateOf(availableBlocks.first()) }
    var selectedFloor by remember { mutableStateOf("All") }
    var showBlockDropdown by remember { mutableStateOf(false) }
    var showChangeDialog by remember { mutableStateOf(false) }
    var requestedRoom by remember { mutableStateOf("") }
    var paymentPreference by remember { mutableStateOf("Pay Later") } // "Pay Now" or "Pay Later" (Section 8)

    // Search active change requests for this student
    val activeRequest = HostelDataStore.roomChangeRequests.find { 
        it.studentRoll == studentRoll && it.status != "Approved" 
    }

    // Static master rooms data matching blocks A, B, C, D
    val roomsList = remember {
        listOf(
            RoomItem("101", "Block A", "5 Sharing Non-AC", "1", true),
            RoomItem("102", "Block A", "4 Sharing AC", "1", false),
            RoomItem("201", "Block A", "4 Sharing Non-AC", "2", true),
            RoomItem("202", "Block A", "3 Sharing AC", "2", true),
            
            RoomItem("103", "Block B", "5 Sharing Non-AC", "1", true),
            RoomItem("203", "Block B", "3 Sharing AC", "2", false),
            RoomItem("303", "Block B", "2 Sharing AC", "3", true),

            RoomItem("104", "Block C", "5 Sharing Non-AC", "1", true),
            RoomItem("105", "Block C", "4 Sharing AC", "1", true),
            RoomItem("204", "Block C", "4 Sharing Non-AC", "2", false),
            RoomItem("304", "Block C", "3 Sharing AC", "3", true),

            RoomItem("106", "Block D", "5 Sharing Non-AC", "1", true),
            RoomItem("206", "Block D", "4 Sharing AC", "2", true),
            RoomItem("406", "Block D", "2 Sharing AC", "4", true)
        )
    }

    // Filter rooms dynamically by Block AND Floor (Section 3)
    val filteredRooms = remember(selectedBlock, selectedFloor) {
        roomsList.filter { room ->
            val matchBlock = room.block == selectedBlock
            val matchFloor = if (selectedFloor == "All") true else room.floor == selectedFloor
            matchBlock && matchFloor
        }
    }

    val vacantRoomsForSelection = remember(selectedBlock) {
        roomsList.filter { it.block == selectedBlock && it.isAvailable }.map { it.number }
    }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "ROOM AVAILABILITY",
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
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Main Content Column
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Block & Floor Filters Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "WING:",
                                    color = subTextColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(60.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
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
                                        Text(selectedBlock, color = textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Icon(Icons.Default.ExpandMore, contentDescription = null, tint = subTextColor, modifier = Modifier.size(16.dp))
                                    }
                                    DropdownMenu(
                                        expanded = showBlockDropdown,
                                        onDismissRequest = { showBlockDropdown = false },
                                        modifier = Modifier.background(if (isDark) Color(0xFF101415) else Color(0xFFE3EAE9))
                                    ) {
                                        availableBlocks.forEach { block ->
                                            DropdownMenuItem(
                                                text = { Text(block, color = textColor) },
                                                onClick = {
                                                    selectedBlock = block
                                                    showBlockDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Floor selection row (Section 3)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "FLOOR:",
                                    color = subTextColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(60.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    listOf("All", "1", "2", "3", "4").forEach { floor ->
                                        val isActive = selectedFloor == floor
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isActive) {
                                                        if (isDark) Color(0xFF29FCF3).copy(alpha = 0.2f) else Color(0xFF006A66).copy(alpha = 0.2f)
                                                    } else {
                                                        if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)
                                                    },
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { selectedFloor = floor }
                                                .padding(vertical = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = floor,
                                                color = if (isActive) {
                                                    if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                } else subTextColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Grid list displaying filtered floor-wise rooms
                    if (filteredRooms.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No rooms found on Floor $selectedFloor in $selectedBlock.", color = subTextColor)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(filteredRooms, key = { "${it.block}-${it.number}" }) { room ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (isDark) Color.White.copy(alpha = 0.05f)
                                            else Color.White.copy(alpha = 0.8f)
                                        )
                                        .border(
                                            1.dp,
                                            if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                                            RoundedCornerShape(16.dp)
                                        )
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Room ${room.number}", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        if (room.isAvailable) Color(0xFF027E3D).copy(alpha = 0.15f) else Color(0xFF93000A).copy(alpha = 0.15f),
                                                        RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    if (room.isAvailable) "VACANT" else "FULL",
                                                    color = if (room.isAvailable) Color(0xFF76DB8F) else Color(0xFFFFB4AB),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 8.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                        Text(room.type, color = subTextColor, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                                        Text("Floor: ${room.floor}", color = subTextColor, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp)

                                        if (room.isAvailable) {
                                            Button(
                                                onClick = { onApplyClick(room.number) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                ),
                                                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(26.dp)
                                            ) {
                                                Text(
                                                    "Apply",
                                                    color = if (isDark) Color(0xFF003735) else Color.White,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.height(26.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Room Allocation & Change Panel (Moved to Bottom - Section 2)
                    GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.PublishedWithChanges, contentDescription = null, tint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), modifier = Modifier.size(18.dp))
                                Text("Room Allocation & Change", color = textColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }

                            if (activeRequest != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            when (activeRequest.status) {
                                                "Rejected" -> Color(0xFF93000A).copy(alpha = 0.15f)
                                                else -> Color(0xFFB89400).copy(alpha = 0.15f)
                                            },
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(
                                            "STATUS: ${activeRequest.status.uppercase()}",
                                            color = when (activeRequest.status) {
                                                "Rejected" -> Color(0xFFFFB4AB)
                                                else -> Color(0xFFFFD43F)
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp
                                        )
                                        Text("Requested Room: Room ${activeRequest.requestedRoom} (${activeRequest.currentRoom} -> ${activeRequest.requestedRoom})", color = textColor, fontSize = 12.sp)
                                        if (activeRequest.status == "Rejected") {
                                            Text("Reason: ${activeRequest.rejectReason}", color = Color(0xFFFFB4AB), fontSize = 11.sp)
                                        }
                                    }
                                }

                                if (activeRequest.status == "Rejected") {
                                    GlassButton(
                                        text = "Request Again",
                                        onClick = {
                                            HostelDataStore.roomChangeRequests.remove(activeRequest)
                                        }
                                    )
                                }
                            } else {
                                Text(
                                    "Request to change your current room assignment to a different block wing or room type.",
                                    color = subTextColor,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp
                                )
                                GlassButton(
                                    text = "Submit Change Request",
                                    onClick = { showChangeDialog = true }
                                )
                            }
                        }
                    }
                }

                StudentBottomBar(
                    activeTab = "rooms",
                    onHomeClick = onHomeClick,
                    onRoomsClick = {},
                    onFinanceClick = onFinanceClick,
                    onProfileClick = onProfileClick,
                    onSupportClick = onSupportClick,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }

    // Room Selection Dialog
    if (showChangeDialog) {
        AlertDialog(
            onDismissRequest = { showChangeDialog = false },
            title = {
                Text(
                    "Select Room for Change",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select a vacant room in $selectedBlock:", color = subTextColor, fontSize = 14.sp)
                    if (vacantRoomsForSelection.isEmpty()) {
                        Text("No vacant rooms in this wing.", color = Color(0xFFFFB4AB), fontWeight = FontWeight.Bold)
                    } else {
                        vacantRoomsForSelection.forEach { roomNum ->
                            val isSelected = requestedRoom == roomNum
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) {
                                            if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                        } else {
                                            if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) {
                                            if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                        } else Color.Transparent,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { requestedRoom = roomNum }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text("Room $roomNum (Vacant)", color = textColor, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Display fee difference calculations and Pay Now / Pay Later options (Section 8)
                    if (requestedRoom.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = subTextColor.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Shifting Non-AC (current 101/104 Non-AC) to AC room
                        val isShiftingToAC = roomsList.find { it.number == requestedRoom }?.type?.contains("AC") == true
                        val diffAmount = if (isShiftingToAC) "Rs. 20,000" else "Rs. 0"

                        Text("Shifting Fee Difference: $diffAmount", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        if (isShiftingToAC) {
                            Text("Non-AC Sharing (Rs. 80,000/yr) -> AC Sharing (Rs. 1,00,000/yr)", color = subTextColor, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Payment Mode for Shifting Difference:", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Pay Now", "Pay Later").forEach { pref ->
                                    val isSelected = paymentPreference == pref
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isSelected) {
                                                    if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
                                                } else {
                                                    if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) {
                                                    if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                } else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { paymentPreference = pref }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(pref, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (requestedRoom.isNotEmpty()) {
                    Button(
                        onClick = {
                            val isShiftingToAC = roomsList.find { it.number == requestedRoom }?.type?.contains("AC") == true
                            val feeDiff = if (isShiftingToAC) "Rs. 20,000" else "Rs. 0"

                            // Handle pay now / pay later (Section 8)
                            if (isShiftingToAC) {
                                if (paymentPreference == "Pay Now") {
                                    Toast.makeText(context, "Paid Shifting Fee of $feeDiff successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    // Add as unpaid charge item
                                    HostelDataStore.fines.add(
                                        com.example.hprams.data.FineItem(
                                            id = "CHG-${(100..999).random()}",
                                            studentRoll = studentRoll,
                                            amount = feeDiff,
                                            reason = "Shifting Difference Fee (Room $currentRoomNum -> $requestedRoom)",
                                            status = "Unpaid"
                                        )
                                    )
                                    Toast.makeText(context, "Rs. 20,000 added as outstanding charge to your Finance dashboard.", Toast.LENGTH_LONG).show()
                                }
                            }

                            HostelDataStore.roomChangeRequests.add(
                                RoomChangeRequest(
                                    id = "REQ-${(1000..9999).random()}",
                                    studentRoll = studentRoll,
                                    studentName = studentName,
                                    currentRoom = currentRoomNum,
                                    requestedRoom = requestedRoom,
                                    gender = studentGender,
                                    status = "Pending"
                                )
                            )
                            Toast.makeText(context, "Room change request submitted for Room $requestedRoom!", Toast.LENGTH_LONG).show()
                            showChangeDialog = false
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
                TextButton(onClick = { showChangeDialog = false }) {
                    Text("Cancel", color = subTextColor)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
