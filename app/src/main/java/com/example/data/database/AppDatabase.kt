package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ProductDao
import com.example.data.dao.SaleDao
import com.example.data.dao.StoreSettingsDao
import com.example.data.model.Product
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.StoreSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    Product::class,
    Sale::class,
    SaleItem::class,
    StoreSettings::class
  ],
  version = 4,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun productDao(): ProductDao
  abstract fun saleDao(): SaleDao
  abstract fun storeSettingsDao(): StoreSettingsDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "pos_ambulante_database"
        )
          .fallbackToDestructiveMigration()
          .addCallback(AppDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class AppDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.productDao(), database.storeSettingsDao())
          }
        }
      }
    }

    suspend fun populateInitialData(productDao: ProductDao, settingsDao: StoreSettingsDao) {
      // Configuración inicial
      settingsDao.insertOrUpdate(
        StoreSettings(
          id = 1,
          storeName = "Moda & Calzado Ambulante",
          ownerName = "Mi Negocio",
          ownerPhone = "",
          currencySymbol = "$",
          receiptFooter = "¡Gracias por su compra! Garantía de cambio 3 días.",
          countryPhonePrefix = "+57"
        )
      )

      // Productos de ropa y calzado de ejemplo para vendedores ambulantes
      val sampleProducts = listOf(
        Product(
          name = "Camiseta Básica Oversize",
          category = "Ropa",
          size = "M",
          color = "Negro",
          costPrice = 12000.0,
          salePrice = 25000.0,
          stockQuantity = 15,
          minStockAlert = 3,
          barcodeOrCode = "ROP-01"
        ),
        Product(
          name = "Camiseta Básica Oversize",
          category = "Ropa",
          size = "L",
          color = "Blanco",
          costPrice = 12000.0,
          salePrice = 25000.0,
          stockQuantity = 12,
          minStockAlert = 3,
          barcodeOrCode = "ROP-02"
        ),
        Product(
          name = "Jeans Slim Fit Stretch",
          category = "Ropa",
          size = "32",
          color = "Azul Clásico",
          costPrice = 35000.0,
          salePrice = 65000.0,
          stockQuantity = 8,
          minStockAlert = 2,
          barcodeOrCode = "ROP-03"
        ),
        Product(
          name = "Bermuda Playera Cargo",
          category = "Ropa",
          size = "L",
          color = "Beige",
          costPrice = 18000.0,
          salePrice = 35000.0,
          stockQuantity = 10,
          minStockAlert = 2,
          barcodeOrCode = "ROP-04"
        ),
        Product(
          name = "Zapatillas Urbanas Sneakers",
          category = "Calzado",
          size = "40",
          color = "Blanco",
          costPrice = 45000.0,
          salePrice = 85000.0,
          stockQuantity = 6,
          minStockAlert = 2,
          barcodeOrCode = "CAL-01"
        ),
        Product(
          name = "Zapatillas Urbanas Sneakers",
          category = "Calzado",
          size = "41",
          color = "Negro",
          costPrice = 45000.0,
          salePrice = 85000.0,
          stockQuantity = 5,
          minStockAlert = 2,
          barcodeOrCode = "CAL-02"
        ),
        Product(
          name = "Sandalias Ergonómicas Confort",
          category = "Calzado",
          size = "38",
          color = "Negro",
          costPrice = 14000.0,
          salePrice = 28000.0,
          stockQuantity = 9,
          minStockAlert = 3,
          barcodeOrCode = "CAL-03"
        ),
        Product(
          name = "Gorra Urbana Snapback",
          category = "Accesorios",
          size = "Única",
          color = "Negro / Logo",
          costPrice = 9000.0,
          salePrice = 20000.0,
          stockQuantity = 14,
          minStockAlert = 3,
          barcodeOrCode = "ACC-01"
        ),
        Product(
          name = "Pack 3 Medias Tobilleras",
          category = "Accesorios",
          size = "Única",
          color = "Surtido",
          costPrice = 5000.0,
          salePrice = 12000.0,
          stockQuantity = 20,
          minStockAlert = 5,
          barcodeOrCode = "ACC-02"
        ),
        Product(
          name = "Correa / Cinturón Cuero Sintético",
          category = "Accesorios",
          size = "110cm",
          color = "Café",
          costPrice = 8000.0,
          salePrice = 18000.0,
          stockQuantity = 7,
          minStockAlert = 2,
          barcodeOrCode = "ACC-03"
        )
      )

      productDao.insertProducts(sampleProducts)
    }
  }
}
