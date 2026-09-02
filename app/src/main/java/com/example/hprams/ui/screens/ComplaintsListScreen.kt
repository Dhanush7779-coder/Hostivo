package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.GatePassRequest
import com.example.hprams.data.HostelDataStore
import com.example.hprams.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintsListScreen(
    onHomeClick: () -> Unit,
    onRoomsClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onProfileClick: () -> Unit,
    onNewComplaintClick: () -> Unit,
    onComplaintClick: (complaintId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var showInfoDialog by remember { mutableStateOf(false) }
    var showGatePassDialog by remember { mutableStateOf(false) }
    var gatePassType by remember { mutableStateOf("Outing") }

    var outDate by remember { mutableStateOf("") }
    var outTime by remember { mutableStateOf("") }
    var inDate by remember { mutableStateOf("") }
    var inTime by remember { mutableStateOf("") }
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var placeOfGoing by remember { mutableStateOf("") }
    var reasonText by remember { mutableStateOf("") }

    // Read current student context
    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentGender = currentStudent?.gender ?: "Male"
    val studentName = currentStudent?.name ?: "C.Venkat"
    val studentRoll = currentStudent?.roll ?: "231801380007"

    val studentComplaints by remember(studentName) { derivedStateOf { HostelDataStore.complaints.filter { it.studentName == studentName } } }
    val studentPasses by remember(studentRoll) { derivedStateOf { HostelDataStore.gatePassRequests.filter { it.studentRoll == studentRoll } } }

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
                title = "Support & Complaints",
                subtitle = "Assistance, tickets & gate pass requests",
                onBackClick = onHomeClick
            )

            // 2. Hostel Information & Timings Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF0F172A))
                    .clickable { showInfoDialog = true }
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6366F1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Hostel Information & Timings",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Warden details, Mess schedules & Rules",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White.copy(alpha = 0.6f))
                }
            }

            // 3. Action Buttons: New Complaint & Request Gate Pass
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNewComplaintClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) {
                    Icon(Icons.Default.AddComment, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Complaint +", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = { showGatePassDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Request Gate Pass", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // 4. Active Complaints Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Your Active Complaints",
                    color = Color(0xFF0F172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                if (studentComplaints.isEmpty()) {
                    ModernSectionCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No active complaints registered.", color = Color(0xFF64748B), fontSize = 13.sp)
                        }
                    }
                } else {
                    studentComplaints.forEach { cmp ->
                        ModernSectionCard(
                            modifier = Modifier.clickable { onComplaintClick(cmp.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFAF5FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Build, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(cmp.title, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${cmp.category} • ${cmp.date}", color = Color(0xFF64748B), fontSize = 11.sp)
                                    }
                                }

                                val statusColor = when (cmp.status) {
                                    "Resolved" -> Color(0xFF10B981)
                                    "In Progress" -> Color(0xFF6366F1)
                                    else -> Color(0xFFD97706)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(statusColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = cmp.status.uppercase(),
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Gate Pass Requests Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Gate Pass Status & Logs",
                    color = Color(0xFF0F172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                if (studentPasses.isEmpty()) {
                    ModernSectionCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.AirplaneTicket, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No recent gate pass requests.", color = Color(0xFF64748B), fontSize = 13.sp)
                        }
                    }
                } else {
                    studentPasses.forEach { pass ->
                        ModernSectionCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEEF2FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("${pass.type} Pass: ${pass.placeOfGoing}", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Out: ${pass.outDate} ${pass.outTime} • In: ${pass.inDate} ${pass.inTime}", color = Color(0xFF64748B), fontSize = 11.sp)
                                    }
                                }

                                val passStatusColor = when (pass.wardenApproval) {
                                    "Approved" -> Color(0xFF10B981)
                                    "Rejected" -> Color(0xFFDC2626)
                                    else -> Color(0xFFD97706)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(passStatusColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = pass.wardenApproval.uppercase(),
                                        color = passStatusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
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
                    BottomNavItem("rooms", "Rooms", Icons.Default.Bed),
                    BottomNavItem("finance", "Finance", Icons.Default.AccountBalanceWallet),
                    BottomNavItem("community", "Community", Icons.Default.Groups)
                ),
                selectedId = "community",
                onSelect = { id ->
                    when (id) {
                        "home" -> onHomeClick()
                        "rooms" -> onRoomsClick()
                        "finance" -> onFinanceClick()
                    }
                }
            )
        }
    }

    // Hostel Info Dialog
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Text("Hostel Information & Timings", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("• Main Campus Gate Closes: 09:30 PM", color = Color(0xFF334155), fontSize = 13.sp)
                    Text("• Breakfast: 07:30 AM - 09:00 AM", color = Color(0xFF334155), fontSize = 13.sp)
                    Text("• Lunch: 12:30 PM - 02:00 PM", color = Color(0xFF334155), fontSize = 13.sp)
                    Text("• Dinner: 07:30 PM - 09:00 PM", color = Color(0xFF334155), fontSize = 13.sp)
                    Text("• Chief Warden Office: Block Admin Ground Floor", color = Color(0xFF334155), fontSize = 13.sp)
                    Text("• Emergency Helpline: +91 94924 09574", color = Color(0xFF334155), fontSize = 13.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showInfoDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("Close", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Gate Pass Request Dialog
    if (showGatePassDialog) {
        AlertDialog(
            onDismissRequest = { showGatePassDialog = false },
            title = {
                Text("Request Campus Gate Pass", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Outing", "Home Visit", "Emergency").forEach { t ->
                            val isSel = gatePassType == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) Color(0xFF0F172A) else Color(0xFFF1F5F9))
                                    .clickable { gatePassType = t }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = t,
                                    color = if (isSel) Color.White else Color(0xFF334155),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    HostivoTextField(
                        value = placeOfGoing,
                        onValueChange = { placeOfGoing = it },
                        label = "Destination / Place",
                        placeholder = "e.g. City Mall, Home"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HostivoTextField(
                            value = outDate,
                            onValueChange = { outDate = it },
                            label = "Out Date",
                            placeholder = "DD/MM/YYYY",
                            modifier = Modifier.weight(1f)
                        )
                        HostivoTextField(
                            value = outTime,
                            onValueChange = { outTime = it },
                            label = "Out Time",
                            placeholder = "05:00 PM",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HostivoTextField(
                            value = inDate,
                            onValueChange = { inDate = it },
                            label = "Return Date",
                            placeholder = "DD/MM/YYYY",
                            modifier = Modifier.weight(1f)
                        )
                        HostivoTextField(
                            value = inTime,
                            onValueChange = { inTime = it },
                            label = "Return Time",
                            placeholder = "08:30 PM",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HostivoTextField(
                        value = reasonText,
                        onValueChange = { reasonText = it },
                        label = "Purpose / Reason",
                        placeholder = "Medical checkup, shopping, etc."
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (placeOfGoing.isBlank() || outDate.isBlank()) {
                            Toast.makeText(context, "Please fill required pass details", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newPass = GatePassRequest(
                            id = "PASS-${(1000..9999).random()}",
                            studentName = studentName,
                            studentRoll = studentRoll,
                            type = gatePassType,
                            gender = studentGender,
                            wardenApproval = "Pending",
                            outDate = outDate,
                            outTime = outTime,
                            inDate = inDate,
                            inTime = inTime,
                            placeOfGoing = placeOfGoing,
                            reason = reasonText
                        )
                        HostelDataStore.saveGatePass(newPass)
                        Toast.makeText(context, "Gate pass submitted for Warden approval!", Toast.LENGTH_LONG).show()
                        showGatePassDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("Submit Pass", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGatePassDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
