package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val category: String, // "Ropa", "Calzado", "Accesorios", "Bazar", etc.
  val size: String = "", // "S", "M", "L", "XL", "38", "39", "40", "41", "42", "Única", etc.
  val color: String = "", // "Negro", "Blanco", "Azul", etc.
  val costPrice: Double, // Costo de compra
  val salePrice: Double, // Precio de venta al público
  val stockQuantity: Int, // Existencias disponibles
  val minStockAlert: Int = 3, // Umbral para alerta de bajo inventario
  val barcodeOrCode: String = "", // Código interno o referencia rápida
  val isActive: Boolean = true
) {
  val isLowStock: Boolean
    get() = stockQuantity <= minStockAlert && stockQuantity > 0

  val isOutOfStock: Boolean
    get() = stockQuantity <= 0

  val displayVariant: String
    get() = listOf(size, color).filter { it.isNotBlank() }.joinToString(" • ")

  val profitMarginPerUnit: Double
    get() = salePrice - costPrice
}
