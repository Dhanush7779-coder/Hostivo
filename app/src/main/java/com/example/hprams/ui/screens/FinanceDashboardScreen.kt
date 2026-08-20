package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.*
import com.example.hprams.theme.isAppDarkTheme
import com.example.hprams.theme.AccentColor
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.PaymentItem
import com.example.hprams.data.FineItem
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceDashboardScreen(
    onHomeClick: () -> Unit,
    onRoomsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()
    val context = LocalContext.current

    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentFines by remember { derivedStateOf { HostelDataStore.fines.filter { it.studentRoll == HostelDataStore.currentStudentRoll } } }
    val studentPayments by remember { derivedStateOf { HostelDataStore.payments.filter { it.studentId == HostelDataStore.currentStudentRoll } } }

    var showExternalUploadForm by remember { mutableStateOf(false) }
    var showReceiptDialog by remember { mutableStateOf<PaymentItem?>(null) }

    // External Receipt Fields
    var extAmount by remember { mutableStateOf("80000") }
    var extMethod by remember { mutableStateOf("UPI") }
    var extReference by remember { mutableStateOf("") }
    var extRemarks by remember { mutableStateOf("") }
    var isUploadingReceipt by remember { mutableStateOf(false) }
    var uploadReceiptStatus by remember { mutableStateOf("No receipt image attached") }
    var extReceiptUrl by remember { mutableStateOf("") }

    val receiptLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                isUploadingReceipt = true
                uploadReceiptStatus = "Uploading receipt..."
                val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
                    .child("receipts/PAY-${System.currentTimeMillis()}.jpg")
                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            extReceiptUrl = downloadUrl.toString()
                            uploadReceiptStatus = "Receipt uploaded successfully!"
                            isUploadingReceipt = false
                            Toast.makeText(context, "Receipt attached successfully!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        uploadReceiptStatus = "Upload failed: ${e.message}"
                        isUploadingReceipt = false
                        Toast.makeText(context, "Receipt upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    )

    // Register Razorpay listeners
    DisposableEffect(Unit) {
        HostelDataStore.onPaymentSuccessCallback = { paymentId ->
            Toast.makeText(context, "Payment Successful: $paymentId", Toast.LENGTH_LONG).show()
            val newPayment = PaymentItem(
                paymentId = "PAY-${(1000..9999).random()}",
                studentId = HostelDataStore.currentStudentRoll,
                amount = "80000",
                paymentMethod = "Razorpay (Online)",
                paymentType = "RAZORPAY",
                paymentStatus = "PAID",
                razorpayPaymentId = paymentId,
                razorpayOrderId = "order_${(100000..999999).random()}",
                paymentDate = "18 Aug 2026",
                createdAt = "18 Aug 2026",
                updatedAt = "18 Aug 2026"
            )
            HostelDataStore.savePayment(newPayment)

            currentStudent?.let {
                val updated = it.copy(
                    feePaidStatus = "Paid",
                    paymentStatus = "Paid",
                    approvalStatus = "Approved"
                )
                HostelDataStore.saveStudent(updated)
            }
        }

        HostelDataStore.onPaymentErrorCallback = { code, response ->
            Toast.makeText(context, "Payment Failed ($code): $response", Toast.LENGTH_LONG).show()
        }

        onDispose {
            HostelDataStore.onPaymentSuccessCallback = null
            HostelDataStore.onPaymentErrorCallback = null
        }
    }

    fun launchRazorpayCheckout() {
        val activity = context as? android.app.Activity
        if (activity == null) {
            Toast.makeText(context, "Cannot find Android Activity context", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val co = com.razorpay.Checkout()
            co.setKeyID("rzp_test_TRJYyHxJqBlObH")
            
            val options = JSONObject()
            options.put("name", "Hostivo Hostels")
            options.put("description", "Hostel Fee Payment")
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg")
            options.put("theme.color", "#006A66")
            options.put("currency", "INR")
            options.put("amount", "8000000") // in paise (Rs. 80,000)
            
            val prefill = JSONObject()
            prefill.put("email", currentStudent?.email ?: "ammananasanju@gmail.com")
            prefill.put("contact", currentStudent?.phone ?: "9492409574")
            options.put("prefill", prefill)


            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(context, "Razorpay checkout error: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "FINANCE & PAYMENTS",
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
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp)
                ) {
                    // Fee summary card
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Semester Fee Summary",
                                    color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Fee Amount Due", color = subTextColor)
                                    Text("Rs. 80,000 / Sem", color = textColor, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Fee Paid Status", color = subTextColor)
                                    Text(
                                        text = currentStudent?.feePaidStatus ?: "Pending",
                                        color = if (currentStudent?.feePaidStatus == "Paid") Color(0xFF76DB8F) else Color(0xFFFFB4AB),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Verification Status", color = subTextColor)
                                    Text(currentStudent?.approvalStatus ?: "Pending", color = textColor, fontWeight = FontWeight.Bold)
                                }

                                if (currentStudent?.feePaidStatus != "Paid") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Button(
                                            onClick = { launchRazorpayCheckout() },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                                        ) {
                                            Icon(Icons.Default.Payment, contentDescription = null, tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Pay Now", color = Color.White)
                                        }
                                        Button(
                                            onClick = { showExternalUploadForm = !showExternalUploadForm },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                                        ) {
                                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = textColor)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Paid Outside", color = textColor)
                                        }
                                    }
                                } else {
                                    // Paid, show receipt button
                                    val successfulPayment = studentPayments.find { it.paymentStatus == "PAID" }
                                    if (successfulPayment != null) {
                                        GlassButton(
                                            text = "View Digital Receipt",
                                            onClick = { showReceiptDialog = successfulPayment },
                                            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = if (isDark) Color(0xFF003735) else Color.White) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // External payment submission form
                    if (showExternalUploadForm && currentStudent?.feePaidStatus != "Paid") {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        "Submit External Payment Receipt",
                                        color = AccentColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    OutlinedTextField(
                                        value = extAmount,
                                        onValueChange = { extAmount = it },
                                        label = { Text("Amount Paid") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = extMethod,
                                        onValueChange = { extMethod = it },
                                        label = { Text("Payment Method (UPI/Bank)") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = extReference,
                                        onValueChange = { extReference = it },
                                        label = { Text("UTR / Transaction Reference ID") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = uploadReceiptStatus,
                                        color = if (extReceiptUrl.isNotEmpty()) Color(0xFF76DB8F) else Color.White.copy(alpha = 0.6f),
                                        fontSize = 12.sp
                                    )
                                    Button(
                                        onClick = {
                                            receiptLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                                    ) {
                                        Text(if (isUploadingReceipt) "Uploading Receipt..." else "Choose Receipt Image", color = Color.White)
                                    }

                                    Button(
                                        onClick = {
                                            if (extAmount.isEmpty() || extReference.isEmpty()) {
                                                Toast.makeText(context, "Amount & Reference ID are required", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val payment = PaymentItem(
                                                paymentId = "PAY-${(1000..9999).random()}",
                                                studentId = HostelDataStore.currentStudentRoll,
                                                amount = extAmount,
                                                paymentMethod = extMethod,
                                                paymentType = "EXTERNAL",
                                                paymentStatus = "UNDER_VERIFICATION",
                                                paymentReference = extReference,
                                                receiptUrl = extReceiptUrl,
                                                paymentDate = "18 Aug 2026",
                                                createdAt = "18 Aug 2026",
                                                updatedAt = "18 Aug 2026"
                                            )
                                            HostelDataStore.savePayment(payment)
                                            
                                             val notification = com.example.hprams.data.NotificationItem(
                                                 id = "NTF-${(1000..9999).random()}",
                                                 userId = "Admin",
                                                 title = "Receipt Submitted",
                                                 message = "Student ${HostelDataStore.currentStudentRoll} submitted a receipt of Rs. ${extAmount} for verification.",
                                                 type = "PAYMENT",
                                                 timestamp = "18 Aug 2026",
                                                 deepLink = "admin_dashboard"
                                             )
                                             HostelDataStore.saveNotification(notification, context)

                                            currentStudent?.let {
                                                val updated = it.copy(
                                                    feePaidStatus = "Under Verification",
                                                    paymentStatus = "Under Verification"
                                                )
                                                HostelDataStore.saveStudent(updated)
                                            }
                                            showExternalUploadForm = false
                                            Toast.makeText(context, "Receipt submitted successfully! Pending verification.", Toast.LENGTH_LONG).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Submit to Admin", color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    // Payment History Timeline
                    item {
                        Text(
                            "Payment History",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (studentPayments.isEmpty()) {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text("No payment history records found.", color = subTextColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        items(studentPayments, key = { it.paymentId }) { payment ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth().clickable { showReceiptDialog = payment }
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("ID: ${payment.paymentId}", color = textColor, fontWeight = FontWeight.Bold)
                                            Text(payment.paymentDate, color = subTextColor, fontSize = 12.sp)
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    if (payment.paymentStatus == "PAID") Color(0xFF4CAF50).copy(alpha = 0.2f) else Color(0xFFE57373).copy(alpha = 0.2f),
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                payment.paymentStatus,
                                                color = if (payment.paymentStatus == "PAID") Color(0xFF81C784) else Color(0xFFE57373),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Method: ${payment.paymentMethod}", color = subTextColor, fontSize = 12.sp)
                                        Text("Rs. ${payment.amount}", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Fines Section
                    item {
                        Text(
                            "Hostel Fines & Penalties",
                            color = textColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (studentFines.isEmpty()) {
                        item {
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Text("No outstanding fines found.", color = subTextColor, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                            }
                        }
                    } else {
                        items(studentFines, key = { it.id }) { fine ->
                            val isPaid = fine.status == "Paid"
                            GlassCard(modifier = Modifier.fillMaxWidth()) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = if (isPaid) Color(0xFF76DB8F) else Color(0xFFFFB4AB))
                                            Text(fine.reason, color = textColor, fontWeight = FontWeight.Bold)
                                        }
                                        Text(
                                            text = fine.status.uppercase(),
                                            color = if (isPaid) Color(0xFF76DB8F) else Color(0xFFFFB4AB),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(fine.amount, color = textColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        if (!isPaid) {
                                            Button(
                                                onClick = {
                                                    fine.status = "Paid"
                                                    Toast.makeText(context, "Fine of ${fine.amount} paid!", Toast.LENGTH_SHORT).show()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                                                ),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Text("Pay", color = if (isDark) Color(0xFF003735) else Color.White, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                StudentBottomBar(
                    activeTab = "finance",
                    onHomeClick = onHomeClick,
                    onRoomsClick = onRoomsClick,
                    onFinanceClick = {},
                    onProfileClick = onProfileClick,
                    onSupportClick = onSupportClick,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }

    // Receipt Detail Dialog
    showReceiptDialog?.let { payment ->
        AlertDialog(
            onDismissRequest = { showReceiptDialog = null },
            title = { Text("Fee Receipt Details", color = textColor, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Hostel: Hostivo Campus Wing", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Student Name:", color = subTextColor, fontSize = 12.sp)
                        Text(currentStudent?.name ?: "C venkat Dhanush", color = textColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Roll Number:", color = subTextColor, fontSize = 12.sp)
                        Text(payment.studentId, color = textColor, fontSize = 12.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payment ID:", color = subTextColor, fontSize = 12.sp)
                        Text(payment.paymentId, color = textColor, fontSize = 12.sp)
                    }
                    if (payment.paymentType == "RAZORPAY") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Order ID:", color = subTextColor, fontSize = 12.sp)
                            Text(payment.razorpayOrderId, color = textColor, fontSize = 12.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment UTR:", color = subTextColor, fontSize = 12.sp)
                            Text(payment.razorpayPaymentId, color = textColor, fontSize = 12.sp)
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("UTR Reference:", color = subTextColor, fontSize = 12.sp)
                            Text(payment.paymentReference, color = textColor, fontSize = 12.sp)
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Amount Paid:", color = subTextColor, fontSize = 12.sp)
                        Text("Rs. ${payment.amount}", color = AccentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payment Status:", color = subTextColor, fontSize = 12.sp)
                        Text(payment.paymentStatus, color = if (payment.paymentStatus == "PAID") Color(0xFF81C784) else Color(0xFFE57373), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReceiptDialog = null },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                ) {
                    Text("OK", color = Color.White)
                }
            },
            containerColor = if (isDark) Color(0xFF101415) else Color(0xFFF3F6F6),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
