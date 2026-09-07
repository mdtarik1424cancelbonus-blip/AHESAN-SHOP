package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CheckoutScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrderSuccessScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ScreenTab
import com.example.ui.screens.ShopViewModel
import com.example.ui.theme.AhesanPrimary
import com.example.ui.theme.AhesanSecondary
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import com.example.ui.theme.BentoCardBorder
import com.example.ui.theme.BentoSlate100

class MainActivity : ComponentActivity() {
  private val viewModel: ShopViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        AhesanShopApp(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun AhesanShopApp(viewModel: ShopViewModel) {
  val currentTab by viewModel.currentTab.collectAsState()
  val selectedProduct by viewModel.selectedProduct.collectAsState()
  val selectedOrder by viewModel.selectedOrder.collectAsState()
  val showCheckout by viewModel.showCheckoutScreen.collectAsState()
  val lastPlacedOrderId by viewModel.lastPlacedOrderId.collectAsState()
  val showAdminPanel by viewModel.showAdminPanel.collectAsState()
  val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
  val cartItemCount by viewModel.cartItemCount.collectAsState()

  // Handle Back Navigation cleanly
  BackHandler(enabled = selectedProduct != null || showCheckout || lastPlacedOrderId != null || selectedOrder != null || showAdminPanel || currentTab != ScreenTab.HOME) {
    when {
      showAdminPanel -> viewModel.closeAdminPanel()
      lastPlacedOrderId != null -> {
        viewModel.resetConfirmation()
        viewModel.setTab(ScreenTab.ORDERS)
      }
      showCheckout -> viewModel.cancelCheckout()
      selectedOrder != null -> viewModel.closeOrderDetail()
      selectedProduct != null -> viewModel.closeProductDetail()
      currentTab != ScreenTab.HOME -> viewModel.setTab(ScreenTab.HOME)
    }
  }

  // Full Screen Overlays: Admin Panel, Order Confirmation, Checkout, Product Detail
  when {
    showAdminPanel && isAdminLoggedIn -> {
      AdminDashboardScreen(
        viewModel = viewModel,
        onClose = { viewModel.closeAdminPanel() }
      )
    }
    lastPlacedOrderId != null -> {
      OrderSuccessScreen(
        orderId = lastPlacedOrderId!!,
        viewModel = viewModel,
        onViewOrders = {
          viewModel.resetConfirmation()
          viewModel.setTab(ScreenTab.ORDERS)
        },
        onBackHome = {
          viewModel.resetConfirmation()
          viewModel.setTab(ScreenTab.HOME)
        }
      )
    }
    showCheckout -> {
      CheckoutScreen(
        viewModel = viewModel,
        onOrderPlaced = { orderId ->
          // Navigation handled inside VM with lastPlacedOrderId
        }
      )
    }
    selectedProduct != null -> {
      ProductDetailScreen(
        product = selectedProduct!!,
        viewModel = viewModel
      )
    }
    else -> {
      // Main Shell with Bottom Navigation (Home | Categories | Cart | Orders | Account)
      Scaffold(
        bottomBar = {
          NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier
              .border(BorderStroke(1.dp, BentoCardBorder))
              .testTag("bottom_nav_bar")
          ) {
            // 1. Home
            NavigationBarItem(
              selected = currentTab == ScreenTab.HOME,
              onClick = { viewModel.setTab(ScreenTab.HOME) },
              icon = {
                Icon(
                  imageVector = Icons.Default.Home,
                  contentDescription = "Home",
                  modifier = Modifier.size(24.dp)
                )
              },
              label = {
                Text(
                  text = "হোম",
                  fontSize = 11.sp,
                  fontWeight = if (currentTab == ScreenTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AhesanPrimary,
                selectedTextColor = AhesanPrimary,
                indicatorColor = AhesanPrimary.copy(alpha = 0.12f)
              ),
              modifier = Modifier.testTag("nav_tab_home")
            )

            // 2. Categories
            NavigationBarItem(
              selected = currentTab == ScreenTab.CATEGORIES,
              onClick = { viewModel.setTab(ScreenTab.CATEGORIES) },
              icon = {
                Icon(
                  imageVector = Icons.Default.Category,
                  contentDescription = "Categories",
                  modifier = Modifier.size(24.dp)
                )
              },
              label = {
                Text(
                  text = "ক্যাটাগরি",
                  fontSize = 11.sp,
                  fontWeight = if (currentTab == ScreenTab.CATEGORIES) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AhesanPrimary,
                selectedTextColor = AhesanPrimary,
                indicatorColor = AhesanPrimary.copy(alpha = 0.12f)
              ),
              modifier = Modifier.testTag("nav_tab_categories")
            )

            // 3. Cart (with Badge)
            NavigationBarItem(
              selected = currentTab == ScreenTab.CART,
              onClick = { viewModel.setTab(ScreenTab.CART) },
              icon = {
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
                    modifier = Modifier.size(24.dp)
                  )
                }
              },
              label = {
                Text(
                  text = "কার্ট",
                  fontSize = 11.sp,
                  fontWeight = if (currentTab == ScreenTab.CART) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AhesanPrimary,
                selectedTextColor = AhesanPrimary,
                indicatorColor = AhesanPrimary.copy(alpha = 0.12f)
              ),
              modifier = Modifier.testTag("nav_tab_cart")
            )

            // 4. Orders
            NavigationBarItem(
              selected = currentTab == ScreenTab.ORDERS,
              onClick = { viewModel.setTab(ScreenTab.ORDERS) },
              icon = {
                Icon(
                  imageVector = Icons.Default.ListAlt,
                  contentDescription = "Orders",
                  modifier = Modifier.size(24.dp)
                )
              },
              label = {
                Text(
                  text = "অর্ডার",
                  fontSize = 11.sp,
                  fontWeight = if (currentTab == ScreenTab.ORDERS) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AhesanPrimary,
                selectedTextColor = AhesanPrimary,
                indicatorColor = AhesanPrimary.copy(alpha = 0.12f)
              ),
              modifier = Modifier.testTag("nav_tab_orders")
            )

            // 5. Account
            NavigationBarItem(
              selected = currentTab == ScreenTab.ACCOUNT,
              onClick = { viewModel.setTab(ScreenTab.ACCOUNT) },
              icon = {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = "Account",
                  modifier = Modifier.size(24.dp)
                )
              },
              label = {
                Text(
                  text = "অ্যাকাউন্ট",
                  fontSize = 11.sp,
                  fontWeight = if (currentTab == ScreenTab.ACCOUNT) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AhesanPrimary,
                selectedTextColor = AhesanPrimary,
                indicatorColor = AhesanPrimary.copy(alpha = 0.12f)
              ),
              modifier = Modifier.testTag("nav_tab_account")
            )
          }
        },
        modifier = Modifier.fillMaxSize()
      ) { innerPadding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
          Crossfade(targetState = currentTab, label = "tab_transition") { tab ->
            when (tab) {
              ScreenTab.HOME -> HomeScreen(viewModel = viewModel)
              ScreenTab.CATEGORIES -> CategoriesScreen(viewModel = viewModel)
              ScreenTab.CART -> CartScreen(viewModel = viewModel)
              ScreenTab.ORDERS -> OrdersScreen(viewModel = viewModel)
              ScreenTab.ACCOUNT -> AccountScreen(viewModel = viewModel)
            }
          }
        }
      }
    }
  }
}
