package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.WhatsAppCustomerCareBanner
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.AhesanSecondary
import com.example.ui.theme.BkashPink
import com.example.ui.theme.BkashPinkLight

enum class PaymentType {
  FULL_PAYMENT,
  CASH_ON_DELIVERY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
  viewModel: ShopViewModel,
  onOrderPlaced: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val cartItems by viewModel.cartItems.collectAsState()
  val settings by viewModel.storeSettings.collectAsState()
  val currentUser by viewModel.currentUser.collectAsState()

  // Form states
  var name by remember { mutableStateOf(currentUser?.name ?: "") }
  var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
  var address by remember { mutableStateOf(currentUser?.address ?: "") }
  var selectedDistrict by remember { mutableStateOf(currentUser?.district ?: "Dhaka (ঢাকা)") }
  var thana by remember { mutableStateOf(currentUser?.thana ?: "") }
  var deliveryInstructions by remember { mutableStateOf("") }

  // District dropdown expansion
  var districtExpanded by remember { mutableStateOf(false) }
  val districtList = listOf(
    "Dhaka (ঢাকা)",
    "Chittagong (চট্টগ্রাম)",
    "Sylhet (সিলেট)",
    "Rajshahi (রাজশাহী)",
    "Khulna (খুলনা)",
    "Barisal (বরিশাল)",
    "Rangpur (রংপুর)",
    "Mymensingh (ময়মনসিংহ)",
    "Gazipur (গাজীপুর)",
    "Narayanganj (নারায়ণগঞ্জ)",
    "Comilla (কুমিল্লা)",
    "Bogra (বগুড়া)"
  )

  // Payment method selection
  var paymentType by remember { mutableStateOf(PaymentType.FULL_PAYMENT) }
  var selectedBkashNumber by remember { mutableStateOf(settings.bkashNumber1) }
  var bkashSenderNumber by remember { mutableStateOf("") }
  var trxId by remember { mutableStateOf("") }
  var isSubmitting by remember { mutableStateOf(false) }

  // Delivery charge calculation
  val isInsideCity = selectedDistrict.contains("Dhaka", ignoreCase = true)
  val subtotal = cartItems.sumOf { it.unitPrice * it.quantity }
  val isFreeDelivery = subtotal >= settings.freeDeliveryThreshold
  val deliveryCharge = if (isFreeDelivery) 0.0 else if (isInsideCity) settings.insideCityDeliveryCharge else settings.outsideCityDeliveryCharge
  val grandTotal = subtotal + deliveryCharge

