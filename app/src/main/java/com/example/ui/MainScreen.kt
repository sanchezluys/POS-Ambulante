package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InitialSetupDialog
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.PosSaleScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.viewmodel.PosViewModel

enum class PosTab(
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector
) {
  POS("Vender", Icons.Filled.Storefront, Icons.Outlined.Storefront),
  INVENTORY("Inventario", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
  REPORTS("Reportes", Icons.Filled.Assessment, Icons.Outlined.Assessment),
  SETTINGS("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun MainScreen(
  viewModel: PosViewModel
) {
  var selectedTab by rememberSaveable { mutableIntStateOf(0) }
  val cartItemsCount by viewModel.cartTotalItemsCount.collectAsState()
  val isDarkMode by viewModel.isDarkMode.collectAsState()
  val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()
  val storeSettings by viewModel.storeSettings.collectAsState()

  Scaffold(
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "POS Ambulante",
              fontWeight = FontWeight.Black,
              fontSize = 17.sp,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = MaterialTheme.colorScheme.primaryContainer,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "OFFLINE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
              )
            }
          }

          // Dark Mode Switch Button in Top-Right Corner
          IconButton(
            onClick = { viewModel.toggleDarkMode() },
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .testTag("dark_mode_toggle_button")
          ) {
            Icon(
              imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
              contentDescription = if (isDarkMode) "Activar modo claro" else "Activar modo oscuro",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        PosTab.entries.forEachIndexed { index, tab ->
          val isSelected = selectedTab == index
          NavigationBarItem(
            selected = isSelected,
            onClick = { selectedTab = index },
            icon = {
              if (tab == PosTab.POS && cartItemsCount > 0) {
                BadgedBox(
                  badge = {
                    Badge(
                      containerColor = MaterialTheme.colorScheme.secondary,
                      contentColor = MaterialTheme.colorScheme.onSecondary
                    ) {
                      Text("$cartItemsCount", fontWeight = FontWeight.Bold)
                    }
                  }
                ) {
                  Icon(
                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.title
                  )
                }
              } else {
                Icon(
                  imageVector = if (isSelected) tab.selectedIcon else tab.unpersonalTabIcon(tab, isSelected),
                  contentDescription = tab.title
                )
              }
            },
            label = {
              Text(
                text = tab.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.primary,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              indicatorColor = MaterialTheme.colorScheme.primaryContainer,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (PosTab.entries[selectedTab]) {
        PosTab.POS -> PosSaleScreen(viewModel = viewModel)
        PosTab.INVENTORY -> InventoryScreen(viewModel = viewModel)
        PosTab.REPORTS -> ReportsScreen(viewModel = viewModel)
        PosTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
      }
    }
  }

  // First Launch Business Setup Dialog
  if (isFirstLaunch) {
    InitialSetupDialog(
      currentSettings = storeSettings,
      onSave = { updated ->
        viewModel.completeFirstLaunch(updated)
      }
    )
  }
}

private fun PosTab.unpersonalTabIcon(tab: PosTab, isSelected: Boolean): ImageVector {
  return if (isSelected) tab.selectedIcon else tab.unselectedIcon
}

