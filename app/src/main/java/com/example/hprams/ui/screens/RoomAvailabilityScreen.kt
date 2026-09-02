package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.RoomChangeRequest
import com.example.hprams.data.RoomRules
import com.example.hprams.ui.components.*

data class AmenityItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val containerColor: Color,
    val iconTint: Color
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

    // Get current student profile
    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentGender = currentStudent?.gender ?: "Male"
    val studentName = currentStudent?.name ?: "C.Venkat"
    val studentRoll = currentStudent?.roll ?: "231801380007"
    val currentRoomNum = currentStudent?.room ?: "102"
    val currentBlock = currentStudent?.block ?: (if (studentGender == "Male") "Block B" else "Block C")

    val isAc = RoomRules.isAc(currentRoomNum)
    val capacity = RoomRules.getRoomCapacity(currentRoomNum)
    val floor = RoomRules.getFloor(currentRoomNum)
    val roomType = RoomRules.getRoomType(currentRoomNum)
    val currentOccupantsCount = RoomRules.getCurrentOccupantsCount(currentBlock, currentRoomNum).coerceAtLeast(1)

    // Roommates list from database or realistic co-residents in the same room
    val dbRoommates = HostelDataStore.students.filter {
        it.block.equals(currentBlock, ignoreCase = true) &&
        it.room.equals(currentRoomNum, ignoreCase = true) &&
        it.roll != studentRoll
    }

    val activeRequest = HostelDataStore.roomChangeRequests.find { 
        it.studentRoll == studentRoll && it.status == "Pending" 
    }

    var showChangeDialog by remember { mutableStateOf(false) }
    var requestedRoom by remember { mutableStateOf("") }
    var selectedTargetBlock by remember { mutableStateOf(currentBlock) }

    val studentBlocks = if (studentGender == "Male") listOf("Block A", "Block B") else listOf("Block C", "Block D")

    val amenities = listOf(
        AmenityItem(
            title = if (isAc) "Air Conditioning" else "Natural Cross Ventilation",
            description = if (isAc) "Climate Controlled 24/7" else "High-Speed Ceiling Fans & Windows",
            icon = if (isAc) Icons.Default.AcUnit else Icons.Default.Air,
            containerColor = CardBlueBg,
            iconTint = IconBlueTint
        ),
        AmenityItem(
            title = "High-Speed Wi-Fi",
            description = "1 Gbps Campus Mesh Access Point",
            icon = Icons.Default.Wifi,
            containerColor = CardGreenBg,
            iconTint = IconGreenTint
        ),
        AmenityItem(
            title = "Study Station",
            description = "Ergonomic Desk, Chair & Bookrack",
            icon = Icons.Default.School,
            containerColor = CardPinkBg,
            iconTint = IconPinkTint
        ),
        AmenityItem(
            title = "Personal Wardrobe",
            description = "Steel Almirah with Digital Locker",
            icon = Icons.Default.Checkroom,
            containerColor = CardPurpleBg,
            iconTint = IconPurpleTint
        ),
        AmenityItem(
            title = "Attached Washroom",
            description = "Hot Water Geyser & Daily Sanitation",
            icon = Icons.Default.Shower,
            containerColor = CardBlueBg,
            iconTint = IconBlueTint
        ),
        AmenityItem(
            title = "Power Backup",
            description = "24x7 Generator Backup for Lights & Plugs",
            icon = Icons.Default.Bolt,
            containerColor = CardPinkBg,
            iconTint = IconPinkTint
        ),
        AmenityItem(
            title = "Housekeeping",
            description = "Daily Room Cleaning & Trash Disposal",
            icon = Icons.Default.CleaningServices,
            containerColor = CardGreenBg,
            iconTint = IconGreenTint
        ),
        AmenityItem(
            title = "Campus Balcony View",
            description = "Private Balcony with Campus Garden View",
            icon = Icons.Default.Balcony,
            containerColor = CardPurpleBg,
            iconTint = IconPurpleTint
        )
    )

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
            Spacer(modifier = Modifier.height(8.dp))

            // 1. Top SubPage Header
            ModernSubPageHeader(
                title = "Hostel Room & Amenities",
                subtitle = "Assigned: $currentBlock • Room $currentRoomNum",
                onBackClick = onHomeClick
            )

            // 2. Room Information Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF6366F1),
                                Color(0xFF7C3AED),
                                Color(0xFF8B5CF6)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bed,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Room $currentRoomNum",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$currentBlock • Floor $floor",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.25f))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "ALLOCATED",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.2f))

                    // Key Room Specs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("ROOM TYPE", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(roomType, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text("SHARING", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$capacity Persons", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text("OCCUPANCY", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("$currentOccupantsCount / $capacity Occupants", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // 3. Roommate Information Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Roommates",
                        color = Color(0xFF0F172A),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Room $currentRoomNum",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Current student's own card
                ModernSectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEEF2FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = studentName.firstOrNull()?.toString() ?: "S",
                                    color = Color(0xFF6366F1),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$studentName (You)",
                                        color = Color(0xFF0F172A),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFECFDF5))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("Bed 1", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    text = "Roll: $studentRoll • Primary Resident",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // DB Roommates or Mock Roommates if room has more capacity
                if (dbRoommates.isNotEmpty()) {
                    dbRoommates.forEachIndexed { idx, rm ->
                        ModernSectionCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFAF5FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = rm.name.firstOrNull()?.toString() ?: "R",
                                            color = Color(0xFF8B5CF6),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = rm.name,
                                                color = Color(0xFF0F172A),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFEEF2FF))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("Bed ${idx + 2}", color = Color(0xFF6366F1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = "Roll: ${rm.roll} • ${rm.phone.ifEmpty { "Contact Available" }}",
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Fallback friendly placeholder roommate preview
                    val mockRoommates = listOf(
                        Triple("Rahul Sharma", "231801380012", "Bed 2"),
                        Triple("Ankit Patel", "231801380045", "Bed 3")
                    ).take((capacity - 1).coerceAtMost(2))

                    mockRoommates.forEach { (name, roll, bed) ->
                        ModernSectionCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFAF5FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = name.first().toString(),
                                            color = Color(0xFF8B5CF6),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = name,
                                                color = Color(0xFF0F172A),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFEEF2FF))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(bed, color = Color(0xFF6366F1), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = "Roll: $roll • Computer Science",
                                            color = Color(0xFF64748B),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Room Features & Amenities Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Room Features & Amenities",
                    color = Color(0xFF0F172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                // 2-column grid of amenities
                amenities.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(item.containerColor)
                                    .padding(14.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = null,
                                            tint = item.iconTint,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = item.title,
                                            color = Color(0xFF0F172A),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.description,
                                            color = Color(0xFF64748B),
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Room Change Option
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                if (activeRequest != null) {
                    ModernSectionCard(backgroundColor = Color(0xFFFEF3C7)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Room Change Request Pending",
                                    color = Color(0xFF92400E),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Requested Room: ${activeRequest.requestedRoom} • Warden Reviewing",
                                    color = Color(0xFFB45309),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else {
                    OutlinedButton(
                        onClick = { showChangeDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF0F172A)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Apply for Room Change", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
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
                    BottomNavItem("rooms", "Rooms", Icons.Default.Bed),
                    BottomNavItem("finance", "Finance", Icons.Default.AccountBalanceWallet),
                    BottomNavItem("community", "Community", Icons.Default.Groups)
                ),
                selectedId = "rooms",
                onSelect = { id ->
                    when (id) {
                        "home" -> onHomeClick()
                        "finance" -> onFinanceClick()
                        "community" -> onSupportClick()
                    }
                }
            )
        }
    }

    // Modern Room Change Dialog
    if (showChangeDialog) {
        AlertDialog(
            onDismissRequest = { showChangeDialog = false },
            title = {
                Text("Apply for Room Change", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Select a Room to shift in $studentGender Hostel:", color = Color(0xFF64748B), fontSize = 12.sp)

                    // Target block selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        studentBlocks.forEach { b ->
                            val isSel = selectedTargetBlock == b
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) Color(0xFF0F172A) else Color(0xFFF1F5F9))
                                    .clickable { selectedTargetBlock = b }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = b,
                                    color = if (isSel) Color.White else Color(0xFF334155),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    // Available rooms list
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            (101..120).forEach { rNum ->
                                val rStr = rNum.toString()
                                val rType = RoomRules.getRoomType(rStr)
                                val isAvail = RoomRules.isRoomAvailable(selectedTargetBlock, rStr)
                                val isSelected = requestedRoom == rStr

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) Color(0xFF6366F1).copy(alpha = 0.15f)
                                            else Color.White
                                        )
                                        .clickable { requestedRoom = rStr }
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Room $rStr ($selectedTargetBlock)", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(rType, color = Color(0xFF64748B), fontSize = 11.sp)
                                        }
                                        Text(
                                            text = if (isAvail) "Available" else "Full",
                                            color = if (isAvail) Color(0xFF16A34A) else Color(0xFFDC2626),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
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
                            val newReq = RoomChangeRequest(
                                id = "REQ-${(1000..9999).random()}",
                                studentRoll = studentRoll,
                                studentName = studentName,
                                currentRoom = currentRoomNum,
                                requestedRoom = requestedRoom,
                                gender = studentGender,
                                status = "Pending"
                            )
                            HostelDataStore.saveRoomChange(newReq)
                            Toast.makeText(context, "Room change request submitted for Room $requestedRoom!", Toast.LENGTH_LONG).show()
                            showChangeDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                    ) {
                        Text("Submit Request", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
