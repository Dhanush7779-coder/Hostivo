package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.hprams.data.FineItem
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.PaymentItem
import com.example.hprams.ui.components.*
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
                paymentDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            )
            HostelDataStore.savePayment(newPayment)
            currentStudent?.let { s ->
                s.feePaidStatus = "Paid"
                s.paymentStatus = "Paid"
                HostelDataStore.saveStudent(s)
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

    fun startRazorpayPayment(amountStr: String) {
        try {
            val amountInPaise = (amountStr.toLongOrNull() ?: 80000L) * 100
            val activity = context as? android.app.Activity ?: return
            val checkout = com.razorpay.Checkout()
            checkout.setKeyID("rzp_test_YourKeyHere") // Test key

            val options = JSONObject()
            options.put("name", "Hostivo Student Portal")
            options.put("description", "Hostel Semester Fee Payment")
            options.put("currency", "INR")
            options.put("amount", amountInPaise)
            options.put("prefill.email", currentStudent?.email ?: "student@hostivo.edu")
            options.put("prefill.contact", currentStudent?.phone ?: "9876543210")

            checkout.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(context, "Razorpay Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

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
                title = "Finance & Payments",
                subtitle = "Manage fees, dues & external receipts",
                onBackClick = onHomeClick
            )

            // 2. Semester Fee Summary Card
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
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Semester Fee Summary",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "2026-2027",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("FEE AMOUNT DUE", color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Rs. 80,000", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Text("/ Semester", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val feePaid = currentStudent?.feePaidStatus ?: "Pending"
                            val isPaid = feePaid.equals("Paid", ignoreCase = true)
                            Text("PAYMENT STATUS", color = Color.White.copy(alpha = 0.75f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isPaid) Color(0xFF10B981) else Color(0xFFF59E0B))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = feePaid.uppercase(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.2f))

                    // Action Buttons: Pay Now & Paid Outside
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { startRazorpayPayment("80000") },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pay Now", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showExternalUploadForm = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Paid Outside", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // 3. Payment History Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Payment History",
                    color = Color(0xFF0F172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                if (studentPayments.isEmpty()) {
                    ModernSectionCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No payment history records found.", color = Color(0xFF64748B), fontSize = 13.sp)
                        }
                    }
                } else {
                    studentPayments.forEach { payment ->
                        ModernSectionCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEEF2FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Rs. ${payment.amount}", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${payment.paymentMethod} • ${payment.paymentDate.ifEmpty { "Recently" }}", color = Color(0xFF64748B), fontSize = 11.sp)
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (payment.paymentStatus == "PAID") Color(0xFFECFDF5) else Color(0xFFFEF3C7))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = payment.paymentStatus,
                                        color = if (payment.paymentStatus == "PAID") Color(0xFF10B981) else Color(0xFFD97706),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Hostel Fines & Penalties Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Hostel Fines & Penalties",
                    color = Color(0xFF0F172A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                if (studentFines.isEmpty()) {
                    ModernSectionCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No outstanding fines or penalties.", color = Color(0xFF64748B), fontSize = 13.sp)
                        }
                    }
                } else {
                    studentFines.forEach { fine ->
                        ModernSectionCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFF1F2)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Gavel, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(fine.reason, color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(fine.amount, color = Color(0xFFF43F5E), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    }
                                }

                                if (fine.status == "Unpaid") {
                                    Button(
                                        onClick = {
                                            fine.status = "Paid"
                                            Toast.makeText(context, "Fine marked as paid!", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text("Pay", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFECFDF5))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("PAID", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
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
                selectedId = "finance",
                onSelect = { id ->
                    when (id) {
                        "home" -> onHomeClick()
                        "rooms" -> onRoomsClick()
                        "community" -> onSupportClick()
                    }
                }
            )
        }
    }

    // Modal Sheet / Dialog for Paid Outside / Upload Receipt
    if (showExternalUploadForm) {
        AlertDialog(
            onDismissRequest = { showExternalUploadForm = false },
            title = {
                Text("Submit External Fee Receipt", fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    HostivoTextField(
                        value = extAmount,
                        onValueChange = { extAmount = it },
                        label = "Amount Paid (INR)",
                        placeholder = "80000"
                    )

                    HostivoTextField(
                        value = extReference,
                        onValueChange = { extReference = it },
                        label = "Transaction / UTR Reference",
                        placeholder = "UTR1234567890"
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
                        Text(if (isUploadingReceipt) "Uploading..." else "Attach Receipt Photo")
                    }

                    Text(
                        text = uploadReceiptStatus,
                        color = if (extReceiptUrl.isNotEmpty()) Color(0xFF16A34A) else Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (extReference.isBlank()) {
                            Toast.makeText(context, "Please enter transaction reference number", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val newPayment = PaymentItem(
                            paymentId = "EXT-${(1000..9999).random()}",
                            studentId = HostelDataStore.currentStudentRoll,
                            amount = extAmount,
                            paymentMethod = "External ($extMethod)",
                            paymentType = "EXTERNAL",
                            paymentStatus = "UNDER_VERIFICATION",
                            paymentReference = extReference,
                            receiptUrl = extReceiptUrl,
                            paymentDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                        )
                        HostelDataStore.savePayment(newPayment)
                        Toast.makeText(context, "Receipt submitted for Admin verification!", Toast.LENGTH_LONG).show()
                        showExternalUploadForm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("Submit Receipt", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExternalUploadForm = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
