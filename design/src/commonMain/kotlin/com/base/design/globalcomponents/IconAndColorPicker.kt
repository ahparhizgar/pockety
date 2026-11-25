package com.base.design.globalcomponents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews

/** Color option model for categories, pockets, and icon customization. */
data class ItemColorOption(
    val name: String,
    val color: Color,
    val onColor: Color = Color.White,
)

/** Icon option model with category filter grouping. */
data class ItemIconOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val categoryFilter: String = "All",
)

val defaultItemColors =
    listOf(
        ItemColorOption(
            name = "Coral Red",
            color = Color(0xFFE57373),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Red",
            color = Color(0xFFE53935),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Orange",
            color = Color(0xFFEF6C00),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Warm Amber",
            color = Color(0xFFB87D4B),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Gold",
            color = Color(0xFFF9A825),
            onColor = Color(0xFF3E2723),
        ),
        ItemColorOption(
            name = "Brown",
            color = Color(0xFF6D4C41),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Forest Green",
            color = Color(0xFF316342),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Emerald",
            color = Color(0xFF4A7C59),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Sage",
            color = Color(0xFF9DD3AA),
            onColor = Color(0xFF00210E),
        ),
        ItemColorOption(
            name = "Deep Teal",
            color = Color(0xFF324C41),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Slate Muted",
            color = Color(0xFF4A6458),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Cyan",
            color = Color(0xFF00ACC1),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Blue",
            color = Color(0xFF1E88E5),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Indigo",
            color = Color(0xFF3949AB),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Violet",
            color = Color(0xFF8E24AA),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Pink",
            color = Color(0xFFEC407A),
            onColor = Color.White,
        ),
        ItemColorOption(
            name = "Graphite",
            color = Color(0xFF757575),
            onColor = Color.White,
        ),
    )

val defaultIconCategoryFilters =
    listOf("All", "Food", "Shopping", "Transport", "Living", "Wellness", "Finance")

val defaultItemIcons =
    listOf(
        // Food
        ItemIconOption("restaurant", "Dining", Icons.Default.Restaurant, "Food"),
        ItemIconOption("coffee", "Coffee", Icons.Default.Coffee, "Food"),
        ItemIconOption("fastfood", "Burger", Icons.Default.Fastfood, "Food"),
        ItemIconOption("local_pizza", "Pizza", Icons.Default.LocalPizza, "Food"),
        ItemIconOption("icecream", "Treats", Icons.Default.Icecream, "Food"),
        ItemIconOption("bakery_dining", "Bakery", Icons.Default.BakeryDining, "Food"),

        // Shopping
        ItemIconOption("shopping_cart", "Cart", Icons.Default.ShoppingCart, "Shopping"),
        ItemIconOption("shopping_bag", "Retail", Icons.Default.ShoppingBag, "Shopping"),
        ItemIconOption("store", "Market", Icons.Default.Store, "Shopping"),
        ItemIconOption("checkroom", "Apparel", Icons.Default.Checkroom, "Shopping"),
        ItemIconOption("loyalty", "Perks", Icons.Default.Loyalty, "Shopping"),

        // Transport
        ItemIconOption("directions_car", "Auto", Icons.Default.DirectionsCar, "Transport"),
        ItemIconOption("local_gas_station", "Fuel", Icons.Default.LocalGasStation, "Transport"),
        ItemIconOption("directions_bus", "Transit", Icons.Default.DirectionsBus, "Transport"),
        ItemIconOption("flight", "Travel", Icons.Default.Flight, "Transport"),
        ItemIconOption("two_wheeler", "Moto", Icons.Default.TwoWheeler, "Transport"),

        // Living / Utilities
        ItemIconOption("home", "Housing", Icons.Default.Home, "Living"),
        ItemIconOption("bolt", "Power", Icons.Default.Bolt, "Living"),
        ItemIconOption("water_drop", "Water", Icons.Default.WaterDrop, "Living"),
        ItemIconOption("wifi", "Internet", Icons.Default.Wifi, "Living"),
        ItemIconOption("pets", "Pets", Icons.Default.Pets, "Living"),

        // Wellness
        ItemIconOption("favorite", "Care", Icons.Default.Favorite, "Wellness"),
        ItemIconOption("medication", "Rx", Icons.Default.Medication, "Wellness"),
        ItemIconOption("medical_services", "Health", Icons.Default.MedicalServices, "Wellness"),
        ItemIconOption("fitness_center", "Gym", Icons.Default.FitnessCenter, "Wellness"),
        ItemIconOption("spa", "Spa", Icons.Default.Spa, "Wellness"),
        ItemIconOption("school", "Study", Icons.Default.School, "Wellness"),
        ItemIconOption("palette", "Art", Icons.Default.Palette, "Wellness"),

        // Finance
        ItemIconOption("payments", "Income", Icons.Default.Payments, "Finance"),
        ItemIconOption("savings", "Savings", Icons.Default.Savings, "Finance"),
        ItemIconOption("credit_card", "Card", Icons.Default.CreditCard, "Finance"),
        ItemIconOption("receipt_long", "Bills", Icons.AutoMirrored.Default.ReceiptLong, "Finance"),
        ItemIconOption("account_balance", "Bank", Icons.Default.AccountBalance, "Finance"),
    )

