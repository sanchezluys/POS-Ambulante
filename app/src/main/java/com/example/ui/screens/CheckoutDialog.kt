package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SaleWithItems
import com.example.ui.components.QuickTenderChip
import com.example.ui.theme.PosPrimary
import com.example.ui.theme.PosSecondary
import com.example.ui.theme.PosSuccess
import com.example.ui.theme.PosWhatsApp
import com.example.ui.viewmodel.CartItem
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutBottomSheet(
  viewModel: PosViewModel,
  onDismiss: () -> Unit,
  onSaleCompleted: (SaleWithItems) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val cartItems by viewModel.cartItems.collectAsState()
  val subtotal by viewModel.cartSubtotal.collectAsState()
  val discount by viewModel.discountAmount.collectAsState()
  val total by viewModel.cartTotalAmount.collectAsState()
  val paymentMethod by viewModel.paymentMethod.collectAsState()
  val cashTendered by viewModel.cashTendered.collectAsState()
  val changeGiven by viewModel.changeGiven.collectAsState()
  val customerName by viewModel.customerName.collectAsState()
  val customerPhone by viewModel.customerPhone.collectAsState()
  val storeSettings by viewModel.storeSettings.collectAsState()
  val symbol = storeSettings.currencySymbol
  val sep = storeSettings.thousandsSeparator

  var discountInput by remember { mutableStateOf(if (discount > 0) discount.toInt().toString() else "") }
  var cashInput by remember { mutableStateOf(if (cashTendered > 0) cashTendered.toInt().toString() else "") }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    modifier = Modifier.fillMaxHeight(0.92f)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .verticalScroll(rememberScrollState())
    ) {
      // Sheet Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Carrito & Cobro",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }
        Text(
          text = "${cartItems.sumOf { it.quantity }} prendas/artículos",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Cart Items List
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          if (cartItems.isEmpty()) {
            Text(
              text = "El carrito está vacío",
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          } else {
            cartItems.forEachIndexed { index, item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (item.displayVariant.isNotBlank()) {
                    Text(
                      text = item.displayVariant,
                      fontSize = 12.sp,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                  Text(
                    text = "${FormatUtils.formatCurrency(item.unitPrice, symbol, sep)} c/u",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }

                // Quantity changer
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                  IconButton(
                    onClick = { viewModel.updateCartQuantity(index, item.quantity - 1) },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = if (item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                      contentDescription = "Disminuir",
                      modifier = Modifier.size(16.dp),
                      tint = if (item.quantity == 1) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                  }

                  Text(
                    text = "${item.quantity}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                  )

                  IconButton(
                    onClick = { viewModel.updateCartQuantity(index, item.quantity + 1) },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Add,
                      contentDescription = "Aumentar",
                      modifier = Modifier.size(16.dp),
                      tint = MaterialTheme.colorScheme.primary
                    )
                  }
                }

                Spacer(modifier = Modifier.width(12.dp))
                Text(
                  text = FormatUtils.formatCurrency(item.totalPrice, symbol, sep),
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              if (index < cartItems.size - 1) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Totals & Discount Section
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Subtotal:", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
              text = FormatUtils.formatCurrency(subtotal, symbol, sep),
              fontWeight = FontWeight.SemiBold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Discount input field
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Descuento ($symbol):", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
              value = discountInput,
              onValueChange = {
                discountInput = it
                val parsed = it.toDoubleOrNull() ?: 0.0
                viewModel.setDiscount(parsed)
              },
              placeholder = { Text("0") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier
                .width(120.dp)
                .height(52.dp)
                .testTag("discount_input")
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          HorizontalDivider()
          Spacer(modifier = Modifier.height(8.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TOTAL A COBRAR:",
              fontWeight = FontWeight.Black,
              fontSize = 16.sp
            )
            Text(
              text = FormatUtils.formatCurrency(total, symbol, sep),
              fontWeight = FontWeight.Black,
              fontSize = 22.sp,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Payment Method Selector
      Text(
        text = "MÉTODO DE PAGO",
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val methods = listOf(
          Triple("EFECTIVO", "Efectivo", Icons.Default.LocalAtm),
          Triple("TRANSFERENCIA", "Transfer/Nequi", Icons.Default.QrCode),
          Triple("TARJETA", "Tarjeta", Icons.Default.CreditCard)
        )

        methods.forEach { (key, label, icon) ->
          val isSelected = paymentMethod == key
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(12.dp))
              .clickable {
                viewModel.setPaymentMethod(key)
                if (key != "EFECTIVO") {
                  viewModel.setCashTendered(total)
                  cashInput = total.toInt().toString()
                }
              }
              .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
              ),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
              )
            }
          }
        }
      }

      // Cash calculator & change if EFECTIVO
      if (paymentMethod.contains("EFECTIVO", ignoreCase = true)) {
        Spacer(modifier = Modifier.height(14.dp))
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "💵 Calculadora de Vueltas / Cambio",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Quick cash tender buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              QuickTenderChip(
                label = "Exacto",
                amount = total,
                currencySymbol = symbol,
                onClick = { amt ->
                  cashInput = amt.toInt().toString()
                  viewModel.setCashTendered(amt)
                },
                modifier = Modifier.weight(1f)
              )
              val rounded1 = (Math.ceil(total / 10000.0) * 10000.0).coerceAtLeast(total)
              if (rounded1 > total) {
                QuickTenderChip(
                  label = FormatUtils.formatCurrency(rounded1, symbol, sep),
                  amount = rounded1,
                  currencySymbol = symbol,
                  onClick = { amt ->
                    cashInput = amt.toInt().toString()
                    viewModel.setCashTendered(amt)
                  },
                  modifier = Modifier.weight(1f)
                )
              }
              val rounded2 = rounded1 + 10000.0
              QuickTenderChip(
                label = FormatUtils.formatCurrency(rounded2, symbol, sep),
                amount = rounded2,
                currencySymbol = symbol,
                onClick = { amt ->
                  cashInput = amt.toInt().toString()
                  viewModel.setCashTendered(amt)
                },
                modifier = Modifier.weight(1f)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              OutlinedTextField(
                value = cashInput,
                onValueChange = {
                  cashInput = it
                  val parsed = it.toDoubleOrNull() ?: 0.0
                  viewModel.setCashTendered(parsed)
                },
                label = { Text("Efectivo recibido") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                  .weight(1f)
                  .testTag("cash_tendered_input")
              )
            }

            if (changeGiven > 0) {
              Spacer(modifier = Modifier.height(8.dp))
              Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "🪙 VUELTAS / CAMBIO:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    fontSize = 13.sp
                  )
                  Text(
                    text = FormatUtils.formatCurrency(changeGiven, symbol, sep),
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2E7D32),
                    fontSize = 17.sp
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Customer Info for WhatsApp Receipt
      Text(
        text = "DATOS DEL CLIENTE (PARA RECIBO WHATSAPP)",
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = customerName,
          onValueChange = { viewModel.setCustomerName(it) },
          label = { Text("Nombre (opcional)") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("customer_name_input")
        )

        OutlinedTextField(
          value = customerPhone,
          onValueChange = { viewModel.setCustomerPhone(it) },
          label = { Text("WhatsApp (opcional)") },
          leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PosWhatsApp) },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .weight(1f)
            .testTag("customer_phone_input")
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Finalize Sale Button
      Button(
        onClick = {
          viewModel.processCheckout { saleWithItems ->
            onDismiss()
            onSaleCompleted(saleWithItems)
          }
        },
        enabled = cartItems.isNotEmpty(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("finalize_sale_button")
      ) {
        Icon(Icons.Default.Check, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "COBRAR Y GENERAR RECIBO",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
