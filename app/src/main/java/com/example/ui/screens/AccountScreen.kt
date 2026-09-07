package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.WhatsAppCustomerCareBanner
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.AhesanSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.util.WhatsAppHelper

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSlate100
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentUser by viewModel.currentUser.collectAsState()
  val settings by viewModel.storeSettings.collectAsState()
  val wishlistItems by viewModel.wishlistItems.collectAsState()
  val notifications by viewModel.notifications.collectAsState()
  val orders by viewModel.orders.collectAsState()

  var showAdminPinDialog by remember { mutableStateOf(false) }
  var adminPinInput by remember { mutableStateOf("") }
  var pinError by remember { mutableStateOf(false) }

  var showProfileDialog by remember { mutableStateOf(false) }
  var profileName by remember { mutableStateOf(currentUser?.name ?: "") }
  var profilePhone by remember { mutableStateOf(currentUser?.phone ?: "") }
  var profileAddress by remember { mutableStateOf(currentUser?.address ?: "") }
  var profileDistrict by remember { mutableStateOf(currentUser?.district ?: "Dhaka") }
  var profileThana by remember { mutableStateOf(currentUser?.thana ?: "") }

  // Quick Order Tracking ID search
  var searchOrderId by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "আমার অ্যাকাউন্ট (My Account)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary) },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.White,
          titleContentColor = TextPrimary
        ),
        modifier = Modifier.border(BorderStroke(1.dp, BentoCardBorder))
      )
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Profile Summary Bento Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BentoCardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .background(BentoSlate100, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = AhesanPrimary,
                  modifier = Modifier.size(30.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = currentUser?.name?.ifEmpty { "সম্মানিত গ্রাহক" } ?: "সম্মানিত গ্রাহক",
                  fontSize = 16.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
                Text(
                  text = currentUser?.phone?.ifEmpty { "ফোন নম্বর সেট করা নেই" } ?: "ফোন নম্বর সেট করা নেই",
                  fontSize = 12.sp,
                  color = TextSecondary
                )
                if (!currentUser?.address.isNullOrBlank()) {
                  Text(
                    text = "${currentUser?.address}, ${currentUser?.district}",
                    fontSize = 11.sp,
                    color = TextTertiary,
                    maxLines = 1
                  )
                }
              }

              IconButton(
                onClick = {
                  profileName = currentUser?.name ?: ""
                  profilePhone = currentUser?.phone ?: ""
                  profileAddress = currentUser?.address ?: ""
                  profileDistrict = currentUser?.district ?: "Dhaka"
                  profileThana = currentUser?.thana ?: ""
                  showProfileDialog = true
                }
              ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = AhesanPrimary)
              }
            }
          }
        }
      }

      // 2. Order Tracking Quick Search Bento Card
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
              text = "অর্ডার ট্র্যাকিং (Track Order)",
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically
            ) {
              OutlinedTextField(
                value = searchOrderId,
                onValueChange = { searchOrderId = it.uppercase() },
                placeholder = { Text("AHESAN-...", fontSize = 12.sp, color = TextTertiary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                  focusedContainerColor = BentoSlate100,
                  unfocusedContainerColor = BentoSlate100,
                  focusedBorderColor = AhesanPrimary.copy(alpha = 0.5f),
                  unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                  .weight(1f)
                  .height(50.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Button(
                onClick = {
                  val found = orders.find { it.orderId.equals(searchOrderId.trim(), ignoreCase = true) }
                  if (found != null) {
                    viewModel.setTab(ScreenTab.ORDERS)
                    viewModel.openOrderDetail(found)
                  } else {
                    Toast.makeText(context, "এই অর্ডারের কোনো তথ্য পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
                  }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(50.dp)
              ) {
                Text("ট্র্যাক করুন", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 3. Wishlist Bento Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BentoCardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "পছন্দের তালিকা (${wishlistItems.size})",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimary
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (wishlistItems.isEmpty()) {
              Text(
                text = "কোনো পণ্য পছন্দের তালিকায় যোগ করা নেই।",
                fontSize = 12.sp,
                color = TextTertiary
              )
            } else {
              wishlistItems.forEach { wish ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  AsyncImage(
                    model = wish.productImage,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                      .size(46.dp)
                      .clip(RoundedCornerShape(12.dp))
                      .background(BentoSlate100)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(text = wish.productName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, color = TextPrimary)
                    Text(text = "৳${wish.price.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AhesanPrimary)
                  }
                  Button(
                    onClick = { viewModel.moveWishlistToCart(wish) },
                    colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                  ) {
                    Text(text = "কার্টে নিন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }
      }

      // 4. Notifications Bento Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, BentoCardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Notifications, contentDescription = null, tint = AhesanPrimary, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "বিজ্ঞপ্তি ও আপডেট (${notifications.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (notifications.isEmpty()) {
              Text(text = "কোনো নতুন বিজ্ঞপ্তি নেই।", fontSize = 12.sp, color = TextTertiary)
            } else {
              notifications.take(5).forEach { notif ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                  Text(text = notif.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                  Text(text = notif.message, fontSize = 11.sp, color = TextSecondary)
                  HorizontalDivider(modifier = Modifier.padding(top = 6.dp), color = BentoCardBorder)
                }
              }
            }
          }
        }
      }

      // 5. WhatsApp Customer Care Support Banner
      item {
        WhatsAppCustomerCareBanner(phoneNumber = settings.customerCareWhatsApp)
      }

      // 6. Admin Panel Entry Bento Card
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_panel_entry_card")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .background(Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.AdminPanelSettings,
                  contentDescription = "Admin",
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "অ্যাডমিন কন্ট্রোল প্যানেল",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = "পণ্য, পেমেন্ট ও অর্ডার ম্যানেজমেন্ট",
                  fontSize = 11.sp,
                  color = Color.LightGray
                )
              }
            }

            Button(
              onClick = {
                adminPinInput = ""
                pinError = false
                showAdminPinDialog = true
              },
              colors = ButtonDefaults.buttonColors(containerColor = AhesanSecondary),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("open_admin_btn")
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = "প্রবেশ করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      // Bottom spacing
      item {
        Spacer(modifier = Modifier.height(70.dp))
      }
    }
  }

  // Profile Edit Dialog
  if (showProfileDialog) {
    AlertDialog(
      onDismissRequest = { showProfileDialog = false },
      title = { Text("প্রোফাইল আপডেট করুন", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = profileName,
            onValueChange = { profileName = it },
            label = { Text("আপনার নাম") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = profilePhone,
            onValueChange = { profilePhone = it },
            label = { Text("মোবাইল নম্বর") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = profileAddress,
            onValueChange = { profileAddress = it },
            label = { Text("পূর্ণ ঠিকানা") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = profileDistrict,
            onValueChange = { profileDistrict = it },
            label = { Text("জেলা") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = profileThana,
            onValueChange = { profileThana = it },
            label = { Text("থানা") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.saveUserProfile(profileName, profilePhone, "", profileAddress, profileDistrict, profileThana)
            showProfileDialog = false
            Toast.makeText(context, "প্রোফাইল সফলভাবে আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary)
        ) {
          Text("সংরক্ষণ করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showProfileDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }

  // Admin PIN Dialog
  if (showAdminPinDialog) {
    AlertDialog(
      onDismissRequest = { showAdminPinDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = AhesanPrimary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("অ্যাডমিন পিন কোড দিন", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column {
          Text(
            text = "দয়া করে অ্যাডমিন পিন প্রদান করুন (ডিফল্ট পিন: 1234):",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(10.dp))
          OutlinedTextField(
            value = adminPinInput,
            onValueChange = {
              adminPinInput = it
              pinError = false
            },
            label = { Text("৪ ডিজিট পিন") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            isError = pinError,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_pin_input")
          )
          if (pinError) {
            Text(
              text = "ভুল পিন কোড! পুনরায় চেষ্টা করুন।",
              color = Color.Red,
              fontSize = 11.sp,
              modifier = Modifier.padding(top = 4.dp)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (viewModel.loginAdmin(adminPinInput)) {
              showAdminPinDialog = false
              viewModel.openAdminPanel()
            } else {
              pinError = true
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
          modifier = Modifier.testTag("admin_pin_confirm_btn")
        ) {
          Text("লগইন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAdminPinDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }
}
