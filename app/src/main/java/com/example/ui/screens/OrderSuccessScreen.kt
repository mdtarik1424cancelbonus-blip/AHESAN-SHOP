package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusBadge
import com.example.ui.components.WhatsAppCustomerCareBanner
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.AhesanSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.util.WhatsAppHelper

@Composable
fun OrderSuccessScreen(
  orderId: String,
  viewModel: ShopViewModel,
  onViewOrders: () -> Unit,
  onBackHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val orders by viewModel.orders.collectAsState()
  val settings by viewModel.storeSettings.collectAsState()

  val order = orders.find { it.orderId == orderId }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // 1. Success Animation / Icon
    item {
      Spacer(modifier = Modifier.height(20.dp))
      Box(
        modifier = Modifier
          .size(80.dp)
          .background(Color(0xFFD1FAE5), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = "Success",
          tint = Color(0xFF059669),
          modifier = Modifier.size(54.dp)
        )
      }
      Spacer(modifier = Modifier.height(12.dp))
      Text(
        text = "অর্ডার সফলভাবে গ্রহণ করা হয়েছে!",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = AhesanPrimary,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "অর্ডার ট্র্যাকিং আইডি: $orderId",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // 2. Status & Verification Banner
    item {
      val isFull = order?.paymentMethod == "FULL_PAYMENT"
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFF59E0B))),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "বর্তমান স্ট্যাটাস:",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color(0xFF92400E)
            )
            StatusBadge(status = order?.orderStatus ?: "Verification Pending")
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = if (isFull) {
              "আপনার সম্পূর্ণ পেমেন্ট TrxID (${order?.trxId}) জমা হয়েছে। আমাদের টিম ম্যানুয়ালি যাচাই করার পর অর্ডারটি দ্রুত প্রসেসিং শুরু হবে।"
            } else {
              "আপনার ক্যাশ অন ডেলিভারির অগ্রিম ডেলিভারি চার্জ TrxID (${order?.trxId}) জমা হয়েছে। ভেরিফিকেশনের পর ডেলিভারির জন্য পাঠানো হবে।"
            },
            fontSize = 12.sp,
            color = Color(0xFF78350F),
            lineHeight = 16.sp
          )
        }
      }
    }

    // 3. Order Details Card
    if (order != null) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "অর্ডারের বিবরণ",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = AhesanPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            DetailRow("গ্রাহকের নাম:", order.customerName)
            DetailRow("মোবাইল নম্বর:", order.customerPhone)
            DetailRow("ডেলিভারি ঠিকানা:", "${order.deliveryAddress}, ${order.thana}, ${order.district}")
            DetailRow("পেমেন্ট পদ্ধতি:", if (order.paymentMethod == "FULL_PAYMENT") "সম্পূর্ণ পেমেন্ট (Full Payment)" else "Cash on Delivery")
            DetailRow("bKash TrxID:", order.trxId ?: "")
            DetailRow("প্রেরক bKash নম্বর:", order.bkashNumber ?: "")

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            DetailRow("পণ্য মূল্য:", "৳${order.subtotal.toInt()}")
            DetailRow("ডেলিভারি চার্জ:", "৳${order.deliveryCharge.toInt()}")
            DetailRow("সর্বমোট অর্ডার মূল্য:", "৳${order.totalAmount.toInt()}", isBold = true)
            DetailRow(
              "bKash-এ পরিশোধিত অগ্রিম:",
              "৳${(order.paidAmount ?: 0.0).toInt()}",
              isBold = true,
              valueColor = AhesanPrimary
            )
            if (order.paymentMethod == "CASH_ON_DELIVERY") {
              DetailRow(
                "ডেলিভারির সময় প্রদেয় ক্যাশ:",
                "৳${order.subtotal.toInt()}",
                isBold = true,
                valueColor = Color(0xFF9A3412)
              )
            }
          }
        }
      }
    }

    // 4. Dedicated WhatsApp Customer Care Button
    item {
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F8EE)),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(WhatsAppGreen)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "“অর্ডার বা যেকোনো সমস্যার জন্য WhatsApp-এ যোগাযোগ করুন।”",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F5132),
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              WhatsAppHelper.openWhatsAppChat(
                context = context,
                phoneNumber = settings.customerCareWhatsApp,
                message = "আমি AHESAN SHOP-এ নতুন অর্ডার করেছি। অর্ডার আইডি: $orderId। TrxID: ${order?.trxId ?: ""}"
              )
            },
            colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
            shape = RoundedCornerShape(8.dp)
          ) {
            Text(text = "WhatsApp Customer Care (${settings.customerCareWhatsApp})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }
    }

    // 5. Navigation Buttons
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onViewOrders,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("view_orders_btn")
        ) {
          Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "আমার অর্ডারসমূহ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Button(
          onClick = onBackHome,
          colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("back_to_home_btn")
        ) {
          Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "হোম পেজে যান", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun DetailRow(
  label: String,
  value: String,
  isBold: Boolean = false,
  valueColor: Color = Color.Unspecified
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      fontSize = 12.sp,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
    )
    Text(
      text = value,
      fontSize = 12.sp,
      color = if (valueColor != Color.Unspecified) valueColor else MaterialTheme.colorScheme.onSurface,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
    )
  }
}