/** Reusable Accent Color Picker with row of circular swatches and selected check indicator. */
@Composable
fun AccentColorPicker(
    colors: List<ItemColorOption>,
    selectedColor: ItemColorOption,
    onColorSelected: (ItemColorOption) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Accent Color",
) {
  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = title,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = selectedColor.name,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
      )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp).horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
      ) {
        colors.forEach { option ->
          val isSelected = option.name == selectedColor.name
          Box(
              modifier = Modifier.size(44.dp),
              contentAlignment = Alignment.Center,
          ) {
            if (isSelected) {
              Surface(
                  modifier = Modifier.size(44.dp),
                  shape = CircleShape,
                  color = Color.Transparent,
                  border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
              ) {}
              Surface(
                  modifier = Modifier.size(36.dp),
                  shape = CircleShape,
                  color = option.color,
                  shadowElevation = 2.dp,
                  onClick = { onColorSelected(option) },
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Selected ${option.name}",
                      tint = option.onColor,
                      modifier = Modifier.size(18.dp),
                  )
                }
              }
            } else {
              Surface(
                  modifier = Modifier.size(38.dp),
                  shape = CircleShape,
                  color = option.color,
                  shadowElevation = 1.dp,
                  onClick = { onColorSelected(option) },
              ) {}
            }
          }
        }
      }
    }
  }
}

/** Filter chips for icon categories. */
@Composable
fun IconCategoryFilterChips(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    filters.forEach { filter ->
      val isSelected = filter == selectedFilter
      Surface(
          shape = CircleShape,
          color =
              if (isSelected) MaterialTheme.colorScheme.primary
              else MaterialTheme.colorScheme.surfaceContainerLowest,
          contentColor =
              if (isSelected) MaterialTheme.colorScheme.onPrimary
              else MaterialTheme.colorScheme.onSurfaceVariant,
          shadowElevation = if (isSelected) 2.dp else 1.dp,
          onClick = { onFilterSelected(filter) },
      ) {
        Text(
            text = filter,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
      }
    }
  }
}

/** 5-column grid of icon options with adaptive borders and selection styling. */
@Composable
fun ItemIconGrid(
    icons: List<ItemIconOption>,
    selectedIcon: ItemIconOption,
    onIconSelected: (ItemIconOption) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
) {
  Surface(
      modifier = modifier.fillMaxWidth(),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      val rows = icons.chunked(5)
      rows.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          row.forEach { iconOption ->
            val isSelected = iconOption.id == selectedIcon.id
            Surface(
                modifier = Modifier.weight(1f).aspectRatio(1f),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                border =
                    if (isSelected) {
                      BorderStroke(2.dp, accentColor)
                    } else {
                      null
                    },
                shadowElevation = 1.dp,
                onClick = { onIconSelected(iconOption) },
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = iconOption.icon,
                    contentDescription = iconOption.label,
                    tint =
                        if (isSelected) {
                          accentColor
                        } else {
                          MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    modifier = Modifier.size(24.dp),
                )
              }
            }
          }
          if (row.size < 5) {
            repeat(5 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
          }
        }
      }
    }
  }
}

/** Combined section containing filter chips and icon selection grid. */
@Composable
fun IconPickerSection(
    icons: List<ItemIconOption>,
    selectedIcon: ItemIconOption,
    onIconSelected: (ItemIconOption) -> Unit,
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Select Icon",
    accentColor: Color = MaterialTheme.colorScheme.primary,
) {
  val displayedIcons =
      if (selectedFilter == "All") {
        icons
      } else {
        val filtered = icons.filter { it.categoryFilter == selectedFilter }
        if (filtered.isEmpty()) icons else filtered
      }

  Column(
      modifier = modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
          text = title,
          style = MaterialTheme.typography.labelLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
          text = selectedIcon.label,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.secondary,
      )
    }

    IconCategoryFilterChips(
        filters = filters,
        selectedFilter = selectedFilter,
        onFilterSelected = onFilterSelected,
    )

    ItemIconGrid(
        icons = displayedIcons,
        selectedIcon = selectedIcon,
        onIconSelected = onIconSelected,
        accentColor = accentColor,
    )
  }
}

@ComponentPreviews
@Composable
private fun AccentColorPickerPreview() {
  AccentColorPicker(
      colors = defaultItemColors,
      selectedColor = defaultItemColors.first(),
      onColorSelected = {},
  )
}

@ComponentPreviews
@Composable
private fun IconCategoryFilterChipsPreview() {
  IconCategoryFilterChips(
      filters = defaultIconCategoryFilters,
      selectedFilter = "All",
      onFilterSelected = {},
  )
}

@ComponentPreviews
@Composable
private fun ItemIconGridPreview() {
  ItemIconGrid(
      icons = defaultItemIcons.take(10),
      selectedIcon = defaultItemIcons.first(),
      onIconSelected = {},
  )
}

@ComponentPreviews
@Composable
private fun IconPickerSectionPreview() {
  IconPickerSection(
      icons = defaultItemIcons,
      selectedIcon = defaultItemIcons.first(),
      onIconSelected = {},
      filters = defaultIconCategoryFilters,
      selectedFilter = "All",
      onFilterSelected = {},
  )
}
