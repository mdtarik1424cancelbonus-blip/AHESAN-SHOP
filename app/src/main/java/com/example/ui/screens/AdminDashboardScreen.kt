package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.CategoryEntity
import com.example.data.local.OrderEntity
import com.example.data.local.ProductEntity
import com.example.data.local.StoreSettingsEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.AhesanSecondary
import com.example.ui.theme.BkashPink

enum class AdminSection {
  ORDERS,
  PRODUCTS,
  CATEGORIES,
  SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: ShopViewModel,
  onClose: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedSection by remember { mutableStateOf(AdminSection.ORDERS) }

  val orders by viewModel.orders.collectAsState()
  val products by viewModel.allProducts.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val settings by viewModel.storeSettings.collectAsState()

  // State for Add/Edit Product Dialog
  var showProductDialog by remember { mutableStateOf(false) }
  var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

  // State for Add Category Dialog
  var showCategoryDialog by remember { mutableStateOf(false) }

  // State for Order Status Update Dialog
  var statusOrderTarget by remember { mutableStateOf<OrderEntity?>(null) }
  var newOrderStatusInput by remember { mutableStateOf("") }
  var adminNoteInput by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(text = "অ্যাডমিন কন্ট্রোল প্যানেল", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = "AHESAN SHOP Management", fontSize = 11.sp, color = Color.LightGray)
          }
        },
        navigationIcon = {
          IconButton(onClick = onClose) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close", tint = Color.White)
          }
        },
        actions = {
          TextButton(
            onClick = {
              viewModel.logoutAdmin()
              onClose()
            }
          ) {
            Text(text = "লগআউট", color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color(0xFF0F172A),
          titleContentColor = Color.White
        )
      )
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      // Top Navigation Tabs
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF1E293B))
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AdminNavTab(
          title = "অর্ডারসমূহ (${orders.size})",
          icon = Icons.Default.Receipt,
          isSelected = selectedSection == AdminSection.ORDERS,
          onClick = { selectedSection = AdminSection.ORDERS }
        )
        AdminNavTab(
          title = "পণ্যসমূহ (${products.size})",
          icon = Icons.Default.Inventory,
          isSelected = selectedSection == AdminSection.PRODUCTS,
          onClick = { selectedSection = AdminSection.PRODUCTS }
        )
        AdminNavTab(
          title = "ক্যাটাগরি (${categories.size})",
          icon = Icons.Default.Category,
          isSelected = selectedSection == AdminSection.CATEGORIES,
          onClick = { selectedSection = AdminSection.CATEGORIES }
        )
        AdminNavTab(
          title = "দোকান সেটিংস",
          icon = Icons.Default.Settings,
          isSelected = selectedSection == AdminSection.SETTINGS,
          onClick = { selectedSection = AdminSection.SETTINGS }
        )
      }

      // Main Content Area
      when (selectedSection) {
        AdminSection.ORDERS -> {
          AdminOrdersView(
            orders = orders,
            onVerifyPayment = { order, approve ->
              viewModel.verifyPaymentAndConfirmOrder(
                orderId = order.orderId,
                approve = approve,
                note = if (approve) "Payment verified by Admin via bKash" else "Invalid Transaction ID"
              )
              Toast.makeText(context, if (approve) "অর্ডার কনফার্ম করা হয়েছে" else "পেমেন্ট বাতিল করা হয়েছে", Toast.LENGTH_SHORT).show()
            },
            onUpdateStatusClick = { order ->
              statusOrderTarget = order
              newOrderStatusInput = order.orderStatus
              adminNoteInput = order.adminNote ?: ""
            }
          )
        }
        AdminSection.PRODUCTS -> {
          AdminProductsView(
            products = products,
            onAddClick = {
              editingProduct = null
              showProductDialog = true
            },
            onEditClick = { prod ->
              editingProduct = prod
              showProductDialog = true
            },
            onDeleteClick = { prod ->
              viewModel.deleteProduct(prod.id)
              Toast.makeText(context, "পণ্য মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
            }
          )
        }
        AdminSection.CATEGORIES -> {
          AdminCategoriesView(
            categories = categories,
            onAddClick = { showCategoryDialog = true },
            onDeleteClick = { cat ->
              viewModel.deleteCategory(cat)
              Toast.makeText(context, "ক্যাটাগরি মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
            }
          )
        }
        AdminSection.SETTINGS -> {
          AdminSettingsView(
            settings = settings,
            onSave = { updated ->
              viewModel.updateSettings(updated)
              Toast.makeText(context, "সেটিংস সফলভাবে সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
            }
          )
        }
      }
    }
  }

  // Update Status Modal
  if (statusOrderTarget != null) {
    val statuses = listOf(
      "Payment Verification Pending",
      "Delivery Charge Verification Pending",
      "Confirmed",
      "Confirmed – Cash on Delivery",
      "Processing",
      "Shipped",
      "Delivered",
      "Cancelled",
      "Payment Rejected"
    )
    var statusDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = { statusOrderTarget = null },
      title = { Text("অর্ডারের স্ট্যাটাস পরিবর্তন (${statusOrderTarget!!.orderId})") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          ExposedDropdownMenuBox(
            expanded = statusDropdownExpanded,
            onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded }
          ) {
            OutlinedTextField(
              value = newOrderStatusInput,
              onValueChange = {},
              readOnly = true,
              label = { Text("নতুন স্ট্যাটাস") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
              expanded = statusDropdownExpanded,
              onDismissRequest = { statusDropdownExpanded = false }
            ) {
              statuses.forEach { st ->
                DropdownMenuItem(
                  text = { Text(st) },
                  onClick = {
                    newOrderStatusInput = st
                    statusDropdownExpanded = false
                  }
                )
              }
            }
          }

          OutlinedTextField(
            value = adminNoteInput,
            onValueChange = { adminNoteInput = it },
            label = { Text("অ্যাডমিন নোট / কাস্টমার মেসেজ") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateOrderStatus(statusOrderTarget!!.orderId, newOrderStatusInput, adminNoteInput)
            statusOrderTarget = null
            Toast.makeText(context, "অর্ডার স্ট্যাটাস আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary)
        ) {
          Text("সংরক্ষণ করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { statusOrderTarget = null }) {
          Text("বাতিল")
        }
      }
    )
  }

  // Add / Edit Product Modal
  if (showProductDialog) {
    ProductFormDialog(
      existing = editingProduct,
      categories = categories,
      onDismiss = { showProductDialog = false },
      onSave = { prod ->
        if (editingProduct == null) {
          viewModel.addProduct(prod)
        } else {
          viewModel.updateProduct(prod.copy(id = editingProduct!!.id))
        }
        showProductDialog = false
        Toast.makeText(context, "পণ্য সফলভাবে সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
      }
    )
  }

  // Add Category Modal
  if (showCategoryDialog) {
    var catName by remember { mutableStateOf("") }
    var catNameBn by remember { mutableStateOf("") }
    var catIcon by remember { mutableStateOf("category") }

    AlertDialog(
      onDismissRequest = { showCategoryDialog = false },
      title = { Text("নতুন ক্যাটাগরি যোগ করুন") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = catName,
            onValueChange = { catName = it },
            label = { Text("ইংরেজি নাম (e.g. Perfume)") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = catNameBn,
            onValueChange = { catNameBn = it },
            label = { Text("বাংলা নাম (e.g. পারফিউম ও সুবাস)") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (catName.isNotBlank()) {
              viewModel.addCategory(
                CategoryEntity(
                  name = catName.trim(),
                  nameBn = catNameBn.trim(),
                  iconName = catIcon
                )
              )
              showCategoryDialog = false
              Toast.makeText(context, "ক্যাটাগরি যোগ করা হয়েছে", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary)
        ) {
          Text("যোগ করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showCategoryDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }
}

@Composable
fun AdminNavTab(
  title: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) AhesanPrimary else Color(0xFF334155),
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = title,
        color = Color.White,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        fontSize = 12.sp
      )
    }
  }
}

// ----------------------------------------------------
// Section 1: Orders & Manual Payment Verification View
// ----------------------------------------------------
@Composable
fun AdminOrdersView(
  orders: List<OrderEntity>,
  onVerifyPayment: (OrderEntity, Boolean) -> Unit,
  onUpdateStatusClick: (OrderEntity) -> Unit
) {
  if (orders.isEmpty()) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
      Text("বর্তমানে কোনো অর্ডার নেই।", color = Color.Gray)
    }
    return
  }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    items(orders) { order ->
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth().testTag("admin_order_card_${order.orderId}")
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Header
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = order.orderId, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AhesanPrimary)
              Text(text = "গ্রাহক: ${order.customerName} (${order.customerPhone})", fontSize = 12.sp)
            }
            StatusBadge(status = order.orderStatus)
          }

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

          // Payment details for manual verification
          Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text(
                text = "bKash ভেরিফিকেশন তথ্য:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = BkashPink
              )
              Text(text = "পেমেন্ট ধরন: ${if (order.paymentMethod == "FULL_PAYMENT") "সম্পূর্ণ পেমেন্ট" else "Cash on Delivery"}", fontSize = 12.sp)
              Text(text = "Transaction ID (TrxID): ${order.trxId}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = BkashPink)
              Text(text = "প্রেরক bKash নম্বর: ${order.bkashNumber}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              Text(text = "পরিশোধিত অগ্রিম টাকা: ৳${(order.paidAmount ?: 0.0).toInt()} (মোট অর্ডার: ৳${order.totalAmount.toInt()})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text(text = "বর্তমান পেমেন্ট স্ট্যাটাস: ${order.paymentStatus}", fontSize = 11.sp, color = Color.DarkGray)
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(text = "ডেলিভারি ঠিকানা: ${order.deliveryAddress}, ${order.thana}, ${order.district}", fontSize = 11.sp, color = Color.Gray)

          Spacer(modifier = Modifier.height(10.dp))

          // Manual Verification & Status Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // If verification pending, offer quick Approve / Reject
            if (order.orderStatus.contains("Pending", ignoreCase = true)) {
              Button(
                onClick = { onVerifyPayment(order, true) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f).height(38.dp)
              ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("পেমেন্ট কনফার্ম", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = { onVerifyPayment(order, false) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                modifier = Modifier.weight(1f).height(38.dp)
              ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("পেমেন্ট বাতিল", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            OutlinedButton(
              onClick = { onUpdateStatusClick(order) },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.weight(1f).height(38.dp)
            ) {
              Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("স্ট্যাটাস পরিবর্তন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

// ----------------------------------------------------
// Section 2: Products Management View
// ----------------------------------------------------
@Composable
fun AdminProductsView(
  products: List<ProductEntity>,
  onAddClick: () -> Unit,
  onEditClick: (ProductEntity) -> Unit,
  onDeleteClick: (ProductEntity) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "মোট পণ্য: ${products.size}টি", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("admin_add_product_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("নতুন পণ্য যোগ করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    LazyColumn(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(products) { prod ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = prod.imageUrl,
              contentDescription = null,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(text = prod.nameBn.ifEmpty { prod.name }, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
              Text(text = "ক্যাটাগরি: ${prod.category} • স্টক: ${prod.stockQuantity}টি", fontSize = 11.sp, color = Color.Gray)
              Text(
                text = "দাম: ৳${(prod.discountPrice ?: prod.regularPrice).toInt()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AhesanPrimary
              )
            }

            Row {
              IconButton(onClick = { onEditClick(prod) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = AhesanPrimary, modifier = Modifier.size(20.dp))
              }
              IconButton(onClick = { onDeleteClick(prod) }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
              }
            }
          }
        }
      }
    }
  }
}

// ----------------------------------------------------
// Section 3: Categories Management View
// ----------------------------------------------------
@Composable
fun AdminCategoriesView(
  categories: List<CategoryEntity>,
  onAddClick: () -> Unit,
  onDeleteClick: (CategoryEntity) -> Unit
) {
  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = "ক্যাটাগরি সংখ্যা: ${categories.size}টি", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      Button(
        onClick = onAddClick,
        colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
        shape = RoundedCornerShape(8.dp)
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("নতুন ক্যাটাগরি", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    LazyColumn(
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      items(categories) { cat ->
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = cat.nameBn.ifEmpty { cat.name }, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text(text = "English: ${cat.name}", fontSize = 11.sp, color = Color.Gray)
            }
            IconButton(onClick = { onDeleteClick(cat) }) {
              Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
          }
        }
      }
    }
  }
}

// ----------------------------------------------------
// Section 4: Store Settings View (bKash & WhatsApp & Delivery)
// ----------------------------------------------------
@Composable
fun AdminSettingsView(
  settings: StoreSettingsEntity,
  onSave: (StoreSettingsEntity) -> Unit
) {
  var bkash1 by remember { mutableStateOf(settings.bkashNumber1) }
  var bkash2 by remember { mutableStateOf(settings.bkashNumber2) }
  var whatsapp by remember { mutableStateOf(settings.customerCareWhatsApp) }
  var insideCityFee by remember { mutableStateOf(settings.insideCityDeliveryCharge.toString()) }
  var outsideCityFee by remember { mutableStateOf(settings.outsideCityDeliveryCharge.toString()) }
  var freeThreshold by remember { mutableStateOf(settings.freeDeliveryThreshold.toString()) }
  var adminPin by remember { mutableStateOf(settings.adminPin) }

  LazyColumn(
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
    modifier = Modifier.fillMaxSize()
  ) {
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "bKash পেমেন্ট নম্বরসমূহ", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BkashPink)
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = bkash1,
            onValueChange = { bkash1 = it },
            label = { Text("bKash Personal নম্বর ১") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = bkash2,
            onValueChange = { bkash2 = it },
            label = { Text("bKash Personal নম্বর ২") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "কাস্টমার কেয়ার WhatsApp", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AhesanPrimary)
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = whatsapp,
            onValueChange = { whatsapp = it },
            label = { Text("WhatsApp নম্বর (01613285997)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "ডেলিভারি চার্জ ও ফ্রি ডেলিভারি সীমা", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AhesanPrimary)
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = insideCityFee,
            onValueChange = { insideCityFee = it },
            label = { Text("ঢাকার ভিতরে ডেলিভারি চার্জ (টাকা)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = outsideCityFee,
            onValueChange = { outsideCityFee = it },
            label = { Text("ঢাকার বাইরে ডেলিভারি চার্জ (টাকা)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = freeThreshold,
            onValueChange = { freeThreshold = it },
            label = { Text("ফ্রি ডেলিভারি পেতে ন্যূনতম অর্ডার মূল্য (টাকা)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "অ্যাডমিন সুরক্ষা পিন (Security PIN)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AhesanPrimary)
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = adminPin,
            onValueChange = { adminPin = it },
            label = { Text("অ্যাডমিন পিন কোড") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    item {
      Button(
        onClick = {
          onSave(
            settings.copy(
              bkashNumber1 = bkash1.trim(),
              bkashNumber2 = bkash2.trim(),
              customerCareWhatsApp = whatsapp.trim(),
              insideCityDeliveryCharge = insideCityFee.toDoubleOrNull() ?: 70.0,
              outsideCityDeliveryCharge = outsideCityFee.toDoubleOrNull() ?: 130.0,
              freeDeliveryThreshold = freeThreshold.toDoubleOrNull() ?: 3000.0,
              adminPin = adminPin.trim().ifEmpty { "1234" }
            )
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_admin_settings_btn")
      ) {
        Text("সেটিংস সংরক্ষণ করুন (Save Settings)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }
  }
}

// ----------------------------------------------------
// Product Add / Edit Dialog
// ----------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormDialog(
  existing: ProductEntity?,
  categories: List<CategoryEntity>,
  onDismiss: () -> Unit,
  onSave: (ProductEntity) -> Unit
) {
  var name by remember { mutableStateOf(existing?.name ?: "") }
  var nameBn by remember { mutableStateOf(existing?.nameBn ?: "") }
  var description by remember { mutableStateOf(existing?.description ?: "") }
  var regularPrice by remember { mutableStateOf(existing?.regularPrice?.toString() ?: "") }
  var discountPrice by remember { mutableStateOf(existing?.discountPrice?.toString() ?: "") }
  var stockQuantity by remember { mutableStateOf(existing?.stockQuantity?.toString() ?: "10") }
  var category by remember { mutableStateOf(existing?.category ?: categories.firstOrNull()?.name ?: "Electronics") }
  var brand by remember { mutableStateOf(existing?.brand ?: "AHESAN") }
  var sku by remember { mutableStateOf(existing?.sku ?: "") }
  var variantsSize by remember { mutableStateOf(existing?.variantsSize ?: "") }
  var variantsColor by remember { mutableStateOf(existing?.variantsColor ?: "") }
  var imageUrl by remember { mutableStateOf(existing?.imageUrl ?: "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80") }
  var isFeatured by remember { mutableStateOf(existing?.isFeatured ?: false) }
  var isNewArrival by remember { mutableStateOf(existing?.isNewArrival ?: false) }

  var catExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(text = if (existing == null) "নতুন পণ্য যোগ করুন" else "পণ্য সম্পাদনা করুন", fontWeight = FontWeight.Bold) },
    text = {
      LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().height(420.dp)
      ) {
        item {
          OutlinedTextField(value = nameBn, onValueChange = { nameBn = it }, label = { Text("পণ্যের বাংলা নাম *") }, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("English Name") }, modifier = Modifier.fillMaxWidth())
        }
        item {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = regularPrice, onValueChange = { regularPrice = it }, label = { Text("নিয়মিত মূল্য (৳) *") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = discountPrice, onValueChange = { discountPrice = it }, label = { Text("ছাড়ের মূল্য (৳)") }, modifier = Modifier.weight(1f))
          }
        }
        item {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = stockQuantity, onValueChange = { stockQuantity = it }, label = { Text("স্টক পরিমাণ *") }, modifier = Modifier.weight(1f))
            OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("ব্র্যান্ড") }, modifier = Modifier.weight(1f))
          }
        }
        item {
          ExposedDropdownMenuBox(
            expanded = catExpanded,
            onExpandedChange = { catExpanded = !catExpanded }
          ) {
            OutlinedTextField(
              value = category,
              onValueChange = {},
              readOnly = true,
              label = { Text("ক্যাটাগরি") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
              modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
              expanded = catExpanded,
              onDismissRequest = { catExpanded = false }
            ) {
              categories.forEach { c ->
                DropdownMenuItem(
                  text = { Text(c.nameBn.ifEmpty { c.name }) },
                  onClick = {
                    category = c.name
                    catExpanded = false
                  }
                )
              }
            }
          }
        }
        item {
          OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU / কোড") }, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = variantsSize, onValueChange = { variantsSize = it }, label = { Text("সাইজ ভ্যারিয়েন্ট (যেমন: M, L, XL)") }, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = variantsColor, onValueChange = { variantsColor = it }, label = { Text("কালার ভ্যারিয়েন্ট (যেমন: Black, White)") }, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("ছবির লিঙ্ক (Image URL)") }, modifier = Modifier.fillMaxWidth())
        }
        item {
          OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("পণ্যের বিস্তারিত বিবরণ") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        }
        item {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it })
            Text("ফিচার্ড পণ্য", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Checkbox(checked = isNewArrival, onCheckedChange = { isNewArrival = it })
            Text("নতুন আগমন", fontSize = 12.sp)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val reg = regularPrice.toDoubleOrNull() ?: 0.0
          val disc = discountPrice.toDoubleOrNull()
          val stock = stockQuantity.toIntOrNull() ?: 1
          if (nameBn.isNotBlank() && reg > 0) {
            onSave(
              ProductEntity(
                name = name.ifEmpty { nameBn },
                nameBn = nameBn,
                description = description,
                regularPrice = reg,
                discountPrice = if (disc != null && disc > 0 && disc < reg) disc else null,
                stockQuantity = stock,
                category = category,
                brand = brand.ifEmpty { "AHESAN" },
                sku = sku,
                variantsSize = variantsSize,
                variantsColor = variantsColor,
                imageUrl = imageUrl,
                isFeatured = isFeatured,
                isNewArrival = isNewArrival
              )
            )
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary)
      ) {
        Text("সংরক্ষণ করুন")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("বাতিল")
      }
    }
  )
}
