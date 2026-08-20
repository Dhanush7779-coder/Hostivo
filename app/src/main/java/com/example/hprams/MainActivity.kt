package com.example.hprams

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.hprams.theme.HPRAMSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), com.razorpay.PaymentResultListener {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Start Realtime Database sync
    com.example.hprams.data.HostelDataStore.initializeSync(applicationContext)

    // Preload Razorpay checkout to improve loading times
    com.razorpay.Checkout.preload(applicationContext)

    enableEdgeToEdge()
    setContent {
      HPRAMSTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          MainNavigation()
        }
      }
    }
  }

  override fun onPaymentSuccess(razorpayPaymentId: String?) {
    com.example.hprams.data.HostelDataStore.onPaymentSuccessCallback?.invoke(razorpayPaymentId ?: "")
  }

  override fun onPaymentError(code: Int, response: String?) {
    com.example.hprams.data.HostelDataStore.onPaymentErrorCallback?.invoke(code, response ?: "")
  }
}
