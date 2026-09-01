package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.PosError
import com.example.ui.theme.PosErrorContainer
import com.example.ui.theme.PosPrimary
import com.example.ui.theme.PosPrimaryContainer
import com.example.ui.theme.PosSecondary
import com.example.ui.theme.PosSuccess
import com.example.ui.theme.PosSuccessContainer
import com.example.util.FormatUtils

@Composable
fun StockBadge(
  stock: Int,
  minAlert: Int,
  modifier: Modifier = Modifier
) {
  val (bgColor, textColor, text, icon) = when {
    stock <= 0 -> Quadruple(
      PosErrorContainer,
      PosError,
      "Agotado",
      Icons.Default.Error
    )
    stock <= minAlert -> Quadruple(
      Color(0xFFFFF3E0),
      Color(0xFFE65100),
      "$stock disp. (Bajo)",
      Icons.Default.Warning
    )
    else -> Quadruple(
      PosSuccessContainer,
      PosSuccess,
      "$stock disponibles",
      Icons.Default.CheckCircle
    )
  }

  Surface(
    color = bgColor,
    shape = RoundedCornerShape(8.dp),
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(12.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = text,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun CategoryFilterRow(
  categories: List<String>,
  selectedCategory: String,
  onCategorySelected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = modifier
  ) {
    categories.forEach { category ->
      val isSelected = category == selectedCategory
      FilterChip(
        selected = isSelected,
        onClick = { onCategorySelected(category) },
        label = {
          Text(
            text = category,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp
          )
        },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primary,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = null,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.testTag("category_chip_$category")
      )
    }
  }
}

@Composable
fun MetricStatCard(
  title: String,
  value: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
  subtitle: String? = null,
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
  accentColor: Color = MaterialTheme.colorScheme.primary
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor)
  ) {
    Column(
      modifier = Modifier.padding(16.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = title,
          fontSize = 13.sp,
          fontWeight = FontWeight.Medium,
          color = contentColor.copy(alpha = 0.8f)
        )
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
          )
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = value,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = contentColor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
      if (subtitle != null) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = contentColor.copy(alpha = 0.7f)
        )
      }
    }
  }
}

@Composable
fun QuickTenderChip(
  label: String,
  amount: Double,
  onClick: (Double) -> Unit,
  currencySymbol: String = "$",
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick(amount) }
      .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
    color = MaterialTheme.colorScheme.surface
  ) {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
      Text(
        text = label,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.primary
      )
    }
  }
}

@Composable
fun EmptyStateView(
  icon: ImageVector,
  title: String,
  description: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.primaryContainer),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(32.dp)
      )
    }
    Spacer(modifier = Modifier.height(16.dp))
    Text(
      text = title,
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = description,
      fontSize = 13.sp,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}
