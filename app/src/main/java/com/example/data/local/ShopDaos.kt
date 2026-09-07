package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
  @Query("SELECT * FROM products ORDER BY id DESC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id")
  fun getProductById(id: Long): Flow<ProductEntity?>

  @Query("SELECT * FROM products WHERE isFeatured = 1 ORDER BY id DESC")
  fun getFeaturedProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isNewArrival = 1 ORDER BY id DESC")
  fun getNewArrivals(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE discountPrice IS NOT NULL AND discountPrice < regularPrice ORDER BY id DESC")
  fun getDiscountProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE category = :category ORDER BY id DESC")
  fun getProductsByCategory(category: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR nameBn LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
  fun searchProducts(query: String): Flow<List<ProductEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllProducts(products: List<ProductEntity>)

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Query("DELETE FROM products WHERE id = :id")
  suspend fun deleteProductById(id: Long)

  @Query("SELECT COUNT(*) FROM products")
  suspend fun getProductCount(): Int
}

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories ORDER BY displayOrder ASC, id ASC")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllCategories(categories: List<CategoryEntity>)

  @Update
  suspend fun updateCategory(category: CategoryEntity)

  @Delete
  suspend fun deleteCategory(category: CategoryEntity)

  @Query("DELETE FROM categories WHERE id = :id")
  suspend fun deleteCategoryById(id: Long)

  @Query("SELECT COUNT(*) FROM categories")
  suspend fun getCategoryCount(): Int
}

@Dao
interface CartDao {
  @Query("SELECT * FROM cart_items ORDER BY id DESC")
  fun getAllCartItems(): Flow<List<CartItemEntity>>

  @Query("SELECT COUNT(*) FROM cart_items")
  fun getCartItemCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCartItem(item: CartItemEntity): Long

  @Update
  suspend fun updateCartItem(item: CartItemEntity)

  @Delete
  suspend fun deleteCartItem(item: CartItemEntity)

  @Query("DELETE FROM cart_items WHERE id = :id")
  suspend fun deleteCartItemById(id: Long)

  @Query("DELETE FROM cart_items")
  suspend fun clearCart()
}

@Dao
interface OrderDao {
  @Query("SELECT * FROM orders ORDER BY orderDate DESC")
  fun getAllOrders(): Flow<List<OrderEntity>>

  @Query("SELECT * FROM orders WHERE orderId = :orderId")
  fun getOrderByOrderId(orderId: String): Flow<OrderEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: OrderEntity): Long

  @Update
  suspend fun updateOrder(order: OrderEntity)

  @Query("UPDATE orders SET orderStatus = :status, paymentStatus = :paymentStatus, adminVerificationNote = :note WHERE orderId = :orderId")
  suspend fun updateOrderStatus(orderId: String, status: String, paymentStatus: String, note: String?)

  @Query("DELETE FROM orders WHERE orderId = :orderId")
  suspend fun deleteOrderById(orderId: String)

  @Query("SELECT COUNT(*) FROM orders")
  suspend fun getTotalOrdersCount(): Int
}

@Dao
interface OrderItemDao {
  @Query("SELECT * FROM order_items WHERE orderIdString = :orderId")
  fun getItemsForOrder(orderId: String): Flow<List<OrderItemEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrderItems(items: List<OrderItemEntity>)
}

@Dao
interface PaymentDao {
  @Query("SELECT * FROM payments ORDER BY createdAt DESC")
  fun getAllPayments(): Flow<List<PaymentEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPayment(payment: PaymentEntity): Long

  @Query("UPDATE payments SET paymentStatus = :status, note = :note WHERE id = :id")
  suspend fun updatePaymentStatus(id: Long, status: String, note: String)
}

@Dao
interface ReviewDao {
  @Query("SELECT * FROM reviews WHERE productId = :productId ORDER BY createdAt DESC")
  fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertReview(review: ReviewEntity): Long
}

@Dao
interface WishlistDao {
  @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
  fun getWishlistItems(): Flow<List<WishlistItemEntity>>

  @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE productId = :productId)")
  fun isProductWishlisted(productId: Long): Flow<Boolean>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun addToWishlist(item: WishlistItemEntity): Long

  @Query("DELETE FROM wishlist WHERE productId = :productId")
  suspend fun removeFromWishlist(productId: Long)
}

@Dao
interface BannerDao {
  @Query("SELECT * FROM banners WHERE isActive = 1 ORDER BY displayOrder ASC, id ASC")
  fun getActiveBanners(): Flow<List<BannerEntity>>

  @Query("SELECT * FROM banners ORDER BY id DESC")
  fun getAllBanners(): Flow<List<BannerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBanner(banner: BannerEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAllBanners(banners: List<BannerEntity>)

  @Update
  suspend fun updateBanner(banner: BannerEntity)

  @Delete
  suspend fun deleteBanner(banner: BannerEntity)

  @Query("DELETE FROM banners WHERE id = :id")
  suspend fun deleteBannerById(id: Long)

  @Query("SELECT COUNT(*) FROM banners")
  suspend fun getBannerCount(): Int
}

@Dao
interface StoreSettingsDao {
  @Query("SELECT * FROM store_settings WHERE id = 1")
  fun getSettings(): Flow<StoreSettingsEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateSettings(settings: StoreSettingsEntity)
}

@Dao
interface NotificationDao {
  @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
  fun getAllNotifications(): Flow<List<NotificationEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotification(notification: NotificationEntity): Long

  @Query("UPDATE notifications SET isRead = 1")
  suspend fun markAllAsRead()
}

@Dao
interface UserDao {
  @Query("SELECT * FROM users LIMIT 1")
  fun getCurrentUser(): Flow<UserEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveUser(user: UserEntity): Long
}
