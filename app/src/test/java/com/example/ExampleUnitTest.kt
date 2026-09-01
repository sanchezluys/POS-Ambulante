package com.example

import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.StoreSettings
import com.example.util.ContactUtils
import com.example.util.FormatUtils
import com.example.util.ReceiptUtils
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun `currency formatting works with different separators`() {
    val amount = 1500000.0

    val dotFormatted = FormatUtils.formatCurrency(amount, "$", ".")
    assertTrue("Dot separator formatted: $dotFormatted", dotFormatted.contains("1.500.000"))

    val commaFormatted = FormatUtils.formatCurrency(amount, "$", ",")
    assertTrue("Comma separator formatted: $commaFormatted", commaFormatted.contains("1,500,000"))

    val noneFormatted = FormatUtils.formatCurrency(amount, "$", "none")
    assertEquals("$ 1500000", noneFormatted)
  }

  @Test
  fun `phone number sanitization cleans various input formats`() {
    assertEquals("3001234567", ContactUtils.cleanPhoneNumber("300-123-4567"))
    assertEquals("3001234567", ContactUtils.cleanPhoneNumber("(300) 123 4567"))
    assertEquals("+573001234567", ContactUtils.cleanPhoneNumber("+57 300 123 4567"))
    assertEquals("+18005550199", ContactUtils.cleanPhoneNumber("+1 (800) 555-0199"))
    assertEquals("", ContactUtils.cleanPhoneNumber("   "))
  }

  @Test
  fun `product margin and display variant calculations are accurate`() {
    val productWithVariant = Product(
      name = "Zapatillas Urbanas",
      category = "Calzado",
      size = "41",
      color = "Blanco",
      costPrice = 45000.0,
      salePrice = 85000.0,
      stockQuantity = 5
    )
    assertEquals(40000.0, productWithVariant.profitMarginPerUnit, 0.01)
    assertEquals("41 • Blanco", productWithVariant.displayVariant)

    val productNoVariant = Product(
      name = "Gorra Clásica",
      category = "Accesorios",
      costPrice = 10000.0,
      salePrice = 20000.0,
      stockQuantity = 8
    )
    assertEquals("", productNoVariant.displayVariant)
  }

  @Test
  fun `digital receipt generation includes mandatory metadata and items`() {
    val settings = StoreSettings(
      storeName = "Tienda Ropa Moda",
      ownerName = "Carlos",
      ownerPhone = "3001234567",
      address = "Centro Comercial Calle 10 # 5-20",
      taxId = "10203040-1",
      currencySymbol = "$",
      thousandsSeparator = ".",
      receiptFooter = "¡Gracias por su compra! Vuelva pronto."
    )

    val sale = Sale(
      saleId = 101,
      receiptNumber = "REC-1001",
      totalAmount = 50000.0,
      subtotal = 55000.0,
      discountAmount = 5000.0,
      paymentMethod = "EFECTIVO",
      cashTendered = 60000.0,
      changeGiven = 10000.0,
      customerName = "Juan Pérez",
      customerPhone = "3119876543",
      timestamp = 1700000000000L
    )

    val items = listOf(
      SaleItem(
        id = 1,
        saleId = 101,
        productId = 1,
        productName = "Pantalón Jean",
        category = "Ropa",
        size = "32",
        color = "Azul",
        unitPrice = 55000.0,
        unitCost = 30000.0,
        quantity = 1,
        totalPrice = 55000.0
      )
    )

    val receipt = ReceiptUtils.generateReceiptText(sale, items, settings)

    assertTrue(receipt.contains("TIENDA ROPA MODA"))
    assertTrue(receipt.contains("Carlos"))
    assertTrue(receipt.contains("3001234567"))
    assertTrue(receipt.contains("Centro Comercial Calle 10 # 5-20"))
    assertTrue(receipt.contains("Juan Pérez"))
    assertTrue(receipt.contains("Pantalón Jean"))
    assertTrue(receipt.contains("EFECTIVO") || receipt.contains("Efectivo"))
    assertTrue(receipt.contains("¡Gracias por su compra! Vuelva pronto."))
  }
}


