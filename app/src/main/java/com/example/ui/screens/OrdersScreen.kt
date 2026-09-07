package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OrderEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.WhatsAppCustomerCareBanner
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.BkashPink
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.util.WhatsAppHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSlate100
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val orders by viewModel.orders.collectAsState()
  val selectedOrder by viewModel.selectedOrder.collectAsState()
  val settings by viewModel.storeSettings.collectAsState()

  if (selectedOrder != null) {
    OrderDetailView(
      order = selectedOrder!!,
      viewModel = viewModel,
      onBack = { viewModel.closeOrderDetail() }
    )
    return
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "আমার অর্ডারসমূহ (${orders.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary) },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.White,
          titleContentColor = TextPrimary
        ),
        modifier = Modifier.border(BorderStroke(1.dp, BentoCardBorder))
      )
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    if (orders.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(64.dp)
          )
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "আপনি এখনও কোনো অর্ডার করেননি।",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "পছন্দের পণ্য কার্টে যুক্ত করে সহজেই অর্ডার সম্পন্ন করুন।",
            fontSize = 12.sp,
            color = TextSecondary
          )
          Spacer(modifier = Modifier.height(20.dp))
          Button(
            onClick = { viewModel.setTab(ScreenTab.HOME) },
            colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(text = "শপিং শুরু করুন", fontWeight = FontWeight.Bold)
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Customer Care quick reminder
        item {
          WhatsAppCustomerCareBanner(phoneNumber = settings.customerCareWhatsApp)
        }

        items(orders) { order ->
          OrderCardItem(
            order = order,
            onClick = { viewModel.openOrderDetail(order) },
            onWhatsAppSupport = {
              WhatsAppHelper.openWhatsAppChat(
                context = context,
                phoneNumber = settings.customerCareWhatsApp,
                message = "আমার অর্ডার (${order.orderId}) সম্পর্কে তথ্য জানতে চাই।"
              )
            }
          )
        }
      }
    }
  }
}

@Composable
fun OrderCardItem(
  order: OrderEntity,
  onClick: () -> Unit,
  onWhatsAppSupport: () -> Unit
) {
  val dateFormatted = remember(order.createdAt) {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))
  }

  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BentoCardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .clickable { onClick() }
      .testTag("order_item_${order.orderId}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Order ID & Date
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = order.orderId,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = AhesanPrimary
          )
          Text(
            text = dateFormatted,
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
        StatusBadge(status = order.orderStatus)
      }

      HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BentoCardBorder)

      // Payment info row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = if (order.paymentMethod == "FULL_PAYMENT") "সম্পূর্ণ পেমেন্ট (Full Payment)" else "Cash on Delivery",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = TextPrimary
          )
          Text(
            text = "TrxID: ${order.trxId}",
            fontSize = 11.sp,
            color = BkashPink,
            fontWeight = FontWeight.Bold
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "৳${order.totalAmount.toInt()}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
          )
          Text(
            text = "মোট ${order.itemsCount}টি আইটেম",
            fontSize = 11.sp,
            color = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Actions row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "বিস্তারিত দেখুন >",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = AhesanPrimary
        )

        Button(
          onClick = onWhatsAppSupport,
          colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(32.dp)
        ) {
          Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(13.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(text = "সহায়তা", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailView(
  order: OrderEntity,
  viewModel: ShopViewModel,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val settings by viewModel.storeSettings.collectAsState()
  val orderItems by viewModel.getOrderItems(order.orderId).collectAsState(emptyList())

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "অর্ডার ট্র্যাকিং ও বিবরণ", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = AhesanPrimary,
          titleContentColor = Color.White
        )
      )
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Status Progress Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = order.orderId, fontWeight = FontWeight.Black, fontSize = 16.sp, color = AhesanPrimary)
              StatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "পেমেন্ট স্ট্যাটাস: ${order.paymentStatus}",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = BkashPink
            )

            if (!order.adminNote.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(6.dp))
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "অ্যাডমিন নোট: ${order.adminNote}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(8.dp)
                )
              }
            }
          }
        }
      }

      // 2. Ordered Items List
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "অর্ডারকৃত পণ্যসমূহ",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = AhesanPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            orderItems.forEach { item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(text = item.productName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                  if (item.selectedVariant.isNotBlank()) {
                    Text(text = item.selectedVariant, fontSize = 11.sp, color = Color.Gray)
                  }
                  Text(text = "৳${item.unitPrice.toInt()} × ${item.quantity}", fontSize = 11.sp, color = Color.Gray)
                }
                Text(
                  text = "৳${item.totalPrice.toInt()}",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
              HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.surfaceVariant)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "পণ্য মূল্য (Subtotal):", fontSize = 12.sp)
              Text(text = "৳${order.subtotal.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "ডেলিভারি চার্জ:", fontSize = 12.sp)
              Text(text = "৳${order.deliveryCharge.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "সর্বমোট মূল্য:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
              Text(text = "৳${order.totalAmount.toInt()}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = AhesanPrimary)
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "bKash অগ্রিম পরিশোধিত:", fontSize = 12.sp, color = BkashPink, fontWeight = FontWeight.Bold)
              Text(text = "৳${(order.paidAmount ?: 0.0).toInt()}", fontSize = 12.sp, color = BkashPink, fontWeight = FontWeight.Bold)
            }

            if (order.paymentMethod == "CASH_ON_DELIVERY") {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "ডেলিভারির সময় প্রদেয় ক্যাশ:", fontSize = 12.sp, color = Color(0xFF9A3412), fontWeight = FontWeight.Bold)
                Text(text = "৳${order.subtotal.toInt()}", fontSize = 12.sp, color = Color(0xFF9A3412), fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 3. Delivery & Customer Details
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "ডেলিভারির ঠিকানা", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AhesanPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "গ্রাহক: ${order.customerName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "ফোন: ${order.customerPhone}", fontSize = 12.sp)
            Text(text = "ঠিকানা: ${order.deliveryAddress}", fontSize = 12.sp)
            Text(text = "এলাকা ও জেলা: ${order.thana}, ${order.district}", fontSize = 12.sp)
            if (order.deliveryInstructions.isNotBlank()) {
              Text(text = "নির্দেশনা: ${order.deliveryInstructions}", fontSize = 11.sp, color = Color.Gray)
            }
          }
        }
      }

      // 4. WhatsApp Care Button
      item {
        WhatsAppCustomerCareBanner(phoneNumber = settings.customerCareWhatsApp)
      }
    }
  }
}
