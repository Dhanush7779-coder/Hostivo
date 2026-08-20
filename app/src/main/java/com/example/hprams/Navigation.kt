package com.example.hprams

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hprams.ui.screens.*

@Composable
fun MainNavigation() {
  val navController = rememberNavController()
  val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

  NavHost(
    navController = navController,
    startDestination = "splash",
    modifier = Modifier.fillMaxSize()
  ) {
    // -------------------------------------------------------------
    // AUTHENTICATION
    // -------------------------------------------------------------
    composable("splash") {
      SplashScreen(
        onGetStartedClick = { navController.navigate("auth_selector") },
        onAutoLogin = { dest ->
            navController.navigate(dest) {
                popUpTo("splash") { inclusive = true }
            }
        },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("auth_selector") {
      AuthSelectorScreen(
        onGoogleSuccess = { email, name ->
          val matchedStudent = com.example.hprams.data.HostelDataStore.students.find { it.email.lowercase() == email.lowercase() }
          if (matchedStudent != null) {
            if (matchedStudent.approvalStatus != "Approved") {
              navController.navigate("approval_pending") {
                popUpTo("auth_selector") { inclusive = true }
              }
            } else {
              com.example.hprams.data.HostelDataStore.currentRole = matchedStudent.role
              com.example.hprams.data.HostelDataStore.currentStudentRoll = matchedStudent.roll
              val destination = when (matchedStudent.role) {
                "Warden" -> "warden_dashboard"
                "Security" -> "security_dashboard"
                else -> "student_dashboard"
              }
              navController.navigate(destination) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
              }
            }
          } else {
            // New user registration - Prefill email and name details
            com.example.hprams.data.HostelDataStore.prefilledEmail = email
            com.example.hprams.data.HostelDataStore.prefilledName = name
            navController.navigate("email_register")
          }
        },
        onPhoneClick = { navController.navigate("phone_login") },
        onEmailClick = { navController.navigate("email_login") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("approval_pending") {
      ApprovalPendingScreen(
        onBackClick = { navController.navigate("auth_selector") { popUpTo("splash") { inclusive = true } } },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("email_login") {
      EmailLoginScreen(
        onBackClick = { navController.popBackStack() },
        onSignInClick = { email, pass, role ->
          val sharedPrefs = navController.context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE)
          val trimmedEmail = email.trim().lowercase()
          if (trimmedEmail == "ammananasanju@gmail.com") {
              auth.signInWithEmailAndPassword(trimmedEmail, pass)
                  .addOnCompleteListener { task ->
                      if (task.isSuccessful) {
                          sharedPrefs.edit()
                              .putString("loggedInRole", "Admin")
                              .putString("loggedInRoll", "")
                              .apply()
                          navController.navigate("admin_dashboard") {
                              popUpTo(navController.graph.startDestinationId) { inclusive = true }
                          }
                      } else {
                          android.widget.Toast.makeText(navController.context, "Admin Authentication Failed: Incorrect password or credentials.", android.widget.Toast.LENGTH_LONG).show()
                      }
                  }
          } else {
              auth.signInWithEmailAndPassword(trimmedEmail, pass)
                  .addOnCompleteListener { task ->
                      if (task.isSuccessful) {
                          val matched = com.example.hprams.data.HostelDataStore.students.find { it.email.lowercase() == trimmedEmail }
                          if (role != "Admin" && role != "Guest" && matched != null && matched.approvalStatus != "Approved") {
                              navController.navigate("approval_pending") {
                                  popUpTo("auth_selector") { inclusive = true }
                              }
                          } else {
                              if (role == "Warden" && matched != null) {
                                  com.example.hprams.data.HostelDataStore.currentWardenScope = if (matched.gender == "Female") "Girls" else "Boys"
                              }
                              sharedPrefs.edit()
                                  .putString("loggedInRole", role)
                                  .putString("loggedInRoll", matched?.roll ?: "")
                                  .apply()
                              val destination = when (role) {
                                  "Warden" -> "warden_dashboard"
                                  "Admin" -> "admin_dashboard"
                                  "Security" -> "security_dashboard"
                                  "Guest" -> "guest_dashboard"
                                  else -> "student_dashboard"
                              }
                              navController.navigate(destination) {
                                  popUpTo(navController.graph.startDestinationId) { inclusive = true }
                              }
                          }
                      } else {
                          android.widget.Toast.makeText(navController.context, "Login credentials incorrect. Please try again.", android.widget.Toast.LENGTH_LONG).show()
                      }
                  }
          }
        },
        onSignUpClick = { navController.navigate("email_register") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("email_register") {
      EmailRegisterScreen(
        onBackClick = { navController.popBackStack() },
        onSignUpClick = { name, email, roll, phone, pass, father, emergency, role, gender, dob, block, room, paymentPreference, receiptUrl, receiptReference ->
          val resolvedRoll = if (role == "Student") roll else "STAFF-${(1000..9999).random()}"
          val feeStatus = when (paymentPreference) {
              "Pay Now" -> "Paid"
              "Upload Receipt" -> "Under Verification"
              else -> "Pending"
          }
          val newProfile = com.example.hprams.data.StudentProfile(
              roll = resolvedRoll,
              name = name,
              email = email,
              phone = phone,
              gender = gender,
              block = block,
              room = room,
              feePaidStatus = feeStatus,
              paymentStatus = feeStatus,
              approvalStatus = "Pending",
              fatherName = father,
              emergencyPhone = emergency,
              role = role,
              dob = dob
          )
          
          auth.createUserWithEmailAndPassword(email, pass)
              .addOnCompleteListener { task ->
                  if (task.isSuccessful) {
                      val sharedPrefs = navController.context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE)
                      sharedPrefs.edit()
                          .putString("loggedInRole", role)
                          .putString("loggedInRoll", newProfile.roll)
                          .apply()
                      
                      // Save student profile
                      com.example.hprams.data.HostelDataStore.saveStudent(newProfile)

                      // Create payment item if applicable
                      if (role == "Student") {
                          if (paymentPreference == "Upload Receipt") {
                              val newPayment = com.example.hprams.data.PaymentItem(
                                  paymentId = "PAY-${(1000..9999).random()}",
                                  studentId = resolvedRoll,
                                  amount = "110000",
                                  paymentMethod = "External Receipt (Upload)",
                                  paymentType = "EXTERNAL",
                                  paymentStatus = "UNDER_VERIFICATION",
                                  paymentReference = receiptReference,
                                  receiptUrl = receiptUrl,
                                  paymentDate = "20 Aug 2026",
                                  createdAt = "20 Aug 2026",
                                  updatedAt = "20 Aug 2026"
                              )
                              com.example.hprams.data.HostelDataStore.savePayment(newPayment)
                          } else if (paymentPreference == "Pay Now") {
                              val newPayment = com.example.hprams.data.PaymentItem(
                                  paymentId = "PAY-${(1000..9999).random()}",
                                  studentId = resolvedRoll,
                                  amount = "110000",
                                  paymentMethod = "Razorpay (Online)",
                                  paymentType = "RAZORPAY",
                                  paymentStatus = "PAID",
                                  paymentReference = "pay_online_${(100000..999999).random()}",
                                  paymentDate = "20 Aug 2026",
                                  createdAt = "20 Aug 2026",
                                  updatedAt = "20 Aug 2026"
                              )
                              com.example.hprams.data.HostelDataStore.savePayment(newPayment)
                          }
                      }

                      com.example.hprams.data.HostelDataStore.currentStudentRoll = newProfile.roll
                      com.example.hprams.data.HostelDataStore.prefilledEmail = ""
                      com.example.hprams.data.HostelDataStore.prefilledName = ""
                      navController.navigate("approval_pending") {
                          popUpTo("auth_selector") { inclusive = true }
                      }
                  } else {
                      android.widget.Toast.makeText(navController.context, "Registration Failed: ${task.exception?.message}", android.widget.Toast.LENGTH_LONG).show()
                  }
              }
        },
        onSignInClick = { navController.navigate("email_login") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("phone_login") {
      PhoneLoginScreen(
        onBackClick = { navController.popBackStack() },
        onSendOtpClick = { phone -> /* Handle sending OTP */ },
        onVerifyOtpClick = { otp -> 
          navController.navigate("student_dashboard") {
            popUpTo("auth_selector") { inclusive = true }
          }
        },
        modifier = Modifier.fillMaxSize()
      )
    }

    // -------------------------------------------------------------
    // STUDENT FLOW
    // -------------------------------------------------------------
    composable("student_dashboard") {
      StudentDashboardScreen(
        onSignOutClick = { 
          auth.signOut()
          val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).build()
          com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(navController.context, gso).signOut()
          val sharedPrefs = navController.context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE)
          sharedPrefs.edit().remove("loggedInRole").remove("loggedInRoll").apply()
          navController.navigate("auth_selector") {
            popUpTo("splash") { inclusive = true }
          } 
        },
        onProfileClick = { navController.navigate("profile") },
        onNotificationsClick = { navController.navigate("notifications") },
        onRoomsClick = { navController.navigate("room_availability") },
        onFinanceClick = { navController.navigate("finance_dashboard") },
        onSupportClick = { navController.navigate("complaints_list") },
        onAnnouncementsClick = { navController.navigate("announcements") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("room_availability") {
      RoomAvailabilityScreen(
        onHomeClick = { navController.navigate("student_dashboard") },
        onFinanceClick = { navController.navigate("finance_dashboard") },
        onProfileClick = { navController.navigate("profile") },
        onSupportClick = { navController.navigate("complaints_list") },
        onApplyClick = { roomId -> navController.navigate("hostel_application") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("finance_dashboard") {
      FinanceDashboardScreen(
        onHomeClick = { navController.navigate("student_dashboard") },
        onRoomsClick = { navController.navigate("room_availability") },
        onProfileClick = { navController.navigate("profile") },
        onSupportClick = { navController.navigate("complaints_list") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("hostel_application") {
      HostelApplicationScreen(
        onBackClick = { navController.popBackStack() },
        onSubmitClick = { navController.navigate("allocation_status") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("allocation_status") {
      AllocationStatusScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("complaints_list") {
      ComplaintsListScreen(
        onHomeClick = { navController.navigate("student_dashboard") },
        onRoomsClick = { navController.navigate("room_availability") },
        onFinanceClick = { navController.navigate("finance_dashboard") },
        onProfileClick = { navController.navigate("profile") },
        onNewComplaintClick = { navController.navigate("new_complaint") },
        onComplaintClick = { id -> navController.navigate("complaint_detail/$id") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("new_complaint") {
      NewComplaintScreen(
        onBackClick = { navController.popBackStack() },
        onSubmitClick = { navController.navigate("complaints_list") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable(
      route = "complaint_detail/{id}",
      arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStackEntry ->
      val id = backStackEntry.arguments?.getString("id") ?: "CMP-XXXX"
      ComplaintDetailScreen(
        complaintId = id,
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("announcements") {
      AnnouncementsScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("notifications") {
      NotificationsScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("profile") {
      ProfileScreen(
        onHomeClick = { navController.navigate("student_dashboard") },
        onRoomsClick = { navController.navigate("room_availability") },
        onFinanceClick = { navController.navigate("finance_dashboard") },
        onSupportClick = { navController.navigate("complaints_list") },
        onSignOutClick = {
          auth.signOut()
          val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).build()
          com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(navController.context, gso).signOut()
          val sharedPrefs = navController.context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE)
          sharedPrefs.edit().remove("loggedInRole").remove("loggedInRoll").apply()
          navController.navigate("auth_selector") {
            popUpTo("splash") { inclusive = true }
          }
        },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("settings") {
      SettingsScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    // -------------------------------------------------------------
    // WARDEN FLOW
    // -------------------------------------------------------------
    composable("warden_dashboard") {
      WardenDashboardScreen(
        onSignOutClick = {
          auth.signOut()
          val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).build()
          com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(navController.context, gso).signOut()
          val sharedPrefs = navController.context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE)
          sharedPrefs.edit().remove("loggedInRole").remove("loggedInRoll").apply()
          navController.navigate("auth_selector") {
            popUpTo("splash") { inclusive = true }
          }
        },
        onAllocationsClick = { navController.navigate("room_allocation_management") },
        onComplaintsClick = { navController.navigate("complaint_dashboard") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("room_allocation_management") {
      WardenRoomApprovalScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("complaint_dashboard") {
      WardenComplaintsBoardScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("handyman_assignment") {
      HandymanAssignmentScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("warden_reports") {
      WardenReportsScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    // -------------------------------------------------------------
    // ADMIN FLOW
    // -------------------------------------------------------------
    composable("admin_dashboard") {
      AdminDashboardScreen(
        onSignOutClick = {
          auth.signOut()
          val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).build()
          com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(navController.context, gso).signOut()
          val sharedPrefs = navController.context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE)
          sharedPrefs.edit().remove("loggedInRole").remove("loggedInRoll").apply()
          navController.navigate("auth_selector") {
            popUpTo("splash") { inclusive = true }
          }
        },
        onAnalyticsClick = { navController.navigate("admin_analytics") },
        onReportsClick = { navController.navigate("admin_reports") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("admin_analytics") {
      AdminAnalyticsScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("admin_reports") {
      AdminReportsScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    // -------------------------------------------------------------
    // GUEST FLOW
    // -------------------------------------------------------------
    composable("guest_dashboard") {
      GuestDashboardScreen(
        onSignOutClick = {
          auth.signOut()
          val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).build()
          com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(navController.context, gso).signOut()
          val sharedPrefs = navController.context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE)
          sharedPrefs.edit().remove("loggedInRole").remove("loggedInRoll").apply()
          navController.navigate("auth_selector") {
            popUpTo("splash") { inclusive = true }
          }
        },
        onHostelInfoClick = { navController.navigate("hostel_info") },
        onAppStatusClick = { navController.navigate("guest_application_status") },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("hostel_info") {
      HostelInfoScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("guest_application_status") {
      GuestApplicationStatusScreen(
        onBackClick = { navController.popBackStack() },
        modifier = Modifier.fillMaxSize()
      )
    }

    composable("security_dashboard") {
      SecurityDashboardScreen(
        onSignOutClick = {
          auth.signOut()
          val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN).build()
          com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(navController.context, gso).signOut()
          val sharedPrefs = navController.context.getSharedPreferences("HostivoPrefs", android.content.Context.MODE_PRIVATE)
          sharedPrefs.edit().remove("loggedInRole").remove("loggedInRoll").apply()
          navController.navigate("auth_selector") {
            popUpTo("splash") { inclusive = true }
          }
        },
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}
