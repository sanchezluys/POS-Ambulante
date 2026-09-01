package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.data.model.SaleWithItems
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.EmptyStateView
import com.example.ui.components.StockBadge
import com.example.ui.theme.PosPrimary
import com.example.ui.theme.PosSecondary
import com.example.ui.theme.PosSuccess
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils

@Composable
fun PosSaleScreen(
  viewModel: PosViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val searchQuery by viewModel.posSearchQuery.collectAsState()
  val selectedCategory by viewModel.posSelectedCategory.collectAsState()
  val products by viewModel.filteredPosProducts.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val cartTotal by viewModel.cartTotalAmount.collectAsState()
  val cartCount by viewModel.cartTotalItemsCount.collectAsState()
  val storeSettings by viewModel.storeSettings.collectAsState()
  val activeReceiptSale by viewModel.activeReceiptSale.collectAsState()
  val symbol = storeSettings.currencySymbol
  val sep = storeSettings.thousandsSeparator

  var showCheckoutSheet by remember { mutableStateOf(false) }
  var showQuickCustomDialog by remember { mutableStateOf(false) }
  var showNewSaleConfirmDialog by remember { mutableStateOf(false) }

  val categories = listOf("Todos", "Ropa", "Calzado", "Accesorios", "Bajo Stock")

  Box(modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(bottom = if (cartItems.isNotEmpty()) 80.dp else 0.dp)
    ) {
      // Top Header & Search Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Storefront,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = storeSettings.storeName,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "Modo Offline",
                fontSize = 11.sp,
                color = PosSuccess,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Button "Nueva Venta"
            Button(
              onClick = {
                if (cartItems.isNotEmpty()) {
                  showNewSaleConfirmDialog = true
                } else {
                  viewModel.setPosSearchQuery("")
                  viewModel.setPosCategory("Todos")
                  Toast.makeText(context, "Nueva venta lista", Toast.LENGTH_SHORT).show()
                }
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
              ),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier.testTag("new_sale_button")
            ) {
              Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Nueva Venta", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // Button for Quick Custom Unregistered Item
            Button(
              onClick = { showQuickCustomDialog = true },
              colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
              ),
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
              modifier = Modifier.testTag("open_quick_item_button")
            ) {
              Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Rápida", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setPosSearchQuery(it) },
          placeholder = { Text("Buscar prenda, calzado, talla, color...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = MaterialTheme.colorScheme.primary)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { viewModel.setPosSearchQuery("") }) {
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
            .testTag("pos_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Categories Scroll
        CategoryFilterRow(
          categories = categories,
          selectedCategory = selectedCategory,
          onCategorySelected = { viewModel.setPosCategory(it) },
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
        )
      }

      // Products Grid
      if (products.isEmpty()) {
        EmptyStateView(
          icon = Icons.Default.ShoppingCart,
          title = if (searchQuery.isNotBlank()) "No se encontraron productos" else "No hay productos en esta categoría",
          description = "Usa 'Venta Rápida' para cobrar artículos sin registrar o agrega productos en la pestaña de Inventario.",
          modifier = Modifier.weight(1f)
        )
      } else {
        LazyVerticalGrid(
          columns = GridCells.Adaptive(minSize = 160.dp),
          contentPadding = PaddingValues(16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("products_grid")
        ) {
          items(products, key = { it.id }) { product ->
            ProductPosCard(
              product = product,
              currencySymbol = symbol,
              thousandsSeparator = sep,
              onAddToCart = { viewModel.addProductToCart(product) }
            )
          }
        }
      }
    }

    // Sticky Bottom Cart Bar
    AnimatedVisibility(
      visible = cartItems.isNotEmpty(),
      enter = slideInVertically(initialOffsetY = { it }),
      exit = slideOutVertically(targetOffsetY = { it }),
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(16.dp)
    ) {
      Surface(
        color = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .clickable { showCheckoutSheet = true }
          .testTag("sticky_cart_bar")
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "$cartCount ${if (cartCount == 1) "artículo" else "artículos"}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium
              )
              Text(
                text = FormatUtils.formatCurrency(cartTotal, symbol, sep),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Black
              )
            }
          }

          Button(
            onClick = { showCheckoutSheet = true },
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.secondary,
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            modifier = Modifier.testTag("checkout_cta_button")
          ) {
            Text(
              text = "COBRAR",
              fontWeight = FontWeight.Black,
              fontSize = 14.sp
            )
          }
        }
      }
    }
  }

  // Checkout Bottom Sheet
  if (showCheckoutSheet) {
    CheckoutBottomSheet(
      viewModel = viewModel,
      onDismiss = { showCheckoutSheet = false },
      onSaleCompleted = { saleWithItems ->
        viewModel.showReceiptDialog(saleWithItems)
      }
    )
  }

  // Quick Custom Item Dialog
  if (showQuickCustomDialog) {
    QuickCustomItemDialog(
      viewModel = viewModel,
      currencySymbol = symbol,
      thousandsSeparator = sep,
      onDismiss = { showQuickCustomDialog = false }
    )
  }

  // Receipt Dialog (Triggered after sale or when viewing history)
  activeReceiptSale?.let { saleWithItems ->
    ReceiptDialog(
      saleWithItems = saleWithItems,
      settings = storeSettings,
      onDismiss = { viewModel.dismissReceiptDialog() }
    )
  }

  // New Sale Confirmation Dialog
  if (showNewSaleConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showNewSaleConfirmDialog = false },
      title = { Text("¿Iniciar Nueva Venta?", fontWeight = FontWeight.Bold) },
      text = { Text("Se limpiará el carrito actual ($cartCount artículos). ¿Deseas continuar?") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.clearCart()
            viewModel.setPosSearchQuery("")
            viewModel.setPosCategory("Todos")
            showNewSaleConfirmDialog = false
            Toast.makeText(context, "Nueva venta iniciada", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
          )
        ) {
          Text("Sí, Nueva Venta", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewSaleConfirmDialog = false }) {
          Text("Cancelar")
        }
      }
    )
  }
}

@Composable
fun ProductPosCard(
  product: Product,
  currencySymbol: String,
  thousandsSeparator: String = ".",
  onAddToCart: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onAddToCart() }
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
      .testTag("product_pos_card_${product.id}")
  ) {
    Column(
      modifier = Modifier.padding(12.dp)
    ) {
      // Category & Stock Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = product.category,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
        StockBadge(stock = product.stockQuantity, minAlert = product.minStockAlert)
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Product Name
      Text(
        text = product.name,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 18.sp
      )

      // Variant (Size • Color)
      if (product.displayVariant.isNotBlank()) {
        Spacer(modifier = Modifier.height(3.dp))
        Text(
          text = product.displayVariant,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Price & Quick Add Button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = FormatUtils.formatCurrency(product.salePrice, currencySymbol, thousandsSeparator),
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        IconButton(
          onClick = onAddToCart,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .testTag("add_to_cart_btn_${product.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
