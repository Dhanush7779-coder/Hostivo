package com.example.hprams.ui.screens

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.hprams.data.AnnouncementItem
import com.example.hprams.data.HostelDataStore
import com.example.hprams.ui.components.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFinanceScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val totalStudentsCount = HostelDataStore.students.count { it.role == "Student" }
    val paidStudents = HostelDataStore.students.filter { it.feePaidStatus == "Paid" && it.role == "Student" }
    val pendingStudents = HostelDataStore.students.filter { it.feePaidStatus != "Paid" && it.role == "Student" }
    
    val totalCollected = paidStudents.size * 110000
    val totalPending = pendingStudents.size * 110000
    val totalFinesCollected = HostelDataStore.fines.filter { it.status == "Paid" }.sumOf { it.amount.toIntOrNull() ?: 0 }

    // Download Clean CSV Report to Downloads Folder
    fun downloadFinancialSpreadsheet() {
        try {
            val csvContent = buildString {
                appendLine("HOSTIVO HOSTEL FINANCIAL REPORT")
                appendLine("Generated At,${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
                appendLine("Total Students,$totalStudentsCount,Total Paid,${paidStudents.size},Total Pending,${pendingStudents.size}")
                appendLine("Total Collections,INR $totalCollected,Total Pending Dues,INR $totalPending,Fines Collected,INR $totalFinesCollected")
                appendLine()
                appendLine("Student Name,Roll Number,Email,Phone,Hostel Block,Room No,Fee Status,Amount (INR),Payment Mode,Approval Status")
                HostelDataStore.students.filter { it.role == "Student" }.forEach { s ->
                    appendLine("\"${s.name}\",\"${s.roll}\",\"${s.email}\",\"${s.phone}\",\"${s.block}\",\"${s.room}\",\"${s.feePaidStatus}\",\"110000\",\"${s.paymentStatus}\",\"${s.approvalStatus}\"")
                }
                appendLine()
                appendLine("DISCIPLINARY FINES SUMMARY")
                appendLine("Fine ID,Student Roll,Reason,Amount (INR),Status")
                HostelDataStore.fines.forEach { f ->
                    appendLine("\"${f.id}\",\"${f.studentRoll}\",\"${f.reason}\",\"${f.amount}\",\"${f.status}\"")
                }
            }

            val fileName = "Hostivo_Financial_Statement_${System.currentTimeMillis()}.csv"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { stream ->
                        stream.write(csvContent.toByteArray())
                    }
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, fileName)
                file.writeText(csvContent)
            }
            Toast.makeText(context, "✅ Excel/CSV Financial Report downloaded to Downloads folder!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Download failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Financial Intelligence & Dues", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A))
                        Text("Campus revenue, payments & reminder dispatcher", fontSize = 12.sp, color = Color(0xFF64748B))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A))
                    }
                },
                actions = {
                    IconButton(onClick = { downloadFinancialSpreadsheet() }) {
                        Icon(Icons.Default.Download, contentDescription = "Download CSV", tint = Color(0xFF10B981))
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

            // Revenue KPI Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardGreenBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Total Collected", fontSize = 12.sp, color = Color(0xFF065F46), fontWeight = FontWeight.Medium)
                        Text("₹$totalCollected", fontSize = 18.sp, color = Color(0xFF065F46), fontWeight = FontWeight.ExtraBold)
                        Text("${paidStudents.size} Students Paid", fontSize = 11.sp, color = Color(0xFF047857))
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardPinkBg)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Pending Dues", fontSize = 12.sp, color = Color(0xFF9F1239), fontWeight = FontWeight.Medium)
                        Text("₹$totalPending", fontSize = 18.sp, color = Color(0xFF9F1239), fontWeight = FontWeight.ExtraBold)
                        Text("${pendingStudents.size} Unpaid", fontSize = 11.sp, color = Color(0xFFBE123C))
                    }
                }
            }

            // Automated Reminder Dispatcher
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Automated Dues Notification Dispatcher", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                    Text(
                        "Broadcast an urgent payment alert directly into the personal feed of all ${pendingStudents.size} students with unpaid dues.",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp
                    )

                    Button(
                        onClick = {
                            if (pendingStudents.isEmpty()) {
                                Toast.makeText(context, "All students have paid! No pending dues.", Toast.LENGTH_SHORT).show()
                            } else {
                                val dueNotice = AnnouncementItem(
                                    id = "FEE-${(1000..9999).random()}",
                                    title = "[FEE OVERDUE] Urgent Hostel Semester Fee Clearance",
                                    category = "FINANCE NOTICE",
                                    date = "Today",
                                    content = "Important: Semester hostel fee payment of Rs. 1,10,000 is overdue. Please pay via the Fee Portal to avoid late clearance penalties.",
                                    targetHostel = "All"
                                )
                                HostelDataStore.saveAnnouncement(dueNotice)
                                Toast.makeText(context, "✅ Payment notification sent to ${pendingStudents.size} students!", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send Fee Reminder Notice (${pendingStudents.size} Students)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Export to Excel / CSV Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Export Financial Records", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                    Text("Generate a comprehensive spreadsheet with student details, payments, mode of payment, and disciplinary fines.", color = Color(0xFF64748B), fontSize = 12.sp)

                    Button(
                        onClick = { downloadFinancialSpreadsheet() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download Real Excel/CSV Statement", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Unpaid Students Roster
            Text("Students with Pending Fees", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
            if (pendingStudents.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Text("No pending fees! All students are paid up to date.", modifier = Modifier.padding(16.dp), color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            } else {
                pendingStudents.forEach { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(s.name, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), fontSize = 14.sp)
                                Text("${s.roll} • ${s.block}, Rm ${s.room}", color = Color(0xFF64748B), fontSize = 12.sp)
                                Text("Ph: ${s.phone}", color = Color(0xFF334155), fontSize = 11.sp)
                            }
                            Text("₹1,10,000", fontWeight = FontWeight.ExtraBold, color = Color(0xFFEF4444), fontSize = 14.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
