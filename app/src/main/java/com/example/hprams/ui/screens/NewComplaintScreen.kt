package com.example.hprams.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hprams.ui.components.GlassBackground
import com.example.hprams.ui.components.GlassButton
import com.example.hprams.ui.components.GlassCard
import com.example.hprams.ui.components.GlassTextField
import com.example.hprams.data.HostelDataStore
import com.example.hprams.data.ComplaintTicket
import com.google.firebase.storage.FirebaseStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewComplaintScreen(
    onBackClick: () -> Unit,
    onSubmitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var uploadStatus by remember { mutableStateOf("No image attached") }
    var isUploading by remember { mutableStateOf(false) }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                isUploading = true
                uploadStatus = "Uploading image..."
                val storageRef = FirebaseStorage.getInstance().reference
                    .child("complaints/CMP-${System.currentTimeMillis()}.jpg")
                storageRef.putFile(uri)
                    .addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                            imageUrl = downloadUrl.toString()
                            uploadStatus = "Image uploaded successfully!"
                            isUploading = false
                            Toast.makeText(context, "Complaint image attached!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        uploadStatus = "Upload failed: ${e.message}"
                        isUploading = false
                        Toast.makeText(context, "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    )

    GlassBackground(modifier = modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "NEW COMPLAINT",
                            color = Color(0xFF29FCF3),
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassTextField(
                            value = category,
                            onValueChange = { category = it },
                            label = "Category (Electrical, Plumbing, Wi-Fi)"
                        )

                        GlassTextField(
                            value = roomNumber,
                            onValueChange = { roomNumber = it },
                            label = "Room Number"
                        )

                        GlassTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Detailed Description"
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = uploadStatus,
                            color = if (imageUrl.isNotEmpty()) Color(0xFF76DB8F) else Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )

                        GlassButton(
                            text = if (isUploading) "Uploading..." else "Attach Image",
                            onClick = {
                                photoLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            icon = {
                                Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White)
                            }
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        GlassButton(
                            text = "Submit Ticket",
                            onClick = {
                                if (category.isEmpty() || description.isEmpty()) {
                                    Toast.makeText(context, "Category and description are required", Toast.LENGTH_SHORT).show()
                                    return@GlassButton
                                }
                                 val newTicket = ComplaintTicket(
                                    id = "CMP-${(1000..9999).random()}",
                                    studentName = HostelDataStore.getStudent(HostelDataStore.currentStudentRoll)?.name ?: "Student",
                                    title = "$category Request",
                                    category = category.uppercase(),
                                    description = description,
                                    status = "Pending",
                                    date = "18 Aug 2026",
                                    gender = HostelDataStore.getStudent(HostelDataStore.currentStudentRoll)?.gender ?: "Male",
                                    imageUrl = imageUrl
                                )
                                HostelDataStore.saveComplaint(newTicket)
                                
                                val notification = com.example.hprams.data.NotificationItem(
                                    id = "NTF-${(1000..9999).random()}",
                                    userId = "Warden",
                                    title = "New Complaint: ${newTicket.category}",
                                    message = "${newTicket.studentName} (Room ${roomNumber}) submitted a new complaint request.",
                                    type = "COMPLAINT",
                                    timestamp = "18 Aug 2026",
                                    deepLink = "warden_dashboard"
                                )
                                HostelDataStore.saveNotification(notification, context)
                                onSubmitClick()
                            },
                            icon = {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                            }
                        )
                    }
                }
            }
        }
    }
}
