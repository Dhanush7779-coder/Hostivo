# Hostivo - HPRAMS (Hostel Process & Resource Allocation Management System)

> **HPRAMS** stands for **Hostel Process & Resource Allocation Management System**.

## 📖 About the App
Hostivo (HPRAMS) is a modern, unified campus hostel management mobile platform designed to digitize and streamline daily residential operations. It brings students, wardens, security personnel, and administrators together under a synchronized, role-based ecosystem. The app automates smart room allocation with live occupancy indicators and simplifies semester fee collections through both online gateways and receipt verifications. It replaces manual registers with digital gate passes featuring QR checkpoints and real-time curfew tracking. With integrated maintenance ticketing, profile customization, and instant announcement broadcasts, Hostivo delivers a secure, paperless, and seamless hostel living experience.

---

## 🎯 Key Outcomes & Benefits
- **100% Paperless Administration:** Eliminates manual record-keeping for room allotments, gate passes, fee dues, and discipline fines.
- **Enhanced Campus Security & Safety:** Enables live monitoring of student movements with automated check-in/out timestamps and curfew violation alerts.
- **Transparent & Faster Financial Processing:** Delivers instant fee payments via Razorpay, external receipt uploads, and real-time transaction reconciliation.
- **Optimized Room & Asset Management:** Provides visual occupancy metrics to prevent overcrowding and maximize hostel capacity planning.
- **Quicker Complaint Resolution:** Accelerates maintenance turnaround through categorized grievance tickets and handyman status tracking.
- **Instant Campus Communication:** Keeps all residents informed with real-time targeted notices for mess menus, events, and urgent hostel circulars.

---

## 🚀 Core Modules & Features

### 🎓 1. Student Portal
- **Dashboard & Home:** Quick shortcuts for Gate Pass, Fee Portal, Support, and Community.
- **Smart Room Showcase:** View allocated Block, Room number, Floor, Roommates, and 8+ amenities.
- **Fee Management:** Semester dues summary, Razorpay online gateway, and external receipt uploads with payment history.
- **Support & Complaints:** Raise maintenance tickets with photo attachments and track resolution status.
- **Profile Self-Service:** Edit personal contact details, guardian info, and upload custom profile photos.

### 🛡️ 2. Warden Console
- **Jurisdiction Dashboard:** Dedicated Boys/Girls hostel supervision.
- **Room Shift Approvals:** Review and approve student room change requests with instant capacity checks.
- **Gate Pass Approvals:** One-tap approval for student outing and emergency home passes.
- **Discipline & Fines:** Issue disciplinary penalties with violation reasons and amounts.
- **Broadcaster:** Publish targeted hostel notices for mess menus, maintenance, and events.

### 👮 3. Security Checkpoint
- **Live Pass Verifier:** Real-time search and QR-based gate pass validation.
- **Check-In / Check-Out:** Instant timestamp logging for student exits and returns.
- **Curfew Tracker:** Automated late-entry logs and curfew monitoring.

### 🏛️ 4. Admin Management Center
- **Visual Room Allotment:** Interactive room selector with live occupancy tracking (`1/4 Occupied` to `Full`).
- **Account Creation & Password Setup:** Create and manage credentials for Students, Wardens, and Security officers.
- **Financial Audit & Reconciliation:** Review and verify offline bank receipts and fee transactions.

---

## 🛠️ Technology Stack
- **Frontend / UI:** Android Jetpack Compose, Material 3, Coil Image Loader
- **Language:** Kotlin
- **Backend & Database:** Firebase Realtime Database, Firebase Authentication, Firebase Storage
- **Payment Gateway:** Razorpay Android SDK
- **Local Persistence:** Room Database / Cache

---

## 🔧 Setup & Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/Dhanush7779-coder/Hostivo.git
   ```

2. **Add `google-services.json`:**
   Place your `google-services.json` file inside `app/google-services.json`.

3. **Build and Run:**
   ```bash
   ./gradlew assembleDebug
   ```
