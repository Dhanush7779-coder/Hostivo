package com.example.hprams.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

// Student profile model containing fatherName and emergencyPhone fields
data class StudentProfile(
    val roll: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val gender: String = "",
    val block: String = "",
    val room: String = "",
    var feePaidStatus: String = "Pending",
    var paymentStatus: String = "Pending",
    var approvalStatus: String = "Pending",
    val fatherName: String = "",
    val emergencyPhone: String = "",
    val role: String = "Student", // "Student", "Warden", "Security"
    val dob: String = "",
    var profileImageUrl: String = ""
)

data class RoomChangeRequest(
    val id: String = "",
    val studentRoll: String = "",
    val studentName: String = "",
    val currentRoom: String = "",
    val requestedRoom: String = "",
    val gender: String = "",
    var status: String = "Pending",
    var rejectReason: String = "",
    val refundOption: String = "",
    val refundDetails: String = ""
)

data class ComplaintTicket(
    val id: String = "",
    val studentName: String = "",
    val title: String = "",
    val category: String = "",
    val description: String = "",
    var status: String = "Pending",
    val date: String = "",
    val gender: String = "",
    var assignedHandyman: String = "",
    val imageUrl: String = ""
)

data class GatePassRequest(
    val id: String = "",
    val studentName: String = "",
    val studentRoll: String = "",
    val type: String = "",
    val gender: String = "",
    var wardenApproval: String = "Pending",
    val outDate: String = "",
    val outTime: String = "",
    val inDate: String = "",
    val inTime: String = "",
    val parentName: String = "",
    val parentPhone: String = "",
    val placeOfGoing: String = "",
    val reason: String = "",
    var checkoutTime: String = "--:--",
    var checkinTime: String = "--:--",
    var lateRemarks: String = "",
    var isLate: Boolean = false
)

data class AnnouncementItem(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val date: String = "",
    val content: String = "",
    val targetHostel: String = "All"
)

data class FineItem(
    val id: String = "",
    val studentRoll: String = "",
    val reason: String = "",
    val amount: String = "",
    var status: String = "Unpaid"
)

