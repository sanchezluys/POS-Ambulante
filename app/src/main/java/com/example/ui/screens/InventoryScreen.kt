package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MetricStatCard
import com.example.ui.components.StockBadge
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils

@Composable
fun InventoryScreen(
  viewModel: PosViewModel,
  modifier: Modifier = Modifier
) {
  val allProducts by viewModel.allProducts.collectAsState()
  val storeSettings by viewModel.storeSettings.collectAsState()
  val symbol = storeSettings.currencySymbol
  val sep = storeSettings.thousandsSeparator

  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Todos") }
  var productToEdit by remember { mutableStateOf<Product?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }
  var productToDelete by remember { mutableStateOf<Product?>(null) }

  val categories = listOf("Todos", "Ropa", "Calzado", "Accesorios", "Bajo Stock")

  val filteredProducts = remember(allProducts, searchQuery, selectedCategory) {
    allProducts.filter { prod ->
      val matchesQuery = searchQuery.isBlank() ||
        prod.name.contains(searchQuery, ignoreCase = true) ||
        prod.category.contains(searchQuery, ignoreCase = true) ||
        prod.size.contains(searchQuery, ignoreCase = true) ||
        prod.color.contains(searchQuery, ignoreCase = true) ||
        prod.barcodeOrCode.contains(searchQuery, ignoreCase = true)

      val matchesCategory = selectedCategory == "Todos" ||
        (selectedCategory == "Bajo Stock" && (prod.isLowStock || prod.isOutOfStock)) ||
        prod.category.equals(selectedCategory, ignoreCase = true)

      matchesQuery && matchesCategory
    }
  }

  // Inventory Totals
  val totalUnits = remember(allProducts) { allProducts.sumOf { it.stockQuantity } }
  val totalCostValue = remember(allProducts) { allProducts.sumOf { it.costPrice * it.stockQuantity } }
  val totalSaleValue = remember(allProducts) { allProducts.sumOf { it.salePrice * it.stockQuantity } }

  Box(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 72.dp)
    ) {
      // Header Section
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(horizontal = 16.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Control de Inventario",
          fontWeight = FontWeight.Black,
          fontSize = 20.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Gestiona tallas, stock y costos de tus prendas y calzado",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Inventory KPI Cards
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          MetricStatCard(
            title = "Prendas/Pares",
            value = "$totalUnits un.",
            subtitle = "${allProducts.size} referencias",
            icon = Icons.Default.Inventory,
            modifier = Modifier.weight(1f)
          )

          MetricStatCard(
            title = "Valor Inventario",
            value = FormatUtils.formatCurrency(totalSaleValue, symbol, sep),
            subtitle = "Costo: ${FormatUtils.formatCurrency(totalCostValue, symbol, sep)}",
            icon = Icons.Default.MonetizationOn,
            modifier = Modifier.weight(1.2f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Buscar en inventario...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.primary)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("inventory_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Categories Row
        CategoryFilterRow(
          categories = categories,
          selectedCategory = selectedCategory,
          onCategorySelected = { selectedCategory = it },
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
        )
      }

      // Products List
      if (filteredProducts.isEmpty()) {
        EmptyStateView(
          icon = Icons.Default.Inventory,
          title = if (searchQuery.isNotBlank()) "No se encontraron productos" else "Inventario vacío",
          description = "Toca el botón '+' para agregar nuevas prendas, zapatos o accesorios a tu inventario.",
          modifier = Modifier.weight(1f)
        )
      } else {
        LazyColumn(
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("inventory_list")
        ) {
          items(filteredProducts, key = { it.id }) { product ->
            InventoryProductItem(
              product = product,
              currencySymbol = symbol,
              thousandsSeparator = sep,
              onAdjustStock = { delta -> viewModel.adjustStock(product.id, delta) },
              onEdit = { productToEdit = product },
              onDelete = { productToDelete = product }
            )
          }
        }
      }
    }

    // FAB to Add Product
    FloatingActionButton(
      onClick = { showAddDialog = true },
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("add_product_fab")
    ) {
      Icon(Icons.Default.Add, contentDescription = "Nuevo Producto")
    }
  }

  // Add Product Dialog
  if (showAddDialog) {
    AddEditProductDialog(
      productToEdit = null,
      currencySymbol = symbol,
      onSave = { newProduct -> viewModel.addProduct(newProduct) },
      onDismiss = { showAddDialog = false }
    )
  }

  // Edit Product Dialog
  productToEdit?.let { prod ->
    AddEditProductDialog(
      productToEdit = prod,
      currencySymbol = symbol,
      onSave = { updatedProduct -> viewModel.updateProduct(updatedProduct) },
      onDismiss = { productToEdit = null }
    )
  }

  // Delete Confirmation Dialog
  productToDelete?.let { prod ->
    AlertDialog(
      onDismissRequest = { productToDelete = null },
      title = { Text("¿Eliminar producto?") },
      text = { Text("¿Estás seguro de que deseas eliminar '${prod.name}' (${prod.displayVariant}) de tu inventario?") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.deleteProduct(prod)
            productToDelete = null
          }
        ) {
          Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { productToDelete = null }) {
          Text("Cancelar")
        }
      }
    )
  }
}

@Composable
fun InventoryProductItem(
  product: Product,
  currencySymbol: String,
  thousandsSeparator: String = ".",
  onAdjustStock: (Int) -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
      .testTag("inventory_item_${product.id}")
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = product.category,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          if (product.displayVariant.isNotBlank()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = product.displayVariant,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        StockBadge(stock = product.stockQuantity, minAlert = product.minStockAlert)
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = product.name,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Price & Profit breakdown
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Venta: ",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = FormatUtils.formatCurrency(product.salePrice, currencySymbol, thousandsSeparator),
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Costo: ",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = FormatUtils.formatCurrency(product.costPrice, currencySymbol, thousandsSeparator),
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "(Ganancia: ${FormatUtils.formatCurrency(product.profitMarginPerUnit, currencySymbol, thousandsSeparator)})",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFF2E7D32)
            )
          }
        }

        // Quick Stock Adjust Buttons
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          IconButton(
            onClick = { onAdjustStock(-1) },
            enabled = product.stockQuantity > 0,
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Restar 1",
              modifier = Modifier.size(16.dp)
            )
          }

          Text(
            text = "${product.stockQuantity}",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
          )

          IconButton(
            onClick = { onAdjustStock(1) },
            modifier = Modifier.size(28.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Sumar 1",
              modifier = Modifier.size(16.dp),
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
      Spacer(modifier = Modifier.height(4.dp))

      // Action Buttons (Edit & Delete)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        TextButton(
          onClick = onEdit,
          modifier = Modifier.testTag("edit_product_${product.id}")
        ) {
          Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Editar", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.width(4.dp))

        TextButton(
          onClick = onDelete,
          modifier = Modifier.testTag("delete_product_${product.id}")
        ) {
          Icon(
            Icons.Default.Delete,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Eliminar", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
        }
      }
    }
  }
}
