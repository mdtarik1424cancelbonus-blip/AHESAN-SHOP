package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.ProductEntity
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.AhesanSecondary
import com.example.ui.theme.GoldStar
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusConfirmed
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusShipped
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.util.WhatsAppHelper

import androidx.compose.foundation.BorderStroke
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSlate100

@Composable
fun ProductCard(
  product: ProductEntity,
  isWishlisted: Boolean,
  onClick: () -> Unit,
  onAddToCart: () -> Unit,
  onToggleWishlist: () -> Unit,
  modifier: Modifier = Modifier
) {
  val hasDiscount = product.discountPrice != null && product.discountPrice < product.regularPrice
  val discountPercent = if (hasDiscount) {
    (((product.regularPrice - product.discountPrice!!) / product.regularPrice) * 100).toInt()
  } else 0

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .clickable { onClick() }
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = BorderStroke(1.dp, BentoCardBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(6.dp)) {
      // Bento Inset Image Container
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .clip(RoundedCornerShape(18.dp))
          .background(BentoSlate100)
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(product.imageUrl)
            .crossfade(true)
            .build(),
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Discount Badge
        if (hasDiscount) {
          Surface(
            color = AhesanSecondary,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
              .align(Alignment.TopStart)
              .padding(6.dp)
          ) {
            Text(
              text = "-$discountPercent%",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
            )
          }
        }

        // Wishlist Button
        IconButton(
          onClick = onToggleWishlist,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(6.dp)
            .size(30.dp)
            .background(Color.White.copy(alpha = 0.9f), CircleShape)
            .testTag("wishlist_btn_${product.id}")
        ) {
          Icon(
            imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Wishlist",
            tint = if (isWishlisted) Color.Red else Color.Gray,
            modifier = Modifier.size(16.dp)
          )
        }

        // Stock indicator if low or out
        if (product.stockQuantity <= 0) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "স্টক শেষ",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }

      // Details
      Column(
        modifier = Modifier
          .padding(horizontal = 6.dp, vertical = 8.dp)
          .fillMaxWidth()
      ) {
        // Brand & Category
        Text(
          text = "${product.brand} • ${product.category}",
          fontSize = 10.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Title
        Text(
          text = product.nameBn.ifEmpty { product.name },
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 17.sp,
          modifier = Modifier.height(34.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Rating
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = GoldStar,
            modifier = Modifier.size(13.dp)
          )
          Text(
            text = String.format("%.1f", product.rating),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "(${product.reviewsCount})",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Price Row
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            val currentPrice = product.discountPrice ?: product.regularPrice
            Text(
              text = "৳${currentPrice.toInt()}",
              fontSize = 15.sp,
              fontWeight = FontWeight.ExtraBold,
              color = AhesanPrimary
            )
            if (hasDiscount) {
              Text(
                text = "৳${product.regularPrice.toInt()}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textDecoration = TextDecoration.LineThrough
              )
            }
          }

          // Cart Quick Add Button
          Button(
            onClick = onAddToCart,
            enabled = product.stockQuantity > 0,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = AhesanPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .height(32.dp)
              .testTag("add_cart_btn_${product.id}")
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingCart,
              contentDescription = "Add to Cart",
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "কার্ট",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun WhatsAppCustomerCareBanner(
  phoneNumber: String = "01613285997",
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(22.dp))
      .clickable {
        WhatsAppHelper.openWhatsAppChat(
          context = context,
          phoneNumber = phoneNumber,
          message = "অর্ডার বা যেকোনো সমস্যার জন্য WhatsApp-এ যোগাযোগ করতে চাই।"
        )
      }
      .testTag("whatsapp_care_card"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = Color(0xFFF0FDF4)
    ),
    border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(46.dp)
          .background(WhatsAppGreen, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Chat,
          contentDescription = "WhatsApp",
          tint = Color.White,
          modifier = Modifier.size(24.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Customer Care",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF065F46)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            color = WhatsAppGreen,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "অনলাইন সাপোর্ট",
              fontSize = 9.sp,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
        }
        Text(
          text = "WhatsApp: $phoneNumber",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF047857)
        )
        Text(
          text = "“অর্ডার বা যেকোনো সমস্যার জন্য WhatsApp-এ যোগাযোগ করুন।”",
          fontSize = 11.sp,
          color = Color(0xFF059669)
        )
      }

      Button(
        onClick = {
          WhatsAppHelper.openWhatsAppChat(
            context = context,
            phoneNumber = phoneNumber,
            message = "অর্ডার বা যেকোনো সমস্যার জন্য WhatsApp-এ যোগাযোগ করতে চাই।"
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
        shape = RoundedCornerShape(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.testTag("whatsapp_customer_care_btn")
      ) {
        Text(
          text = "WhatsApp",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

@Composable
fun StatusBadge(
  status: String,
  modifier: Modifier = Modifier
) {
  val (color, bgColor) = when {
    status.contains("Confirmed", ignoreCase = true) || status.contains("Paid", ignoreCase = true) ->
      StatusConfirmed to Color(0xFFD1FAE5)
    status.contains("Pending", ignoreCase = true) ->
      StatusPending to Color(0xFFFEF3C7)
    status.contains("Processing", ignoreCase = true) ->
      StatusProcessing to Color(0xFFDBEAFE)
    status.contains("Shipped", ignoreCase = true) ->
      StatusShipped to Color(0xFFEDE9FE)
    status.contains("Delivered", ignoreCase = true) ->
      StatusDelivered to Color(0xFFDCFCE7)
    status.contains("Cancel", ignoreCase = true) || status.contains("Reject", ignoreCase = true) ->
      StatusCancelled to Color(0xFFFEE2E2)
    else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.primaryContainer
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(6.dp),
    modifier = modifier
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .background(color, CircleShape)
      )
      Text(
        text = status,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
