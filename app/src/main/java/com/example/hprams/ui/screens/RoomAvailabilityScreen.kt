package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
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
import com.example.hprams.theme.AccentColor
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.RoomChangeRequest

data class RoomItem(
    val number: String,
    val block: String,
    val type: String,
    val floor: String,
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
    val currentRoomNum = currentStudent?.room ?: "101"
    val currentBlock = currentStudent?.block ?: (if (studentGender == "Male") "Block A" else "Block C")

    // Gender-based wing allocation constraints
    val studentBlocks = if (studentGender == "Male") {
        listOf("Block A", "Block B")
    } else {
        listOf("Block C", "Block D")
    }

    var selectedBlock by remember { mutableStateOf(studentBlocks.first()) }
    var showChangeDialog by remember { mutableStateOf(false) }
    var requestedRoom by remember { mutableStateOf("") }
    var paymentPreference by remember { mutableStateOf("Pay Later") }
    var refundOption by remember { mutableStateOf("Back to Source Account") }
    var refundDetails by remember { mutableStateOf("") }

    // Search active change requests for this student
    val activeRequest = HostelDataStore.roomChangeRequests.find { 
        it.studentRoll == studentRoll && it.status == "Pending" 
    }

    // Dynamic master rooms data matching blocks A, B, C, D using RoomRules
    val roomsList = remember(HostelDataStore.students.size) {
        val list = mutableListOf<RoomItem>()
        studentBlocks.forEach { block ->
            (101..120).forEach { rNum ->
                val roomNumberStr = rNum.toString()
                val rType = com.example.hprams.data.RoomRules.getRoomType(roomNumberStr)
                val rFloor = com.example.hprams.data.RoomRules.getFloor(roomNumberStr)
                val rAvail = com.example.hprams.data.RoomRules.isRoomAvailable(block, roomNumberStr)
                list.add(RoomItem(roomNumberStr, block, rType, rFloor, rAvail))
            }
        }
        list
    }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "ROOM CHANGE CONTROL",
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
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Current allocation info Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                "Your Current Room Allocation",
                                color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text("Current Wing/Block: $currentBlock", color = textColor, fontSize = 13.sp)
                            Text("Current Room Number: $currentRoomNum", color = textColor, fontSize = 13.sp)
                            
                            val currentIsAc = com.example.hprams.data.RoomRules.isAc(currentRoomNum)
                            Text("Room Type: ${if (currentIsAc) "AC Room" else "Non-AC Room"}", color = subTextColor, fontSize = 12.sp)

                            Divider(color = Color.White.copy(alpha = 0.1f))

                            if (activeRequest != null) {
                                Text("Pending Room Change Request:", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Requested Room: ${activeRequest.requestedRoom} (${selectedBlock})", color = textColor, fontSize = 12.sp)
                                Text("Status: ${activeRequest.status}", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            } else {
                                Button(
                                    onClick = {
                                        requestedRoom = ""
                                        showChangeDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)),
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                ) {
                                    Text("Apply for Room Change", color = if (isDark) Color(0xFF003735) else Color.White)
                                }
                            }
                        }
                    }

                    // Room Details Map CheatSheet
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Hostel Room Map & Rules Details", color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("• Floor 1: Rooms 101-105 (4-Sharing, Non-AC) | 106-107 (4-Sharing, AC) | 108-110 (6-Sharing, Non-AC)", color = subTextColor, fontSize = 11.sp)
                            Text("• Floor 2: Rooms 111-114 (3-Sharing, Non-AC) | 115-117 (3-Sharing, AC) | 118-120 (6-Sharing, Non-AC)", color = subTextColor, fontSize = 11.sp)
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

    // Room Selection & Shifting Request Dialog
    if (showChangeDialog) {
        AlertDialog(
            onDismissRequest = { showChangeDialog = false },
            title = {
                Text(
                    "Apply for Room Change",
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Select a Room to occupy from blocks in your scope:", color = subTextColor, fontSize = 13.sp)

                    // Scrollable List of All Rooms & Occupancy
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            roomsList.forEach { room ->
                                val currentCount = com.example.hprams.data.RoomRules.getCurrentOccupantsCount(room.block, room.number)
                                val maxCap = com.example.hprams.data.RoomRules.getRoomCapacity(room.number)
                                val isSelected = requestedRoom == room.number && selectedBlock == room.block

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isSelected) AccentColor.copy(alpha = 0.25f)
                                            else Color.White.copy(alpha = 0.05f)
                                        )
                                        .clickable {
                                            requestedRoom = room.number
                                            selectedBlock = room.block
                                        }
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Room ${room.number} (${room.block})", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(room.type, color = subTextColor, fontSize = 11.sp)
                                        }
                                        Text(
                                            text = "$currentCount / $maxCap occupied",
                                            color = if (currentCount < maxCap) Color(0xFF81C784) else Color(0xFFE57373),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Dynamically calculate and display Shifting Fees
                    if (requestedRoom.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Divider(color = subTextColor.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(4.dp))

                        val currentIsAc = com.example.hprams.data.RoomRules.isAc(currentRoomNum)
                        val currentPrice = if (currentIsAc) 100000 else 80000

                        val requestedIsAc = com.example.hprams.data.RoomRules.isAc(requestedRoom)
                        val requestedPrice = if (requestedIsAc) 100000 else 80000

                        val priceDiff = requestedPrice - currentPrice

                        if (priceDiff == 0) {
                            Text("Shifting Fee: Rs. 0 (Same Room Type)", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        } else if (priceDiff > 0) {
                            // Charge is higher
                            Text("Shifting Fee Due: Rs. $priceDiff", color = textColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Non-AC Sharing (Rs. 80,000/yr) -> AC Sharing (Rs. 1,00,000/yr)", color = subTextColor, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            
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
                                                if (isSelected) AccentColor.copy(alpha = 0.2f)
                                                else Color.White.copy(alpha = 0.05f)
                                            )
                                            .clickable { paymentPreference = pref }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(pref, color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        } else {
                            // New room is cheaper, refund is due
                            val refundAmount = kotlin.math.abs(priceDiff)
                            Text("Cheaper Room Refund Due: Rs. $refundAmount", color = Color(0xFF81C784), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("AC Sharing (Rs. 1,00,000/yr) -> Non-AC Sharing (Rs. 80,000/yr)", color = subTextColor, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            Text("Select Refund Destination Option:", color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                listOf("Back to Source Account", "Transfer to College Fees", "UPI / Bank Account").forEach { opt ->
                                    val isSelected = refundOption == opt
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { refundOption = opt }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        RadioButton(selected = isSelected, onClick = { refundOption = opt })
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(opt, color = textColor, fontSize = 12.sp)
                                    }
                                }
                            }

                            if (refundOption == "UPI / Bank Account") {
                                OutlinedTextField(
                                    value = refundDetails,
                                    onValueChange = { refundDetails = it },
                                    label = { Text("UPI ID or Bank Account Details") },
                                    placeholder = { Text("e.g. upi@id or AccNo: 123, IFSC: ABC") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (requestedRoom.isNotEmpty()) {
                    Button(
                        onClick = {
                            val currentIsAc = com.example.hprams.data.RoomRules.isAc(currentRoomNum)
                            val currentPrice = if (currentIsAc) 100000 else 80000
                            val requestedIsAc = com.example.hprams.data.RoomRules.isAc(requestedRoom)
                            val requestedPrice = if (requestedIsAc) 100000 else 80000
                            val priceDiff = requestedPrice - currentPrice

                            if (priceDiff > 0) {
                                if (paymentPreference == "Pay Now") {
                                    Toast.makeText(context, "Paid Shifting Fee of Rs. $priceDiff successfully!", Toast.LENGTH_LONG).show()
                                } else {
                                    HostelDataStore.fines.add(
                                        com.example.hprams.data.FineItem(
                                            id = "CHG-${(100..999).random()}",
                                            studentRoll = studentRoll,
                                            amount = "Rs. $priceDiff",
                                            reason = "Shifting Difference Fee (Room $currentRoomNum -> $requestedRoom)",
                                            status = "Unpaid"
                                        )
                                    )
                                    Toast.makeText(context, "Rs. $priceDiff added as outstanding charge to your Finance dashboard.", Toast.LENGTH_LONG).show()
                                }
                            }

                            val newReq = RoomChangeRequest(
                                id = "REQ-${(1000..9999).random()}",
                                studentRoll = studentRoll,
                                studentName = studentName,
                                currentRoom = currentRoomNum,
                                requestedRoom = requestedRoom,
                                gender = studentGender,
                                status = "Pending",
                                refundOption = if (priceDiff < 0) refundOption else "",
                                refundDetails = if (priceDiff < 0 && refundOption == "UPI / Bank Account") refundDetails else ""
                            )
                            HostelDataStore.saveRoomChange(newReq)
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
