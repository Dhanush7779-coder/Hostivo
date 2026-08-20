package com.example.hprams.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.*
import com.example.hprams.data.HostelDataStore
import com.example.hprams.theme.isAppDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardenReportsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    val subTextColor = getAppSubTextColor()

    var showCSVPreview by remember { mutableStateOf(false) }
    var generatedCSV by remember { mutableStateOf("") }

    // Generate CSV string representing Excel export scoped to Warden Gender wing (Section 11)
    fun exportWardenReportToCSV() {
        val targetGender = if (HostelDataStore.currentWardenScope == "Girls") "Female" else "Male"
        val filteredStudents = HostelDataStore.students.filter { it.gender == targetGender }

        val builder = java.lang.StringBuilder()
        builder.append("ROLL NUMBER,NAME,EMAIL,PHONE,GENDER,BLOCK,ROOM,FEES STATUS\n")
        filteredStudents.forEach { std ->
            builder.append("${std.roll},${std.name},${std.email},${std.phone},${std.gender},${std.block},${std.room},${std.feePaidStatus}\n")
        }
        generatedCSV = builder.toString()
        showCSVPreview = true
        com.example.hprams.util.CsvDownloader.downloadCsv(context, "warden_wing_roster_${System.currentTimeMillis()}.csv", generatedCSV)
    }

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "WARDEN REPORTS & EXPORTS",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = textColor)
                        }
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
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Warden Wing Overview",
                            color = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Divider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f))
                        Text(
                            "Warden Duty Scope: ${HostelDataStore.currentWardenScope} Wing Portal",
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            "Complaints log check: All resolved tickets logged cleanly",
                            color = textColor,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { exportWardenReportToCSV() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isDark) Color(0xFF003735) else Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export Wing Roster to Excel (CSV)", color = if (isDark) Color(0xFF003735) else Color.White)
                        }
                    }
                }

                if (showCSVPreview) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "CSV File Export Preview (hprams_warden_wing_export.csv):",
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isDark) Color.Black.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.7f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Text(
                                    generatedCSV,
                                    color = textColor,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
