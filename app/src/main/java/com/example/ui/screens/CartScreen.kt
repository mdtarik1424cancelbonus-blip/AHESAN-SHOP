package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCartCheckout
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.CartItemEntity
import com.example.ui.components.WhatsAppCustomerCareBanner
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.AhesanSecondary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSlate100
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val cartItems by viewModel.cartItems.collectAsState()
  val settings by viewModel.storeSettings.collectAsState()

  val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
  val isFreeDelivery = subtotal >= settings.freeDeliveryThreshold
  // Default estimate for inside Dhaka
  val estimatedDelivery = if (isFreeDelivery) 0.0 else settings.insideCityDeliveryCharge
  val total = subtotal + estimatedDelivery

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "শপিং কার্ট (${cartItems.size})",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
          )
        },
        actions = {
          if (cartItems.isNotEmpty()) {
            TextButton(onClick = { viewModel.clearCart() }) {
              Text(text = "সব মুছুন", color = TextSecondary, fontWeight = FontWeight.SemiBold)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.White,
          titleContentColor = TextPrimary
        ),
        modifier = Modifier.border(BorderStroke(1.dp, BentoCardBorder))
      )
    },
    bottomBar = {
      if (cartItems.isNotEmpty()) {
        Surface(
          color = Color.White,
          border = BorderStroke(1.dp, BentoCardBorder),
          shadowElevation = 4.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "সর্বমোট প্রদেয়:",
                  fontSize = 12.sp,
                  color = TextSecondary
                )
                Text(
                  text = "৳${total.toInt()}",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Black,
                  color = AhesanPrimary
                )
              }

              Button(
                onClick = { viewModel.startCheckout() },
                colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .height(48.dp)
                  .testTag("proceed_checkout_btn")
              ) {
                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "অর্ডার করুন", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              }
            }
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    if (cartItems.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.ShoppingBag,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(64.dp)
          )
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "আপনার কার্ট বর্তমানে খালি আছে!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "পছন্দের পণ্যগুলো কার্টে যুক্ত করে সহজে অর্ডার সম্পন্ন করুন।",
            fontSize = 12.sp,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(20.dp))
          Button(
            onClick = { viewModel.setTab(ScreenTab.HOME) },
            colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(text = "কেনাকাটা শুরু করুন", fontWeight = FontWeight.Bold)
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
        // Free Delivery progress notification
        if (settings.freeDeliveryThreshold > 0) {
          item {
            Surface(
              color = if (isFreeDelivery) Color(0xFFD1FAE5) else Color(0xFFFEF3C7),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, if (isFreeDelivery) Color(0xFFA7F3D0) else Color(0xFFFDE68A)),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = if (isFreeDelivery)
                    "🎉 অভিনন্দন! আপনি ফ্রি ডেলিভারি পাচ্ছেন!"
                  else
                    "আরও ৳${(settings.freeDeliveryThreshold - subtotal).toInt()} টাকার পণ্য কিনলে ফ্রি ডেলিভারি পাবেন!",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isFreeDelivery) Color(0xFF065F46) else Color(0xFF92400E)
                )
              }
            }
          }
        }

        // Cart items list
        items(cartItems) { item ->
          CartItemCard(
            item = item,
            onIncrease = { viewModel.updateCartQuantity(item, 1) },
            onDecrease = { viewModel.updateCartQuantity(item, -1) },
            onRemove = { viewModel.removeCartItem(item) }
          )
        }

        // Order Price Breakdown Summary
        item {
          Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BentoCardBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "হিসাব বিবরণী (Summary)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "পণ্য মূল্য (Subtotal):", fontSize = 13.sp, color = TextSecondary)
                Text(text = "৳${subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
              }

              Spacer(modifier = Modifier.height(6.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(text = "ডেলিভারি চার্জ:", fontSize = 13.sp, color = TextSecondary)
                  Text(text = "(চেকআউটে এলাকা অনুযায়ী নির্ধারিত হবে)", fontSize = 10.sp, color = TextTertiary)
                }
                Text(
                  text = if (isFreeDelivery) "ফ্রি (Free)" else "৳${estimatedDelivery.toInt()}",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isFreeDelivery) AhesanPrimary else TextPrimary
                )
              }

              HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BentoCardBorder)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "সর্বমোট (Total):", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "৳${total.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AhesanPrimary)
              }
            }
          }
        }

        // WhatsApp Customer Care banner
        item {
          WhatsAppCustomerCareBanner(phoneNumber = settings.customerCareWhatsApp)
        }

        // Continue Shopping Button
        item {
          OutlinedButton(
            onClick = { viewModel.setTab(ScreenTab.HOME) },
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, BentoCardBorder),
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
          ) {
            Text(text = "আরো কেনাকাটা করুন", fontWeight = FontWeight.SemiBold, color = TextPrimary)
          }
        }
      }
    }
  }
}

@Composable
fun CartItemCard(
  item: CartItemEntity,
  onIncrease: () -> Unit,
  onDecrease: () -> Unit,
  onRemove: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BentoCardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("cart_item_${item.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Product thumbnail bento pod
      AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
          .data(item.productImage)
          .crossfade(true)
          .build(),
        contentDescription = item.productName,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(72.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(BentoSlate100)
      )

      Spacer(modifier = Modifier.width(12.dp))

      // Details
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.productName,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 2,
          color = TextPrimary
        )

        // Selected Variants
        val variantInfo = listOfNotNull(
          item.selectedSize?.let { "Size: $it" },
          item.selectedColor?.let { "Color: $it" }
        ).joinToString(" • ")

        if (variantInfo.isNotBlank()) {
          Text(
            text = variantInfo,
            fontSize = 11.sp,
            color = TextSecondary
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "৳${item.unitPrice.toInt()} × ${item.quantity} = ৳${(item.unitPrice * item.quantity).toInt()}",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = AhesanPrimary
        )
      }

      // Quantity controls & delete
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onRemove,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Remove",
            tint = Color.Red,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(BentoSlate100, RoundedCornerShape(12.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          IconButton(
            onClick = onDecrease,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
          }
          Text(
            text = "${item.quantity}",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 6.dp)
          )
          IconButton(
            onClick = onIncrease,
            modifier = Modifier.size(24.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
          }
        }
      }
    }
  }
}
