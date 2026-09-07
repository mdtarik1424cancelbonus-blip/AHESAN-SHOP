package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

  suspend fun populateInitialDataIfEmpty(database: AppDatabase) {
    withContext(Dispatchers.IO) {
      val categoryCount = database.categoryDao().getCategoryCount()
      if (categoryCount == 0) {
        seedCategories(database.categoryDao())
      }

      val productCount = database.productDao().getProductCount()
      if (productCount == 0) {
        seedProducts(database.productDao())
      }

      val bannerCount = database.bannerDao().getBannerCount()
      if (bannerCount == 0) {
        seedBanners(database.bannerDao())
      }

      // Ensure default settings exist
      database.storeSettingsDao().insertOrUpdateSettings(
        StoreSettingsEntity(
          id = 1,
          bkashNumber1 = "01823473621",
          bkashNumber2 = "01613285997",
          customerCareWhatsApp = "01613285997",
          insideCityDeliveryCharge = 70.0,
          outsideCityDeliveryCharge = 130.0,
          freeDeliveryThreshold = 3000.0,
          adminPin = "1234"
        )
      )

      // Sample initial notification
      database.notificationDao().insertNotification(
        NotificationEntity(
          title = "AHESAN SHOP-এ স্বাগতম!",
          message = "আমাদের অনলাইন শপিং প্ল্যাটফর্মে আপনাকে স্বাগতম। সেরা অফার ও দ্রুত ডেলিভারির সুবিধা উপভোগ করুন।"
        )
      )
    }
  }

  private suspend fun seedCategories(categoryDao: CategoryDao) {
    val categories = listOf(
      CategoryEntity(name = "Electronics", nameBn = "ইলেকট্রনিক্স", iconName = "devices", displayOrder = 1),
      CategoryEntity(name = "Mobile Accessories", nameBn = "মোবাইল এক্সেসরিজ", iconName = "phone_android", displayOrder = 2),
      CategoryEntity(name = "Fashion", nameBn = "ফ্যাশন", iconName = "checkroom", displayOrder = 3),
      CategoryEntity(name = "Men's Fashion", nameBn = "পুরুষদের ফ্যাশন", iconName = "male", displayOrder = 4),
      CategoryEntity(name = "Women's Fashion", nameBn = "মহিলাদের ফ্যাশন", iconName = "female", displayOrder = 5),
      CategoryEntity(name = "Beauty & Personal Care", nameBn = "সৌন্দর্য ও যত্ন", iconName = "spa", displayOrder = 6),
      CategoryEntity(name = "Home & Kitchen", nameBn = "বাড়ি ও রান্নাঘর", iconName = "kitchen", displayOrder = 7),
      CategoryEntity(name = "Grocery", nameBn = "মুদি সামগ্রী", iconName = "shopping_basket", displayOrder = 8),
      CategoryEntity(name = "Sports", nameBn = "খেলাধুলা", iconName = "sports_soccer", displayOrder = 9),
      CategoryEntity(name = "Shoes", nameBn = "জুতা ও স্যান্ডেল", iconName = "ice_skating", displayOrder = 10),
      CategoryEntity(name = "Bags", nameBn = "ব্যাগ ও লাগেজ", iconName = "luggage", displayOrder = 11),
      CategoryEntity(name = "Watches", nameBn = "ঘড়ি কালেকশন", iconName = "watch", displayOrder = 12),
      CategoryEntity(name = "Gadgets", nameBn = "স্মার্ট গ্যাজেটস", iconName = "headphones", displayOrder = 13),
      CategoryEntity(name = "Other", nameBn = "অন্যান্য পণ্য", iconName = "more_horiz", displayOrder = 14)
    )
    categoryDao.insertAllCategories(categories)
  }

  private suspend fun seedProducts(productDao: ProductDao) {
    val products = listOf(
      ProductEntity(
        name = "Wireless Bluetooth Earbuds Pro",
        nameBn = "ওয়্যারলেস ব্লুটুথ এয়ারবাডস প্রো",
        description = "হাই-ফাই স্টেরিও সাউন্ড, অ্যাক্টিভ নয়েজ ক্যান্সেলেশন এবং ২৪ ঘণ্টা ব্যাটারি ব্যাকআপ সমৃদ্ধ প্রিমিয়াম ইয়ারফোন। ডিপ বাস এবং ওয়াটার রেজিস্ট্যান্ট বিল্ড।",
        regularPrice = 1850.0,
        discountPrice = 1390.0,
        stockQuantity = 25,
        category = "Gadgets",
        brand = "SoundWave",
        sku = "SW-EAR-01",
        variantsColor = "Black, White, Deep Blue",
        isAvailable = true,
        isFeatured = true,
        isNewArrival = true,
        rating = 4.8f,
        reviewsCount = 42,
        imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Smart Fitness Watch AMOLED",
        nameBn = "স্মার্ট ফিটনেস ওয়াচ অ্যামোলেড",
        description = "১.৪৩ ইঞ্চি অ্যামোলেড ডিসপ্লে, হার্ট রেট ও ব্লাড অক্সিজেন ট্র্যাকিং, ব্লুটুথ কলিং ও দীর্ঘস্থায়ী ব্যাটারি সমৃদ্ধ আধুনিক স্মার্টওয়াচ।",
        regularPrice = 3450.0,
        discountPrice = 2850.0,
        stockQuantity = 18,
        category = "Watches",
        brand = "FitTech",
        sku = "FT-WATCH-202",
        variantsColor = "Space Black, Silver Gray, Rose Gold",
        isAvailable = true,
        isFeatured = true,
        isNewArrival = true,
        rating = 4.9f,
        reviewsCount = 38,
        imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Premium Cotton Panjabi for Men",
        nameBn = "প্রিমিয়াম সুতি কটন পাঞ্জাবি",
        description = "১০০% প্রিমিয়াম পিওর কটন ফেব্রিক, নিখুঁত কম্পিউটার এমব্রয়ডারি কলার ডিজাইন এবং অত্যন্ত আরামদায়ক কাটিং। উৎসব ও দৈনন্দিন ব্যবহারের জন্য সেরা।",
        regularPrice = 2200.0,
        discountPrice = 1650.0,
        stockQuantity = 35,
        category = "Men's Fashion",
        brand = "AHESAN Heritage",
        sku = "AH-PJ-101",
        variantsSize = "38, 40, 42, 44",
        variantsColor = "Snow White, Navy Blue, Maroon, Olive Green",
        isAvailable = true,
        isFeatured = true,
        isNewArrival = true,
        rating = 4.7f,
        reviewsCount = 56,
        imageUrl = "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Exclusive Silk Jamdani Sharee",
        nameBn = "এক্সক্লুসিভ সিল্ক জামদানি শাড়ি",
        description = "ঐতিহ্যবাহী নিখুঁত ঢাকাই জামদানি নকশা, সফট হাফ সিল্ক ফেব্রিক। যেকোনো উৎসব, বিয়ে বা অনুষ্ঠানে চমৎকার আভিজাত্য ফুটিয়ে তোলে।",
        regularPrice = 4500.0,
        discountPrice = 3600.0,
        stockQuantity = 12,
        category = "Women's Fashion",
        brand = "Dhakai Weaves",
        sku = "DW-JAM-55",
        variantsColor = "Royal Blue, Crimson Red, Bottle Green, Golden Yellow",
        isAvailable = true,
        isFeatured = true,
        isNewArrival = false,
        rating = 4.9f,
        reviewsCount = 29,
        imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Fast Charging 20000mAh Power Bank",
        nameBn = "ফাস্ট চার্জিং ২০০০০ এমএএইচ পাওয়ার ব্যাংক",
        description = "২২.৫ ওয়াট পিডি ও কিউসি ৩.০ ফাস্ট চার্জিং সমর্থন। ডিজিটাল ডিসপ্লে ও মাল্টিপল আউটপুট পোর্ট সহ ভ্রমণ ও সার্বক্ষণিক ব্যাকআপের নিশ্চয়তা।",
        regularPrice = 2100.0,
        discountPrice = 1750.0,
        stockQuantity = 40,
        category = "Mobile Accessories",
        brand = "ChargeMaster",
        sku = "CM-PB-20K",
        variantsColor = "Matte Black, Glacier White",
        isAvailable = true,
        isFeatured = false,
        isNewArrival = true,
        rating = 4.6f,
        reviewsCount = 64,
        imageUrl = "https://images.unsplash.com/photo-1609592426804-984478ec09be?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Men's Lightweight Breathable Running Shoes",
        nameBn = "পুরুষদের লাইটওয়েট রানিং স্নিকার্স",
        description = "অত্যন্ত আরামদায়ক কুশন সোল, সহজে বাতাস চলাচল উপযোগী মেশ আপার এবং নন-স্লিপ গ্রিপ। রানিং ও স্পোর্টসের জন্য আদর্শ।",
        regularPrice = 2800.0,
        discountPrice = 2150.0,
        stockQuantity = 20,
        category = "Shoes",
        brand = "AeroStep",
        sku = "AS-RUN-09",
        variantsSize = "39, 40, 41, 42, 43, 44",
        variantsColor = "Triple Black, Steel Grey, Navy Orange",
        isAvailable = true,
        isFeatured = true,
        isNewArrival = false,
        rating = 4.8f,
        reviewsCount = 31,
        imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Waterproof Laptop Travel Backpack",
        nameBn = "ওয়াটারপ্রুফ ল্যাপটপ ট্রাভেল ব্যাকপ্যাক",
        description = "১৫.৬ ইঞ্চি ল্যাপটপ কম্পার্টমেন্ট, ইউএসবি চার্জিং পোর্ট, সিকিউর অ্যান্টি-থেফ্ট জিপার এবং প্রিমিয়াম ওয়াটারপ্রুফ অক্সফোর্ড ফেব্রিক।",
        regularPrice = 1950.0,
        discountPrice = 1490.0,
        stockQuantity = 22,
        category = "Bags",
        brand = "UrbanPack",
        sku = "UP-BAG-77",
        variantsColor = "Charcoal Black, Navy Blue",
        isAvailable = true,
        isFeatured = false,
        isNewArrival = true,
        rating = 4.7f,
        reviewsCount = 27,
        imageUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Organic Pure Mustard Oil 1 Litre",
        nameBn = "ঘানিভাঙা খাঁটি সরিষার তেল ১ লিটার",
        description = "প্রাকৃতিক উপায়ে কাঠের ঘানিতে ভাঙা খাঁটি সরিষার তেল। কোনো প্রকার ক্ষতিকর কেমিক্যাল বা প্রিজারভেটিভ ছাড়া শতভাগ নিরাপদ ও ঝাঁঝালো।",
        regularPrice = 380.0,
        discountPrice = 320.0,
        stockQuantity = 50,
        category = "Grocery",
        brand = "Shuddho Agro",
        sku = "SA-OIL-1L",
        variantsWeight = "500ml, 1 Litre, 2 Litre, 5 Litre",
        isAvailable = true,
        isFeatured = false,
        isNewArrival = false,
        rating = 4.9f,
        reviewsCount = 88,
        imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Stainless Steel Electric Kettle 2.0L",
        nameBn = "স্টেইনলেস স্টিল ইলেকট্রিক কেটলি ২.০ লিটার",
        description = "দ্রুত পানি গরম করার অটো শাট-অফ সুরক্ষা ফিচার। খাদ্যোপযোগী গ্রেড স্টেইনলেস স্টিল বডি এবং শক্তিশালী ১৫০০ ওয়াট হিটিং কয়েল।",
        regularPrice = 1450.0,
        discountPrice = 1150.0,
        stockQuantity = 15,
        category = "Home & Kitchen",
        brand = "KitchenPro",
        sku = "KP-KET-02",
        variantsColor = "Silver Steel, Gloss Black",
        isAvailable = true,
        isFeatured = false,
        isNewArrival = false,
        rating = 4.5f,
        reviewsCount = 19,
        imageUrl = "https://images.unsplash.com/photo-1594213114663-d94db9b17125?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Vitamin C Brightening Face Serum 30ml",
        nameBn = "ভিটামিন সি ব্রাইটনিং ফেস সিরাম ৩০ মি.লি.",
        description = "প্রাকৃতিক ভিটামিন সি ও হায়ালুরোনিক অ্যাসিড সমৃদ্ধ ত্বকের উজ্জ্বলতা বৃদ্ধিকারী সিরাম। ত্বকের দাগ দূর করে এবং ত্বক মসৃণ ও প্রাণবন্ত রাখে।",
        regularPrice = 1200.0,
        discountPrice = 890.0,
        stockQuantity = 30,
        category = "Beauty & Personal Care",
        brand = "GlowEssence",
        sku = "GE-SERUM-30",
        variantsWeight = "30ml, 50ml",
        isAvailable = true,
        isFeatured = true,
        isNewArrival = true,
        rating = 4.8f,
        reviewsCount = 45,
        imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Portable Mini Bluetooth Speaker",
        nameBn = "পোর্টেবল মিনি ব্লুটুথ স্পিকার",
        description = "৩৬০ ডিগ্রি ক্রিস্টাল ক্লিয়ার অডিও, ৩ডি সারাউন্ড বাস এবং ১০ ঘণ্টার প্লে-টাইম। পকেটে বহনযোগ্য এবং ওয়াটারপ্রুফ ডিজাইন।",
        regularPrice = 1650.0,
        discountPrice = 1250.0,
        stockQuantity = 28,
        category = "Electronics",
        brand = "SoundWave",
        sku = "SW-SPK-MINI",
        variantsColor = "Matte Black, Camo Green, Coral Red",
        isAvailable = true,
        isFeatured = false,
        isNewArrival = true,
        rating = 4.6f,
        reviewsCount = 23,
        imageUrl = "https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=600&q=80"
      ),
      ProductEntity(
        name = "Professional FIFA Standard Football",
        nameBn = "প্রফেশনাল ফিফা স্ট্যান্ডার্ড ফুটবল",
        description = "হাই-গ্রেড পিইউ লেদার এবং নিখুঁত হ্যান্ড-স্টিচড ফিনিশিং। যেকোনো মাঠে টেকসই ও নিখুঁত ফ্লাইট কন্ট্রোল সুবিধা।",
        regularPrice = 1350.0,
        discountPrice = 990.0,
        stockQuantity = 16,
        category = "Sports",
        brand = "StrikerBD",
        sku = "SB-FB-05",
        variantsSize = "Size 5",
        isAvailable = true,
        isFeatured = false,
        isNewArrival = false,
        rating = 4.7f,
        reviewsCount = 14,
        imageUrl = "https://images.unsplash.com/photo-1614632537423-1e6c2e7e0aab?auto=format&fit=crop&w=600&q=80"
      )
    )
    productDao.insertAllProducts(products)
  }

  private suspend fun seedBanners(bannerDao: BannerDao) {
    val banners = listOf(
      BannerEntity(
        title = "AHESAN SHOP মেগা ধামাকা অফার",
        subtitle = "সেরা গ্যাজেটস ও ফ্যাশন পণ্যে ৫০% পর্যন্ত অবিশ্বাস্য মূল্যছাড়!",
        imageUrl = "banner_promo_1788706732739", // uses our generated banner drawable
        discountText = "UP TO 50% OFF",
        isActive = true,
        displayOrder = 1
      ),
      BannerEntity(
        title = "নতুন কালেকশন ও ট্রেন্ডিং ফ্যাশন",
        subtitle = "সারা বাংলাদেশে দ্রুততম হোম ডেলিভারি ও ক্যাশ অন ডেলিভারি সুবিধা!",
        imageUrl = "",
        discountText = "FAST DELIVERY",
        isActive = true,
        displayOrder = 2
      )
    )
    bannerDao.insertAllBanners(banners)
  }
}
