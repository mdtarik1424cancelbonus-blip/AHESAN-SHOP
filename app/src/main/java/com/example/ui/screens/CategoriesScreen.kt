package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.IceSkating
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CategoryEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.AhesanPrimary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSlate100
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
  viewModel: ShopViewModel,
  modifier: Modifier = Modifier
) {
  val categories by viewModel.categories.collectAsState()
  val allProducts by viewModel.allProducts.collectAsState()
  val wishlistItems by viewModel.wishlistItems.collectAsState()

  var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = if (selectedCategory == null) "সকল ক্যাটাগরি" else (selectedCategory!!.nameBn.ifEmpty { selectedCategory!!.name }),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = TextPrimary
          )
        },
        navigationIcon = {
          if (selectedCategory != null) {
            IconButton(onClick = { selectedCategory = null }) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = Color.White,
          titleContentColor = TextPrimary,
          navigationIconContentColor = TextPrimary
        ),
        modifier = Modifier.border(BorderStroke(1.dp, BentoCardBorder))
      )
    },
    modifier = modifier.fillMaxSize()
  ) { padding ->
    if (selectedCategory == null) {
      // Categories Grid
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .padding(bottom = 80.dp)
      ) {
        items(categories) { category ->
          val count = allProducts.count { it.category.equals(category.name, ignoreCase = true) }
          CategoryGridCard(
            category = category,
            productCount = count,
            onClick = { selectedCategory = category }
          )
        }
      }
    } else {
      // Products in selected category
      val catProducts = allProducts.filter {
        it.category.equals(selectedCategory!!.name, ignoreCase = true)
      }

      if (catProducts.isEmpty()) {
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
              tint = Color.Gray,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "এই ক্যাটাগরিতে বর্তমানে কোনো পণ্য নেই।",
              fontSize = 14.sp,
              color = Color.Gray
            )
          }
        }
      } else {
        val pairs = catProducts.chunked(2)
        LazyColumn(
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(bottom = 80.dp)
        ) {
          item {
            Text(
              text = "মোট পণ্য: ${catProducts.size}টি",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          items(pairs) { pair ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              for (prod in pair) {
                val isWish = wishlistItems.any { it.productId == prod.id }
                Box(modifier = Modifier.weight(1f)) {
                  ProductCard(
                    product = prod,
                    isWishlisted = isWish,
                    onClick = { viewModel.openProductDetail(prod) },
                    onAddToCart = { viewModel.addToCart(prod) },
                    onToggleWishlist = { viewModel.toggleWishlist(prod) }
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
  }
}

@Composable
fun CategoryGridCard(
  category: CategoryEntity,
  productCount: Int,
  onClick: () -> Unit
) {
  val icon = getCategoryIcon(category.name, category.iconName)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .clickable { onClick() }
      .testTag("category_card_${category.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, BentoCardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .background(BentoSlate100, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = category.name,
          tint = AhesanPrimary,
          modifier = Modifier.size(26.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = category.nameBn.ifEmpty { category.name },
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 2,
        color = TextPrimary
      )

      Spacer(modifier = Modifier.height(4.dp))

      Surface(
        color = BentoSlate100,
        shape = RoundedCornerShape(6.dp)
      ) {
        Text(
          text = "${productCount}টি পণ্য",
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium,
          color = TextSecondary,
          modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }
  }
}

fun getCategoryIcon(name: String, iconName: String): ImageVector {
  val lower = name.lowercase()
  return when {
    lower.contains("mobile") || lower.contains("phone") -> Icons.Default.PhoneAndroid
    lower.contains("electronic") -> Icons.Default.Devices
    lower.contains("men") -> Icons.Default.Male
    lower.contains("women") -> Icons.Default.Female
    lower.contains("fashion") || lower.contains("cloth") -> Icons.Default.Checkroom
    lower.contains("beauty") || lower.contains("care") -> Icons.Default.Spa
    lower.contains("kitchen") || lower.contains("home") -> Icons.Default.Kitchen
    lower.contains("grocery") -> Icons.Default.ShoppingBasket
    lower.contains("sport") -> Icons.Default.SportsSoccer
    lower.contains("shoe") -> Icons.Default.IceSkating
    lower.contains("bag") -> Icons.Default.Luggage
    lower.contains("watch") -> Icons.Default.Watch
    lower.contains("gadget") -> Icons.Default.Headphones
    else -> Icons.Default.Category
  }
}
