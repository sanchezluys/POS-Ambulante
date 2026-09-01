package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Sale
import com.example.data.model.SaleWithItems
import com.example.ui.components.EmptyStateView
import com.example.ui.components.MetricStatCard
import com.example.ui.theme.PosPrimary
import com.example.ui.theme.PosSecondary
import com.example.ui.theme.PosSuccess
import com.example.ui.theme.PosWhatsApp
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils
import java.util.Calendar

@Composable
fun ReportsScreen(
  viewModel: PosViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val selectedDate by viewModel.selectedReportDate.collectAsState()
  val reportSummary by viewModel.dailyReportSummary.collectAsState()
  val storeSettings by viewModel.storeSettings.collectAsState()
  val symbol = storeSettings.currencySymbol
  val sep = storeSettings.thousandsSeparator

  var saleToDelete by remember { mutableStateOf<Sale?>(null) }
  var saleToViewReceipt by remember { mutableStateOf<SaleWithItems?>(null) }

  // Date Picker dialog trigger
  val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
  val datePickerDialog = remember(context, selectedDate) {
    DatePickerDialog(
      context,
      { _, year, month, dayOfMonth ->
        val newCal = Calendar.getInstance().apply {
          set(Calendar.YEAR, year)
          set(Calendar.MONTH, month)
          set(Calendar.DAY_OF_MONTH, dayOfMonth)
        }
        viewModel.setSelectedReportDate(newCal.timeInMillis)
      },
      calendar.get(Calendar.YEAR),
      calendar.get(Calendar.MONTH),
      calendar.get(Calendar.DAY_OF_MONTH)
    )
  }

  val isToday = remember(selectedDate) {
    val today = Calendar.getInstance()
    val sel = Calendar.getInstance().apply { timeInMillis = selectedDate }
    today.get(Calendar.YEAR) == sel.get(Calendar.YEAR) &&
      today.get(Calendar.DAY_OF_YEAR) == sel.get(Calendar.DAY_OF_YEAR)
  }

  val isYesterday = remember(selectedDate) {
    val yest = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    val sel = Calendar.getInstance().apply { timeInMillis = selectedDate }
    yest.get(Calendar.YEAR) == sel.get(Calendar.YEAR) &&
      yest.get(Calendar.DAY_OF_YEAR) == sel.get(Calendar.DAY_OF_YEAR)
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("reports_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header & Date Controls
    item {
      Column {
        Text(
          text = "Reporte Diario de Ventas",
          fontWeight = FontWeight.Black,
          fontSize = 22.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Control de ingresos, utilidades y balance diario",
          fontSize = 13.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date selection chips
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          FilterChip(
            selected = isToday,
            onClick = { viewModel.setTodayReport() },
            label = { Text("Hoy") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
          )

          FilterChip(
            selected = isYesterday,
            onClick = { viewModel.setYesterdayReport() },
            label = { Text("Ayer") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
          )

          OutlinedButton(
            onClick = { datePickerDialog.show() },
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("choose_date_button")
          ) {
            Icon(
              Icons.Default.CalendarMonth,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (!isToday && !isYesterday) FormatUtils.formatDateShort(selectedDate) else "Calendario",
              fontSize = 12.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Active Date Banner
        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.CalendarMonth,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = FormatUtils.formatDateOnly(selectedDate),
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    // Metric Summary Cards
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricStatCard(
            title = "Total Vendido",
            value = FormatUtils.formatCurrency(reportSummary.totalSales, symbol, sep),
            subtitle = "${reportSummary.salesCount} ventas realizadas",
            icon = Icons.Default.MonetizationOn,
            accentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
          )

          MetricStatCard(
            title = "Ganancia Neta",
            value = FormatUtils.formatCurrency(reportSummary.totalProfit, symbol, sep),
            subtitle = "Margen: ${"%.1f".format(reportSummary.marginPercentage)}%",
            icon = Icons.Default.TrendingUp,
            accentColor = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          MetricStatCard(
            title = "Efectivo",
            value = FormatUtils.formatCurrency(reportSummary.cashTotal, symbol, sep),
            subtitle = "Caja física",
            icon = Icons.Default.LocalAtm,
            modifier = Modifier.weight(1f)
          )

          MetricStatCard(
            title = "Transferencias",
            value = FormatUtils.formatCurrency(reportSummary.transferTotal, symbol, sep),
            subtitle = "Nequi / Daviplata / Banco",
            icon = Icons.Default.CreditCard,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // Share Daily Report Button
    item {
      Button(
        onClick = { viewModel.shareDailyReportWhatsApp(context) },
        colors = ButtonDefaults.buttonColors(
          containerColor = PosWhatsApp,
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("share_daily_report_button")
      ) {
        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Enviar Reporte del Día por WhatsApp",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
    }

    // Top Selling Items of the Day
    if (reportSummary.topItems.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Leaderboard,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Artículos Más Vendidos Hoy",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            reportSummary.topItems.take(5).forEachIndexed { index, (name, qty, total) ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(22.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = "${index + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = name,
                      fontWeight = FontWeight.SemiBold,
                      fontSize = 13.sp
                    )
                    Text(
                      text = "$qty unidades vendidas",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Text(
                  text = FormatUtils.formatCurrency(total, symbol),
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = MaterialTheme.colorScheme.primary
                )
              }
              if (index < reportSummary.topItems.size - 1 && index < 4) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
              }
            }
          }
        }
      }
    }

    // Transactions list header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Detalle de Transacciones",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "${reportSummary.sales.size} ventas",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    if (reportSummary.sales.isEmpty()) {
      item {
        EmptyStateView(
          icon = Icons.Default.Receipt,
          title = "Sin ventas en esta fecha",
          description = "Las ventas realizadas en esta fecha aparecerán aquí con su detalle de productos y recibo digital.",
          modifier = Modifier.fillMaxWidth()
        )
      }
    } else {
      items(reportSummary.sales, key = { it.sale.saleId }) { saleWithItems ->
        TransactionItemCard(
          saleWithItems = saleWithItems,
          currencySymbol = symbol,
          thousandsSeparator = sep,
          onViewReceipt = { saleToViewReceipt = saleWithItems },
          onDelete = { saleToDelete = saleWithItems.sale }
        )
      }
    }
  }

  // View Receipt Dialog
  saleToViewReceipt?.let { saleWithItems ->
    ReceiptDialog(
      saleWithItems = saleWithItems,
      settings = storeSettings,
      onDismiss = { saleToViewReceipt = null }
    )
  }

  // Delete Sale Dialog
  saleToDelete?.let { sale ->
    AlertDialog(
      onDismissRequest = { saleToDelete = null },
      title = { Text("¿Anular / Eliminar Venta?") },
      text = { Text("¿Estás seguro de anular la venta ${sale.receiptNumber} por ${FormatUtils.formatCurrency(sale.totalAmount, symbol, sep)}? Esto eliminará el registro.") },
      confirmButton = {
        TextButton(
          onClick = {
            viewModel.deleteSale(sale)
            saleToDelete = null
          }
        ) {
          Text("Anular", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { saleToDelete = null }) {
          Text("Cancelar")
        }
      }
    )
  }
}

@Composable
fun TransactionItemCard(
  saleWithItems: SaleWithItems,
  currencySymbol: String,
  thousandsSeparator: String = ".",
  onViewReceipt: () -> Unit,
  onDelete: () -> Unit
) {
  val sale = saleWithItems.sale
  val items = saleWithItems.items

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
      .testTag("sale_item_${sale.saleId}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
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
              text = sale.receiptNumber,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = FormatUtils.formatTimeOnly(sale.timestamp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Surface(
          color = if (sale.paymentMethod.contains("EFECTIVO", ignoreCase = true)) Color(0xFFE8F5E9) else Color(0xFFE3F2FD),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = sale.paymentMethod,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (sale.paymentMethod.contains("EFECTIVO", ignoreCase = true)) Color(0xFF2E7D32) else Color(0xFF1565C0),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Items summary list
      items.forEach { item ->
        val variant = listOf(item.size, item.color).filter { it.isNotBlank() }.joinToString(" • ")
        Text(
          text = "• ${item.quantity}x ${item.productName} ${if (variant.isNotBlank()) "($variant)" else ""}",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
      Spacer(modifier = Modifier.height(6.dp))

      // Totals & Profit
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Total: ${FormatUtils.formatCurrency(sale.totalAmount, currencySymbol, thousandsSeparator)}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Ganancia: ${FormatUtils.formatCurrency(sale.netProfit, currencySymbol, thousandsSeparator)}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2E7D32)
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          TextButton(
            onClick = onViewReceipt,
            modifier = Modifier.testTag("view_receipt_btn_${sale.saleId}")
          ) {
            Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Recibo", fontSize = 12.sp)
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              Icons.Default.Delete,
              contentDescription = "Anular",
              tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}
