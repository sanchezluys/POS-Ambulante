package com.example

import android.app.Application
import com.example.data.database.AppDatabase
import com.example.data.repository.PosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class PosApplication : Application() {
  private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
  
  val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
  val repository by lazy {
    PosRepository(
      database.productDao(),
      database.saleDao(),
      database.storeSettingsDao()
    )
  }
}
