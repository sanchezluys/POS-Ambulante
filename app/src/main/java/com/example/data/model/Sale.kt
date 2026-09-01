package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
  @PrimaryKey(autoGenerate = true)
  val saleId: Long = 0,
  val timestamp: Long = System.currentTimeMillis(),
  val receiptNumber: String, // Ej: "REC-1001"
  val customerName: String = "Cliente",
  val customerPhone: String = "", // Número para envío por WhatsApp
  val paymentMethod: String = "EFECTIVO", // "EFECTIVO", "TRANSFERENCIA", "MIXTO"
  val subtotal: Double,
  val discountAmount: Double = 0.0,
  val totalAmount: Double,
  val cashTendered: Double = 0.0, // Efectivo recibido
  val changeGiven: Double = 0.0, // Vueltas / Cambio entregado
  val totalCost: Double = 0.0, // Costo total para cálculo de ganancia
  val netProfit: Double = 0.0, // totalAmount - totalCost
  val notes: String = ""
)
