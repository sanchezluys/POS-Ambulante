package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.StoreSettings
import com.example.ui.components.CURRENCY_OPTIONS
import com.example.ui.theme.PosPrimary
import com.example.ui.theme.PosWhatsApp
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils

@Composable
fun SettingsScreen(
  viewModel: PosViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val currentSettings by viewModel.storeSettings.collectAsState()

  var storeName by remember(currentSettings) { mutableStateOf(currentSettings.storeName) }
  var ownerName by remember(currentSettings) { mutableStateOf(currentSettings.ownerName) }
  var ownerPhone by remember(currentSettings) { mutableStateOf(currentSettings.ownerPhone) }
  var address by remember(currentSettings) { mutableStateOf(currentSettings.address) }
  var taxId by remember(currentSettings) { mutableStateOf(currentSettings.taxId) }
  var selectedCurrencyCode by remember(currentSettings) { mutableStateOf(currentSettings.currencyCode.ifBlank { "COL" }) }
  var currencySymbol by remember(currentSettings) { mutableStateOf(currentSettings.currencySymbol.ifBlank { "$" }) }
  var thousandsSeparator by remember(currentSettings) { mutableStateOf(currentSettings.thousandsSeparator.ifBlank { "." }) }
  var receiptFooter by remember(currentSettings) { mutableStateOf(currentSettings.receiptFooter) }
  var logoUri by remember(currentSettings) { mutableStateOf(currentSettings.logoUri) }
  var showResetDialog by remember { mutableStateOf(false) }

  val isLocked = currentSettings.isConfigured

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri: Uri? ->
      if (uri != null) {
        val savedPath = FormatUtils.saveLogoToInternalStorage(context, uri)
        if (savedPath != null) {
          logoUri = savedPath
          Toast.makeText(context, "¡Logo actualizado!", Toast.LENGTH_SHORT).show()
        }
      }
    }
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("settings_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Column {
        Text(
          text = "Ajustes del Negocio",
          fontWeight = FontWeight.Black,
          fontSize = 22.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Gestiona el logo, mensaje de recibo y configuración del punto de venta",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Logo & Receipt Customization Card (Always Editable)
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Image,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Logo y Pie de Página del Recibo",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Logo Selector Box
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            ),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Logo del Punto de Venta (Opcional)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
                if (logoUri.isNotBlank()) {
                  IconButton(
                    onClick = { logoUri = "" },
                    modifier = Modifier.size(28.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = "Quitar logo",
                      tint = MaterialTheme.colorScheme.error,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              if (logoUri.isNotBlank()) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(68.dp)
                      .clip(RoundedCornerShape(10.dp))
                      .background(Color.White)
                      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                  ) {
                    AsyncImage(
                      model = logoUri,
                      contentDescription = "Logo del negocio",
                      contentScale = ContentScale.Crop,
                      modifier = Modifier.size(68.dp)
                    )
                  }

                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "Logo activo",
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                      text = "Se incluirá en todos los recibos y documentos compartidos.",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                      onClick = {
                        photoPickerLauncher.launch(
                          PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                      },
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.height(34.dp)
                    ) {
                      Text("Cambiar Logo", fontSize = 11.sp)
                    }
                  }
                }
              } else {
                OutlinedButton(
                  onClick = {
                    photoPickerLauncher.launch(
                      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                  },
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pick_settings_logo_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Seleccionar Logo del Negocio", fontSize = 13.sp)
                }
                Text(
                  text = "Opcional: Aparecerá en la cabecera de tus recibos digitales de WhatsApp",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(top = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Pie de Página del Recibo (Siempre editable)
          OutlinedTextField(
            value = receiptFooter,
            onValueChange = { receiptFooter = it },
            label = { Text("Mensaje al pie del recibo WhatsApp *") },
            placeholder = { Text("Ej: ¡Gracias por su compra! Garantía de cambio 3 días.") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_receipt_footer_input")
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              val updated = currentSettings.copy(
                logoUri = logoUri,
                receiptFooter = receiptFooter.trim().ifBlank { "¡Gracias por su compra! Garantía de cambio 3 días." },
                // If not locked yet, also apply other values
                storeName = if (isLocked) currentSettings.storeName else storeName.ifBlank { "Mi Negocio Ambulante" },
                ownerName = if (isLocked) currentSettings.ownerName else ownerName.trim(),
                ownerPhone = if (isLocked) currentSettings.ownerPhone else ownerPhone.trim(),
                address = if (isLocked) currentSettings.address else address.trim(),
                taxId = if (isLocked) currentSettings.taxId else taxId.trim(),
                currencyCode = if (isLocked) currentSettings.currencyCode else selectedCurrencyCode,
                currencySymbol = if (isLocked) currentSettings.currencySymbol else currencySymbol.ifBlank { "$" },
                thousandsSeparator = if (isLocked) currentSettings.thousandsSeparator else thousandsSeparator.ifBlank { "." },
                isConfigured = true
              )
              viewModel.updateSettings(updated)
              Toast.makeText(context, "¡Ajustes guardados correctamente!", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("save_settings_button")
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar Cambios", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Business Profile Settings Card (Locked for Security once configured)
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Store,
                contentDescription = null,
                tint = if (isLocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Datos del Punto de Venta",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            }

            if (isLocked) {
              Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Fijado",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }
            }
          }

          if (isLocked) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
              ),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Security,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Por seguridad y consistencia fiscal, los datos de identificación, moneda y formato están bloqueados. Solo el logo y el pie de página son editables.",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  lineHeight = 14.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Nombre del Negocio
          OutlinedTextField(
            value = storeName,
            onValueChange = { if (!isLocked) storeName = it },
            enabled = !isLocked,
            label = { Text("Nombre del Negocio / Puesto *") },
            placeholder = { Text("Ej: Moda & Calzado Ambulante") },
            leadingIcon = {
              Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = if (isLocked) {
              { Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray, modifier = Modifier.size(18.dp)) }
            } else null,
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_store_name_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Nombre vendedor (Línea 1)
          OutlinedTextField(
            value = ownerName,
            onValueChange = { if (!isLocked) ownerName = it },
            enabled = !isLocked,
            label = { Text("Nombre vendedor / Dueño") },
            placeholder = { Text("Ej: Carlos") },
            leadingIcon = {
              Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = if (isLocked) {
              { Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray, modifier = Modifier.size(18.dp)) }
            } else null,
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_owner_name_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // WhatsApp / Teléfono (Línea 2)
          OutlinedTextField(
            value = ownerPhone,
            onValueChange = { if (!isLocked) ownerPhone = it },
            enabled = !isLocked,
            label = { Text("WhatsApp / Teléfono") },
            placeholder = { Text("Ej: 3001234567") },
            leadingIcon = {
              Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = if (isLocked) {
              { Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray, modifier = Modifier.size(18.dp)) }
            } else null,
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_owner_phone_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Dirección / Ubicación
          OutlinedTextField(
            value = address,
            onValueChange = { if (!isLocked) address = it },
            enabled = !isLocked,
            label = { Text("Dirección / Ubicación del Puesto") },
            placeholder = { Text("Ej: Esquina Cra 7 con Calle 12, Puesto #4") },
            leadingIcon = {
              Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = if (isLocked) {
              { Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray, modifier = Modifier.size(18.dp)) }
            } else null,
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_address_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          // RUT / NIT (Opcional)
          OutlinedTextField(
            value = taxId,
            onValueChange = { if (!isLocked) taxId = it },
            enabled = !isLocked,
            label = { Text("RUT / NIT (Opcional)") },
            placeholder = { Text("Ej: 1023456789-1") },
            leadingIcon = {
              Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            },
            trailingIcon = if (isLocked) {
              { Icon(Icons.Default.Lock, contentDescription = "Bloqueado", tint = Color.Gray, modifier = Modifier.size(18.dp)) }
            } else null,
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_tax_id_input")
          )

          Spacer(modifier = Modifier.height(12.dp))

          // Currency Selector: USD, COL, SOL
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Moneda del Negocio",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              if (isLocked) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Bloqueado",
                  tint = Color.Gray,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              CURRENCY_OPTIONS.forEach { opt ->
                val isSelected = selectedCurrencyCode.equals(opt.code, ignoreCase = true)
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                      if (isSelected) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.surface
                    )
                    .border(
                      width = if (isSelected) 2.dp else 1.dp,
                      color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                      shape = RoundedCornerShape(10.dp)
                    )
                    .clickable(enabled = !isLocked) {
                      if (!isLocked) {
                        selectedCurrencyCode = opt.code
                        currencySymbol = opt.symbol
                      }
                    }
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = opt.code,
                      fontWeight = FontWeight.Black,
                      fontSize = 13.sp,
                      color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = opt.symbol,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium,
                      color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Separador de Miles (Punto o Coma o Ninguno)
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Separador de Miles (Opcional)",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              if (isLocked) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = "Bloqueado",
                  tint = Color.Gray,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
            Text(
              text = "Formato para separar miles en precios (ej. 10.000 vs 10,000)",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              // Option 1: Punto (.)
              val isDot = thousandsSeparator == "."
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    if (isDot) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                  )
                  .border(
                    width = if (isDot) 2.dp else 1.dp,
                    color = if (isDot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(10.dp)
                  )
                  .clickable(enabled = !isLocked) { if (!isLocked) thousandsSeparator = "." }
                  .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "Punto ( . )",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isDot) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "10.000",
                    fontSize = 10.sp,
                    color = if (isDot) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              // Option 2: Coma (,)
              val isComma = thousandsSeparator == ","
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    if (isComma) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                  )
                  .border(
                    width = if (isComma) 2.dp else 1.dp,
                    color = if (isComma) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(10.dp)
                  )
                  .clickable(enabled = !isLocked) { if (!isLocked) thousandsSeparator = "," }
                  .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "Coma ( , )",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isComma) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "10,000",
                    fontSize = 10.sp,
                    color = if (isComma) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              // Option 3: Sin separador (none)
              val isNone = thousandsSeparator == "none" || thousandsSeparator == "NONE" || thousandsSeparator.isBlank()
              Box(
                modifier = Modifier
                  .weight(1f)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    if (isNone) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surface
                  )
                  .border(
                    width = if (isNone) 2.dp else 1.dp,
                    color = if (isNone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(10.dp)
                  )
                  .clickable(enabled = !isLocked) { if (!isLocked) thousandsSeparator = "none" }
                  .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "Ninguno",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isNone) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "10000",
                    fontSize = 10.sp,
                    color = if (isNone) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }
    }

    // Street Vendor Quick Guide Card
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Guía para Vendedores Ambulantes",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "⚡ 100% Offline: Funciona sin internet ni saldo móvil. Todas tus ventas e inventario quedan guardadas en tu teléfono de forma segura.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "🧾 Recibos Digitales: No gastes en impresoras térmicas portátiles. Envía el recibo por WhatsApp con tu logo personalizado con un solo toque.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "🪙 Vueltas Rápidas: Ingresa el billete con el que te pagan y la app calcula el cambio exacto automáticamente para evitar errores.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Reset Sample Data
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.RestartAlt,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Restablecer Catálogo de Prueba",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.error
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Recarga los productos iniciales de ropa y calzado de ejemplo. Esto no borrará tus ventas registradas.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedButton(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("reset_sample_data_button")
          ) {
            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Recargar Catálogo Inicial", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }
        }
      }
    }

    // App Version & Author Footer
    item {
      val versionName = remember(context) {
        try {
          val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
          pInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
          "1.0.0"
        }
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp, bottom = 24.dp)
          .testTag("settings_footer_info"),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        HorizontalDivider(
          modifier = Modifier.padding(bottom = 16.dp),
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
        Text(
          text = "Versión $versionName",
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "by sanchezluys@gmail.com  2026",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Punto de Venta Offline • Moda, Calzado & Comercio",
          fontSize = 11.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
        )
      }
    }
  }

  // Confirmation dialog for sample data reset
  if (showResetDialog) {
    AlertDialog(
      onDismissRequest = { showResetDialog = false },
      title = {
        Text("¿Restablecer catálogo de ejemplo?", fontWeight = FontWeight.Bold)
      },
      text = {
        Text("Esta acción recargará los 11 productos base de ropa, calzado y accesorios de ejemplo.")
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.resetToSampleData()
            showResetDialog = false
            Toast.makeText(context, "¡Catálogo de ejemplo recargado!", Toast.LENGTH_SHORT).show()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
          Text("Restablecer")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetDialog = false }) {
          Text("Cancelar")
        }
      }
    )
  }
}
