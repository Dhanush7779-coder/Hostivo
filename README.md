# HPRAMS: Luxury Campus Living Smart App (Android Client)

This is the premium Jetpack Compose Android client for HPRAMS (Hostel Management and Portal System), featuring a modern **Lumina Glass** (glassmorphic + OLED dark-first) design.

## Firebase Setup Instructions

To get the app fully operational, you must configure a project in the Firebase Console:

1. **Create a Firebase Project**:
   - Go to [Firebase Console](https://console.firebase.google.com/).
   - Create a project named `HPRAMS`.

2. **Add Android App**:
   - Register your Android app with package name: `com.example.hprams`
   - Download the generated `google-services.json` file.
   - Place `google-services.json` in the app module directory at:
     `E:\HPRAMS-Android\app\google-services.json`

3. **Enable Firebase Products in Console**:
   - **Authentication**:
     - Enable **Email/Password** provider.
     - Enable **Phone** provider (for OTP).
     - Enable **Google Sign-In** provider.
   - **Cloud Firestore**:
     - Enable Firestore Database in Test mode or Production mode.
     - Deploy the security rules located in `firebase/firestore.rules`.
   - **Cloud Storage**:
     - Enable Cloud Storage for image uploads (e.g. complaint photos).
     - Deploy rules from `firebase/storage.rules`.
   - **Firebase Cloud Messaging (FCM)**:
     - Enable Cloud Messaging for push notifications.
   - **Cloud Functions**:
     - Deploy the function hooks located in `firebase/functions` to handle backend triggers like auto-allocation, capacity enforcement, and complaint logs.