  // Amount required to pay via bKash right now
  val bkashAdvanceRequired = if (paymentType == PaymentType.FULL_PAYMENT) {
    grandTotal
  } else {
    deliveryCharge // Advance delivery charge for COD
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "চেকআউট ও পেমেন্ট", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.cancelCheckout() },
            modifier = Modifier.testTag("checkout_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = AhesanPrimary,
          titleContentColor = Color.White
        )
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
      // 1. Delivery Information Form
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "১. ডেলিভারির তথ্য (Customer Details)",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = AhesanPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Name
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("গ্রাহকের নাম (Full Name) *") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_name_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Phone
            OutlinedTextField(
              value = phone,
              onValueChange = { phone = it },
              label = { Text("মোবাইল নম্বর (01XXXXXXXXX) *") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_phone_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // District Dropdown
            ExposedDropdownMenuBox(
              expanded = districtExpanded,
              onExpandedChange = { districtExpanded = !districtExpanded },
              modifier = Modifier.fillMaxWidth()
            ) {
              OutlinedTextField(
                value = selectedDistrict,
                onValueChange = {},
                readOnly = true,
                label = { Text("জেলা (District) *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                modifier = Modifier
                  .menuAnchor()
                  .fillMaxWidth()
                  .testTag("checkout_district_dropdown")
              )
              ExposedDropdownMenu(
                expanded = districtExpanded,
                onDismissRequest = { districtExpanded = false }
              ) {
                districtList.forEach { dist ->
                  DropdownMenuItem(
                    text = { Text(dist) },
                    onClick = {
                      selectedDistrict = dist
                      districtExpanded = false
                    }
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Thana / Area
            OutlinedTextField(
              value = thana,
              onValueChange = { thana = it },
              label = { Text("থানা / উপজেলা / এলাকা (Thana/Area) *") },
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_thana_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Full Address
            OutlinedTextField(
              value = address,
              onValueChange = { address = it },
              label = { Text("পূর্ণ ঠিকানা (বাসা/রোড/গ্রাম) *") },
              minLines = 2,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_address_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Optional instructions
            OutlinedTextField(
              value = deliveryInstructions,
              onValueChange = { deliveryInstructions = it },
              label = { Text("ডেলিভারি সংক্রান্ত বিশেষ নির্দেশনা (ঐচ্ছিক)") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      // 2. Delivery Charge & Calculation Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "২. অর্ডারের হিসাব (Order Summary)",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = AhesanPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "পণ্যের মোট মূল্য (${cartItems.size}টি পণ্য):", fontSize = 13.sp)
              Text(text = "৳${subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "ডেলিভারি চার্জ (${if (isInsideCity) "ঢাকার ভিতরে" else "ঢাকার বাইরে"}):",
                  fontSize = 13.sp
                )
                if (isFreeDelivery) {
                  Text(text = "৳${settings.freeDeliveryThreshold.toInt()} টাকার বেশি অর্ডারে ফ্রি ডেলিভারি!", fontSize = 10.sp, color = Color(0xFF10B981))
                }
              }
              Text(
                text = if (isFreeDelivery) "ফ্রি (৳০)" else "৳${deliveryCharge.toInt()}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isFreeDelivery) Color(0xFF10B981) else Color.Black
              )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = "সর্বমোট পরিমাণ:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
              Text(text = "৳${grandTotal.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = AhesanPrimary)
            }
          }
        }
      }

      // 3. Payment Method Selection (Option A vs Option B)
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "৩. পেমেন্ট পদ্ধতি নির্বাচন করুন (Payment Option)",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = AhesanPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Option A: FULL PAYMENT
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (paymentType == PaymentType.FULL_PAYMENT) BkashPinkLight else MaterialTheme.colorScheme.surfaceVariant,
              border = if (paymentType == PaymentType.FULL_PAYMENT) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BkashPink)) else null,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { paymentType = PaymentType.FULL_PAYMENT }
                .testTag("payment_opt_full")
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = paymentType == PaymentType.FULL_PAYMENT,
                  onClick = { paymentType = PaymentType.FULL_PAYMENT },
                  colors = RadioButtonDefaults.colors(selectedColor = BkashPink)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "সম্পূর্ণ পেমেন্ট (Full Payment)",
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = if (paymentType == PaymentType.FULL_PAYMENT) BkashPink else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(color = BkashPink, shape = RoundedCornerShape(4.dp)) {
                      Text(
                        text = "bKash",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = "পণ্য মূল্য + ডেলিভারি চার্জ = সম্পূর্ণ টাকা bKash-এ অগ্রিম পরিশোধ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = "প্রদেয় অগ্রিম টাকা: ৳${grandTotal.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BkashPink
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Option B: CASH ON DELIVERY
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = if (paymentType == PaymentType.CASH_ON_DELIVERY) Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant,
              border = if (paymentType == PaymentType.CASH_ON_DELIVERY) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(AhesanSecondary)) else null,
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable { paymentType = PaymentType.CASH_ON_DELIVERY }
                .testTag("payment_opt_cod")
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = paymentType == PaymentType.CASH_ON_DELIVERY,
                  onClick = { paymentType = PaymentType.CASH_ON_DELIVERY },
                  colors = RadioButtonDefaults.colors(selectedColor = AhesanSecondary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "Cash on Delivery",
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = if (paymentType == PaymentType.CASH_ON_DELIVERY) Color(0xFF9A3412) else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(color = AhesanSecondary, shape = RoundedCornerShape(4.dp)) {
                      Text(
                        text = "COD",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = "পণ্যের মূল্য ডেলিভারির সময় Cash; ডেলিভারি চার্জ অগ্রিম bKash",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = "অগ্রিম প্রদেয় ডেলিভারি চার্জ: ৳${deliveryCharge.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9A3412)
                  )
                }
              }
            }

            // CRITICAL COD Explicit Disclaimer requirement
            if (paymentType == PaymentType.CASH_ON_DELIVERY) {
              Spacer(modifier = Modifier.height(10.dp))
              Surface(
                color = Color(0xFFFFFBEB),
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFF59E0B)))
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "“Cash on Delivery নির্বাচন করলে পণ্যের মূল্য ডেলিভারির সময় Cash-এ পরিশোধ করতে হবে। তবে Delivery Charge অর্ডার নিশ্চিত করার আগে bKash-এর মাধ্যমে Advance পরিশোধ করতে হবে।”",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF92400E),
                    lineHeight = 16.sp
                  )
                }
              }
            }
          }
        }
      }

      // 4. bKash Payment Instructions & Numbers
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "৪. bKash পেমেন্ট নির্দেশনা",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = BkashPink
              )
              Surface(
                color = BkashPink,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "Personal Send Money",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "অনুগ্রহ করে নিচে উল্লেখিত যেকোনো একটি bKash পার্সোনাল নম্বরে Send Money করুন:",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Number 1: 01823473621
            BkashNumberRow(
              number = settings.bkashNumber1,
              label = "bKash Personal ১",
              isSelected = selectedBkashNumber == settings.bkashNumber1,
              onSelect = { selectedBkashNumber = settings.bkashNumber1 },
              onCopy = {
                clipboardManager.setText(AnnotatedString(settings.bkashNumber1))
                Toast.makeText(context, "${settings.bkashNumber1} কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
              }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Number 2: 01613285997
            BkashNumberRow(
              number = settings.bkashNumber2,
              label = "bKash Personal ২",
              isSelected = selectedBkashNumber == settings.bkashNumber2,
              onSelect = { selectedBkashNumber = settings.bkashNumber2 },
              onCopy = {
                clipboardManager.setText(AnnotatedString(settings.bkashNumber2))
                Toast.makeText(context, "${settings.bkashNumber2} কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
              }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Summary Breakdown for clarity
            Surface(
              color = MaterialTheme.colorScheme.surfaceVariant,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = "পেমেন্ট সারসংক্ষেপ (Payment Breakdown):",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (paymentType == PaymentType.FULL_PAYMENT) {
                  Text(text = "• পণ্যের মূল্য: ৳${subtotal.toInt()}", fontSize = 12.sp)
                  Text(text = "• ডেলিভারি চার্জ: ৳${deliveryCharge.toInt()}", fontSize = 12.sp)
                  Text(
                    text = "• bKash-এ এখনই পাঠাতে হবে: ৳${grandTotal.toInt()} (Total Paid in Advance)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BkashPink
                  )
                } else {
                  Text(
                    text = "• পণ্যের মূল্য (ডেলিভারির সময় Cash): ৳${subtotal.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = "• অগ্রিম ডেলিভারি চার্জ (bKash): ৳${deliveryCharge.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF9A3412)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = "টাকা পাঠানোর পর নিচের তথ্যগুলো পূরণ করুন:",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Sender bKash Number
            OutlinedTextField(
              value = bkashSenderNumber,
              onValueChange = { bkashSenderNumber = it },
              label = { Text("যে bKash নম্বর থেকে টাকা পাঠিয়েছেন *") },
              singleLine = true,
              placeholder = { Text("01XXXXXXXXX") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_bkash_sender_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Transaction ID (TrxID)
            OutlinedTextField(
              value = trxId,
              onValueChange = { trxId = it.uppercase() },
              label = { Text("bKash Transaction ID (TrxID) *") },
              singleLine = true,
              placeholder = { Text("যেমন: BKX8923KL") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("checkout_trxid_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // MANDATORY DISCLAIMER
            Surface(
              color = Color(0xFFF1F5F9),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "⚠️ বিশেষ বিজ্ঞপ্তি: আপনার প্রদত্ত bKash TrxID এবং প্রেরক নম্বরটি আমাদের অ্যাডমিন টিম ম্যানুয়ালি যাচাই (Verify) করার পর অর্ডারটি নিশ্চিত করা হবে।",
                fontSize = 10.sp,
                color = Color.DarkGray,
                lineHeight = 14.sp,
                modifier = Modifier.padding(8.dp)
              )
            }
          }
        }
      }

      // 5. WhatsApp Customer Care Support Banner
      item {
        WhatsAppCustomerCareBanner(phoneNumber = settings.customerCareWhatsApp)
      }

      // 6. Submit Order Button
      item {
        Button(
          onClick = {
            if (name.isBlank() || phone.isBlank() || address.isBlank() || thana.isBlank()) {
              Toast.makeText(context, "অনুগ্রহ করে সব তারকাচিহ্নিত (*) তথ্য পূরণ করুন", Toast.LENGTH_LONG).show()
              return@Button
            }
            if (bkashSenderNumber.isBlank() || trxId.isBlank()) {
              Toast.makeText(context, "অনুগ্রহ করে bKash নম্বর এবং Transaction ID দিন", Toast.LENGTH_LONG).show()
              return@Button
            }

            isSubmitting = true
            viewModel.saveUserProfile(name, phone, "", address, selectedDistrict, thana)

            viewModel.placeOrder(
              customerName = name.trim(),
              customerPhone = phone.trim(),
              deliveryAddress = address.trim(),
              district = selectedDistrict,
              thana = thana.trim(),
              deliveryInstructions = deliveryInstructions.trim(),
              subtotal = subtotal,
              deliveryCharge = deliveryCharge,
              totalAmount = grandTotal,
              paymentMethod = if (paymentType == PaymentType.FULL_PAYMENT) "FULL_PAYMENT" else "CASH_ON_DELIVERY",
              bkashSenderNumber = bkashSenderNumber.trim(),
              trxId = trxId.trim(),
              paidAmount = bkashAdvanceRequired,
              targetBkashNumber = selectedBkashNumber,
              onSuccess = { orderId ->
                isSubmitting = false
                onOrderPlaced(orderId)
              }
            )
          },
          enabled = !isSubmitting && cartItems.isNotEmpty(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("submit_order_btn")
        ) {
          Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isSubmitting) "অর্ডার প্রক্রিয়াধীন..." else "অর্ডার নিশ্চিত করুন (Confirm Order)",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun BkashNumberRow(
  number: String,
  label: String,
  isSelected: Boolean,
  onSelect: () -> Unit,
  onCopy: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) BkashPinkLight else Color(0xFFF8FAFC),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) BkashPink else Color(0xFFCBD5E1))
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .clickable { onSelect() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
          selected = isSelected,
          onClick = onSelect,
          colors = RadioButtonDefaults.colors(selectedColor = BkashPink)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Text(text = label, fontSize = 11.sp, color = Color.Gray)
          Text(
            text = number,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) BkashPink else Color.Black
          )
        }
      }

      IconButton(onClick = onCopy, modifier = Modifier.size(36.dp)) {
        Icon(
          imageVector = Icons.Default.ContentCopy,
          contentDescription = "Copy Number",
          tint = BkashPink,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
