package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.SaleWithItems
import com.example.data.model.StoreSettings
import com.example.ui.theme.PosWhatsApp
import com.example.ui.theme.PosWhatsAppDark
import com.example.util.FormatUtils
import com.example.util.ReceiptUtils

@Composable
fun ReceiptDialog(
  saleWithItems: SaleWithItems,
  settings: StoreSettings,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val clipboardManager = LocalClipboardManager.current
  val sale = saleWithItems.sale
  val items = saleWithItems.items
  val symbol = settings.currencySymbol
  val sep = settings.thousandsSeparator

  var whatsappPhone by remember {
    mutableStateOf(sale.customerPhone.ifBlank { "" })
  }

  val receiptText = remember(saleWithItems, settings) {
    ReceiptUtils.generateReceiptText(sale, items, settings)
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
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Receipt,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Recibo Digital",
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_receipt_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Cerrar")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Receipt Paper Card Layout
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFAF9F6)
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Store Logo (if configured)
            if (settings.logoUri.isNotBlank()) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.White)
                  .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
              ) {
                AsyncImage(
                  model = settings.logoUri,
                  contentDescription = "Logo del negocio",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.size(56.dp)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
            }

            // Store Title & Meta
            Text(
              text = settings.storeName.uppercase(),
              fontWeight = FontWeight.Black,
              fontSize = 16.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth(),
              color = Color(0xFF212121)
            )
            if (settings.taxId.isNotBlank()) {
              Text(
                text = "RUT/NIT: ${settings.taxId}",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF616161)
              )
            }
            if (settings.ownerName.isNotBlank()) {
              Text(
                text = "Vendedor: ${settings.ownerName}",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF616161)
              )
            }
            if (settings.ownerPhone.isNotBlank()) {
              Text(
                text = "Tel: ${settings.ownerPhone}",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF616161)
              )
            }
            if (settings.address.isNotBlank()) {
              Text(
                text = "Dir: ${settings.address}",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF616161)
              )
            } else {
              Text(
                text = "Punto de Venta Móvil / Ambulante",
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF757575)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Date & Receipt number
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "N°: ${sale.receiptNumber}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
              )
              Text(
                text = FormatUtils.formatDateTime(sale.timestamp),
                fontSize = 11.sp,
                color = Color(0xFF616161)
              )
            }

            if (sale.customerName.isNotBlank() && sale.customerName != "Cliente") {
              Text(
                text = "Cliente: ${sale.customerName}",
                fontSize = 12.sp,
                color = Color(0xFF424242)
              )
            }

            Text(
              text = "Pago: ${sale.paymentMethod}",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Items List
            Text(
              text = "ARTÍCULOS",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF757575)
            )
            Spacer(modifier = Modifier.height(4.dp))

            items.forEach { item ->
              val variant = listOf(item.size, item.color).filter { it.isNotBlank() }.joinToString(" • ")
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "${item.quantity}x ${item.productName}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF212121)
                  )
                  if (variant.isNotBlank()) {
                    Text(
                      text = variant,
                      fontSize = 11.sp,
                      color = Color(0xFF757575)
                    )
                  }
                }
                Text(
                  text = FormatUtils.formatCurrency(item.totalPrice, symbol, sep),
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color(0xFF212121)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            // Totals
            if (sale.discountAmount > 0) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Subtotal:", fontSize = 12.sp, color = Color(0xFF616161))
                Text(
                  text = FormatUtils.formatCurrency(sale.subtotal, symbol, sep),
                  fontSize = 12.sp,
                  color = Color(0xFF616161)
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Descuento:", fontSize = 12.sp, color = Color(0xFFD32F2F))
                Text(
                  text = "-${FormatUtils.formatCurrency(sale.discountAmount, symbol, sep)}",
                  fontSize = 12.sp,
                  color = Color(0xFFD32F2F)
                )
              }
            }

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "TOTAL:",
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = Color(0xFF212121)
              )
              Text(
                text = FormatUtils.formatCurrency(sale.totalAmount, symbol, sep),
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
              )
            }

            if (sale.paymentMethod.contains("EFECTIVO", ignoreCase = true) && sale.cashTendered > 0) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Recibido:", fontSize = 12.sp, color = Color(0xFF616161))
                Text(
                  text = FormatUtils.formatCurrency(sale.cashTendered, symbol, sep),
                  fontSize = 12.sp,
                  color = Color(0xFF616161)
                )
              }
              if (sale.changeGiven > 0) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(text = "Vueltas / Cambio:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                  Text(
                    text = FormatUtils.formatCurrency(sale.changeGiven, symbol, sep),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = settings.receiptFooter,
              fontSize = 10.sp,
              textAlign = TextAlign.Center,
              modifier = Modifier.fillMaxWidth(),
              color = Color(0xFF757575)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // WhatsApp Phone Input
        OutlinedTextField(
          value = whatsappPhone,
          onValueChange = { whatsappPhone = it },
          label = { Text("Número de WhatsApp del cliente") },
          placeholder = { Text("Ej: 3001234567 o +57300...") },
          leadingIcon = {
            Icon(Icons.Default.Phone, contentDescription = null, tint = PosWhatsApp)
          },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("whatsapp_phone_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Big Primary Button: Send via WhatsApp
        Button(
          onClick = {
            ReceiptUtils.shareViaWhatsApp(
              context = context,
              phoneNumber = whatsappPhone,
              messageText = receiptText,
              logoUri = settings.logoUri
            )
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = PosWhatsApp,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("send_whatsapp_button")
        ) {
          Icon(
            imageVector = Icons.Default.Send,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Enviar por WhatsApp",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Secondary Actions: Share & Copy
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = {
              ReceiptUtils.shareGeneralText(context, "Recibo de Venta", receiptText, settings.logoUri)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("share_receipt_button")
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Compartir", fontSize = 13.sp)
          }

          OutlinedButton(
            onClick = {
              clipboardManager.setText(AnnotatedString(receiptText))
              Toast.makeText(context, "¡Recibo copiado al portapapeles!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("copy_receipt_button")
          ) {
            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Copiar", fontSize = 13.sp)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("done_receipt_button")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Listo / Nueva Venta", fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}
