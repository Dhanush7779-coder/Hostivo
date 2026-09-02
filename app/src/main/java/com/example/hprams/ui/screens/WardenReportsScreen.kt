package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.hprams.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenReportsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCSVPreview by remember { mutableStateOf(false) }
    var generatedCSV by remember { mutableStateOf("") }

    val targetGender = if (HostelDataStore.currentWardenScope == "Girls") "Female" else "Male"
    val filteredStudents = HostelDataStore.students.filter { it.gender == targetGender }

    fun exportWardenReportToCSV() {
        val builder = java.lang.StringBuilder()
        builder.append("ROLL NUMBER,NAME,EMAIL,PHONE,GENDER,BLOCK,ROOM,FEES STATUS\n")
        filteredStudents.forEach { std ->
            builder.append("${std.roll},${std.name},${std.email},${std.phone},${std.gender},${std.block},${std.room},${std.feePaidStatus}\n")
        }
        generatedCSV = builder.toString()
        showCSVPreview = true
        com.example.hprams.util.CsvDownloader.downloadCsv(context, "warden_wing_roster_${System.currentTimeMillis()}.csv", generatedCSV)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ModernBgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            ModernSubPageHeader(
                title = "Warden Reports & Exports",
                subtitle = "Student roster & residency analytics",
                onBackClick = onBackClick
            )

            // Wing Overview
            ModernSectionCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text("Warden Wing Overview", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active Scope", color = Color(0xFF64748B), fontSize = 12.sp)
                        Text("${HostelDataStore.currentWardenScope} Hostel", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Registered Residents", color = Color(0xFF64748B), fontSize = 12.sp)
                        Text("${filteredStudents.size} Students", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // Export Action Card
            ModernSectionCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFECFDF5)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Export Wing Roster (CSV/Excel)", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Download full occupant list with room details & dues", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = { exportWardenReportToCSV() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Download Resident CSV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            if (showCSVPreview) {
                ModernSectionCard(
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Text("CSV Export Preview", color = Color(0xFF0F172A), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = generatedCSV.take(300) + if (generatedCSV.length > 300) "..." else "",
                        color = Color(0xFF475569),
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
