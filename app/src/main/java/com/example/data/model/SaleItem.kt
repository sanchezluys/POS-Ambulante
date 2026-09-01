package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
  tableName = "sale_items",
  foreignKeys = [
    ForeignKey(
      entity = Sale::class,
      parentColumns = ["saleId"],
      childColumns = ["saleId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("saleId")]
)
data class SaleItem(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val saleId: Long,
  val productId: Long? = null,
  val productName: String,
  val category: String = "General",
  val size: String = "",
  val color: String = "",
  val unitCost: Double,
  val unitPrice: Double,
  val quantity: Int,
  val totalPrice: Double
)
