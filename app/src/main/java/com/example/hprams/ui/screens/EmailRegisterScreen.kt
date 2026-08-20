package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.GlassBackground
import com.example.hprams.ui.components.GlassButton
import com.example.hprams.ui.components.GlassCard
import com.example.hprams.ui.components.GlassTextField
import com.example.hprams.ui.components.getAppSubTextColor
import com.example.hprams.theme.AccentColor
import com.example.hprams.ui.components.getAppTextColor
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
    val context = androidx.compose.ui.platform.LocalContext.current
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()
    val isDark = com.example.hprams.theme.isAppDarkTheme()

    var fullName by remember { mutableStateOf(com.example.hprams.data.HostelDataStore.prefilledName.ifEmpty { "" }) }
    var email by remember { mutableStateOf(com.example.hprams.data.HostelDataStore.prefilledEmail.ifEmpty { "" }) }
    var selectedRole by remember { mutableStateOf("Student") }
    var rollNumber by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // New Registration Fields
    var gender by remember { mutableStateOf("Male") }
    var dob by remember { mutableStateOf("") }
    
    // Auto-resolve Blocks based on Gender
    val blocks = if (gender == "Male") listOf("Block A", "Block B") else listOf("Block C", "Block D")
    var selectedBlock by remember { mutableStateOf(blocks.first()) }
    
    // Keep Block updated when gender changes
    LaunchedEffect(gender) {
        selectedBlock = if (gender == "Male") "Block A" else "Block C"
    }

    var selectedRoom by remember { mutableStateOf("101") }
    var showRoomDropdown by remember { mutableStateOf(false) }
    var paymentPreference by remember { mutableStateOf("Pay Later") } // "Pay Now", "Upload Receipt", "Pay Later"
    
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
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            dob = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR) - 18,
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    GlassBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .safeDrawingPadding()
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back",
                    tint = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 450.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Hostivo",
                            color = AccentColor,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Join the exclusive smart campus network.",
                            color = getAppSubTextColor(),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Card
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        // Full Name
                        GlassTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Full Name",
                            placeholder = "John Doe",
                            leadingIcon = Icons.Default.Person
                        )

                        // Email
                        GlassTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Institutional Email",
                            placeholder = "john.doe@hostivo.edu",
                            leadingIcon = Icons.Default.Mail,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        // Phone and Roll
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (selectedRole == "Student") {
                                GlassTextField(
                                    value = rollNumber,
                                    onValueChange = { rollNumber = it },
                                    label = "Roll Number",
                                    placeholder = "231801380007",
                                    leadingIcon = Icons.Default.Badge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            GlassTextField(
                                value = phone,
                                    onValueChange = { phone = it },
                                    label = "Phone",
                                    placeholder = "9492409574",
                                    leadingIcon = Icons.Default.Call,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    modifier = Modifier.weight(if (selectedRole == "Student") 1f else 2f)
                            )
                        }

                        if (selectedRole == "Student") {
                            // Date of Birth and Gender Selection
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = dob,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Date of Birth", color = subTextColor) },
                                    trailingIcon = {
                                        IconButton(onClick = { datePickerDialog.show() }) {
                                            Icon(Icons.Default.CalendarToday, contentDescription = "Select DOB", tint = AccentColor)
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentColor,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                        focusedTextColor = textColor,
                                        unfocusedTextColor = textColor
                                    ),
                                    modifier = Modifier.weight(1.2f).clickable { datePickerDialog.show() }
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Gender", color = subTextColor, fontSize = 11.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf("Male", "Female").forEach { g ->
                                            val isSelected = gender == g
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .background(
                                                        if (isSelected) AccentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { gender = g }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(g, color = if (isSelected) AccentColor else subTextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            // Wing & Room Selection
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Wing/Block", color = subTextColor, fontSize = 11.sp)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        blocks.forEach { b ->
                                            val isSelected = selectedBlock == b
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .background(
                                                        if (isSelected) AccentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                                                        RoundedCornerShape(8.dp)
                                                    )
                                                    .clickable { selectedBlock = b }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(b.substringAfter("Block "), color = if (isSelected) AccentColor else subTextColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = selectedRoom,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Room", color = subTextColor) },
                                        trailingIcon = {
                                            IconButton(onClick = { showRoomDropdown = true }) {
                                                Icon(Icons.Default.ExpandMore, contentDescription = "Select Room", tint = AccentColor)
                                            }
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentColor,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                            focusedTextColor = textColor,
                                            unfocusedTextColor = textColor
                                        ),
                                        modifier = Modifier.fillMaxWidth().clickable { showRoomDropdown = true }
                                    )
                                    DropdownMenu(
                                        expanded = showRoomDropdown,
                                        onDismissRequest = { showRoomDropdown = false },
                                        modifier = Modifier.background(if (isDark) Color(0xFF101415) else Color(0xFFE3EAE9)).heightIn(max = 200.dp)
                                    ) {
                                        (101..120).forEach { r ->
                                            DropdownMenuItem(
                                                text = { Text(r.toString(), color = textColor) },
                                                onClick = {
                                                    selectedRoom = r.toString()
                                                    showRoomDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Payment Preference selection
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text("Payment Choice", color = subTextColor, fontSize = 11.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Pay Now", "Upload Receipt", "Pay Later").forEach { p ->
                                        val isSelected = paymentPreference == p
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(
                                                    if (isSelected) AccentColor.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { paymentPreference = p }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(p, color = if (isSelected) AccentColor else subTextColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            if (paymentPreference == "Upload Receipt") {
                                GlassTextField(
                                    value = receiptReference,
                                    onValueChange = { receiptReference = it },
                                    label = "UTR / Reference Number",
                                    placeholder = "UTR123456789",
                                    leadingIcon = Icons.Default.Receipt
                                )

                                Button(
                                    onClick = {
                                        receiptLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                                ) {
                                    Text(if (isUploadingReceipt) "Uploading..." else "Select Receipt Image", color = Color.White)
                                }
                                Text(uploadStatus, color = if (receiptUrl.isNotEmpty()) Color(0xFF76DB8F) else subTextColor, fontSize = 11.sp)
                            }

                            // Father's Name
                            GlassTextField(
                                value = fatherName,
                                onValueChange = { fatherName = it },
                                label = "Father's Name",
                                placeholder = "C Venkatesh",
                                leadingIcon = Icons.Default.Person
                            )

                            // Emergency Phone Number
                            GlassTextField(
                                value = emergencyPhone,
                                onValueChange = { emergencyPhone = it },
                                label = "Emergency Phone Number",
                                placeholder = "9573741654",
                                leadingIcon = Icons.Default.Call,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                            )
                        }

                        // Password Field
                        GlassTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            placeholder = "••••••••",
                            leadingIcon = Icons.Default.Lock,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = image,
                                        contentDescription = "Toggle password visibility",
                                        tint = getAppSubTextColor()
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Warning message
                        if (selectedRole == "Student") {
                            Text(
                                text = "Warning: These details cannot be changed again. If any change is required, please contact the hostel Warden.",
                                color = Color(0xFFD9534F),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Submit
                        GlassButton(
                            text = "Create Account",
                            onClick = {
                                if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                                    Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                                    return@GlassButton
                                }
                                val duplicateEmail = com.example.hprams.data.HostelDataStore.students.any { it.email.lowercase() == email.trim().lowercase() }
                                if (duplicateEmail) {
                                    Toast.makeText(context, "This Email Address is already registered!", Toast.LENGTH_SHORT).show()
                                    return@GlassButton
                                }
                                if (selectedRole == "Student") {
                                    if (rollNumber.isEmpty() || fatherName.isEmpty() || emergencyPhone.isEmpty() || dob.isEmpty()) {
                                        Toast.makeText(context, "Please fill all Student fields including Date of Birth", Toast.LENGTH_SHORT).show()
                                        return@GlassButton
                                    }
                                    val duplicateRoll = com.example.hprams.data.HostelDataStore.students.any { it.roll.lowercase() == rollNumber.trim().lowercase() }
                                    if (duplicateRoll) {
                                        Toast.makeText(context, "This Roll Number is already registered!", Toast.LENGTH_SHORT).show()
                                        return@GlassButton
                                    }
                                    if (paymentPreference == "Upload Receipt" && receiptUrl.isEmpty()) {
                                        Toast.makeText(context, "Please upload the payment receipt", Toast.LENGTH_SHORT).show()
                                        return@GlassButton
                                    }
                                }
                                onSignUpClick(
                                    fullName, email, rollNumber, phone, password, fatherName, emergencyPhone, selectedRole,
                                    gender, dob, selectedBlock, selectedRoom, paymentPreference, receiptUrl, receiptReference
                                )
                            }
                        )

                        // Toggle Register
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Already have an account? ",
                                color = getAppSubTextColor(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Sign In",
                                color = AccentColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable(onClick = onSignInClick)
                            )
                        }
                    }
                }
            }
        }
    }
}
