package com.example.hprams.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf

// Data models
data class StudentProfile(
    val roll: String,
    val name: String,
    val email: String,
    val phone: String,
    val gender: String, // "Male" or "Female"
    val block: String,
    val room: String,
    var feePaidStatus: String, // "Paid" or "Pending"
    var paymentStatus: String, // "Success" or "Pending"
    var approvalStatus: String // "Approved by Admin" or "Pending"
)

data class RoomChangeRequest(
    val id: String,
    val studentRoll: String,
    val studentName: String,
    val currentRoom: String,
    val requestedRoom: String,
    val gender: String,
    var status: String, // "Pending", "Approved", "Rejected"
    var rejectReason: String = ""
)

data class ComplaintTicket(
    val id: String,
    val studentName: String,
    val title: String,
    val category: String,
    val description: String,
    var status: String, // "Pending", "Assigned", "Resolved"
    val date: String,
    val gender: String, // to segregate by hostel
    var assignedHandyman: String = ""
)

data class GatePassRequest(
    val id: String,
    val studentName: String,
    val studentRoll: String,
    val type: String, // "Outing" or "Leave"
    val gender: String,
    var wardenApproval: String, // "Pending", "Approved", "Rejected"
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
    val id: String,
    val title: String,
    val category: String, // "GENERAL NOTICE", "EMERGENCY BROADCAST"
    val date: String,
    val content: String,
    val targetHostel: String // "All", "Boys", "Girls"
)

data class FineItem(
    val id: String,
    val studentRoll: String,
    val reason: String,
    val amount: String,
    var status: String // "Unpaid" or "Paid"
)

object HostelDataStore {
    // Current logged-in user context
    var currentRole by mutableStateOf("Student") // "Student", "Warden", "Admin", "Security"
    var currentStudentRoll by mutableStateOf("231801380001")

    // Warden Scoped (Section 6: Boys vs Girls Warden)
    var currentWardenScope by mutableStateOf("Boys") // "Boys" or "Girls"

    // Security Settings (Section 6)
    var securityBlockAssignment by mutableStateOf("Block A & B")
    var securityDutyTimings by mutableStateOf("08:00 AM - 08:00 PM")
    var securityIsPresentToday by mutableStateOf(false)

    // Master configuration settings
    var chiefWardenName by mutableStateOf("Dr. Amit Khanna")
    var chiefWardenPhone by mutableStateOf("+91 9443210987")
    var blockAWardenName by mutableStateOf("Mr. Suresh Kumar")
    var blockAWardenPhone by mutableStateOf("+91 9443210988")
    var blockBWardenName by mutableStateOf("Mrs. Anjali Sen")
    var blockBWardenPhone by mutableStateOf("+91 9443210989")

    // Mess Timings
    var tiffinTiming by mutableStateOf("07:30 AM - 09:00 AM")
    var lunchTiming by mutableStateOf("12:30 PM - 02:00 PM")
    var dinnerTiming by mutableStateOf("07:30 PM - 09:00 PM")

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

    // Master Collections with Roll format 23180138XXXX (Section 7)
    val students = mutableStateListOf(
        StudentProfile("231801380001", "Alex Vance", "alex.vance@hprams.edu", "+91 9876543210", "Female", "Block C", "104", "Paid", "Success", "Approved by Admin"),
        StudentProfile("231801380002", "Dhanush Kumar", "dhanush.k@hprams.edu", "+91 9123456789", "Male", "Block A", "101", "Pending", "Pending", "Pending")
    )

    val roomChangeRequests = mutableStateListOf<RoomChangeRequest>()

    val complaints = mutableStateListOf(
        ComplaintTicket("CMP-8492", "Alex Vance", "Water leakage in bathroom washbasin.", "PLUMBING", "Leakage is continuous since yesterday night.", "Pending", "14 Aug 2026", "Female"),
        ComplaintTicket("CMP-7382", "Dhanush Kumar", "Ceiling fan speed regulator not working.", "ELECTRICAL", "Regulator is stuck on speed 5 and cannot be lowered.", "Pending", "10 Aug 2026", "Male")
    )

    val gatePassRequests = mutableStateListOf<GatePassRequest>()

    val announcements = mutableStateListOf(
        AnnouncementItem("ANN-001", "Water supply maintenance block A", "EMERGENCY BROADCAST", "14 Aug 2026", "Water supply in Block A will be disconnected between 2 PM to 5 PM today for routine pipe repairs.", "Girls"),
        AnnouncementItem("ANN-002", "Hostel rules & curfew timing strict check", "GENERAL NOTICE", "14 Aug 2026", "Curfew is strictly enforced at 10 PM. All students must present their Digital ID pass at the main gate.", "All")
    )

    val fines = mutableStateListOf<FineItem>()

    val handymen = listOf(
        "Electrician Ramesh",
        "Plumber Suresh",
        "Carpenter Vignesh",
        "Mason Karthik"
    )

    fun getStudent(roll: String): StudentProfile? {
        return students.find { it.roll == roll }
    }
}
