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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.viewmodel.PosViewModel

@Composable
fun QuickCustomItemDialog(
  viewModel: PosViewModel,
  currencySymbol: String,
  thousandsSeparator: String = ".",
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Ropa") }
  var size by remember { mutableStateOf("") }
  var color by remember { mutableStateOf("") }
  var priceInput by remember { mutableStateOf("") }
  var costInput by remember { mutableStateOf("") }
  var quantity by remember { mutableIntStateOf(1) }

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
        .fillMaxWidth(0.92f)
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
              imageVector = Icons.Default.FlashOn,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Venta Rápida / Sin Registro",
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Product Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nombre o Descripción del artículo") },
          placeholder = { Text("Ej: Jean negro roto, Camiseta estampada") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("quick_item_name_input")
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

        // Quick Size selector
        Text(
          text = "Talla / Medida:",
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
            label = { Text("Otra Talla") },
            placeholder = { Text("Ej: 42, L") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = color,
            onValueChange = { color = it },
            label = { Text("Color") },
            placeholder = { Text("Ej: Azul") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Price and Cost inputs
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = priceInput,
            onValueChange = { priceInput = it },
            label = { Text("Precio Venta ($currencySymbol)*") },
            placeholder = { Text("25000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1.2f)
              .testTag("quick_item_price_input")
          )

          OutlinedTextField(
            value = costInput,
            onValueChange = { costInput = it },
            label = { Text("Costo compra") },
            placeholder = { Text("Opcional") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(0.8f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quantity Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Cantidad a vender:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
          Row(
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedButton(
              onClick = { if (quantity > 1) quantity-- },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.size(38.dp)
            ) {
              Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text(
              text = "$quantity",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              modifier = Modifier.padding(horizontal = 12.dp)
            )
            OutlinedButton(
              onClick = { quantity++ },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.size(38.dp)
            ) {
              Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Add to Cart Button
        val parsedPrice = priceInput.toDoubleOrNull() ?: 0.0
        val parsedCost = costInput.toDoubleOrNull() ?: (parsedPrice * 0.5) // default 50% cost estimate if blank

        Button(
          onClick = {
            if (parsedPrice > 0) {
              viewModel.addCustomCartItem(
                name = name.ifBlank { "Artículo Venta Rápida" },
                category = category,
                size = size,
                color = color,
                price = parsedPrice,
                cost = parsedCost,
                quantity = quantity
              )
              onDismiss()
            }
          },
          enabled = parsedPrice > 0,
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("add_quick_item_button")
        ) {
          Icon(Icons.Default.AddShoppingCart, contentDescription = null)
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Agregar al Carrito",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
      }
    }
  }
}
