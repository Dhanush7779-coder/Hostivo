package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.StudentProfile
import com.example.hprams.ui.components.ModernBgColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAccountsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("All") } // "All", "Students", "Staff", "Pending Approvals"
    var searchQuery by remember { mutableStateOf("") }

    // Dialog states
    var showCreateAccountDialog by remember { mutableStateOf(false) }
    var selectedUserForEdit by remember { mutableStateOf<StudentProfile?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<StudentProfile?>(null) }

    // Create/Edit Form fields
    var formName by remember { mutableStateOf("") }
    var formRoll by remember { mutableStateOf("") }
    var formEmail by remember { mutableStateOf("") }
    var formPhone by remember { mutableStateOf("") }
    var formRole by remember { mutableStateOf("Student") } // "Student", "Warden", "Security"
    var formGender by remember { mutableStateOf("Male") }
    var formBlock by remember { mutableStateOf("Block A") }
    var formRoom by remember { mutableStateOf("101") }
    var formFather by remember { mutableStateOf("") }
    var formEmergency by remember { mutableStateOf("") }
    var formFeeStatus by remember { mutableStateOf("Paid") }
    var formApprovalStatus by remember { mutableStateOf("Approved") }

    fun openCreateDialog() {
        formName = ""
        formRoll = "23180138${(1000..9999).random()}"
        formEmail = ""
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
                        if (isEditing) "Edit Profile: $formName" else "Create New Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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

                    OutlinedTextField(
                        value = formName,
                        onValueChange = { formName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = formRoll,
                        onValueChange = { formRoll = it },
                        label = { Text(if (formRole == "Student") "Roll Number" else "Staff ID") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = formEmail,
                        onValueChange = { formEmail = it },
                        label = { Text("Institutional / Personal Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = formPhone,
                        onValueChange = { formPhone = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Gender Selector
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
                                Text(g, fontSize = 13.sp)
                            }
                        }
                    }

                    if (formRole == "Student") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = formBlock,
                                onValueChange = { formBlock = it },
                                label = { Text("Block") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = formRoom,
                                onValueChange = { formRoom = it },
                                label = { Text("Room") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = formFather,
                            onValueChange = { formFather = it },
                            label = { Text("Father's Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = formEmergency,
                            onValueChange = { formEmergency = it },
                            label = { Text("Emergency Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // Approval status selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Account Status:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Approved", "Pending").forEach { st ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (formApprovalStatus == st) (if (st == "Approved") Color(0xFF10B981) else Color(0xFFF59E0B)) else Color(0xFFF1F5F9))
                                        .clickable { formApprovalStatus = st }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
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
                            dob = ""
                        )
                        HostelDataStore.saveStudent(profile)
                        Toast.makeText(context, if (isEditing) "Updated ${profile.name}!" else "Created account for ${profile.name}!", Toast.LENGTH_SHORT).show()
                        showCreateAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
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
            text = { Text("Are you sure you want to remove ${u.name} (${u.roll}) from the campus registry?") },
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
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
                placeholder = { Text("Search by name, roll, room or block...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedBorderColor = Color(0xFF6366F1)
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
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredUsers) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(user.name, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 15.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (user.role == "Student") Color(0xFFEEF2FF) else Color(0xFFECFDF5))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                user.role,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (user.role == "Student") Color(0xFF6366F1) else Color(0xFF10B981)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "${user.roll} • ${user.email}",
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp
                                    )
                                    if (user.role == "Student") {
                                        Text(
                                            "${user.block}, Room ${user.room} • Fees: ${user.feePaidStatus}",
                                            color = Color(0xFF334155),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (user.approvalStatus != "Approved") {
                                        Button(
                                            onClick = {
                                                val updated = user.copy(approvalStatus = "Approved")
                                                HostelDataStore.saveStudent(updated)
                                                Toast.makeText(context, "Approved ${user.name}!", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Approve", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    IconButton(onClick = { openEditDialog(user) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                                    }

                                    IconButton(onClick = { showDeleteConfirmDialog = user }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
