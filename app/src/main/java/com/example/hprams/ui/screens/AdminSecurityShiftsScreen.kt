package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
fun AdminSecurityShiftsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Only load real security staff accounts created in the registry
    val availableGuards = remember(HostelDataStore.students.size) {
        HostelDataStore.students.filter { it.role == "Security" }.map { it.name }
    }

    val gates = listOf("Main Campus Gate", "Gate A (Boys Hostel)", "Gate B (Girls Hostel)")
    val shifts = listOf("Day Shift (08:00 AM - 04:00 PM)", "Evening Shift (04:00 PM - 12:00 AM)", "Night Shift (12:00 AM - 08:00 AM)")

    // Assigned states
    var selectedGate by remember { mutableStateOf(gates[0]) }
    var selectedGuard by remember { mutableStateOf(availableGuards.firstOrNull() ?: "") }
    var selectedShift by remember { mutableStateOf(shifts[0]) }

    // Shift Assignments map (Gate -> Pair(Guard, Shift))
    val assignments = remember {
        mutableStateMapOf<String, Pair<String, String>>()
    }

    var showGateDropdown by remember { mutableStateOf(false) }
    var showGuardDropdown by remember { mutableStateOf(false) }
    var showShiftDropdown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Security Gates & Shift Allotment", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A))
                        Text("Assign registered guards, gates and duty shifts", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = ModernBgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Assignment Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("New Gate & Shift Allocation", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))

                    // 1. Select Gate
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("1. Select Gate:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showGateDropdown = true },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedGate,
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B))
                                }
                            }
                            DropdownMenu(
                                expanded = showGateDropdown,
                                onDismissRequest = { showGateDropdown = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                gates.forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g, color = Color(0xFF0F172A)) },
                                        onClick = {
                                            selectedGate = g
                                            showGateDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 2. Select Security Personnel
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("2. Select Security Officer:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                        if (availableGuards.isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFFEF2F2),
                                border = BorderStroke(1.dp, Color(0xFFFECACA))
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFEF4444))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "No security accounts created yet. Please create security accounts from Accounts section first.",
                                        fontSize = 12.sp,
                                        color = Color(0xFF991B1B)
                                    )
                                }
                            }
                        } else {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { showGuardDropdown = true },
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (selectedGuard.isEmpty()) "Choose Security Officer" else selectedGuard,
                                            color = if (selectedGuard.isEmpty()) Color(0xFF94A3B8) else Color(0xFF0F172A),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 14.sp
                                        )
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B))
                                    }
                                }
                                DropdownMenu(
                                    expanded = showGuardDropdown,
                                    onDismissRequest = { showGuardDropdown = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    availableGuards.forEach { gd ->
                                        DropdownMenuItem(
                                            text = { Text(gd, color = Color(0xFF0F172A)) },
                                            onClick = {
                                                selectedGuard = gd
                                                showGuardDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Select Shift Timing
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("3. Select Shift Timing:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showShiftDropdown = true },
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = selectedShift,
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF64748B))
                                }
                            }
                            DropdownMenu(
                                expanded = showShiftDropdown,
                                onDismissRequest = { showShiftDropdown = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                shifts.forEach { sf ->
                                    DropdownMenuItem(
                                        text = { Text(sf, color = Color(0xFF0F172A)) },
                                        onClick = {
                                            selectedShift = sf
                                            showShiftDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Submit Assignment Button
                    Button(
                        onClick = {
                            if (selectedGuard.isBlank()) {
                                Toast.makeText(context, "Please select a security officer from your created accounts.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            assignments[selectedGate] = Pair(selectedGuard, selectedShift)
                            HostelDataStore.securityOfficerName = selectedGuard
                            HostelDataStore.securityDutyTimings = selectedShift
                            HostelDataStore.securityBlockAssignment = selectedGate
                            Toast.makeText(context, "✅ Allotted $selectedGuard to $selectedGate ($selectedShift)!", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Icon(Icons.Default.AssignmentInd, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Allot Guard to Gate & Shift", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // Active Gate Rosters
            Text("Active Gate Shifts & Rosters", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
            if (assignments.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No active shifts assigned yet. Select a gate, guard, and shift above.", color = Color(0xFF94A3B8), fontSize = 13.sp)
                    }
                }
            } else {
                assignments.forEach { (gate, guardShift) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(gate, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Guard: ${guardShift.first}", color = Color(0xFF6366F1), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Timing: ${guardShift.second}", color = Color(0xFF64748B), fontSize = 12.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFECFDF5))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Active", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
