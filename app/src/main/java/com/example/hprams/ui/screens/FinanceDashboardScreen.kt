package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
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
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.FineItem

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

    // Sync from state store using derivedStateOf
    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentFines by remember { derivedStateOf { HostelDataStore.fines.filter { it.studentRoll == HostelDataStore.currentStudentRoll } } }

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
                    // Main Fee Details Card
                    item {
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    "Semester Fee Summary",
                                    color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Fee Paid Status", color = subTextColor)
                                    Text(
                                        text = currentStudent?.feePaidStatus ?: "Pending",
                                        color = if (currentStudent?.feePaidStatus == "Paid") Color(0xFF76DB8F) else Color(0xFFFFB4AB),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Payment Transaction", color = subTextColor)
                                    Text(currentStudent?.paymentStatus ?: "Pending", color = textColor, fontWeight = FontWeight.Bold)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Approval Status", color = subTextColor)
                                    Text(currentStudent?.approvalStatus ?: "Pending", color = textColor, fontWeight = FontWeight.Bold)
                                }

                                if (currentStudent?.feePaidStatus != "Paid") {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    GlassButton(
                                        text = "Pay Semester Fee",
                                        onClick = {
                                            currentStudent?.feePaidStatus = "Paid"
                                            currentStudent?.paymentStatus = "Success"
                                            currentStudent?.approvalStatus = "Approved by Admin"
                                            Toast.makeText(context, "Semester fee paid successfully!", Toast.LENGTH_SHORT).show()
                                        },
                                        icon = { Icon(Icons.Default.Payment, contentDescription = null, tint = if (isDark) Color(0xFF003735) else Color.White) }
                                    )
                                }
                            }
                        }
                    }

                    // Fines Section
                    item {
                        Text(
                            "Hostel Fines / Penalties",
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
}
