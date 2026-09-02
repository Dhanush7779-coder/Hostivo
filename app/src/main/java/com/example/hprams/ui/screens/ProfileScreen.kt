package com.example.hprams.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.data.HostelDataStore
import com.example.hprams.ui.components.*

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
    val currentStudent = HostelDataStore.students.find { it.roll == HostelDataStore.currentStudentRoll }
    val studentName = currentStudent?.name ?: "C.Venkat"
    val studentRoll = currentStudent?.roll ?: "231801380007"
    val studentEmail = currentStudent?.email ?: "student@hostivo.edu"
    val studentPhone = currentStudent?.phone ?: "+91 94924 09574"
    val studentBlock = currentStudent?.block ?: "Block B"
    val studentRoom = currentStudent?.room ?: "102"
    val fatherName = currentStudent?.fatherName ?: "C Venkatesh"
    val emergencyPhone = currentStudent?.emergencyPhone ?: "+91 95737 41654"
    val dob = currentStudent?.dob ?: "15/08/2004"
    val gender = currentStudent?.gender ?: "Male"

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

            // Profile Header Card
            ModernSectionCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEEF2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = studentName.firstOrNull()?.toString() ?: "S",
                            color = Color(0xFF6366F1),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
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
                }
            }

            // Student Details Card
            ModernSectionCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text("Personal Information", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)

                ProfileInfoRow(label = "Email Address", value = studentEmail, icon = Icons.Default.Email)
                Divider(color = Color(0xFFF1F5F9))
                ProfileInfoRow(label = "Phone Number", value = studentPhone, icon = Icons.Default.Phone)
                Divider(color = Color(0xFFF1F5F9))
                ProfileInfoRow(label = "Gender", value = gender, icon = Icons.Default.Person)
                Divider(color = Color(0xFFF1F5F9))
                ProfileInfoRow(label = "Date of Birth", value = dob, icon = Icons.Default.CalendarToday)
            }

            // Emergency & Guardian Card
            ModernSectionCard(
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                Text("Guardian & Emergency Contact", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold)

                ProfileInfoRow(label = "Father's Name", value = fatherName, icon = Icons.Default.People)
                Divider(color = Color(0xFFF1F5F9))
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
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFF43F5E), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", color = Color(0xFFF43F5E), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
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
