package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "store_settings")
data class StoreSettings(
  @PrimaryKey
  val id: Int = 1,
  val storeName: String = "Moda & Calzado Ambulante",
  val ownerName: String = "Vendedor",
  val ownerPhone: String = "",
  val address: String = "",
  val taxId: String = "", // RUT / NIT (opcional)
  val currencyCode: String = "COL", // "USD", "COL", "SOL"
  val currencySymbol: String = "$",
  val thousandsSeparator: String = ".", // "." for point (1.000) or "," for comma (1,000)
  val receiptFooter: String = "¡Gracias por apoyar el comercio independiente! Vuelva pronto.",
  val countryPhonePrefix: String = "+57",
  val logoUri: String = "", // URI o ruta interna del logo del negocio/punto de venta (opcional)
  val isConfigured: Boolean = false // Indica si la configuración inicial fue establecida (bloquea edición de datos fiscales)
)
