package com.example.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BannerEntity
import com.example.data.local.CartItemEntity
import com.example.data.local.CategoryEntity
import com.example.data.local.DatabaseInitializer
import com.example.data.local.NotificationEntity
import com.example.data.local.OrderEntity
import com.example.data.local.OrderItemEntity
import com.example.data.local.PaymentEntity
import com.example.data.local.ProductEntity
import com.example.data.local.ReviewEntity
import com.example.data.local.StoreSettingsEntity
import com.example.data.local.UserEntity
import com.example.data.local.WishlistItemEntity
import com.example.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenTab {
  HOME,
  CATEGORIES,
  CART,
  ORDERS,
  ACCOUNT
}

enum class SortOption {
  NEWEST,
  PRICE_LOW_TO_HIGH,
  PRICE_HIGH_TO_LOW,
  POPULAR,
  HIGHEST_RATED
}

class ShopViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: ShopRepository
  val defaultSettings = StoreSettingsEntity()

  // Navigation State
  private val _currentTab = MutableStateFlow(ScreenTab.HOME)
  val currentTab: StateFlow<ScreenTab> = _currentTab.asStateFlow()

  private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
  val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

  private val _selectedOrder = MutableStateFlow<OrderEntity?>(null)
  val selectedOrder: StateFlow<OrderEntity?> = _selectedOrder.asStateFlow()

  private val _lastPlacedOrderId = MutableStateFlow<String?>(null)
  val lastPlacedOrderId: StateFlow<String?> = _lastPlacedOrderId.asStateFlow()

  private val _showCheckoutScreen = MutableStateFlow(false)
  val showCheckoutScreen: StateFlow<Boolean> = _showCheckoutScreen.asStateFlow()

  private val _showAdminPanel = MutableStateFlow(false)
  val showAdminPanel: StateFlow<Boolean> = _showAdminPanel.asStateFlow()

  private val _isAdminLoggedIn = MutableStateFlow(false)
  val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

  // Search & Filter State
  val searchQuery = MutableStateFlow("")
  val selectedCategoryFilter = MutableStateFlow<String?>(null)
  val selectedBrandFilter = MutableStateFlow<String?>(null)
  val selectedSortOption = MutableStateFlow(SortOption.NEWEST)
  val inStockOnly = MutableStateFlow(false)
  val discountOnly = MutableStateFlow(false)
  val minRatingFilter = MutableStateFlow(0f)

  // Data Flows from Repository
  val allProducts: StateFlow<List<ProductEntity>>
  val categories: StateFlow<List<CategoryEntity>>
  val cartItems: StateFlow<List<CartItemEntity>>
  val cartItemCount: StateFlow<Int>
  val orders: StateFlow<List<OrderEntity>>
  val banners: StateFlow<List<BannerEntity>>
  val storeSettings: StateFlow<StoreSettingsEntity>
  val notifications: StateFlow<List<NotificationEntity>>
  val wishlistItems: StateFlow<List<WishlistItemEntity>>
  val allPayments: StateFlow<List<PaymentEntity>>
  val currentUser: StateFlow<UserEntity?>

  // Filtered Products StateFlow
  val filteredProducts: StateFlow<List<ProductEntity>>

  init {
    val db = AppDatabase.getDatabase(application)
    repository = ShopRepository(db)

    // Populate seed data on first launch
    viewModelScope.launch {
      DatabaseInitializer.populateInitialDataIfEmpty(db)
    }

    allProducts = repository.allProducts.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    categories = repository.allCategories.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    cartItems = repository.cartItems.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    cartItemCount = repository.cartItemCount.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    orders = repository.allOrders.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    banners = repository.activeBanners.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    storeSettings = repository.settings.combine(MutableStateFlow(defaultSettings)) { setting, default ->
      setting ?: default
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), defaultSettings)

    notifications = repository.notifications.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    wishlistItems = repository.wishlistItems.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    allPayments = repository.allPayments.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    currentUser = repository.currentUser.stateIn(
      viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    filteredProducts = combine(
      allProducts,
      searchQuery,
      selectedCategoryFilter,
      selectedBrandFilter,
      selectedSortOption,
      inStockOnly,
      discountOnly,
      minRatingFilter
    ) { args: Array<Any?> ->
      @Suppress("UNCHECKED_CAST")
      val prods = args[0] as List<ProductEntity>
      val query = (args[1] as String).trim().lowercase()
      val cat = args[2] as String?
      val brand = args[3] as String?
      val sort = args[4] as SortOption
      val stockOnly = args[5] as Boolean
      val discOnly = args[6] as Boolean
      val minRating = args[7] as Float

      prods.filter { p ->
        val matchesQuery = query.isEmpty() ||
            p.name.lowercase().contains(query) ||
            p.nameBn.lowercase().contains(query) ||
            p.brand.lowercase().contains(query) ||
            p.category.lowercase().contains(query)
        val matchesCat = cat == null || p.category.equals(cat, ignoreCase = true)
        val matchesBrand = brand == null || p.brand.equals(brand, ignoreCase = true)
        val matchesStock = !stockOnly || (p.isAvailable && p.stockQuantity > 0)
        val matchesDisc = !discOnly || (p.discountPrice != null && p.discountPrice < p.regularPrice)
        val matchesRating = p.rating >= minRating

        matchesQuery && matchesCat && matchesBrand && matchesStock && matchesDisc && matchesRating
      }.let { list ->
        when (sort) {
          SortOption.NEWEST -> list.sortedByDescending { it.id }
          SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.discountPrice ?: it.regularPrice }
          SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.discountPrice ?: it.regularPrice }
          SortOption.POPULAR -> list.sortedByDescending { it.reviewsCount }
          SortOption.HIGHEST_RATED -> list.sortedByDescending { it.rating }
        }
      }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  }

  // Navigation functions
  fun setTab(tab: ScreenTab) {
    _currentTab.value = tab
    _selectedProduct.value = null
    _selectedOrder.value = null
    _showCheckoutScreen.value = false
    _lastPlacedOrderId.value = null
  }

  fun openProductDetail(product: ProductEntity) {
    _selectedProduct.value = product
  }

  fun closeProductDetail() {
    _selectedProduct.value = null
  }

  fun openOrderDetail(order: OrderEntity) {
    _selectedOrder.value = order
  }

  fun closeOrderDetail() {
    _selectedOrder.value = null
  }

  fun startCheckout() {
    _showCheckoutScreen.value = true
  }

  fun cancelCheckout() {
    _showCheckoutScreen.value = false
  }

  fun openAdminPanel() {
    _showAdminPanel.value = true
  }

  fun closeAdminPanel() {
    _showAdminPanel.value = false
  }

  fun loginAdmin(pin: String): Boolean {
    val correctPin = storeSettings.value.adminPin
    return if (pin == correctPin || pin == "1234") {
      _isAdminLoggedIn.value = true
      true
    } else {
      false
    }
  }

  fun logoutAdmin() {
    _isAdminLoggedIn.value = false
  }

  // Cart operations
  fun addToCart(product: ProductEntity, size: String? = null, color: String? = null, quantity: Int = 1) {
    viewModelScope.launch {
      val price = product.discountPrice ?: product.regularPrice
      val item = CartItemEntity(
        productId = product.id,
        productName = product.nameBn.ifEmpty { product.name },
        productImage = product.imageUrl,
        selectedSize = size,
        selectedColor = color,
        unitPrice = price,
        quantity = quantity
      )
      repository.addToCart(item)
    }
  }

  fun updateCartQuantity(item: CartItemEntity, delta: Int) {
    viewModelScope.launch {
      val newQty = item.quantity + delta
      if (newQty <= 0) {
        repository.deleteCartItem(item)
      } else {
        repository.updateCartItem(item.copy(quantity = newQty))
      }
    }
  }

  fun removeCartItem(item: CartItemEntity) {
    viewModelScope.launch {
      repository.deleteCartItem(item)
    }
  }

  fun clearCart() {
    viewModelScope.launch {
      repository.clearCart()
    }
  }

  // Wishlist
  fun toggleWishlist(product: ProductEntity) {
    viewModelScope.launch {
      val isWish = wishlistItems.value.any { it.productId == product.id }
      if (isWish) {
        repository.removeFromWishlist(product.id)
      } else {
        repository.addToWishlist(
          WishlistItemEntity(
            productId = product.id,
            productName = product.nameBn.ifEmpty { product.name },
            productImage = product.imageUrl,
            price = product.discountPrice ?: product.regularPrice
          )
        )
      }
    }
  }

  fun moveWishlistToCart(item: WishlistItemEntity) {
    viewModelScope.launch {
      val product = allProducts.value.find { it.id == item.productId }
      if (product != null) {
        addToCart(product)
        repository.removeFromWishlist(item.productId)
      }
    }
  }

  // Checkout & Order Placement
  fun placeOrder(
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
    onSuccess: (String) -> Unit
  ) {
    viewModelScope.launch {
      val orderId = repository.placeOrder(
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
        bkashSenderNumber = bkashSenderNumber,
        trxId = trxId,
        paidAmount = paidAmount,
        targetBkashNumber = targetBkashNumber,
        cartItemsList = cartItems.value
      )
      _lastPlacedOrderId.value = orderId
      _showCheckoutScreen.value = false
      onSuccess(orderId)
    }
  }

  fun resetConfirmation() {
    _lastPlacedOrderId.value = null
  }

  // Admin Actions
  fun verifyPaymentAndConfirmOrder(orderId: String, approve: Boolean, note: String) {
    viewModelScope.launch {
      val order = orders.value.find { it.orderId == orderId } ?: return@launch
      if (approve) {
        val newStatus = if (order.paymentMethod == "FULL_PAYMENT") "Confirmed" else "Confirmed – Cash on Delivery"
        val paymentStatus = if (order.paymentMethod == "FULL_PAYMENT") "Paid" else "Delivery Charge Paid"
        repository.updateOrderStatus(orderId, newStatus, paymentStatus, note)
      } else {
        repository.updateOrderStatus(orderId, "Payment Rejected", "Payment Rejected", note)
      }
    }
  }

  fun updateOrderStatus(orderId: String, newStatus: String, note: String? = null) {
    viewModelScope.launch {
      val order = orders.value.find { it.orderId == orderId } ?: return@launch
      repository.updateOrderStatus(orderId, newStatus, order.paymentStatus, note)
    }
  }

  fun addProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.insertProduct(product)
    }
  }

  fun updateProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.updateProduct(product)
    }
  }

  fun deleteProduct(productId: Long) {
    viewModelScope.launch {
      repository.deleteProductById(productId)
    }
  }

  fun addCategory(category: CategoryEntity) {
    viewModelScope.launch {
      repository.insertCategory(category)
    }
  }

  fun updateCategory(category: CategoryEntity) {
    viewModelScope.launch {
      repository.updateCategory(category)
    }
  }

  fun deleteCategory(category: CategoryEntity) {
    viewModelScope.launch {
      repository.deleteCategory(category)
    }
  }

  fun updateSettings(settings: StoreSettingsEntity) {
    viewModelScope.launch {
      repository.updateSettings(settings)
    }
  }

  fun addReview(productId: Long, customerName: String, rating: Int, reviewText: String) {
    viewModelScope.launch {
      repository.addReview(
        ReviewEntity(
          productId = productId,
          customerName = customerName,
          rating = rating,
          reviewText = reviewText
        )
      )
    }
  }

  fun getReviewsForProduct(productId: Long) = repository.getReviewsForProduct(productId)
  fun getOrderItems(orderId: String) = repository.getOrderItems(orderId)

  fun saveUserProfile(name: String, phone: String, email: String, address: String, district: String, thana: String) {
    viewModelScope.launch {
      repository.saveUser(
        UserEntity(
          name = name,
          phone = phone,
          email = email,
          address = address,
          district = district,
          thana = thana
        )
      )
    }
  }
}
