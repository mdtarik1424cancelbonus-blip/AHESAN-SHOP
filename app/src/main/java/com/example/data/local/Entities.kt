package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val nameBn: String,
  val description: String,
  val regularPrice: Double,
  val discountPrice: Double? = null,
  val stockQuantity: Int = 10,
  val category: String,
  val brand: String = "AHESAN",
  val sku: String = "",
  val variantsSize: String = "", // e.g. "M, L, XL"
  val variantsColor: String = "", // e.g. "Black, Blue, White"
  val variantsWeight: String = "", // e.g. "500g"
  val isAvailable: Boolean = true,
  val isFeatured: Boolean = false,
  val isNewArrival: Boolean = false,
  val rating: Float = 4.8f,
  val reviewsCount: Int = 12,
  val imageUrl: String = "",
  val additionalImages: String = "", // Comma-separated URLs
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val nameBn: String,
  val iconName: String = "category",
  val imageUrl: String = "",
  val displayOrder: Int = 0
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val productName: String,
  val productImage: String,
  val selectedSize: String? = null,
  val selectedColor: String? = null,
  val unitPrice: Double,
  val quantity: Int = 1
)

@Entity(tableName = "orders")
data class OrderEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderId: String, // e.g. "AHESAN-20260906-0001"
  val orderDate: Long = System.currentTimeMillis(),
  val customerName: String,
  val customerPhone: String,
  val deliveryAddress: String,
  val district: String,
  val thana: String,
  val deliveryInstructions: String = "",
  val subtotal: Double,
  val deliveryCharge: Double,
  val totalAmount: Double,
  val paymentMethod: String, // "FULL_PAYMENT" or "CASH_ON_DELIVERY"
  val paymentStatus: String, // "Payment Verification Pending", "Delivery Charge Verification Pending", "Confirmed", "Paid", "Rejected"
  val orderStatus: String, // "Pending", "Payment Verification Pending", "Delivery Charge Verification Pending", "Confirmed", "Processing", "Shipped", "Out for Delivery", "Delivered", "Cancelled"
  val itemsCount: Int = 1,
  val bkashNumber: String? = null,
  val trxId: String? = null,
  val paidAmount: Double? = null,
  val adminVerificationNote: String? = null,
  val adminNote: String? = null,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderIdString: String,
  val productId: Long,
  val productName: String,
  val productImage: String,
  val selectedVariant: String = "",
  val unitPrice: Double,
  val quantity: Int,
  val totalPrice: Double
)

@Entity(tableName = "payments")
data class PaymentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderIdString: String,
  val paymentMethod: String, // "FULL_PAYMENT" or "CASH_ON_DELIVERY"
  val bkashSenderNumber: String,
  val trxId: String,
  val amountPaid: Double,
  val targetBkashNumber: String,
  val paymentStatus: String, // "Pending Verification", "Approved", "Rejected"
  val note: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reviews")
data class ReviewEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val customerName: String,
  val rating: Int = 5,
  val reviewText: String,
  val isApproved: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist")
data class WishlistItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: Long,
  val productName: String,
  val productImage: String,
  val price: Double,
  val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "banners")
data class BannerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val subtitle: String,
  val imageUrl: String = "",
  val discountText: String = "",
  val isActive: Boolean = true,
  val displayOrder: Int = 0
)

@Entity(tableName = "store_settings")
data class StoreSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val bkashNumber1: String = "01823473621",
  val bkashNumber2: String = "01613285997",
  val customerCareWhatsApp: String = "01613285997",
  val insideCityDeliveryCharge: Double = 70.0,
  val outsideCityDeliveryCharge: Double = 130.0,
  val freeDeliveryThreshold: Double = 3000.0,
  val adminPin: String = "1234"
)

@Entity(tableName = "notifications")
data class NotificationEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val message: String,
  val orderId: String? = null,
  val timestamp: Long = System.currentTimeMillis(),
  val isRead: Boolean = false
)

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val phone: String,
  val email: String = "",
  val address: String = "",
  val district: String = "Dhaka",
  val thana: String = "",
  val isAdmin: Boolean = false
)
