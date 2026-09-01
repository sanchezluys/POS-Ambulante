package com.example.data.repository

import com.example.data.dao.ProductDao
import com.example.data.dao.SaleDao
import com.example.data.dao.StoreSettingsDao
import com.example.data.database.AppDatabase
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.SaleWithItems
import com.example.data.model.StoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PosRepository(
  private val productDao: ProductDao,
  private val saleDao: SaleDao,
  private val storeSettingsDao: StoreSettingsDao
) {
  val activeProducts: Flow<List<Product>> = productDao.getActiveProducts()
  val allProducts: Flow<List<Product>> = productDao.getAllProducts()
  val storeSettings: Flow<StoreSettings?> = storeSettingsDao.getSettings()
  val allSales: Flow<List<SaleWithItems>> = saleDao.getAllSalesWithItems()

  fun getSalesForDay(startTimestamp: Long, endTimestamp: Long): Flow<List<SaleWithItems>> {
    return saleDao.getSalesBetweenDates(startTimestamp, endTimestamp)
  }

  suspend fun insertProduct(product: Product): Long = withContext(Dispatchers.IO) {
    productDao.insertProduct(product)
  }

  suspend fun updateProduct(product: Product) = withContext(Dispatchers.IO) {
    productDao.updateProduct(product)
  }

  suspend fun deleteProduct(product: Product) = withContext(Dispatchers.IO) {
    productDao.deleteProduct(product)
  }

  suspend fun adjustStock(productId: Long, delta: Int) = withContext(Dispatchers.IO) {
    if (delta > 0) {
      productDao.increaseStock(productId, delta)
    } else if (delta < 0) {
      productDao.decrementStock(productId, -delta)
    }
  }

  suspend fun processSale(sale: Sale, items: List<SaleItem>): Long = withContext(Dispatchers.IO) {
    val saleId = saleDao.insertSale(sale)
    val itemsWithSaleId = items.map { it.copy(saleId = saleId) }
    saleDao.insertSaleItems(itemsWithSaleId)

    // Decrement stock for inventory products
    for (item in itemsWithSaleId) {
      item.productId?.let { prodId ->
        productDao.decrementStock(prodId, item.quantity)
      }
    }
    saleId
  }

  suspend fun updateStoreSettings(settings: StoreSettings) = withContext(Dispatchers.IO) {
    storeSettingsDao.insertOrUpdate(settings)
  }

  suspend fun getSettingsDirect(): StoreSettings? = withContext(Dispatchers.IO) {
    storeSettingsDao.getSettingsDirect()
  }

  suspend fun resetSampleData() = withContext(Dispatchers.IO) {
    AppDatabase.populateInitialData(productDao, storeSettingsDao)
  }

  suspend fun deleteSale(sale: Sale) = withContext(Dispatchers.IO) {
    saleDao.deleteSale(sale)
  }
}
