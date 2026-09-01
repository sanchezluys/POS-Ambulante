package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.AppDatabase
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var database: AppDatabase

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
      .allowMainThreadQueries()
      .build()
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("POS Ambulante", appName)
  }

  @Test
  fun `product margin calculation is accurate`() {
    val product = Product(
      name = "Camiseta Oversize",
      category = "Ropa",
      size = "L",
      costPrice = 12000.0,
      salePrice = 25000.0,
      stockQuantity = 10
    )
    assertEquals(13000.0, product.profitMarginPerUnit, 0.01)
    assertEquals("L", product.displayVariant)
  }

  @Test
  fun `database product insertion and stock decrement works`() = runBlocking {
    val product = Product(
      name = "Jean Slim Fit",
      category = "Ropa",
      size = "32",
      costPrice = 30000.0,
      salePrice = 60000.0,
      stockQuantity = 15
    )
    val id = database.productDao().insertProduct(product)
    assertTrue(id > 0)

    val products = database.productDao().getActiveProducts().first()
    assertEquals(1, products.size)
    assertEquals("Jean Slim Fit", products[0].name)

    // Decrement stock
    database.productDao().decrementStock(id, 3)
    val updated = database.productDao().getProductById(id)
    assertNotNull(updated)
    assertEquals(12, updated?.stockQuantity)
  }

  @Test
  fun `database sale and items creation works`() = runBlocking {
    val sale = Sale(
      receiptNumber = "REC-1001",
      totalAmount = 50000.0,
      subtotal = 50000.0,
      discountAmount = 0.0,
      paymentMethod = "NEQUI",
      customerName = "Cliente Juan",
      customerPhone = "3001234567"
    )
    val saleId = database.saleDao().insertSale(sale)
    assertTrue(saleId > 0)

    val item = SaleItem(
      saleId = saleId,
      productId = 1,
      productName = "Gorra Nike",
      category = "Accesorios",
      size = "",
      color = "Negro",
      unitPrice = 50000.0,
      unitCost = 25000.0,
      quantity = 1,
      totalPrice = 50000.0
    )
    database.saleDao().insertSaleItems(listOf(item))

    val allSales = database.saleDao().getAllSalesWithItems().first()
    assertEquals(1, allSales.size)
    assertEquals("Cliente Juan", allSales[0].sale.customerName)
    assertEquals(1, allSales[0].items.size)
    assertEquals("Gorra Nike", allSales[0].items[0].productName)
  }
}



