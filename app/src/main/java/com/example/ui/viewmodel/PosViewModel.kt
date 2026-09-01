package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.PosApplication
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.SaleWithItems
import com.example.data.model.StoreSettings
import com.example.data.repository.PosRepository
import com.example.util.ReceiptUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

data class CartItem(
  val productId: Long? = null,
  val name: String,
  val category: String = "General",
  val size: String = "",
  val color: String = "",
  val unitCost: Double,
  val unitPrice: Double,
  val quantity: Int,
  val availableStock: Int = 999
) {
  val totalPrice: Double get() = unitPrice * quantity
  val totalCost: Double get() = unitCost * quantity
  val displayVariant: String
    get() = listOf(size, color).filter { it.isNotBlank() }.joinToString(" • ")
}

data class DailyReportSummary(
  val totalSales: Double = 0.0,
  val totalCost: Double = 0.0,
  val totalProfit: Double = 0.0,
  val marginPercentage: Double = 0.0,
  val salesCount: Int = 0,
  val itemsCount: Int = 0,
  val cashTotal: Double = 0.0,
  val transferTotal: Double = 0.0,
  val topItems: List<Triple<String, Int, Double>> = emptyList(),
  val sales: List<SaleWithItems> = emptyList()
)

class PosViewModel(
  application: Application,
  private val repository: PosRepository
) : AndroidViewModel(application) {

  private val prefs = application.getSharedPreferences("pos_ambulante_prefs", Context.MODE_PRIVATE)

  // Dark Mode Preference
  private val _isDarkMode = MutableStateFlow(prefs.getBoolean("pref_dark_mode", false))
  val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

  // First Launch Onboarding
  private val _isFirstLaunch = MutableStateFlow(!prefs.getBoolean("pref_first_launch_done", false))
  val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

  fun toggleDarkMode() {
    val newValue = !_isDarkMode.value
    _isDarkMode.value = newValue
    prefs.edit().putBoolean("pref_dark_mode", newValue).apply()
  }

  fun setDarkMode(enabled: Boolean) {
    _isDarkMode.value = enabled
    prefs.edit().putBoolean("pref_dark_mode", enabled).apply()
  }

  fun completeFirstLaunch(settings: StoreSettings? = null) {
    _isFirstLaunch.value = false
    prefs.edit().putBoolean("pref_first_launch_done", true).apply()
    if (settings != null) {
      updateSettings(settings)
    }
  }

  // Store Settings
  val storeSettings: StateFlow<StoreSettings> = repository.storeSettings
    .combine(MutableStateFlow(StoreSettings())) { saved, default ->
      saved ?: default
    }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StoreSettings())

  // Products
  val allProducts: StateFlow<List<Product>> = repository.allProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeProducts: StateFlow<List<Product>> = repository.activeProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Filter & Search in POS Catalog
  private val _posSearchQuery = MutableStateFlow("")
  val posSearchQuery: StateFlow<String> = _posSearchQuery.asStateFlow()

  private val _posSelectedCategory = MutableStateFlow("Todos")
  val posSelectedCategory: StateFlow<String> = _posSelectedCategory.asStateFlow()

  val filteredPosProducts: StateFlow<List<Product>> = combine(
    activeProducts,
    _posSearchQuery,
    _posSelectedCategory
  ) { products, query, category ->
    products.filter { prod ->
      val matchesQuery = query.isBlank() ||
        prod.name.contains(query, ignoreCase = true) ||
        prod.category.contains(query, ignoreCase = true) ||
        prod.size.contains(query, ignoreCase = true) ||
        prod.color.contains(query, ignoreCase = true) ||
        prod.barcodeOrCode.contains(query, ignoreCase = true)

      val matchesCategory = category == "Todos" ||
        (category == "Bajo Stock" && (prod.isLowStock || prod.isOutOfStock)) ||
        prod.category.equals(category, ignoreCase = true)

      matchesQuery && matchesCategory
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Cart State
  private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
  val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

  private val _discountAmount = MutableStateFlow(0.0)
  val discountAmount: StateFlow<Double> = _discountAmount.asStateFlow()

  private val _customerName = MutableStateFlow("Cliente")
  val customerName: StateFlow<String> = _customerName.asStateFlow()

  private val _customerPhone = MutableStateFlow("")
  val customerPhone: StateFlow<String> = _customerPhone.asStateFlow()

  private val _paymentMethod = MutableStateFlow("EFECTIVO")
  val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

  private val _cashTendered = MutableStateFlow(0.0)
  val cashTendered: StateFlow<Double> = _cashTendered.asStateFlow()

  private val _saleNotes = MutableStateFlow("")
  val saleNotes: StateFlow<String> = _saleNotes.asStateFlow()

  // Computed Cart values
  val cartSubtotal: StateFlow<Double> = _cartItems.combine(MutableStateFlow(Unit)) { items, _ ->
    items.sumOf { it.totalPrice }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val cartTotalCost: StateFlow<Double> = _cartItems.combine(MutableStateFlow(Unit)) { items, _ ->
    items.sumOf { it.totalCost }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val cartTotalAmount: StateFlow<Double> = combine(cartSubtotal, _discountAmount) { subtotal, discount ->
    (subtotal - discount).coerceAtLeast(0.0)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val cartTotalItemsCount: StateFlow<Int> = _cartItems.combine(MutableStateFlow(Unit)) { items, _ ->
    items.sumOf { it.quantity }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val changeGiven: StateFlow<Double> = combine(cartTotalAmount, _cashTendered, _paymentMethod) { total, tendered, method ->
    if (method.contains("EFECTIVO", ignoreCase = true) && tendered > total) {
      tendered - total
    } else {
      0.0
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  // Dialog State
  private val _activeReceiptSale = MutableStateFlow<SaleWithItems?>(null)
  val activeReceiptSale: StateFlow<SaleWithItems?> = _activeReceiptSale.asStateFlow()

  // Daily Reports Selection
  private val _selectedReportDate = MutableStateFlow(System.currentTimeMillis())
  val selectedReportDate: StateFlow<Long> = _selectedReportDate.asStateFlow()

  @OptIn(ExperimentalCoroutinesApi::class)
  val dailyReportSummary: StateFlow<DailyReportSummary> = _selectedReportDate.flatMapLatest { dateMillis ->
    val calendar = Calendar.getInstance().apply {
      timeInMillis = dateMillis
      set(Calendar.HOUR_OF_DAY, 0)
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
      set(Calendar.MILLISECOND, 0)
    }
    val startOfDay = calendar.timeInMillis
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfDay = calendar.timeInMillis

    repository.getSalesForDay(startOfDay, endOfDay)
  }.combine(MutableStateFlow(Unit)) { salesWithItems, _ ->
    calculateReportSummary(salesWithItems)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailyReportSummary())

  // POS Actions
  fun setPosSearchQuery(query: String) {
    _posSearchQuery.value = query
  }

  fun setPosCategory(category: String) {
    _posSelectedCategory.value = category
  }

  fun addProductToCart(product: Product, quantityToAdd: Int = 1) {
    val current = _cartItems.value.toMutableList()
    val existingIndex = current.indexOfFirst { it.productId == product.id }
    if (existingIndex >= 0) {
      val existing = current[existingIndex]
      val newQty = existing.quantity + quantityToAdd
      current[existingIndex] = existing.copy(quantity = newQty)
    } else {
      current.add(
        CartItem(
          productId = product.id,
          name = product.name,
          category = product.category,
          size = product.size,
          color = product.color,
          unitCost = product.costPrice,
          unitPrice = product.salePrice,
          quantity = quantityToAdd,
          availableStock = product.stockQuantity
        )
      )
    }
    _cartItems.value = current
  }

  fun addCustomCartItem(
    name: String,
    category: String,
    size: String,
    color: String,
    price: Double,
    cost: Double,
    quantity: Int
  ) {
    val current = _cartItems.value.toMutableList()
    current.add(
      CartItem(
        productId = null,
        name = if (name.isBlank()) "Artículo Venta Rápida" else name,
        category = if (category.isBlank()) "General" else category,
        size = size,
        color = color,
        unitCost = cost,
        unitPrice = price,
        quantity = quantity.coerceAtLeast(1),
        availableStock = 999
      )
    )
    _cartItems.value = current
  }

  fun updateCartQuantity(index: Int, newQuantity: Int) {
    if (index in _cartItems.value.indices) {
      val current = _cartItems.value.toMutableList()
      if (newQuantity <= 0) {
        current.removeAt(index)
      } else {
        current[index] = current[index].copy(quantity = newQuantity)
      }
      _cartItems.value = current
    }
  }

  fun removeCartItem(index: Int) {
    if (index in _cartItems.value.indices) {
      val current = _cartItems.value.toMutableList()
      current.removeAt(index)
      _cartItems.value = current
    }
  }

  fun clearCart() {
    _cartItems.value = emptyList()
    _discountAmount.value = 0.0
    _cashTendered.value = 0.0
    _customerName.value = "Cliente"
    _customerPhone.value = ""
    _paymentMethod.value = "EFECTIVO"
    _saleNotes.value = ""
  }

  fun setDiscount(amount: Double) {
    _discountAmount.value = amount.coerceAtLeast(0.0)
  }

  fun setCustomerName(name: String) {
    _customerName.value = name
  }

  fun setCustomerPhone(phone: String) {
    _customerPhone.value = phone
  }

  fun setPaymentMethod(method: String) {
    _paymentMethod.value = method
  }

  fun setCashTendered(amount: Double) {
    _cashTendered.value = amount
  }

  fun setSaleNotes(notes: String) {
    _saleNotes.value = notes
  }

  // Checkout Execution
  fun processCheckout(onComplete: (SaleWithItems) -> Unit) {
    val items = _cartItems.value
    if (items.isEmpty()) return

    val subtotal = cartSubtotal.value
    val discount = _discountAmount.value
    val total = cartTotalAmount.value
    val cost = cartTotalCost.value
    val profit = total - cost
    val tendered = if (_paymentMethod.value.contains("EFECTIVO", ignoreCase = true)) {
      if (_cashTendered.value > 0) _cashTendered.value else total
    } else {
      total
    }
    val change = (tendered - total).coerceAtLeast(0.0)

    val randomNum = Random.nextInt(1000, 9999)
    val receiptNum = "REC-$randomNum"

    val sale = Sale(
      timestamp = System.currentTimeMillis(),
      receiptNumber = receiptNum,
      customerName = _customerName.value.ifBlank { "Cliente" },
      customerPhone = _customerPhone.value.trim(),
      paymentMethod = _paymentMethod.value,
      subtotal = subtotal,
      discountAmount = discount,
      totalAmount = total,
      cashTendered = tendered,
      changeGiven = change,
      totalCost = cost,
      netProfit = profit,
      notes = _saleNotes.value
    )

    val saleItems = items.map { item ->
      SaleItem(
        saleId = 0,
        productId = item.productId,
        productName = item.name,
        category = item.category,
        size = item.size,
        color = item.color,
        unitCost = item.unitCost,
        unitPrice = item.unitPrice,
        quantity = item.quantity,
        totalPrice = item.totalPrice
      )
    }

    viewModelScope.launch {
      val saleId = repository.processSale(sale, saleItems)
      val completedSale = sale.copy(saleId = saleId)
      val completedWithItems = SaleWithItems(
        sale = completedSale,
        items = saleItems.map { it.copy(saleId = saleId) }
      )
      _activeReceiptSale.value = completedWithItems
      clearCart()
      onComplete(completedWithItems)
    }
  }

  fun showReceiptDialog(saleWithItems: SaleWithItems) {
    _activeReceiptSale.value = saleWithItems
  }

  fun dismissReceiptDialog() {
    _activeReceiptSale.value = null
  }

  // Inventory Actions
  fun addProduct(product: Product) {
    viewModelScope.launch {
      repository.insertProduct(product)
    }
  }

  fun updateProduct(product: Product) {
    viewModelScope.launch {
      repository.updateProduct(product)
    }
  }

  fun deleteProduct(product: Product) {
    viewModelScope.launch {
      repository.deleteProduct(product)
    }
  }

  fun adjustStock(productId: Long, delta: Int) {
    viewModelScope.launch {
      repository.adjustStock(productId, delta)
    }
  }

  // Store Settings Actions
  fun updateSettings(settings: StoreSettings) {
    viewModelScope.launch {
      repository.updateStoreSettings(settings)
    }
  }

  fun resetToSampleData() {
    viewModelScope.launch {
      repository.resetSampleData()
    }
  }

  // Report Date Actions
  fun setSelectedReportDate(dateMillis: Long) {
    _selectedReportDate.value = dateMillis
  }

  fun setTodayReport() {
    _selectedReportDate.value = System.currentTimeMillis()
  }

  fun setYesterdayReport() {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, -1)
    _selectedReportDate.value = cal.timeInMillis
  }

  fun deleteSale(sale: Sale) {
    viewModelScope.launch {
      repository.deleteSale(sale)
    }
  }

  fun shareDailyReportWhatsApp(context: Context) {
    val summary = dailyReportSummary.value
    val settings = storeSettings.value
    val reportText = ReceiptUtils.generateDailyReportText(
      dateMillis = _selectedReportDate.value,
      totalSales = summary.totalSales,
      totalProfit = summary.totalProfit,
      salesCount = summary.salesCount,
      itemsCount = summary.itemsCount,
      cashTotal = summary.cashTotal,
      transferTotal = summary.transferTotal,
      topItems = summary.topItems,
      settings = settings
    )
    ReceiptUtils.shareGeneralText(context, "Reporte de Ventas", reportText, settings.logoUri)
  }

  private fun calculateReportSummary(sales: List<SaleWithItems>): DailyReportSummary {
    if (sales.isEmpty()) return DailyReportSummary()

    var totalSales = 0.0
    var totalCost = 0.0
    var totalProfit = 0.0
    var itemsCount = 0
    var cashTotal = 0.0
    var transferTotal = 0.0

    val itemMap = mutableMapOf<String, Pair<Int, Double>>() // key -> (quantity, revenue)

    for (saleWithItems in sales) {
      val sale = saleWithItems.sale
      totalSales += sale.totalAmount
      totalCost += sale.totalCost
      totalProfit += sale.netProfit

      if (sale.paymentMethod.contains("EFECTIVO", ignoreCase = true)) {
        cashTotal += sale.totalAmount
      } else {
        transferTotal += sale.totalAmount
      }

      for (item in saleWithItems.items) {
        itemsCount += item.quantity
        val key = item.productName + if (item.size.isNotBlank()) " (${item.size})" else ""
        val current = itemMap.getOrDefault(key, Pair(0, 0.0))
        itemMap[key] = Pair(current.first + item.quantity, current.second + item.totalPrice)
      }
    }

    val topItems = itemMap.map { (name, stats) ->
      Triple(name, stats.first, stats.second)
    }.sortedByDescending { it.second }

    val margin = if (totalSales > 0) (totalProfit / totalSales) * 100 else 0.0

    return DailyReportSummary(
      totalSales = totalSales,
      totalCost = totalCost,
      totalProfit = totalProfit,
      marginPercentage = margin,
      salesCount = sales.size,
      itemsCount = itemsCount,
      cashTotal = cashTotal,
      transferTotal = transferTotal,
      topItems = topItems,
      sales = sales
    )
  }

  companion object {
    fun Factory(application: PosApplication): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
      @Suppress("UNCHECKED_CAST")
      override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PosViewModel(application, application.repository) as T
      }
    }
  }
}
