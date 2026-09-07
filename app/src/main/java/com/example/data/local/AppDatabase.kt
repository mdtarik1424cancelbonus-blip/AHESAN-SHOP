package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    ProductEntity::class,
    CategoryEntity::class,
    CartItemEntity::class,
    OrderEntity::class,
    OrderItemEntity::class,
    PaymentEntity::class,
    ReviewEntity::class,
    WishlistItemEntity::class,
    BannerEntity::class,
    StoreSettingsEntity::class,
    NotificationEntity::class,
    UserEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun categoryDao(): CategoryDao
  abstract fun cartDao(): CartDao
  abstract fun orderDao(): OrderDao
  abstract fun orderItemDao(): OrderItemDao
  abstract fun paymentDao(): PaymentDao
  abstract fun reviewDao(): ReviewDao
  abstract fun wishlistDao(): WishlistDao
  abstract fun bannerDao(): BannerDao
  abstract fun storeSettingsDao(): StoreSettingsDao
  abstract fun notificationDao(): NotificationDao
  abstract fun userDao(): UserDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "ahesan_shop_db"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
