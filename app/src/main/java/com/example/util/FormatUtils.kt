package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.model.Sale
import com.example.data.model.SaleItem
import com.example.data.model.StoreSettings
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {
  fun formatCurrency(
    amount: Double,
    symbol: String = "$",
    thousandsSeparator: String = "."
  ): String {
    if (thousandsSeparator == "none" || thousandsSeparator == "NONE" || thousandsSeparator.isBlank()) {
      return "$symbol ${amount.toLong()}"
    }
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
      groupingSeparator = if (thousandsSeparator == ",") ',' else '.'
      decimalSeparator = if (thousandsSeparator == ",") '.' else ','
    }
    val decimalFormat = DecimalFormat("#,##0", symbols)
    decimalFormat.maximumFractionDigits = 0
    decimalFormat.minimumFractionDigits = 0
    return "$symbol ${decimalFormat.format(amount)}"
  }

  fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
  }

  fun formatDateOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "ES"))
    return sdf.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
  }

  fun formatDateShort(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
  }

  fun formatTimeOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
  }

  fun saveLogoToInternalStorage(context: Context, uri: Uri): String? {
    return try {
      val inputStream = context.contentResolver.openInputStream(uri) ?: return null
      val fileName = "store_logo_${System.currentTimeMillis()}.png"
      val file = java.io.File(context.filesDir, fileName)
      file.outputStream().use { outputStream ->
        inputStream.copyTo(outputStream)
      }
      file.absolutePath
    } catch (e: Exception) {
      uri.toString()
    }
  }

  fun getFileProviderUri(context: Context, filePathOrUri: String): Uri? {
    if (filePathOrUri.isBlank()) return null
    return try {
      val file = java.io.File(filePathOrUri)
      if (file.exists()) {
        androidx.core.content.FileProvider.getUriForFile(
          context,
          "${context.packageName}.fileprovider",
          file
        )
      } else {
        Uri.parse(filePathOrUri)
      }
    } catch (e: Exception) {
      null
    }
  }
}

object ReceiptUtils {

