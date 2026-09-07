package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.BannerEntity
import com.example.data.local.CartItemEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.NotificationEntity
import com.example.data.local.OrderEntity
import com.example.data.local.OrderItemEntity
import com.example.data.local.PaymentEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.StoreSettingsEntity
import com.example.data.local.UserEntity
import com.example.data.local.WishlistItemEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShopRepository(private val db: AppDatabase) {

  // Products
  val allProducts: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
  val featuredProducts: Flow<List<ProductEntity>> = db.productDao().getFeaturedProducts()
  val newArrivals: Flow<List<ProductEntity>> = db.productDao().getNewArrivals()
  val discountProducts: Flow<List<ProductEntity>> = db.productDao().getDiscountProducts()

  fun getProductById(id: Long): Flow<ProductEntity?> = db.productDao().getProductById(id)
  fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = db.productDao().getProductsByCategory(category)
  fun searchProducts(query: String): Flow<List<ProductEntity>> = db.productDao().searchProducts(query)

  suspend fun insertProduct(product: ProductEntity) = db.productDao().insertProduct(product)
  suspend fun updateProduct(product: ProductEntity) = db.productDao().updateProduct(product)
  suspend fun deleteProductById(id: Long) = db.productDao().deleteProductById(id)

  // Categories
  val allCategories: Flow<List<CategoryEntity>> = db.categoryDao().getAllCategories()
  suspend fun insertCategory(category: CategoryEntity) = db.categoryDao().insertCategory(category)
  suspend fun updateCategory(category: CategoryEntity) = db.categoryDao().updateCategory(category)
  suspend fun deleteCategory(category: CategoryEntity) = db.categoryDao().deleteCategory(category)
  suspend fun deleteCategoryById(id: Long) = db.categoryDao().deleteCategoryById(id)

  // Cart
  val cartItems: Flow<List<CartItemEntity>> = db.cartDao().getAllCartItems()
  val cartItemCount: Flow<Int> = db.cartDao().getCartItemCount()
  suspend fun addToCart(item: CartItemEntity) = db.cartDao().insertCartItem(item)
  suspend fun updateCartItem(item: CartItemEntity) = db.cartDao().updateCartItem(item)
  suspend fun deleteCartItem(item: CartItemEntity) = db.cartDao().deleteCartItem(item)
  suspend fun clearCart() = db.cartDao().clearCart()

  // Orders
  val allOrders: Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
  fun getOrderByOrderId(orderId: String): Flow<OrderEntity?> = db.orderDao().getOrderByOrderId(orderId)
  fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>> = db.orderItemDao().getItemsForOrder(orderId)

  suspend fun placeOrder(
    customerName: String,
    customerPhone: String,
    deliveryAddress: String,
    district: String,
    thana: String,
    deliveryInstructions: String,
    subtotal: Double,
    deliveryCharge: Double,
    totalAmount: Double,
    paymentMethod: String,
    bkashSenderNumber: String,
    trxId: String,
    paidAmount: Double,
    targetBkashNumber: String,
    cartItemsList: List<CartItemEntity>
  ): String {
    // Generate Order ID formatted like AHESAN-20260906-0001
    val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    val totalCount = db.orderDao().getTotalOrdersCount() + 1
    val orderId = "AHESAN-$dateStr-%04d".format(totalCount)

    val initialPaymentStatus = if (paymentMethod == "FULL_PAYMENT") {
      "Payment Verification Pending"
    } else {
      "Delivery Charge Verification Pending"
    }

    val initialOrderStatus = if (paymentMethod == "FULL_PAYMENT") {
      "Payment Verification Pending"
    } else {
      "Delivery Charge Verification Pending"
    }

    val order = OrderEntity(
      orderId = orderId,
      customerName = customerName,
      customerPhone = customerPhone,
      deliveryAddress = deliveryAddress,
      district = district,
      thana = thana,
      deliveryInstructions = deliveryInstructions,
      subtotal = subtotal,
      deliveryCharge = deliveryCharge,
      totalAmount = totalAmount,
      paymentMethod = paymentMethod,
      paymentStatus = initialPaymentStatus,
      orderStatus = initialOrderStatus,
      itemsCount = cartItemsList.sumOf { it.quantity },
      bkashNumber = bkashSenderNumber,
      trxId = trxId,
      paidAmount = paidAmount
    )
    db.orderDao().insertOrder(order)

    // Insert order items
    val items = cartItemsList.map {
      val variantStr = listOfNotNull(it.selectedSize, it.selectedColor).joinToString(" / ")
      OrderItemEntity(
        orderIdString = orderId,
        productId = it.productId,
        productName = it.productName,
        productImage = it.productImage,
        selectedVariant = variantStr,
        unitPrice = it.unitPrice,
        quantity = it.quantity,
        totalPrice = it.unitPrice * it.quantity
      )
    }
    db.orderItemDao().insertOrderItems(items)

    // Record Payment
    db.paymentDao().insertPayment(
      PaymentEntity(
        orderIdString = orderId,
        paymentMethod = paymentMethod,
        bkashSenderNumber = bkashSenderNumber,
        trxId = trxId,
        amountPaid = paidAmount,
        targetBkashNumber = targetBkashNumber,
        paymentStatus = "Pending Verification"
      )
    )

    // Notify customer
    val notifMsg = if (paymentMethod == "FULL_PAYMENT") {
      "আপনার অর্ডার ($orderId) সফলভাবে গ্রহণ করা হয়েছে। bKash TrxID ($trxId) যাচাই করা হচ্ছে।"
    } else {
      "আপনার ক্যাশ অন ডেলিভারি অর্ডার ($orderId) গ্রহণ করা হয়েছে। অ্যাডভান্স ডেলিভারি চার্জ TrxID ($trxId) যাচাই করা হচ্ছে।"
    }
    db.notificationDao().insertNotification(
      NotificationEntity(
        title = "নতুন অর্ডার গ্রহণ করা হয়েছে",
        message = notifMsg,
        orderId = orderId
      )
    )

    // Clear user cart
    db.cartDao().clearCart()

    return orderId
  }

  suspend fun updateOrderStatus(orderId: String, status: String, paymentStatus: String, note: String?) {
    db.orderDao().updateOrderStatus(orderId, status, paymentStatus, note)
    db.notificationDao().insertNotification(
      NotificationEntity(
        title = "অর্ডার আপডেট ($orderId)",
        message = "আপনার অর্ডারের বর্তমান স্ট্যাটাস: $status। পেমেন্ট স্ট্যাটাস: $paymentStatus",
        orderId = orderId
      )
    )
  }

  // Payments
  val allPayments: Flow<List<PaymentEntity>> = db.paymentDao().getAllPayments()
  suspend fun updatePaymentStatus(id: Long, status: String, note: String) {
    db.paymentDao().updatePaymentStatus(id, status, note)
  }

  // Reviews
  fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>> = db.reviewDao().getReviewsForProduct(productId)
  suspend fun addReview(review: ReviewEntity) = db.reviewDao().insertReview(review)

  // Wishlist
  val wishlistItems: Flow<List<WishlistItemEntity>> = db.wishlistDao().getWishlistItems()
  fun isProductWishlisted(productId: Long): Flow<Boolean> = db.wishlistDao().isProductWishlisted(productId)
  suspend fun addToWishlist(item: WishlistItemEntity) = db.wishlistDao().addToWishlist(item)
  suspend fun removeFromWishlist(productId: Long) = db.wishlistDao().removeFromWishlist(productId)

  // Banners
  val activeBanners: Flow<List<BannerEntity>> = db.bannerDao().getActiveBanners()
  val allBanners: Flow<List<BannerEntity>> = db.bannerDao().getAllBanners()
  suspend fun insertBanner(banner: BannerEntity) = db.bannerDao().insertBanner(banner)
  suspend fun updateBanner(banner: BannerEntity) = db.bannerDao().updateBanner(banner)
  suspend fun deleteBanner(banner: BannerEntity) = db.bannerDao().deleteBanner(banner)
  suspend fun deleteBannerById(id: Long) = db.bannerDao().deleteBannerById(id)

  // Settings
  val settings: Flow<StoreSettingsEntity?> = db.storeSettingsDao().getSettings()
  suspend fun updateSettings(settings: StoreSettingsEntity) = db.storeSettingsDao().insertOrUpdateSettings(settings)

  // Notifications
  val notifications: Flow<List<NotificationEntity>> = db.notificationDao().getAllNotifications()
  suspend fun markNotificationsRead() = db.notificationDao().markAllAsRead()

  // User
  val currentUser: Flow<UserEntity?> = db.userDao().getCurrentUser()
  suspend fun saveUser(user: UserEntity) = db.userDao().saveUser(user)
}
