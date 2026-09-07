package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object WhatsAppHelper {
  fun openWhatsAppChat(context: Context, phoneNumber: String, message: String = "Hello AHESAN SHOP, আমি আপনাদের শপ থেকে তথ্য জানতে চাই।") {
    try {
      // Normalize number for Bangladesh international format
      val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")
      val formattedNumber = if (cleanNumber.startsWith("880")) {
        cleanNumber
      } else if (cleanNumber.startsWith("0")) {
        "88$cleanNumber"
      } else {
        "880$cleanNumber"
      }

      val encodedMsg = URLEncoder.encode(message, "UTF-8")
      val url = "https://api.whatsapp.com/send?phone=$formattedNumber&text=$encodedMsg"
      val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, "WhatsApp চালু করা সম্ভব হয়নি: ${e.message}", Toast.LENGTH_LONG).show()
    }
  }
}
