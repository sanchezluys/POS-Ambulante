package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.SaleWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSale(sale: Sale): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSaleItems(items: List<SaleItem>)

  @Transaction
  @Query("SELECT * FROM sales ORDER BY timestamp DESC")
  fun getAllSalesWithItems(): Flow<List<SaleWithItems>>

  @Transaction
  @Query("SELECT * FROM sales WHERE saleId = :saleId LIMIT 1")
  fun getSaleWithItemsById(saleId: Long): Flow<SaleWithItems?>

  @Transaction
  @Query("SELECT * FROM sales WHERE timestamp >= :startTimestamp AND timestamp <= :endTimestamp ORDER BY timestamp DESC")
  fun getSalesBetweenDates(startTimestamp: Long, endTimestamp: Long): Flow<List<SaleWithItems>>

  @Query("SELECT COUNT(*) FROM sales")
  suspend fun getSalesCount(): Int

  @Delete
  suspend fun deleteSale(sale: Sale)

  @Query("SELECT * FROM sale_items WHERE saleId = :saleId")
  suspend fun getItemsForSale(saleId: Long): List<SaleItem>
}
