package com.example.hprams.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hprams.data.HostelDataStore
import com.example.hprams.ui.components.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onRoomsClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onSupportClick: () -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    var studentName by remember(currentStudent) { mutableStateOf(currentStudent?.name ?: "C.Venkat") }
    val studentRoll = currentStudent?.roll ?: "231801380007"
    val studentEmail = currentStudent?.email ?: "student@hostivo.edu"
    var studentPhone by remember(currentStudent) { mutableStateOf(currentStudent?.phone ?: "+91 94924 09574") }
    val studentBlock = currentStudent?.block ?: "Block B"
    val studentRoom = currentStudent?.room ?: "102"
    var fatherName by remember(currentStudent) { mutableStateOf(currentStudent?.fatherName ?: "C Venkatesh") }
    var emergencyPhone by remember(currentStudent) { mutableStateOf(currentStudent?.emergencyPhone ?: "+91 95737 41654") }
    var dob by remember(currentStudent) { mutableStateOf(currentStudent?.dob ?: "15/08/2004") }
    val gender = currentStudent?.gender ?: "Male"
    var profilePhotoUrl by remember(currentStudent) { mutableStateOf(currentStudent?.profileImageUrl ?: "") }

    var isUploadingPhoto by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Edit form states
    var editName by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editFatherName by remember { mutableStateOf("") }
    var editEmergencyPhone by remember { mutableStateOf("") }
    var editDob by remember { mutableStateOf("") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                isUploadingPhoto = true
                Toast.makeText(context, "Uploading profile photo...", Toast.LENGTH_SHORT).show()
                val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
                    .child("profile_photos/STUDENT_${studentRoll}_${System.currentTimeMillis()}.jpg")
                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            profilePhotoUrl = downloadUrl.toString()
                            currentStudent?.let { s ->
                                val updated = s.copy(profileImageUrl = profilePhotoUrl)
                                HostelDataStore.saveStudent(updated)
                            }
                            isUploadingPhoto = false
                            Toast.makeText(context, "Profile photo updated successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        isUploadingPhoto = false
                        Toast.makeText(context, "Photo upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    )

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            editDob = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR) - 18,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ModernBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Subpage header
            ModernSubPageHeader(
                title = "Student Profile",
                subtitle = "Campus identity & account settings",
                onBackClick = onHomeClick
            )

            // Profile Header Card with Photo Change Option
            ModernSectionCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Photo Avatar with Edit Badge
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEEF2FF))
                                .border(2.dp, Color(0xFFC7D2FE), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUploadingPhoto) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = Color(0xFF6366F1)
                                )
                            } else if (profilePhotoUrl.isNotEmpty()) {
                                AsyncImage(
                                    model = profilePhotoUrl,
                                    contentDescription = "Profile Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = studentName.firstOrNull()?.toString() ?: "S",
                                    color = Color(0xFF6366F1),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        // Camera Icon Badge
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .align(Alignment.BottomEnd)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A))
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = studentName,
                            color = Color(0xFF0F172A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Roll: $studentRoll",
                            color = Color(0xFF6366F1),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "$studentBlock • Room $studentRoom",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }

                    // Edit Profile Icon Button
                    IconButton(
                        onClick = {
                            editName = studentName
                            editPhone = studentPhone
                            editFatherName = fatherName
                            editEmergencyPhone = emergencyPhone
                            editDob = dob
                            showEditDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color(0xFF6366F1)
                        )
                    }
                }
            }

            // Student Details Card
            ModernSectionCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Personal Information", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Edit",
                        color = Color(0xFF6366F1),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            editName = studentName
                            editPhone = studentPhone
                            editFatherName = fatherName
                            editEmergencyPhone = emergencyPhone
                            editDob = dob
                            showEditDialog = true
                        }
                    )
                }

                ProfileInfoRow(label = "Full Name", value = studentName, icon = Icons.Default.Badge)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                ProfileInfoRow(label = "Email Address", value = studentEmail, icon = Icons.Default.Email)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                ProfileInfoRow(label = "Phone Number", value = studentPhone, icon = Icons.Default.Phone)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                ProfileInfoRow(label = "Gender", value = gender, icon = Icons.Default.Person)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                ProfileInfoRow(label = "Date of Birth", value = dob, icon = Icons.Default.CalendarToday)
            }

            // Emergency & Guardian Card
            ModernSectionCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text("Guardian & Emergency Contact", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)

                ProfileInfoRow(label = "Father's Name", value = fatherName, icon = Icons.Default.People)
                HorizontalDivider(color = Color(0xFFF1F5F9))
                ProfileInfoRow(label = "Emergency Phone", value = emergencyPhone, icon = Icons.Default.PhoneInTalk)
            }

            // Sign Out Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Button(
                    onClick = onSignOutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F2))
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    // Edit Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text("Edit Profile Information", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HostivoTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = "Full Name",
                        placeholder = "Your full name"
                    )

                    HostivoTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = "Phone Number",
                        placeholder = "9492409574",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    HostivoTextField(
                        value = editDob,
                        onValueChange = {},
                        label = "Date of Birth",
                        placeholder = "DD/MM/YYYY",
                        isReadOnly = true,
                        onClick = { datePickerDialog.show() },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(18.dp))
                            }
                        }
                    )

                    HostivoTextField(
                        value = editFatherName,
                        onValueChange = { editFatherName = it },
                        label = "Father's Name",
                        placeholder = "Father's name"
                    )

                    HostivoTextField(
                        value = editEmergencyPhone,
                        onValueChange = { editEmergencyPhone = it },
                        label = "Emergency Phone Number",
                        placeholder = "Emergency contact",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isBlank() || editPhone.isBlank()) {
                            Toast.makeText(context, "Name and Phone cannot be blank", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        studentName = editName
                        studentPhone = editPhone
                        fatherName = editFatherName
                        emergencyPhone = editEmergencyPhone
                        dob = editDob

                        currentStudent?.let { s ->
                            val updated = s.copy(
                                name = editName,
                                phone = editPhone,
                                fatherName = editFatherName,
                                emergencyPhone = editEmergencyPhone,
                                dob = editDob,
                                profileImageUrl = profilePhotoUrl
                            )
                            HostelDataStore.saveStudent(updated)
                        }
                        Toast.makeText(context, "Profile information saved!", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("Save Changes", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = label, color = Color(0xFF64748B), fontSize = 13.sp)
        }
        Text(text = value, color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
