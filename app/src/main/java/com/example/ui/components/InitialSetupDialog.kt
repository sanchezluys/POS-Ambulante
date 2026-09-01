package com.example.ui.components

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.StoreSettings
import com.example.util.FormatUtils

data class CurrencyOption(
  val code: String,
  val label: String,
  val symbol: String,
  val example: String
)

val CURRENCY_OPTIONS = listOf(
  CurrencyOption(code = "COL", label = "COL (Peso)", symbol = "$", example = "$ 25.000"),
  CurrencyOption(code = "USD", label = "USD (Dólar)", symbol = "$", example = "$ 15.00"),
  CurrencyOption(code = "SOL", label = "SOL (Perú)", symbol = "S/.", example = "S/. 35.00")
)

@Composable
fun InitialSetupDialog(
  currentSettings: StoreSettings,
  onSave: (StoreSettings) -> Unit
) {
  val context = LocalContext.current
  var storeName by remember { mutableStateOf(currentSettings.storeName) }
  var ownerName by remember { mutableStateOf(currentSettings.ownerName) }
  var ownerPhone by remember { mutableStateOf(currentSettings.ownerPhone) }
  var address by remember { mutableStateOf(currentSettings.address) }
  var taxId by remember { mutableStateOf(currentSettings.taxId) }
  var selectedCurrencyCode by remember { mutableStateOf(currentSettings.currencyCode.ifBlank { "COL" }) }
  var currencySymbol by remember { mutableStateOf(currentSettings.currencySymbol.ifBlank { "$" }) }
  var thousandsSeparator by remember { mutableStateOf(currentSettings.thousandsSeparator.ifBlank { "." }) }
  var receiptFooter by remember { mutableStateOf(currentSettings.receiptFooter) }
  var logoUri by remember { mutableStateOf(currentSettings.logoUri) }
  var hasAttemptedSave by remember { mutableStateOf(false) }

  val isStoreNameError = hasAttemptedSave && storeName.trim().isBlank()
  val isPhoneError = hasAttemptedSave && ownerPhone.trim().isBlank()
  val isAddressError = hasAttemptedSave && address.trim().isBlank()

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia(),
    onResult = { uri: Uri? ->
      if (uri != null) {
        val savedPath = FormatUtils.saveLogoToInternalStorage(context, uri)
        if (savedPath != null) {
          logoUri = savedPath
          Toast.makeText(context, "¡Logo cargado exitosamente!", Toast.LENGTH_SHORT).show()
        }
      }
    }
  )

  Dialog(
    onDismissRequest = { /* Modal obligatorio en primer inicio */ },
    properties = DialogProperties(
      dismissOnBackPress = false,
      dismissOnClickOutside = false,
      usePlatformDefaultWidth = false
    )
  ) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .padding(vertical = 24.dp)
        .testTag("initial_setup_dialog")
    ) {
      Column(
        modifier = Modifier
          .padding(22.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Icon Header
        Box(
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Storefront,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "¡Configura tu Punto de Venta!",
          fontWeight = FontWeight.Black,
          fontSize = 19.sp,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "Ingresa los datos de tu puesto o negocio para personalizar tus ventas, moneda y recibos.",
          fontSize = 12.sp,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sección de Logo del Negocio (Opcional)
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
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
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Image,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Logo del Negocio / Punto (Opcional)",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              if (logoUri.isNotBlank()) {
                IconButton(
                  onClick = { logoUri = "" },
                  modifier = Modifier.size(28.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar logo",
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
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp)),
                  contentAlignment = Alignment.Center
                ) {
                  AsyncImage(
                    model = logoUri,
                    contentDescription = "Logo del negocio",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(64.dp)
                  )
                }

                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = "Logo cargado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                  )
                  Text(
                    text = "Se mostrará en la cabecera de tus recibos digitales y documentos.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  OutlinedButton(
                    onClick = {
                      photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                      )
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                  ) {
                    Text("Cambiar", fontSize = 11.sp)
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
                  .testTag("pick_initial_logo_button")
              ) {
                Icon(
                  imageVector = Icons.Default.AddPhotoAlternate,
                  contentDescription = null,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Seleccionar Imagen de Logo", fontSize = 12.sp)
              }
              Text(
                text = "Se usará en la cabecera de tus recibos de venta",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nombre del Negocio / Puesto (Requerido)
        OutlinedTextField(
          value = storeName,
          onValueChange = { storeName = it },
          label = { Text("Nombre del Negocio / Puesto * (Obligatorio)") },
          placeholder = { Text("Ej: Moda & Calzado Ambulante") },
          leadingIcon = {
            Icon(Icons.Default.Storefront, contentDescription = null, tint = if (isStoreNameError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
          },
          isError = isStoreNameError,
          supportingText = if (isStoreNameError) {
            { Text("El nombre del negocio es obligatorio", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
          } else null,
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("initial_store_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Vendedor / Dueño (Línea 1 - Opcional)
        OutlinedTextField(
          value = ownerName,
          onValueChange = { ownerName = it },
          label = { Text("Vendedor / Dueño (Opcional)") },
          placeholder = { Text("Ej: Carlos") },
          leadingIcon = {
            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("initial_owner_name_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // WhatsApp / Teléfono (Línea 2 - Requerido)
        OutlinedTextField(
          value = ownerPhone,
          onValueChange = { ownerPhone = it },
          label = { Text("WhatsApp / Teléfono * (Obligatorio)") },
          placeholder = { Text("Ej: 3001234567") },
          leadingIcon = {
            Icon(Icons.Default.Phone, contentDescription = null, tint = if (isPhoneError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
          },
          isError = isPhoneError,
          supportingText = if (isPhoneError) {
            { Text("El número de WhatsApp/Teléfono es obligatorio", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
          } else null,
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("initial_phone_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Dirección / Ubicación (Requerido)
        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Dirección / Ubicación del Puesto * (Obligatorio)") },
          placeholder = { Text("Ej: Esquina Cra 7 con Calle 12, Puesto #4") },
          leadingIcon = {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (isAddressError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
          },
          isError = isAddressError,
          supportingText = if (isAddressError) {
            { Text("La dirección o ubicación es obligatoria", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
          } else null,
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("initial_address_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // RUT / NIT (Opcional)
        OutlinedTextField(
          value = taxId,
          onValueChange = { taxId = it },
          label = { Text("RUT / NIT (Opcional)") },
          placeholder = { Text("Ej: 1023456789-1") },
          leadingIcon = {
            Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("initial_tax_id_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Seleccionar Moneda: USD, COL, SOL
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
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
              text = "Seleccionar Moneda *",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
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
                  .clickable {
                    selectedCurrencyCode = opt.code
                    currencySymbol = opt.symbol
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

        // Separador de Miles (Punto '.' o Coma ',')
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
        ) {
          Text(
            text = "Separador de Miles (Opcional)",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Selecciona el formato de números para tus precios",
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
                .clickable { thousandsSeparator = "." }
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
                .clickable { thousandsSeparator = "," }
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
                .clickable { thousandsSeparator = "none" }
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

        Spacer(modifier = Modifier.height(10.dp))

        // Pie de página de recibo
        OutlinedTextField(
          value = receiptFooter,
          onValueChange = { receiptFooter = it },
          label = { Text("Pie de página para Recibos WhatsApp") },
          placeholder = { Text("Ej: ¡Gracias por su compra! Garantía 3 días.") },
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("initial_footer_input")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Info Banner with Security Note
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Por seguridad, los datos fiscales y de identificación se bloquearán tras guardar. El logo y pie de recibo permanecerán editables.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 14.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save & Start Button
        Button(
          onClick = {
            hasAttemptedSave = true
            if (storeName.trim().isBlank() || ownerPhone.trim().isBlank() || address.trim().isBlank()) {
              Toast.makeText(
                context,
                "Por favor completa los campos obligatorios (*)",
                Toast.LENGTH_LONG
              ).show()
              return@Button
            }

            val updated = currentSettings.copy(
              storeName = storeName.trim(),
              ownerName = ownerName.trim().ifBlank { "Vendedor" },
              ownerPhone = ownerPhone.trim(),
              address = address.trim(),
              taxId = taxId.trim(),
              currencyCode = selectedCurrencyCode,
              currencySymbol = currencySymbol.ifBlank { "$" },
              thousandsSeparator = thousandsSeparator.ifBlank { "." },
              receiptFooter = receiptFooter.trim().ifBlank { "¡Gracias por su compra! Garantía de cambio 3 días." },
              logoUri = logoUri,
              isConfigured = true
            )
            onSave(updated)
            Toast.makeText(context, "¡Punto de venta configurado con éxito!", Toast.LENGTH_SHORT).show()
          },
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("save_initial_setup_button")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Comenzar a Vender",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