  fun generateReceiptText(
    sale: Sale,
    items: List<SaleItem>,
    settings: StoreSettings
  ): String {
    val symbol = settings.currencySymbol
    val sep = settings.thousandsSeparator
    val sb = StringBuilder()

    sb.append("🧾 *RECIBO DE VENTA DIGITAL*\n")
    sb.append("🏬 *${settings.storeName.uppercase()}*\n")
    if (settings.taxId.isNotBlank()) {
      sb.append("🆔 RUT/NIT: ${settings.taxId}\n")
    }
    if (settings.ownerName.isNotBlank()) {
      sb.append("👤 Vendedor: ${settings.ownerName}\n")
    }
    if (settings.ownerPhone.isNotBlank()) {
      sb.append("📞 Tel/WhatsApp: ${settings.ownerPhone}\n")
    }
    if (settings.address.isNotBlank()) {
      sb.append("📍 Dirección: ${settings.address}\n")
    } else {
      sb.append("📍 *Venta Ambulante / Punto Móvil*\n")
    }
    sb.append("📅 Fecha: ${FormatUtils.formatDateTime(sale.timestamp)}\n")
    sb.append("📄 N° Recibo: *${sale.receiptNumber}*\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━━\n")

    if (sale.customerName.isNotBlank() && sale.customerName != "Cliente") {
      sb.append("👤 *Cliente:* ${sale.customerName}\n")
    }
    sb.append("💳 *Método de Pago:* ${sale.paymentMethod}\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("🛍️ *DETALLE DE ARTÍCULOS:*\n")

    for (item in items) {
      val variant = listOf(item.size, item.color).filter { it.isNotBlank() }.joinToString(" • ")
      val variantStr = if (variant.isNotBlank()) " ($variant)" else ""
      sb.append("▪ *${item.quantity}x* ${item.productName}$variantStr\n")
      sb.append("   ${FormatUtils.formatCurrency(item.unitPrice, symbol, sep)} c/u  ➜  *${FormatUtils.formatCurrency(item.totalPrice, symbol, sep)}*\n")
    }

    sb.append("━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("💵 Subtotal: ${FormatUtils.formatCurrency(sale.subtotal, symbol, sep)}\n")
    if (sale.discountAmount > 0) {
      sb.append("🏷️ Descuento: -${FormatUtils.formatCurrency(sale.discountAmount, symbol, sep)}\n")
    }
    sb.append("💰 *TOTAL: ${FormatUtils.formatCurrency(sale.totalAmount, symbol, sep)}*\n")

    if (sale.paymentMethod.contains("EFECTIVO", ignoreCase = true) && sale.cashTendered > 0) {
      sb.append("💵 Efectivo Recibido: ${FormatUtils.formatCurrency(sale.cashTendered, symbol, sep)}\n")
      if (sale.changeGiven > 0) {
        sb.append("🪙 Vueltas / Cambio: ${FormatUtils.formatCurrency(sale.changeGiven, symbol, sep)}\n")
      }
    }

    if (sale.notes.isNotBlank()) {
      sb.append("📝 Nota: ${sale.notes}\n")
    }

    sb.append("━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("${settings.receiptFooter}\n")
    sb.append("✨ _Generado por POS Ambulante_")

    return sb.toString()
  }

  fun generateDailyReportText(
    dateMillis: Long,
    totalSales: Double,
    totalProfit: Double,
    salesCount: Int,
    itemsCount: Int,
    cashTotal: Double,
    transferTotal: Double,
    topItems: List<Triple<String, Int, Double>>,
    settings: StoreSettings
  ): String {
    val symbol = settings.currencySymbol
    val sep = settings.thousandsSeparator
    val sb = StringBuilder()

    sb.append("📊 *REPORTE DIARIO DE VENTAS*\n")
    sb.append("🏬 *${settings.storeName}*\n")
    sb.append("📅 *${FormatUtils.formatDateOnly(dateMillis)}*\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("💰 *TOTAL VENDIDO:* ${FormatUtils.formatCurrency(totalSales, symbol, sep)}\n")
    sb.append("📈 *GANANCIA NETA:* ${FormatUtils.formatCurrency(totalProfit, symbol, sep)}\n")
    val margin = if (totalSales > 0) (totalProfit / totalSales) * 100 else 0.0
    sb.append("📊 Margen de Ganancia: ${"%.1f".format(margin)}%\n")
    sb.append("🧾 Ventas Realizadas: $salesCount\n")
    sb.append("📦 Artículos Vendidos: $itemsCount unidades\n")
    sb.append("━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("💳 *MEDIOS DE PAGO:*\n")
    sb.append("💵 Efectivo: ${FormatUtils.formatCurrency(cashTotal, symbol, sep)}\n")
    sb.append("📱 Transferencias / Digital: ${FormatUtils.formatCurrency(transferTotal, symbol, sep)}\n")
    
    if (topItems.isNotEmpty()) {
      sb.append("━━━━━━━━━━━━━━━━━━━━━\n")
      sb.append("🏆 *TOP ARTÍCULOS MÁS VENDIDOS:*\n")
      topItems.take(5).forEachIndexed { index, (name, qty, total) ->
        sb.append("${index + 1}. *$name*: $qty un. (${FormatUtils.formatCurrency(total, symbol, sep)})\n")
      }
    }

    sb.append("━━━━━━━━━━━━━━━━━━━━━\n")
    sb.append("✅ _Cierre de caja - POS Ambulante_")

    return sb.toString()
  }

  fun shareViaWhatsApp(
    context: Context,
    phoneNumber: String,
    messageText: String,
    logoUri: String = ""
  ) {
    val cleanPhone = phoneNumber.replace(Regex("[^0-9+]"), "").trim()
    val imageProviderUri = if (logoUri.isNotBlank()) FormatUtils.getFileProviderUri(context, logoUri) else null

    try {
      if (cleanPhone.isNotBlank()) {
        // Direct WhatsApp Web / App link with phone number
        val formattedNumber = if (cleanPhone.startsWith("+")) cleanPhone.substring(1) else cleanPhone
        val url = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(messageText)}"
        val intent = Intent(Intent.ACTION_VIEW).apply {
          data = Uri.parse(url)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
      } else {
        // Intent to share via WhatsApp app
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
          if (imageProviderUri != null) {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, imageProviderUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
          } else {
            type = "text/plain"
          }
          setPackage("com.whatsapp")
          putExtra(Intent.EXTRA_TEXT, messageText)
          flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(whatsappIntent)
      }
    } catch (e: Exception) {
      // Fallback to standard Android share chooser
      shareGeneralText(context, "Enviar recibo", messageText, logoUri)
    }
  }

  fun shareGeneralText(
    context: Context,
    title: String,
    text: String,
    logoUri: String = ""
  ) {
    val imageProviderUri = if (logoUri.isNotBlank()) FormatUtils.getFileProviderUri(context, logoUri) else null
    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      if (imageProviderUri != null) {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, imageProviderUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      } else {
        type = "text/plain"
      }
      putExtra(Intent.EXTRA_TEXT, text)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    val shareIntent = Intent.createChooser(sendIntent, title)
    shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    context.startActivity(shareIntent)
  }
}
