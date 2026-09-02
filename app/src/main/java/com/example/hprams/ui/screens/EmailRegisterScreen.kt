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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.RoomRules
import com.example.hprams.ui.components.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailRegisterScreen(
    onBackClick: () -> Unit,
    onSignUpClick: (
        name: String,
        email: String,
        roll: String,
        phone: String,
        pass: String,
        father: String,
        emergency: String,
        role: String,
        gender: String,
        dob: String,
        block: String,
        room: String,
        paymentPreference: String,
        receiptUrl: String,
        receiptReference: String
    ) -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var firstName by remember {
        mutableStateOf(
            HostelDataStore.prefilledName.split(" ").firstOrNull().orEmpty()
        )
    }
    var lastName by remember {
        mutableStateOf(
            HostelDataStore.prefilledName.split(" ").drop(1).joinToString(" ")
        )
    }

    var email by remember { mutableStateOf(HostelDataStore.prefilledEmail.ifEmpty { "" }) }
    var selectedRole by remember { mutableStateOf("Student") }
    var rollNumber by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Student specific parameters
    var gender by remember { mutableStateOf("Male") }
    var dob by remember { mutableStateOf("") }

    val blocks = if (gender == "Male") listOf("Block A", "Block B") else listOf("Block C", "Block D")
    var selectedBlock by remember { mutableStateOf(blocks.first()) }

    LaunchedEffect(gender) {
        selectedBlock = if (gender == "Male") "Block A" else "Block C"
    }

    // Room Selection by specs
    var isAcSelected by remember { mutableStateOf(false) } // false = Non-AC, true = AC
    var selectedSharing by remember { mutableIntStateOf(4) } // 3, 4, 6 sharing

    // Calculate matching rooms based on specs
    val matchingRooms = remember(isAcSelected, selectedSharing) {
        (101..120).filter { rNum ->
            val rStr = rNum.toString()
            val roomAc = RoomRules.isAc(rStr)
            val roomCap = RoomRules.getRoomCapacity(rStr)
            roomAc == isAcSelected && roomCap == selectedSharing
        }.map { it.toString() }
    }

    var selectedRoom by remember { mutableStateOf(matchingRooms.firstOrNull() ?: "101") }
    var showRoomDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(matchingRooms) {
        if (!matchingRooms.contains(selectedRoom)) {
            selectedRoom = matchingRooms.firstOrNull() ?: "101"
        }
    }

    // Dynamic fee calculation based on AC and sharing
    val calculatedFee = remember(isAcSelected, selectedSharing) {
        when {
            isAcSelected && selectedSharing == 3 -> "Rs. 1,10,000 / Sem"
            !isAcSelected && selectedSharing == 3 -> "Rs. 90,000 / Sem"
            isAcSelected && selectedSharing == 4 -> "Rs. 1,00,000 / Sem"
            !isAcSelected && selectedSharing == 4 -> "Rs. 80,000 / Sem"
            else -> "Rs. 65,000 / Sem" // 6 sharing Non-AC
        }
    }

    // Payment preference: ONLY "Pay Later" or "Upload Receipt" (Pay Now is removed during signup)
    var paymentPreference by remember { mutableStateOf("Pay Later") }

    var receiptReference by remember { mutableStateOf("") }
    var receiptUrl by remember { mutableStateOf("") }
    var isUploadingReceipt by remember { mutableStateOf(false) }
    var uploadStatus by remember { mutableStateOf("No file selected") }

    val receiptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                isUploadingReceipt = true
                uploadStatus = "Uploading receipt..."
                val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
                    .child("receipts/REG-${System.currentTimeMillis()}.jpg")
                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            receiptUrl = downloadUrl.toString()
                            uploadStatus = "Receipt attached successfully!"
                            isUploadingReceipt = false
                        }
                    }
                    .addOnFailureListener { e ->
                        uploadStatus = "Upload failed: ${e.message}"
                        isUploadingReceipt = false
                        Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    )

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dob = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR) - 18,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    HostivoBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Header
            HostivoHeader(
                title = "Create Account",
                subtitle = "Join the smart campus network."
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Floating White Card
            HostivoCard {
                // First Name & Last Name (Side by Side)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HostivoTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "First Name",
                        placeholder = "Jane",
                        modifier = Modifier.weight(1f)
                    )
                    HostivoTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Last Name",
                        placeholder = "Doe",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Email Address
                HostivoTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "jane.doe@hostivo.edu",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // Roll Number & Phone (Side by Side)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedRole == "Student") {
                        HostivoTextField(
                            value = rollNumber,
                            onValueChange = { rollNumber = it },
                            label = "Roll Number",
                            placeholder = "231801380007",
                            modifier = Modifier.weight(1.1f)
                        )
                    }
                    HostivoTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number",
                        placeholder = "9492409574",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f)
                    )
                }

                if (selectedRole == "Student") {
                    // Gender & Date of Birth (Side by Side)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // Gender Selector
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "GENDER",
                                color = Color(0xFF6B7280),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("Male", "Female").forEach { g ->
                                    val isSelected = gender == g
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(
                                                if (isSelected) Color(0xFF0F172A) else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { gender = g },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = g,
                                            color = if (isSelected) Color.White else Color(0xFF4B5563),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Date of Birth
                        HostivoTextField(
                            value = dob,
                            onValueChange = {},
                            label = "Date of Birth",
                            placeholder = "DD/MM/YYYY",
                            isReadOnly = true,
                            onClick = { datePickerDialog.show() },
                            trailingIcon = {
                                IconButton(onClick = { datePickerDialog.show() }) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Select DOB",
                                        tint = Color(0xFF6B7280),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            modifier = Modifier.weight(1.1f)
                        )
                    }

                    // Hostel Block Selector
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "HOSTEL BLOCK",
                            color = Color(0xFF6B7280),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            blocks.forEach { b ->
                                val isSelected = selectedBlock == b
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(
                                            if (isSelected) Color(0xFF0F172A) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedBlock = b },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = b,
                                        color = if (isSelected) Color.White else Color(0xFF4B5563),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    // Room Type Specs (AC / Non-AC & Sharing Members Selection)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // AC vs Non-AC
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ROOM TYPE",
                                color = Color(0xFF6B7280),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf(Pair("Non-AC", false), Pair("AC", true)).forEach { (label, acVal) ->
                                    val isSel = isAcSelected == acVal
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(
                                                if (isSel) Color(0xFF0F172A) else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                isAcSelected = acVal
                                                if (acVal && selectedSharing == 6) {
                                                    selectedSharing = 4 // 6 sharing is only non-AC
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = if (isSel) Color.White else Color(0xFF4B5563),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // Sharing (3, 4, 6 sharing)
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text(
                                text = "SHARING",
                                color = Color(0xFF6B7280),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                                    .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val sharingOptions = if (isAcSelected) listOf(3, 4) else listOf(3, 4, 6)
                                sharingOptions.forEach { s ->
                                    val isSel = selectedSharing == s
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .background(
                                                if (isSel) Color(0xFF0F172A) else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { selectedSharing = s },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${s}-Share",
                                            color = if (isSel) Color.White else Color(0xFF4B5563),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Room Selection matching specs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            HostivoTextField(
                                value = if (selectedRoom.isNotEmpty()) "Room $selectedRoom" else "No room available",
                                onValueChange = {},
                                label = "Select Room ($selectedBlock)",
                                isReadOnly = true,
                                onClick = { if (matchingRooms.isNotEmpty()) showRoomDropdown = true },
                                trailingIcon = {
                                    IconButton(onClick = { if (matchingRooms.isNotEmpty()) showRoomDropdown = true }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "Select Room",
                                            tint = Color(0xFF6B7280)
                                        )
                                    }
                                }
                            )
                            DropdownMenu(
                                expanded = showRoomDropdown,
                                onDismissRequest = { showRoomDropdown = false },
                                modifier = Modifier.background(Color.White).heightIn(max = 220.dp)
                            ) {
                                matchingRooms.forEach { r ->
                                    val avail = RoomRules.isRoomAvailable(selectedBlock, r)
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Room $r", color = Color(0xFF1F2937), fontWeight = FontWeight.Bold)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(if (avail) "Available" else "Full", color = if (avail) Color(0xFF16A34A) else Color(0xFFDC2626), fontSize = 11.sp)
                                            }
                                        },
                                        onClick = {
                                            selectedRoom = r
                                            showRoomDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Dynamic Fees Calculation Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFEEF2FF))
                            .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(14.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "SEMESTER HOSTEL FEE",
                                    color = Color(0xFF6366F1),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$selectedSharing Sharing ${if (isAcSelected) "AC" else "Non-AC"}",
                                    color = Color(0xFF475569),
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = calculatedFee,
                                color = Color(0xFF0F172A),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Father's Name & Emergency Phone
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        HostivoTextField(
                            value = fatherName,
                            onValueChange = { fatherName = it },
                            label = "Father's Name",
                            placeholder = "C Venkatesh",
                            modifier = Modifier.weight(1f)
                        )
                        HostivoTextField(
                            value = emergencyPhone,
                            onValueChange = { emergencyPhone = it },
                            label = "Emergency Phone",
                            placeholder = "9573741654",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Payment Preference Selection (ONLY Pay Later & Upload Receipt)
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "FEE PAYMENT OPTION",
                            color = Color(0xFF6B7280),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Color(0xFFF9FAFB), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf("Pay Later", "Upload Receipt").forEach { p ->
                                val isSelected = paymentPreference == p
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(
                                            if (isSelected) Color(0xFF0F172A) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { paymentPreference = p },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = p,
                                        color = if (isSelected) Color.White else Color(0xFF4B5563),
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }

                    if (paymentPreference == "Upload Receipt") {
                        HostivoTextField(
                            value = receiptReference,
                            onValueChange = { receiptReference = it },
                            label = "UTR / Transaction Reference",
                            placeholder = "UTR123456789"
                        )

                        OutlinedButton(
                            onClick = {
                                receiptLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isUploadingReceipt) "Uploading..." else "Select Receipt Photo")
                        }
                        Text(
                            text = uploadStatus,
                            color = if (receiptUrl.isNotEmpty()) Color(0xFF16A34A) else Color(0xFF6B7280),
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "ℹ️ You can pay your semester fee later via the Fee Portal once admitted.",
                            color = Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                }

                // Password
                HostivoTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Create a password",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle password visibility",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                )

                // Confirm Password
                HostivoTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Confirm Password",
                    placeholder = "Repeat your password",
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = "Toggle confirm password visibility",
                                tint = Color(0xFF6B7280)
                            )
                        }
                    }
                )

                // Warning / Info Note
                Text(
                    text = "🔒 Registered details require Admin approval before first sign in.",
                    color = Color(0xFFD97706),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Create Account Button
                HostivoPrimaryButton(
                    text = "Create Account",
                    onClick = {
                        val fullName = "$firstName $lastName".trim()
                        if (firstName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
                            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                            return@HostivoPrimaryButton
                        }
                        if (password != confirmPassword) {
                            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                            return@HostivoPrimaryButton
                        }
                        val duplicateEmail = HostelDataStore.students.any { it.email.lowercase() == email.trim().lowercase() }
                        if (duplicateEmail) {
                            Toast.makeText(context, "This Email Address is already registered!", Toast.LENGTH_SHORT).show()
                            return@HostivoPrimaryButton
                        }
                        if (selectedRole == "Student") {
                            if (rollNumber.isBlank() || fatherName.isBlank() || emergencyPhone.isBlank() || dob.isBlank()) {
                                Toast.makeText(context, "Please fill all Student fields including Date of Birth", Toast.LENGTH_SHORT).show()
                                return@HostivoPrimaryButton
                            }
                            val duplicateRoll = HostelDataStore.students.any { it.roll.lowercase() == rollNumber.trim().lowercase() }
                            if (duplicateRoll) {
                                Toast.makeText(context, "This Roll Number is already registered!", Toast.LENGTH_SHORT).show()
                                return@HostivoPrimaryButton
                            }
                            if (paymentPreference == "Upload Receipt" && receiptUrl.isEmpty()) {
                                Toast.makeText(context, "Please upload the payment receipt", Toast.LENGTH_SHORT).show()
                                return@HostivoPrimaryButton
                            }
                        }

                        onSignUpClick(
                            fullName, email, rollNumber, phone, password, fatherName, emergencyPhone, selectedRole,
                            gender, dob, selectedBlock, selectedRoom, paymentPreference, receiptUrl, receiptReference
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottom prompt: Already have an account? Sign In
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign In",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onSignInClick)
                )
            }
        }
    }
}