data class PaymentItem(
    val paymentId: String = "",
    val studentId: String = "",
    val amount: String = "",
    val currency: String = "INR",
    val paymentMethod: String = "",
    val paymentType: String = "", // "RAZORPAY" or "EXTERNAL"
    val paymentStatus: String = "PENDING", // PENDING, UNDER_VERIFICATION, PAID, FAILED, REJECTED, REFUNDED
    val razorpayOrderId: String = "",
    val razorpayPaymentId: String = "",
    val razorpaySignatureReference: String = "",
    val paymentReference: String = "",
    val receiptUrl: String = "",
    val paymentDate: String = "",
    val verifiedBy: String = "",
    val verifiedAt: String = "",
    val rejectionReason: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

data class NotificationItem(
    val id: String = "",
    val userId: String = "", // roll or "Warden" or "Admin"
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val timestamp: String = "",
    val deepLink: String = "",
    var isRead: Boolean = false
)

object HostelDataStore {
    // Razorpay listener callbacks
    var onPaymentSuccessCallback by mutableStateOf<((String) -> Unit)?>(null)
    var onPaymentErrorCallback by mutableStateOf<((Int, String) -> Unit)?>(null)

    // Current logged-in user context
    var currentRole by mutableStateOf("Admin")
    var currentStudentRoll by mutableStateOf("")
    var prefilledEmail by mutableStateOf("")
    var prefilledName by mutableStateOf("")

    // Warden Scoped
    var currentWardenScope by mutableStateOf("Boys")

    // Security Settings
    var securityBlockAssignment by mutableStateOf("")
    var securityDutyTimings by mutableStateOf("")
    var securityIsPresentToday by mutableStateOf(false)
    var securityOfficerName by mutableStateOf("")

    // Master configuration settings with only Admin
    var chiefWardenName by mutableStateOf("")
    var chiefWardenPhone by mutableStateOf("")
    var blockAWardenName by mutableStateOf("")
    var blockAWardenPhone by mutableStateOf("")
    var blockBWardenName by mutableStateOf("")
    var blockBWardenPhone by mutableStateOf("")
    var adminName by mutableStateOf("C.Venkat Dhanush")

    // Mess Timings
    var tiffinTiming by mutableStateOf("")
    var lunchTiming by mutableStateOf("")
    var dinnerTiming by mutableStateOf("")

    // Room Sharing Fees (Non-AC)
    var fee5Sharing by mutableStateOf("Rs. 40,000 / Sem (Rs. 80,000/Yr)")
    var fee4Sharing by mutableStateOf("Rs. 45,000 / Sem (Rs. 90,000/Yr)")
    var fee3Sharing by mutableStateOf("Rs. 50,000 / Sem (Rs. 1,00,000/Yr)")
    var fee2Sharing by mutableStateOf("Rs. 55,000 / Sem (Rs. 1,10,000/Yr)")

    // Room Sharing Fees (AC)
    var fee5SharingAC by mutableStateOf("Rs. 45,000 / Sem (Rs. 90,000/Yr)")
    var fee4SharingAC by mutableStateOf("Rs. 50,000 / Sem (Rs. 1,00,000/Yr)")
    var fee3SharingAC by mutableStateOf("Rs. 55,000 / Sem (Rs. 1,10,000/Yr)")
    var fee2SharingAC by mutableStateOf("Rs. 60,000 / Sem (Rs. 1,20,000/Yr)")

    // Master Collections
    val students = mutableStateListOf<StudentProfile>()
    val roomChangeRequests = mutableStateListOf<RoomChangeRequest>()
    val complaints = mutableStateListOf<ComplaintTicket>()
    val gatePassRequests = mutableStateListOf<GatePassRequest>()
    val announcements = mutableStateListOf<AnnouncementItem>()
    val fines = mutableStateListOf<FineItem>()
    val payments = mutableStateListOf<PaymentItem>()
    val notifications = mutableStateListOf<NotificationItem>()

    val handymen = listOf(
        "Electrician Ramesh",
        "Plumber Suresh",
        "Carpenter Vignesh",
        "Mason Karthik"
    )

    // Permissions check flag to ask only once
    var permissionsAsked by mutableStateOf(false)

    // Firebase Database Reference
    private val database = FirebaseDatabase.getInstance()
    private val dbRef = database.reference

    fun getStudent(roll: String): StudentProfile? {
        return students.find { it.roll == roll }
    }

    private var cacheDatabase: com.example.hprams.data.local.CacheDatabase? = null

    // Sync functions to synchronize lists with Firebase Database in real time
    fun initializeSync(context: android.content.Context) {
        cacheDatabase = com.example.hprams.data.local.CacheDatabase.getDatabase(context)
        loadFromLocalCache()

        // Sync Students
        dbRef.child("students").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                students.clear()
                val list = mutableListOf<StudentProfile>()
                for (child in snapshot.children) {
                    child.getValue(StudentProfile::class.java)?.let { 
                        students.add(it)
                        list.add(it)
                    }
                }
                saveStudentsToCache(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Room Change Requests
        dbRef.child("roomChangeRequests").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                roomChangeRequests.clear()
                for (child in snapshot.children) {
                    child.getValue(RoomChangeRequest::class.java)?.let { roomChangeRequests.add(it) }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Complaints
        dbRef.child("complaints").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                complaints.clear()
                val list = mutableListOf<ComplaintTicket>()
                for (child in snapshot.children) {
                    child.getValue(ComplaintTicket::class.java)?.let { 
                        complaints.add(it)
                        list.add(it)
                    }
                }
                saveComplaintsToCache(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Gate Pass Requests
        dbRef.child("gatePassRequests").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gatePassRequests.clear()
                for (child in snapshot.children) {
                    child.getValue(GatePassRequest::class.java)?.let { gatePassRequests.add(it) }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Announcements
        dbRef.child("announcements").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isInitial = announcements.isEmpty()
                announcements.clear()
                val list = mutableListOf<AnnouncementItem>()
                for (child in snapshot.children) {
                    child.getValue(AnnouncementItem::class.java)?.let { 
                        announcements.add(it)
                        list.add(it)
                    }
                }
                saveAnnouncementsToCache(list)
                if (!isInitial && list.isNotEmpty()) {
                    val latest = list.last()
                    triggerLocalNotification(context, "Announcement: ${latest.title}", latest.content)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Fines
        dbRef.child("fines").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fines.clear()
                for (child in snapshot.children) {
                    child.getValue(FineItem::class.java)?.let { fines.add(it) }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Payments
        dbRef.child("payments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                payments.clear()
                val list = mutableListOf<PaymentItem>()
                for (child in snapshot.children) {
                    child.getValue(PaymentItem::class.java)?.let { 
                        payments.add(it)
                        list.add(it)
                    }
                }
                savePaymentsToCache(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Sync Notifications
        dbRef.child("notifications").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isInitial = notifications.isEmpty()
                notifications.clear()
                val list = mutableListOf<NotificationItem>()
                for (child in snapshot.children) {
                    child.getValue(NotificationItem::class.java)?.let { 
                        notifications.add(it)
                        list.add(it)
                    }
                }
                saveNotificationsToCache(list)
                if (!isInitial && list.isNotEmpty()) {
                    val latest = list.last()
                    val targetUser = if (currentRole == "Student") currentStudentRoll else currentRole
                    if (latest.userId == "All" || latest.userId == targetUser) {
                        triggerLocalNotification(context, latest.title, latest.message)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadFromLocalCache() {
        val dao = cacheDatabase?.cacheDao() ?: return
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val cachedStudents = dao.getCachedUsers().map {
                    StudentProfile(
                        roll = it.roll,
                        name = it.name,
                        email = it.email,
                        phone = it.phone,
                        gender = it.gender,
                        block = it.block,
                        room = it.room,
                        feePaidStatus = it.feePaidStatus,
                        paymentStatus = it.paymentStatus,
                        approvalStatus = it.approvalStatus,
                        fatherName = it.fatherName,
                        emergencyPhone = it.emergencyPhone,
                        role = it.role,
                        dob = it.dob
                    )
                }
                if (cachedStudents.isNotEmpty()) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        students.clear()
                        students.addAll(cachedStudents)
                    }
                }
                
                val cachedPayments = dao.getCachedPayments().map {
                    PaymentItem(
                        paymentId = it.paymentId,
                        studentId = it.studentId,
                        amount = it.amount,
                        currency = it.currency,
                        paymentMethod = it.paymentMethod,
                        paymentType = it.paymentType,
                        paymentStatus = it.paymentStatus,
                        razorpayOrderId = it.razorpayOrderId,
                        razorpayPaymentId = it.razorpayPaymentId,
                        razorpaySignatureReference = it.razorpaySignatureReference,
                        paymentReference = it.paymentReference,
                        receiptUrl = it.receiptUrl,
                        paymentDate = it.paymentDate,
                        verifiedBy = it.verifiedBy,
                        verifiedAt = it.verifiedAt,
                        rejectionReason = it.rejectionReason,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
                if (cachedPayments.isNotEmpty()) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        payments.clear()
                        payments.addAll(cachedPayments)
                    }
                }

                val cachedComplaints = dao.getCachedComplaints().map {
                    ComplaintTicket(
                        id = it.id,
                        studentName = it.studentName,
                        title = it.title,
                        category = it.category,
                        description = it.description,
                        status = it.status,
                        date = it.date,
                        gender = it.gender,
                        assignedHandyman = it.assignedHandyman,
                        imageUrl = it.imageUrl
                    )
                }
                if (cachedComplaints.isNotEmpty()) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        complaints.clear()
                        complaints.addAll(cachedComplaints)
                    }
                }

                val cachedAnnouncements = dao.getCachedAnnouncements().map {
                    AnnouncementItem(
                        id = it.id,
                        title = it.title,
                        category = it.category,
                        date = it.date,
                        content = it.content,
                        targetHostel = it.targetHostel
                    )
                }
                if (cachedAnnouncements.isNotEmpty()) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        announcements.clear()
                        announcements.addAll(cachedAnnouncements)
                    }
                }

                val cachedNotifications = dao.getCachedNotifications().map {
                    NotificationItem(
                        id = it.id,
                        userId = it.userId,
                        title = it.title,
                        message = it.message,
                        type = it.type,
                        timestamp = it.timestamp,
                        deepLink = it.deepLink,
                        isRead = it.isRead
                    )
                }
                if (cachedNotifications.isNotEmpty()) {
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        notifications.clear()
                        notifications.addAll(cachedNotifications)
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    private fun saveStudentsToCache(list: List<StudentProfile>) {
        val dao = cacheDatabase?.cacheDao() ?: return
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val entities = list.map {
                com.example.hprams.data.local.UserEntity(
                    roll = it.roll,
                    name = it.name,
                    email = it.email,
                    phone = it.phone,
                    gender = it.gender,
                    block = it.block,
                    room = it.room,
                    feePaidStatus = it.feePaidStatus,
                    paymentStatus = it.paymentStatus,
                    approvalStatus = it.approvalStatus,
                    fatherName = it.fatherName,
                    emergencyPhone = it.emergencyPhone,
                    role = it.role,
                    dob = it.dob
                )
            }
            dao.insertUsers(entities)
        }
    }

    private fun saveComplaintsToCache(list: List<ComplaintTicket>) {
        val dao = cacheDatabase?.cacheDao() ?: return
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val entities = list.map {
                com.example.hprams.data.local.ComplaintEntity(
                    id = it.id,
                    studentName = it.studentName,
                    title = it.title,
                    category = it.category,
                    description = it.description,
                    status = it.status,
                    date = it.date,
                    gender = it.gender,
                    assignedHandyman = it.assignedHandyman,
                    imageUrl = it.imageUrl
                )
            }
            dao.insertComplaints(entities)
        }
    }

    private fun saveAnnouncementsToCache(list: List<AnnouncementItem>) {
        val dao = cacheDatabase?.cacheDao() ?: return
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val entities = list.map {
                com.example.hprams.data.local.AnnouncementEntity(
                    id = it.id,
                    title = it.title,
                    category = it.category,
                    date = it.date,
                    content = it.content,
                    targetHostel = it.targetHostel
                )
            }
            dao.insertAnnouncements(entities)
        }
    }

    private fun savePaymentsToCache(list: List<PaymentItem>) {
        val dao = cacheDatabase?.cacheDao() ?: return
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val entities = list.map {
                com.example.hprams.data.local.PaymentEntity(
                    paymentId = it.paymentId,
                    studentId = it.studentId,
                    amount = it.amount,
                    currency = it.currency,
                    paymentMethod = it.paymentMethod,
                    paymentType = it.paymentType,
                    paymentStatus = it.paymentStatus,
                    razorpayOrderId = it.razorpayOrderId,
                    razorpayPaymentId = it.razorpayPaymentId,
                    razorpaySignatureReference = it.razorpaySignatureReference,
                    paymentReference = it.paymentReference,
                    receiptUrl = it.receiptUrl,
                    paymentDate = it.paymentDate,
                    verifiedBy = it.verifiedBy,
                    verifiedAt = it.verifiedAt,
                    rejectionReason = it.rejectionReason,
                    createdAt = it.createdAt,
                    updatedAt = it.updatedAt
                )
            }
            dao.insertPayments(entities)
        }
    }

    private fun saveNotificationsToCache(list: List<NotificationItem>) {
        val dao = cacheDatabase?.cacheDao() ?: return
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            val entities = list.map {
                com.example.hprams.data.local.NotificationEntity(
                    id = it.id,
                    userId = it.userId,
                    title = it.title,
                    message = it.message,
                    type = it.type,
                    timestamp = it.timestamp,
                    deepLink = it.deepLink,
                    isRead = it.isRead
                )
            }
            dao.insertNotifications(entities)
        }
    }

    // Write helper functions
    fun saveStudent(profile: StudentProfile) {
        // Immediate local state update
        val existingIndex = students.indexOfFirst { it.roll == profile.roll }
        if (existingIndex >= 0) {
            students[existingIndex] = profile
        } else {
            students.add(0, profile)
        }
        // Save to Room Cache
        saveStudentsToCache(students.toList())
        // Push to Firebase Realtime Database
        dbRef.child("students").child(profile.roll).setValue(profile)
    }

    fun saveRoomChange(request: RoomChangeRequest) {
        val idx = roomChangeRequests.indexOfFirst { it.id == request.id }
        if (idx >= 0) roomChangeRequests[idx] = request else roomChangeRequests.add(0, request)
        dbRef.child("roomChangeRequests").child(request.id).setValue(request)
    }

    fun saveComplaint(ticket: ComplaintTicket) {
        val idx = complaints.indexOfFirst { it.id == ticket.id }
        if (idx >= 0) complaints[idx] = ticket else complaints.add(0, ticket)
        dbRef.child("complaints").child(ticket.id).setValue(ticket)
    }

    fun saveGatePass(request: GatePassRequest) {
        val idx = gatePassRequests.indexOfFirst { it.id == request.id }
        if (idx >= 0) gatePassRequests[idx] = request else gatePassRequests.add(0, request)
        dbRef.child("gatePassRequests").child(request.id).setValue(request)
    }

    fun saveAnnouncement(announcement: AnnouncementItem) {
        val idx = announcements.indexOfFirst { it.id == announcement.id }
        if (idx >= 0) announcements[idx] = announcement else announcements.add(0, announcement)
        dbRef.child("announcements").child(announcement.id).setValue(announcement)
    }

    fun saveFine(fine: FineItem) {
        val idx = fines.indexOfFirst { it.id == fine.id }
        if (idx >= 0) fines[idx] = fine else fines.add(0, fine)
        dbRef.child("fines").child(fine.id).setValue(fine)
    }

    fun savePayment(payment: PaymentItem) {
        val idx = payments.indexOfFirst { it.paymentId == payment.paymentId }
        if (idx >= 0) payments[idx] = payment else payments.add(0, payment)
        dbRef.child("payments").child(payment.paymentId).setValue(payment)
    }

    fun saveNotification(notification: NotificationItem, context: android.content.Context? = null) {
        dbRef.child("notifications").child(notification.id).setValue(notification)
        
        // Trigger system notification if context is provided and user matches
        if (context != null) {
            val currentTarget = if (currentRole == "Student") currentStudentRoll else currentRole
            if (notification.userId == "All" || notification.userId == currentTarget) {
                triggerLocalNotification(context, notification.title, notification.message)
            }
        }
    }

    private fun triggerLocalNotification(context: android.content.Context, title: String, message: String) {
        val channelId = "hprams_notifications"
        val manager = context.getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(channelId, "Hostel Alerts", android.app.NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        val builder = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        
        manager.notify((1000..9999).random(), builder.build())
    }
}
