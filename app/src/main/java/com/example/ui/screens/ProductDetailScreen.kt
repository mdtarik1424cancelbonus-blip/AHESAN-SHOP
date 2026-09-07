package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.ProductEntity
import com.example.ui.components.WhatsAppCustomerCareBanner
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.AhesanSecondary
import com.example.ui.theme.GoldStar
import androidx.compose.foundation.BorderStroke
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSlate100
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
  product: ProductEntity,
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val wishlistItems by viewModel.wishlistItems.collectAsState()
  val settings by viewModel.storeSettings.collectAsState()
  val reviews by viewModel.getReviewsForProduct(product.id).collectAsState(emptyList())

  val isWishlisted = wishlistItems.any { it.productId == product.id }

  // Variant selection states
  val availableSizes = remember(product) {
    if (product.variantsSize.isNotBlank()) product.variantsSize.split(",").map { it.trim() } else emptyList()
  }
  val availableColors = remember(product) {
    if (product.variantsColor.isNotBlank()) product.variantsColor.split(",").map { it.trim() } else emptyList()
  }

  var selectedSize by remember { mutableStateOf(availableSizes.firstOrNull()) }
  var selectedColor by remember { mutableStateOf(availableColors.firstOrNull()) }
  var quantity by remember { mutableIntStateOf(1) }
  var showZoomDialog by remember { mutableStateOf(false) }
  var showReviewDialog by remember { mutableStateOf(false) }

  // Add review form state
  var reviewAuthor by remember { mutableStateOf("") }
  var reviewRating by remember { mutableIntStateOf(5) }
  var reviewComment by remember { mutableStateOf("") }

  val hasDiscount = product.discountPrice != null && product.discountPrice < product.regularPrice
  val discountPercent = if (hasDiscount) {
    (((product.regularPrice - product.discountPrice!!) / product.regularPrice) * 100).toInt()
  } else 0
  val currentPrice = product.discountPrice ?: product.regularPrice

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "পণ্য বিবরণ", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary) },
        navigationIcon = {
          IconButton(
            onClick = { viewModel.closeProductDetail() },
            modifier = Modifier.testTag("product_detail_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
          }
        },
        actions = {
          IconButton(onClick = { viewModel.toggleWishlist(product) }) {
            Icon(
              imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "Wishlist",
              tint = if (isWishlisted) Color.Red else TextSecondary
            )
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
      // Bottom Action Bar: Add to Cart & Buy Now
      Surface(
        color = Color.White,
        shadowElevation = 0.dp,
        modifier = Modifier
          .fillMaxWidth()
          .border(BorderStroke(1.dp, BentoCardBorder))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Add to Cart
          OutlinedButton(
            onClick = {
              viewModel.addToCart(product, selectedSize, selectedColor, quantity)
            },
            enabled = product.stockQuantity > 0,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("detail_add_to_cart_btn"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AhesanPrimary),
            border = BorderStroke(1.5.dp, AhesanPrimary)
          ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "কার্টে যোগ করুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }

          // Buy Now
          Button(
            onClick = {
              viewModel.addToCart(product, selectedSize, selectedColor, quantity)
              viewModel.closeProductDetail()
              viewModel.setTab(ScreenTab.CART)
              viewModel.startCheckout()
            },
            enabled = product.stockQuantity > 0,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AhesanSecondary),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("detail_buy_now_btn")
          ) {
            Text(text = "এখনই কিনুন", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(MaterialTheme.colorScheme.background),
      contentPadding = PaddingValues(bottom = 20.dp)
    ) {
      // 1. Large Product Image with Zoom Button
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.1f)
            .background(Color.White)
        ) {
          AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
              .data(product.imageUrl)
              .crossfade(true)
              .build(),
            contentDescription = product.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
              .fillMaxSize()
              .clickable { showZoomDialog = true }
          )

          // Zoom overlay button
          IconButton(
            onClick = { showZoomDialog = true },
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(12.dp)
              .background(Color.Black.copy(alpha = 0.5f), CircleShape)
          ) {
            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom Image", tint = Color.White)
          }

          // Discount Badge
          if (hasDiscount) {
            Surface(
              color = AhesanSecondary,
              shape = RoundedCornerShape(bottomEnd = 12.dp),
              modifier = Modifier.align(Alignment.TopStart)
            ) {
              Text(
                text = "-$discountPercent% ছাড়",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }

      // 2. Price and Title Card
      item {
        Card(
          shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Price Row
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "৳${currentPrice.toInt()}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = AhesanPrimary
              )
              if (hasDiscount) {
                Text(
                  text = "৳${product.regularPrice.toInt()}",
                  fontSize = 15.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textDecoration = TextDecoration.LineThrough
                )
                Surface(
                  color = AhesanSecondary.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "সেভ করুন ৳${(product.regularPrice - product.discountPrice!!).toInt()}",
                    color = AhesanSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
              text = product.nameBn.ifEmpty { product.name },
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            if (product.nameBn.isNotBlank() && product.name != product.nameBn) {
              Text(
                text = product.name,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Brand, Category & Stock Status Row
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "ব্র্যান্ড: ${product.brand}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              Surface(
                color = if (product.stockQuantity > 0) Color(0xFFD1FAE5) else Color(0xFFFEE2E2),
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = if (product.stockQuantity > 0) "স্টকে আছে (${product.stockQuantity}টি)" else "স্টক শেষ",
                  color = if (product.stockQuantity > 0) Color(0xFF065F46) else Color(0xFF991B1B),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              // SKU
              if (product.sku.isNotBlank()) {
                Text(
                  text = "SKU: ${product.sku}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // 3. Variant Selection (Size, Color, Weight)
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Sizes
            if (availableSizes.isNotEmpty()) {
              Text(
                text = "সাইজ নির্বাচন করুন:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                availableSizes.forEach { size ->
                  val isSelected = selectedSize == size
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) AhesanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { selectedSize = size }
                  ) {
                    Text(
                      text = size,
                      color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(14.dp))
            }

            // Colors
            if (availableColors.isNotEmpty()) {
              Text(
                text = "রং নির্বাচন করুন:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(8.dp))
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                availableColors.forEach { col ->
                  val isSelected = selectedColor == col
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) AhesanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                      .clip(RoundedCornerShape(8.dp))
                      .clickable { selectedColor = col }
                  ) {
                    Text(
                      text = col,
                      color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                  }
                }
              }
              Spacer(modifier = Modifier.height(14.dp))
            }

            // Weight
            if (product.variantsWeight.isNotBlank()) {
              Text(
                text = "ওজন / পরিমাণ: ${product.variantsWeight}",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(14.dp))
            }

            // Quantity Stepper
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "পরিমাণ (Quantity):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                  .padding(4.dp)
              ) {
                IconButton(
                  onClick = { if (quantity > 1) quantity-- },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }
                Text(
                  text = "$quantity",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp,
                  modifier = Modifier.padding(horizontal = 12.dp)
                )
                IconButton(
                  onClick = { if (quantity < product.stockQuantity) quantity++ },
                  modifier = Modifier.size(32.dp)
                ) {
                  Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
              }
            }
          }
        }
      }

      // 4. WhatsApp Customer Care Support Banner (Mandatory on Product Details)
      item {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
          WhatsAppCustomerCareBanner(phoneNumber = settings.customerCareWhatsApp)
        }
      }

      // 5. Product Description Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "পণ্যের বিবরণ",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = product.description,
              fontSize = 13.sp,
              lineHeight = 20.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // 6. Customer Reviews Section
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "গ্রাহক রিভিউ ও রেটিং",
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(Icons.Default.Star, contentDescription = null, tint = GoldStar, modifier = Modifier.size(16.dp))
                  Text(text = "${product.rating}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text(text = "(${reviews.size}টি রিভিউ)", fontSize = 12.sp, color = Color.Gray)
                }
              }

              Button(
                onClick = { showReviewDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(text = "রিভিউ দিন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (reviews.isEmpty()) {
              Text(
                text = "এখনও কোনো লিখিত রিভিউ নেই। প্রথম রিভিউটি আপনি দিন!",
                fontSize = 12.sp,
                color = Color.Gray
              )
            } else {
              reviews.forEach { rev ->
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(text = rev.customerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row {
                      repeat(rev.rating) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldStar, modifier = Modifier.size(12.dp))
                      }
                    }
                  }
                  Spacer(modifier = Modifier.height(3.dp))
                  Text(text = rev.reviewText, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                  HorizontalDivider(modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                }
              }
            }
          }
        }
      }
    }
  }

  // Full-Screen Image Zoom Dialog
  if (showZoomDialog) {
    Dialog(onDismissRequest = { showZoomDialog = false }) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black)
          .clickable { showZoomDialog = false },
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = product.imageUrl,
          contentDescription = "Zoomed",
          modifier = Modifier.fillMaxWidth(),
          contentScale = ContentScale.Fit
        )
      }
    }
  }

  // Add Review Dialog
  if (showReviewDialog) {
    AlertDialog(
      onDismissRequest = { showReviewDialog = false },
      title = { Text(text = "রিভিউ লিখুন", fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = reviewAuthor,
            onValueChange = { reviewAuthor = it },
            label = { Text("আপনার নাম") },
            modifier = Modifier.fillMaxWidth()
          )

          Text(text = "রেটিং নির্বাচন করুন:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            (1..5).forEach { star ->
              IconButton(onClick = { reviewRating = star }, modifier = Modifier.size(36.dp)) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = "$star Stars",
                  tint = if (star <= reviewRating) GoldStar else Color.LightGray
                )
              }
            }
          }

          OutlinedTextField(
            value = reviewComment,
            onValueChange = { reviewComment = it },
            label = { Text("আপনার মতামত ও অভিজ্ঞতা") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (reviewAuthor.isNotBlank() && reviewComment.isNotBlank()) {
              viewModel.addReview(product.id, reviewAuthor, reviewRating, reviewComment)
              showReviewDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = AhesanPrimary)
        ) {
          Text("সাবমিট করুন")
        }
      },
      dismissButton = {
        TextButton(onClick = { showReviewDialog = false }) {
          Text("বাতিল")
        }
      }
    )
  }
}
