package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.RoomRules
import com.example.hprams.data.StudentProfile
import com.example.hprams.ui.components.HostivoTextField
import com.example.hprams.ui.components.ModernBgColor
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccountsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBackClick() }

    var selectedFilter by remember { mutableStateOf("All") } // "All", "Students", "Staff", "Pending Approvals"
    var searchQuery by remember { mutableStateOf("") }

    // Dialog states
    var showCreateAccountDialog by remember { mutableStateOf(false) }
    var selectedUserForEdit by remember { mutableStateOf<StudentProfile?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<StudentProfile?>(null) }
    var showRoomPickerDialog by remember { mutableStateOf(false) }

    // Create/Edit Form fields
    var formName by remember { mutableStateOf("") }
    var formRoll by remember { mutableStateOf("") }
    var formEmail by remember { mutableStateOf("") }
    var formPassword by remember { mutableStateOf("Hostivo@123") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var formPhone by remember { mutableStateOf("") }
    var formRole by remember { mutableStateOf("Student") } // "Student", "Warden", "Security"
    var formGender by remember { mutableStateOf("Male") }
    var formBlock by remember { mutableStateOf("Block A") }
    var formRoom by remember { mutableStateOf("101") }
    var formFather by remember { mutableStateOf("") }
    var formEmergency by remember { mutableStateOf("") }
    var formFeeStatus by remember { mutableStateOf("Paid") }
    var formApprovalStatus by remember { mutableStateOf("Approved") }

    // Room picker block tab & filter
    var roomPickerBlock by remember { mutableStateOf("Block A") }
    var roomPickerFilter by remember { mutableStateOf("All") } // "All", "Available", "AC", "Non-AC"

    fun openCreateDialog() {
        formName = ""
        formRoll = "23180138${(1000..9999).random()}"
        formEmail = ""
        formPassword = "Hostivo@123"
        isPasswordVisible = false
        formPhone = ""
        formRole = "Student"
        formGender = "Male"
        formBlock = "Block A"
        formRoom = "101"
        formFather = ""
        formEmergency = ""
        formFeeStatus = "Paid"
        formApprovalStatus = "Approved"
        selectedUserForEdit = null
        showCreateAccountDialog = true
    }

    fun openEditDialog(user: StudentProfile) {
        selectedUserForEdit = user
        formName = user.name
        formRoll = user.roll
        formEmail = user.email
        formPassword = ""
        isPasswordVisible = false
        formPhone = user.phone
        formRole = user.role
        formGender = user.gender
        formBlock = user.block
        formRoom = user.room
        formFather = user.fatherName
        formEmergency = user.emergencyPhone
        formFeeStatus = user.feePaidStatus
        formApprovalStatus = user.approvalStatus
        showCreateAccountDialog = true
    }

    val filteredUsers = remember(selectedFilter, searchQuery, HostelDataStore.students.size) {
        val list = HostelDataStore.students.filter {
            when (selectedFilter) {
                "Students" -> it.role == "Student"
                "Staff" -> it.role != "Student"
                "Pending Approvals" -> it.approvalStatus != "Approved"
                else -> true
            }
        }
        if (searchQuery.isBlank()) list
        else {
            val q = searchQuery.trim().lowercase()
            list.filter {
                it.name.lowercase().contains(q) ||
                it.roll.lowercase().contains(q) ||
                it.email.lowercase().contains(q) ||
                it.room.lowercase().contains(q) ||
                it.block.lowercase().contains(q)
            }
        }
    }

    // -------------------------------------------------------------
    // VISUAL ROOM ALLOTMENT PICKER MODAL
    // -------------------------------------------------------------
    if (showRoomPickerDialog) {
        val allRooms = (101..120).map { it.toString() }
        val displayedRooms = allRooms.filter { rNum ->
            val isAC = RoomRules.isAc(rNum)
            val cap = RoomRules.getRoomCapacity(rNum)
            val occ = HostelDataStore.students.count {
                it.block.equals(roomPickerBlock, ignoreCase = true) &&
                it.room == rNum &&
                it.approvalStatus == "Approved" &&
                it.roll != (selectedUserForEdit?.roll ?: "")
            }
            val hasSpace = occ < cap

            when (roomPickerFilter) {
                "Available" -> hasSpace
                "AC" -> isAC
                "Non-AC" -> !isAC
                else -> true
            }
        }

        AlertDialog(
            onDismissRequest = { showRoomPickerDialog = false },
            title = {
                Column {
                    Text("Select & Allot Hostel Room", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF0F172A))
                    Text("Live capacity & occupancy indicators", fontSize = 12.sp, color = Color(0xFF64748B))
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Block Selector Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Block A", "Block B", "Block C", "Block D").forEach { blk ->
                            val isSel = roomPickerBlock == blk
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) Color(0xFF6366F1) else Color(0xFFF1F5F9))
                                    .clickable { roomPickerBlock = blk }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    blk,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    // Filter Chips (All, Available, AC, Non-AC)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("All", "Available", "AC", "Non-AC").forEach { flt ->
                            val isSel = roomPickerFilter == flt
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) Color(0xFF0F172A) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (isSel) Color(0xFF0F172A) else Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .clickable { roomPickerFilter = flt }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    flt,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSel) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    // Room Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        items(displayedRooms) { rNum ->
                            val cap = RoomRules.getRoomCapacity(rNum)
                            val isAC = RoomRules.isAc(rNum)
                            val occ = HostelDataStore.students.count {
                                it.block.equals(roomPickerBlock, ignoreCase = true) &&
                                it.room == rNum &&
                                it.approvalStatus == "Approved" &&
                                it.roll != (selectedUserForEdit?.roll ?: "")
                            }
                            val isFull = occ >= cap
                            val isCurrentlyChosen = (formBlock == roomPickerBlock && formRoom == rNum)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        formBlock = roomPickerBlock
                                        formRoom = rNum
                                        showRoomPickerDialog = false
                                        Toast.makeText(context, "Selected $roomPickerBlock Room $rNum ($occ/$cap occupied)", Toast.LENGTH_SHORT).show()
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isCurrentlyChosen) Color(0xFFEEF2FF) else Color(0xFFF8FAFC)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isCurrentlyChosen) Color(0xFF6366F1) else if (isFull) Color(0xFFFECDD3) else Color(0xFFE2E8F0)
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Room $rNum", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isAC) Color(0xFFE0F2FE) else Color(0xFFF1F5F9))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(if (isAC) "AC" else "Non-AC", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isAC) Color(0xFF0284C7) else Color(0xFF64748B))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("$cap Sharing", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Occupancy badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isFull) Color(0xFFFFF1F2) else Color(0xFFECFDF5))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = if (isFull) "$occ/$cap (FULL)" else "$occ/$cap Occupied",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFull) Color(0xFFF43F5E) else Color(0xFF10B981)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRoomPickerDialog = false }) {
                    Text("Close", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // -------------------------------------------------------------
    // CREATE / EDIT ACCOUNT MODAL
    // -------------------------------------------------------------
    if (showCreateAccountDialog) {
        val isEditing = selectedUserForEdit != null
        AlertDialog(
            onDismissRequest = { showCreateAccountDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isEditing) Icons.Default.Edit else Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color(0xFF6366F1)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (isEditing) "Edit: $formName" else "Create New Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Role Selector Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Student", "Warden", "Security").forEach { r ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (formRole == r) Color(0xFF6366F1) else Color(0xFFF1F5F9))
                                    .clickable {
                                        formRole = r
                                        if (r != "Student" && !formRoll.startsWith("STAFF-")) {
                                            formRoll = "STAFF-${(1000..9999).random()}"
                                        }
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    r,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (formRole == r) Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }

                    HostivoTextField(
                        value = formName,
                        onValueChange = { formName = it },
                        label = "Full Name",
                        placeholder = "Enter full name"
                    )

                    HostivoTextField(
                        value = formRoll,
                        onValueChange = { formRoll = it },
                        label = if (formRole == "Student") "Roll Number" else "Staff ID",
                        placeholder = if (formRole == "Student") "231801380007" else "STAFF-1234"
                    )

                    HostivoTextField(
                        value = formEmail,
                        onValueChange = { formEmail = it },
                        label = "Institutional / Personal Email",
                        placeholder = "email@hostivo.edu",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    // Password field for account creation
                    HostivoTextField(
                        value = formPassword,
                        onValueChange = { formPassword = it },
                        label = if (isEditing) "Update Password (Optional)" else "Account Password",
                        placeholder = "Enter strong password",
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    HostivoTextField(
                        value = formPhone,
                        onValueChange = { formPhone = it },
                        label = "Phone Number",
                        placeholder = "9492409574",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    // Gender Selector
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("GENDER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf("Male", "Female").forEach { g ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { formGender = g }
                                ) {
                                    RadioButton(
                                        selected = (formGender == g),
                                        onClick = { formGender = g },
                                        colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF6366F1))
                                    )
                                    Text(g, fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    if (formRole == "Student") {
                        // Visual Room Allotment Section
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("ROOM ALLOTMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        roomPickerBlock = formBlock
                                        showRoomPickerDialog = true
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(22.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text("$formBlock • Room $formRoom", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                                            Text("Tap to change room / check capacity", fontSize = 11.sp, color = Color(0xFF64748B))
                                        }
                                    }
                                    Text("Change", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }

                        HostivoTextField(
                            value = formFather,
                            onValueChange = { formFather = it },
                            label = "Father's Name",
                            placeholder = "Father's full name"
                        )

                        HostivoTextField(
                            value = formEmergency,
                            onValueChange = { formEmergency = it },
                            label = "Emergency Phone Number",
                            placeholder = "Emergency contact",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }

                    // Approval status selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ACCOUNT STATUS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6B7280))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Approved", "Pending").forEach { st ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (formApprovalStatus == st) (if (st == "Approved") Color(0xFF10B981) else Color(0xFFF59E0B)) else Color(0xFFF1F5F9))
                                        .clickable { formApprovalStatus = st }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        st,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (formApprovalStatus == st) Color.White else Color(0xFF64748B)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formName.isBlank() || formRoll.isBlank() || formEmail.isBlank()) {
                            Toast.makeText(context, "Please fill required fields (Name, Roll/ID, Email).", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Create Firebase Auth user if creating account and password provided
                        if (!isEditing && formPassword.isNotBlank()) {
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(formEmail.trim(), formPassword.trim())
                                .addOnCompleteListener { /* Auth user registered */ }
                        }

                        val profile = StudentProfile(
                            roll = formRoll.trim(),
                            name = formName.trim(),
                            email = formEmail.trim(),
                            phone = formPhone.trim(),
                            gender = formGender,
                            block = formBlock,
                            room = formRoom,
                            feePaidStatus = formFeeStatus,
                            paymentStatus = formFeeStatus,
                            approvalStatus = formApprovalStatus,
                            fatherName = formFather,
                            emergencyPhone = formEmergency,
                            role = formRole,
                            dob = selectedUserForEdit?.dob ?: "15/08/2004",
                            profileImageUrl = selectedUserForEdit?.profileImageUrl ?: ""
                        )
                        HostelDataStore.saveStudent(profile)
                        Toast.makeText(context, if (isEditing) "Updated ${profile.name}!" else "Created account for ${profile.name}!", Toast.LENGTH_SHORT).show()
                        showCreateAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isEditing) "Save Changes" else "Create Account", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateAccountDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // -------------------------------------------------------------
    // DELETE CONFIRMATION MODAL
    // -------------------------------------------------------------
    if (showDeleteConfirmDialog != null) {
        val u = showDeleteConfirmDialog!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Delete Account", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444)) },
            text = { Text("Are you sure you want to remove ${u.name} (${u.roll}) from the campus registry?", color = Color(0xFF0F172A)) },
            confirmButton = {
                Button(
                    onClick = {
                        HostelDataStore.students.removeAll { it.roll == u.roll }
                        Toast.makeText(context, "Deleted ${u.name}", Toast.LENGTH_SHORT).show()
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // -------------------------------------------------------------
    // MAIN PAGE CONTENT
    // -------------------------------------------------------------
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Accounts & Directory", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A))
                        Text("${HostelDataStore.students.size} Total Registered • Students & Staff", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                },
                actions = {
                    IconButton(onClick = { openCreateDialog() }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add User", tint = Color(0xFF6366F1))
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, roll, room or block...", color = Color(0xFF94A3B8)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = Color(0xFF6366F1),
                    focusedTextColor = Color(0xFF0F172A),
                    unfocusedTextColor = Color(0xFF0F172A)
                )
            )

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Students", "Staff", "Pending Approvals").forEach { f ->
                    val isSelected = selectedFilter == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF6366F1) else Color.White)
                            .clickable { selectedFilter = f }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            f,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF64748B)
                        )
                    }
                }
            }

            // User List
            if (filteredUsers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No matching accounts found.", color = Color(0xFF94A3B8), fontSize = 14.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredUsers, key = { it.roll }) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Top Row: Avatar + Name & Roll + Status Badge + Action Buttons
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
                                                .background(
                                                    when (user.role) {
                                                        "Warden" -> Color(0xFFFAF5FF)
                                                        "Security" -> Color(0xFFFFF1F2)
                                                        else -> Color(0xFFEEF2FF)
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                user.name.firstOrNull()?.toString() ?: "U",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp,
                                                color = when (user.role) {
                                                    "Warden" -> Color(0xFF8B5CF6)
                                                    "Security" -> Color(0xFFF43F5E)
                                                    else -> Color(0xFF6366F1)
                                                }
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = user.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Color(0xFF0F172A),
                                                maxLines = 1,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "${user.role} • ${user.roll}",
                                                fontSize = 12.sp,
                                                color = Color(0xFF64748B),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (user.approvalStatus.equals("Approved", ignoreCase = true)) Color(0xFFECFDF5)
                                                    else Color(0xFFFFFBEB)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = user.approvalStatus,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (user.approvalStatus.equals("Approved", ignoreCase = true)) Color(0xFF10B981) else Color(0xFFD97706)
                                            )
                                        }

                                        IconButton(
                                            onClick = { openEditDialog(user) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                                        }

                                        IconButton(
                                            onClick = { showDeleteConfirmDialog = user },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                // Bottom Row: Room badge & Email contact
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (user.role == "Student") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFF8FAFC))
                                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "${user.block} • Room ${user.room}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF334155)
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "Campus Staff",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = user.email,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
