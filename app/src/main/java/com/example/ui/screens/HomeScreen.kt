package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ProductEntity
import com.example.ui.components.ProductCard
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
fun HomeScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val filteredProducts by viewModel.filteredProducts.collectAsState()
  val allProducts by viewModel.allProducts.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val banners by viewModel.banners.collectAsState()
  val cartItemCount by viewModel.cartItemCount.collectAsState()
  val settings by viewModel.storeSettings.collectAsState()
  val wishlistItems by viewModel.wishlistItems.collectAsState()

  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()
  val selectedSort by viewModel.selectedSortOption.collectAsState()
  val inStockOnly by viewModel.inStockOnly.collectAsState()
  val discountOnly by viewModel.discountOnly.collectAsState()

  var showFilterSheet by remember { mutableStateOf(false) }

  val featuredProducts = remember(allProducts) { allProducts.filter { it.isFeatured } }
  val newArrivals = remember(allProducts) { allProducts.filter { it.isNewArrival } }
  val discountProducts = remember(allProducts) {
    allProducts.filter { it.discountPrice != null && it.discountPrice < it.regularPrice }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Bento Top Brand Header
    item {
      Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, BentoCardBorder),
        shadowElevation = 1.dp
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.testTag("app_brand_header")
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .background(AhesanPrimary, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ShoppingBag,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "BANGLADESH EDITION",
                  color = AhesanPrimary,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 1.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "AHESAN ",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                  )
                  Text(
                    text = "SHOP",
                    color = AhesanPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                  )
                }
              }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
              // Quick WhatsApp Header button
              IconButton(
                onClick = {
                  WhatsAppHelper.openWhatsAppChat(
                    context = context,
                    phoneNumber = settings.customerCareWhatsApp,
                    message = "অর্ডার বা যেকোনো সমস্যার জন্য WhatsApp-এ যোগাযোগ করতে চাই।"
                  )
                },
                modifier = Modifier
                  .size(40.dp)
                  .background(Color(0xFFDCFCE7), RoundedCornerShape(12.dp))
                  .testTag("header_whatsapp_btn")
              ) {
                Icon(
                  imageVector = Icons.Default.Chat,
                  contentDescription = "WhatsApp Customer Care",
                  tint = WhatsAppGreen,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.width(8.dp))

              // Cart Header Icon with badge
              IconButton(
                onClick = { viewModel.setTab(ScreenTab.CART) },
                modifier = Modifier
                  .size(40.dp)
                  .background(BentoSlate100, RoundedCornerShape(12.dp))
                  .testTag("header_cart_btn")
              ) {
                BadgedBox(
                  badge = {
                    if (cartItemCount > 0) {
                      Badge(
                        containerColor = AhesanSecondary,
                        contentColor = Color.White
                      ) {
                        Text(text = cartItemCount.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                ) {
                  Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Bento Search Bar
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("search_bar_input"),
            placeholder = {
              Text(
                text = "পণ্য, ব্র্যান্ড বা ক্যাটাগরি খুঁজুন...",
                color = TextTertiary,
                fontSize = 13.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                  Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear search",
                    tint = TextSecondary
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = BentoSlate100,
              unfocusedContainerColor = BentoSlate100,
              focusedBorderColor = AhesanPrimary.copy(alpha = 0.5f),
              unfocusedBorderColor = Color.Transparent
            )
          )
        }
      }
    }

    // 2. Bento Filter & Sort Chips Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // In-Stock Filter
        FilterChip(
          selected = inStockOnly,
          onClick = { viewModel.inStockOnly.value = !inStockOnly },
          label = { Text("স্টকে আছে", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = AhesanPrimary.copy(alpha = 0.15f),
            selectedLabelColor = AhesanPrimary,
            labelColor = TextPrimary
          ),
          border = BorderStroke(1.dp, if (inStockOnly) AhesanPrimary else BentoCardBorder)
        )

        // Discount Filter
        FilterChip(
          selected = discountOnly,
          onClick = { viewModel.discountOnly.value = !discountOnly },
          label = { Text("ছাড়ের অফার", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          shape = RoundedCornerShape(12.dp),
          leadingIcon = {
            Icon(Icons.Default.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp))
          },
          colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = AhesanSecondary.copy(alpha = 0.15f),
            selectedLabelColor = AhesanSecondary,
            labelColor = TextPrimary
          ),
          border = BorderStroke(1.dp, if (discountOnly) AhesanSecondary else BentoCardBorder)
        )

        // Sort: Low to High
        FilterChip(
          selected = selectedSort == SortOption.PRICE_LOW_TO_HIGH,
          onClick = {
            viewModel.selectedSortOption.value =
              if (selectedSort == SortOption.PRICE_LOW_TO_HIGH) SortOption.NEWEST else SortOption.PRICE_LOW_TO_HIGH
          },
          label = { Text("দাম: কম থেকে বেশি", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = AhesanPrimary.copy(alpha = 0.15f),
            selectedLabelColor = AhesanPrimary,
            labelColor = TextPrimary
          ),
          border = BorderStroke(1.dp, if (selectedSort == SortOption.PRICE_LOW_TO_HIGH) AhesanPrimary else BentoCardBorder)
        )

        // Sort: High to Low
        FilterChip(
          selected = selectedSort == SortOption.PRICE_HIGH_TO_LOW,
          onClick = {
            viewModel.selectedSortOption.value =
              if (selectedSort == SortOption.PRICE_HIGH_TO_LOW) SortOption.NEWEST else SortOption.PRICE_HIGH_TO_LOW
          },
          label = { Text("দাম: বেশি থেকে কম", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = AhesanPrimary.copy(alpha = 0.15f),
            selectedLabelColor = AhesanPrimary,
            labelColor = TextPrimary
          ),
          border = BorderStroke(1.dp, if (selectedSort == SortOption.PRICE_HIGH_TO_LOW) AhesanPrimary else BentoCardBorder)
        )

        // Sort: Popular
        FilterChip(
          selected = selectedSort == SortOption.POPULAR,
          onClick = {
            viewModel.selectedSortOption.value =
              if (selectedSort == SortOption.POPULAR) SortOption.NEWEST else SortOption.POPULAR
          },
          label = { Text("জনপ্রিয়", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
          shape = RoundedCornerShape(12.dp),
          colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            selectedContainerColor = AhesanPrimary.copy(alpha = 0.15f),
            selectedLabelColor = AhesanPrimary,
            labelColor = TextPrimary
          ),
          border = BorderStroke(1.dp, if (selectedSort == SortOption.POPULAR) AhesanPrimary else BentoCardBorder)
        )
      }
    }

    // 3. Bento Promotional Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 4.dp)
      ) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          border = BorderStroke(1.dp, BentoCardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_promo_banner")
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(AhesanPrimary, Color(0xFF0F766E), Color(0xFF134E4A))
                )
              )
              .padding(20.dp)
          ) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
              Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "🔥 বিশেষ মেগা অফার • ৫০% পর্যন্ত ছাড়",
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "AHESAN SHOP",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
              )

              Text(
                text = "সেরা কোয়ালিটি ও দ্রুততম ডেলিভারি সারা বাংলাদেশে!",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 16.sp
              )

              Spacer(modifier = Modifier.height(12.dp))

              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  color = Color.White,
                  shape = RoundedCornerShape(12.dp),
                  modifier = Modifier.clickable { viewModel.discountOnly.value = true }
                ) {
                  Text(
                    text = "এখনই কিনুন",
                    color = AhesanPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "ক্যাশ অন ডেলিভারি সুবিধা",
                  color = Color.White.copy(alpha = 0.95f),
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }
    }

    // 4. Bento Categories Squircle Pods
    item {
      Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ক্যাটাগরি সমূহ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
          )
          Text(
            text = "সব দেখুন >",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = AhesanPrimary,
            modifier = Modifier.clickable { viewModel.setTab(ScreenTab.CATEGORIES) }
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // "All" bento item
          item {
            val isAllSelected = selectedCategory == null
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { viewModel.selectedCategoryFilter.value = null }
                .padding(2.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .background(
                    if (isAllSelected) AhesanPrimary else Color.White,
                    RoundedCornerShape(18.dp)
                  )
                  .border(
                    BorderStroke(
                      1.dp,
                      if (isAllSelected) AhesanPrimary else BentoCardBorder
                    ),
                    RoundedCornerShape(18.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ShoppingBag,
                  contentDescription = "All",
                  tint = if (isAllSelected) Color.White else AhesanPrimary,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "সব পণ্য",
                color = if (isAllSelected) AhesanPrimary else TextPrimary,
                fontSize = 11.sp,
                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium
              )
            }
          }

          items(categories) { cat ->
            val isSelected = selectedCategory.equals(cat.name, ignoreCase = true)
            val icon = getCategoryIcon(cat.name, cat.iconName)
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable {
                  viewModel.selectedCategoryFilter.value = if (isSelected) null else cat.name
                }
                .padding(2.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .background(
                    if (isSelected) AhesanPrimary else Color.White,
                    RoundedCornerShape(18.dp)
                  )
                  .border(
                    BorderStroke(
                      1.dp,
                      if (isSelected) AhesanPrimary else BentoCardBorder
                    ),
                    RoundedCornerShape(18.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = cat.name,
                  tint = if (isSelected) Color.White else AhesanPrimary,
                  modifier = Modifier.size(24.dp)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = cat.nameBn.ifEmpty { cat.name },
                color = if (isSelected) AhesanPrimary else TextPrimary,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
              )
            }
          }
        }
      }
    }

    // 5. WhatsApp Customer Care Banner
    item {
      Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
        WhatsAppCustomerCareBanner(phoneNumber = settings.customerCareWhatsApp)
      }
    }

    // If searching or filtering by category, show Search Results Grid
    if (searchQuery.isNotEmpty() || selectedCategory != null) {
      item {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (searchQuery.isNotEmpty()) "অনুসন্ধানের ফলাফল (${filteredProducts.size})"
              else "$selectedCategory (${filteredProducts.size}টি পণ্য)",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
            if (selectedCategory != null) {
              Text(
                text = "ফিল্টার মুছুন",
                fontSize = 12.sp,
                color = Color.Red,
                modifier = Modifier.clickable {
                  viewModel.selectedCategoryFilter.value = null
                  viewModel.searchQuery.value = ""
                }
              )
            }
          }

          if (filteredProducts.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "কোনো পণ্য খুঁজে পাওয়া যায়নি। অন্য কিছু লিখে খুঁজুন।",
                color = Color.Gray,
                fontSize = 13.sp
              )
            }
          }
        }
      }

      // Display products in chunks of 2 for a clean grid inside LazyColumn
      val productPairs = filteredProducts.chunked(2)
      items(productPairs) { pair ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          for (product in pair) {
            val isWish = wishlistItems.any { it.productId == product.id }
            Box(modifier = Modifier.weight(1f)) {
              ProductCard(
                product = product,
                isWishlisted = isWish,
                onClick = { viewModel.openProductDetail(product) },
                onAddToCart = { viewModel.addToCart(product) },
                onToggleWishlist = { viewModel.toggleWishlist(product) }
              )
            }
          }
          if (pair.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    } else {
      // Normal Home sections: Featured, New, Discount, Recommended

      // A. Featured Products (ফিচার্ড পণ্য)
      if (featuredProducts.isNotEmpty()) {
        item {
          SectionHeader(
            title = "ফিচার্ড পণ্যসমূহ",
            subtitle = "আমাদের বিশেষ বাছাইকৃত সেরা পণ্য",
            onSeeAll = { viewModel.setTab(ScreenTab.CATEGORIES) }
          )
        }

        item {
          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 12.dp)
          ) {
            items(featuredProducts) { prod ->
              val isWish = wishlistItems.any { it.productId == prod.id }
              Box(modifier = Modifier.width(170.dp)) {
                ProductCard(
                  product = prod,
                  isWishlisted = isWish,
                  onClick = { viewModel.openProductDetail(prod) },
                  onAddToCart = { viewModel.addToCart(prod) },
                  onToggleWishlist = { viewModel.toggleWishlist(prod) }
                )
              }
            }
          }
        }
      }

      // B. Discount Products (ছাড়ের অফার)
      if (discountProducts.isNotEmpty()) {
        item {
          SectionHeader(
            title = "সেরা ছাড়ের ধামাকা অফার",
            subtitle = "সীমিত সময়ের জন্য বিশেষ মূল্যছাড়",
            onSeeAll = { viewModel.discountOnly.value = true }
          )
        }

        item {
          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 12.dp)
          ) {
            items(discountProducts) { prod ->
              val isWish = wishlistItems.any { it.productId == prod.id }
              Box(modifier = Modifier.width(170.dp)) {
                ProductCard(
                  product = prod,
                  isWishlisted = isWish,
                  onClick = { viewModel.openProductDetail(prod) },
                  onAddToCart = { viewModel.addToCart(prod) },
                  onToggleWishlist = { viewModel.toggleWishlist(prod) }
                )
              }
            }
          }
        }
      }

      // C. New Arrivals (নতুন আগমন)
      if (newArrivals.isNotEmpty()) {
        item {
          SectionHeader(
            title = "নতুন পণ্য কালেকশন",
            subtitle = "সদ্য যুক্ত হওয়া ট্রেন্ডিং আইটেমস",
            onSeeAll = { viewModel.setTab(ScreenTab.CATEGORIES) }
          )
        }

        item {
          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 12.dp)
          ) {
            items(newArrivals) { prod ->
              val isWish = wishlistItems.any { it.productId == prod.id }
              Box(modifier = Modifier.width(170.dp)) {
                ProductCard(
                  product = prod,
                  isWishlisted = isWish,
                  onClick = { viewModel.openProductDetail(prod) },
                  onAddToCart = { viewModel.addToCart(prod) },
                  onToggleWishlist = { viewModel.toggleWishlist(prod) }
                )
              }
            }
          }
        }
      }

      // D. All Products Grid (আপনার জন্য নির্বাচিত পণ্য)
      item {
        SectionHeader(
          title = "আপনার জন্য বিশেষ পণ্য",
          subtitle = "সকল কোয়ালিটি পণ্য একসাথে",
          onSeeAll = null
        )
      }

      val allPairs = allProducts.chunked(2)
      items(allPairs) { pair ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          for (product in pair) {
            val isWish = wishlistItems.any { it.productId == product.id }
            Box(modifier = Modifier.weight(1f)) {
              ProductCard(
                product = product,
                isWishlisted = isWish,
                onClick = { viewModel.openProductDetail(product) },
                onAddToCart = { viewModel.addToCart(product) },
                onToggleWishlist = { viewModel.toggleWishlist(product) }
              )
            }
          }
          if (pair.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
          }
        }
      }
    }
  }
}

@Composable
fun SectionHeader(
  title: String,
  subtitle: String,
  onSeeAll: (() -> Unit)? = null
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column {
      Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
    if (onSeeAll != null) {
      Text(
        text = "সব দেখুন >",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = AhesanPrimary,
        modifier = Modifier.clickable { onSeeAll() }
      )
    }
  }
}
