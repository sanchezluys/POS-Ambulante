package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Product

@Composable
fun AddEditProductDialog(
  productToEdit: Product? = null,
  currencySymbol: String = "$",
  onSave: (Product) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf(productToEdit?.name ?: "") }
  var category by remember { mutableStateOf(productToEdit?.category ?: "Ropa") }
  var size by remember { mutableStateOf(productToEdit?.size ?: "") }
  var color by remember { mutableStateOf(productToEdit?.color ?: "") }
  var costPriceInput by remember {
    mutableStateOf(productToEdit?.costPrice?.toInt()?.toString() ?: "")
  }
  var salePriceInput by remember {
    mutableStateOf(productToEdit?.salePrice?.toInt()?.toString() ?: "")
  }
  var stockQuantityInput by remember {
    mutableStateOf(productToEdit?.stockQuantity?.toString() ?: "10")
  }
  var minStockAlertInput by remember {
    mutableStateOf(productToEdit?.minStockAlert?.toString() ?: "3")
  }
  var barcodeOrCode by remember {
    mutableStateOf(productToEdit?.barcodeOrCode ?: "")
  }

  val categories = listOf("Ropa", "Calzado", "Accesorios", "Bazar", "General")
  val quickSizes = if (category == "Calzado") {
    listOf("37", "38", "39", "40", "41", "42", "43")
  } else {
    listOf("S", "M", "L", "XL", "XXL", "Única")
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .clip(RoundedCornerShape(24.dp)),
      color = MaterialTheme.colorScheme.surface
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Inventory2,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (productToEdit != null) "Editar Producto" else "Nuevo Producto",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nombre de la prenda / calzado *") },
          placeholder = { Text("Ej: Camiseta Polo, Zapatillas Runner") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("product_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category pills
        Text(
          text = "Categoría:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.padding(vertical = 4.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = category == cat
            FilterChip(
              selected = isSelected,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Size chips
        Text(
          text = "Talla sugerida:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.padding(vertical = 4.dp)
        ) {
          quickSizes.take(5).forEach { s ->
            val isSelected = size == s
            FilterChip(
              selected = isSelected,
              onClick = { size = if (size == s) "" else s },
              label = { Text(s, fontSize = 12.sp) }
            )
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = size,
            onValueChange = { size = it },
            label = { Text("Talla") },
            placeholder = { Text("Ej: M, 40") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("product_size_input")
          )
          OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color") },
            placeholder = { Text("Ej: Negro") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("product_color_input")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cost & Sale Price
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = costPriceInput,
            onValueChange = { costPriceInput = it },
            label = { Text("Costo compra ($currencySymbol)") },
            placeholder = { Text("15000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("product_cost_input")
          )

          OutlinedTextField(
            value = salePriceInput,
            onValueChange = { salePriceInput = it },
            label = { Text("Precio Venta ($currencySymbol) *") },
            placeholder = { Text("30000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("product_price_input")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Stock & Min Alert
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = stockQuantityInput,
            onValueChange = { stockQuantityInput = it },
            label = { Text("Existencias (Stock) *") },
            placeholder = { Text("10") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("product_stock_input")
          )

          OutlinedTextField(
            value = minStockAlertInput,
            onValueChange = { minStockAlertInput = it },
            label = { Text("Aviso bajo stock") },
            placeholder = { Text("3") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("product_min_stock_input")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = barcodeOrCode,
          onValueChange = { barcodeOrCode = it },
          label = { Text("Código / Referencia rápida (opcional)") },
          placeholder = { Text("Ej: ROP-12, CAL-05") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        val cost = costPriceInput.toDoubleOrNull() ?: 0.0
        val sale = salePriceInput.toDoubleOrNull() ?: 0.0
        val stock = stockQuantityInput.toIntOrNull() ?: 0
        val minAlert = minStockAlertInput.toIntOrNull() ?: 3
        val isValid = name.isNotBlank() && sale > 0

        Button(
          onClick = {
            if (isValid) {
              val product = Product(
                id = productToEdit?.id ?: 0,
                name = name.trim(),
                category = category,
                size = size.trim(),
                color = color.trim(),
                costPrice = if (cost > 0) cost else (sale * 0.5),
                salePrice = sale,
                stockQuantity = stock,
                minStockAlert = minAlert,
                barcodeOrCode = barcodeOrCode.trim()
              )
              onSave(product)
              onDismiss()
            }
          },
          enabled = isValid,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("save_product_button")
        ) {
          Icon(Icons.Default.Save, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (productToEdit != null) "Guardar Cambios" else "Crear Producto",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
      }
    }
  }
}
