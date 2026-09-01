package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.util.FormatUtils
import com.example.util.ReceiptUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

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
}

