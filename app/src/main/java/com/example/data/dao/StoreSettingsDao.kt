package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.StoreSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreSettingsDao {
  @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
  fun getSettings(): Flow<StoreSettings?>

  @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
  suspend fun getSettingsDirect(): StoreSettings?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(settings: StoreSettings)
}
