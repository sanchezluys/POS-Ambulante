package com.example.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SaleWithItems(
  @Embedded
  val sale: Sale,
  @Relation(
    parentColumn = "saleId",
    entityColumn = "saleId"
  )
  val items: List<SaleItem>
)
