@file:Suppress(
    "LongMethod",
    "LongParameterList",
    "TooManyFunctions",
    "MagicNumber",
    "MatchingDeclarationName",
    "SpreadOperator",
    "MaxLineLength",
    "UnusedPrivateFunction",
    "ComposableParamOrder",
    "PreviewNaming",
    "PreviewPublic",
    "ParameterNaming",
    "UnstableCollections",
)

package com.base.design.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apps
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
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ReceiptLong
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.base.design.ui.ComponentPreviews
import com.base.design.ui.theme.onPrimaryFixed
import com.base.design.ui.theme.primaryFixed

data class AccentColorOption(
    val name: String,
    val color: Color,
    val onColor: Color,
)

enum class IconCategory(
    val label: String,
    val icon: ImageVector,
) {
  All("All", Icons.Default.Apps),
  Food("Food", Icons.Default.Restaurant),
  Shopping("Shopping", Icons.Default.ShoppingBag),
  Transport("Transport", Icons.Default.DirectionsCar),
  Living("Living", Icons.Default.Home),
  Wellness("Wellness", Icons.Default.Favorite),
  Finance("Finance", Icons.Default.Payments),
}

data class IconItem(
    val id: String,
    val name: String,
    val label: String,
    val icon: ImageVector,
    val category: IconCategory,
)

@Composable
fun defaultAccentColors(): List<AccentColorOption> =
    listOf(
        AccentColorOption(
            name = "Forest Sage",
            color = MaterialTheme.colorScheme.primary,
            onColor = MaterialTheme.colorScheme.onPrimary,
        ),
        AccentColorOption(
            name = "Emerald Mint",
            color = MaterialTheme.colorScheme.primaryContainer,
            onColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        AccentColorOption(
            name = "Earthy Sage",
            color = MaterialTheme.colorScheme.secondary,
            onColor = MaterialTheme.colorScheme.onSecondary,
        ),
        AccentColorOption(
            name = "Olive Meadow",
            color = MaterialTheme.colorScheme.tertiary,
            onColor = MaterialTheme.colorScheme.onTertiary,
        ),
        AccentColorOption(
            name = "Crisp Mint",
            color = primaryFixed,
            onColor = onPrimaryFixed,
        ),
        AccentColorOption(
            name = "Soft Lichen",
            color = MaterialTheme.colorScheme.secondaryContainer,
            onColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        AccentColorOption(
            name = "Dark Moss",
            color = MaterialTheme.colorScheme.inverseSurface,
            onColor = MaterialTheme.colorScheme.inverseOnSurface,
        ),
        AccentColorOption(
            name = "Crimson Berry",
            color = MaterialTheme.colorScheme.error,
            onColor = MaterialTheme.colorScheme.onError,
        ),
    )

val defaultIcons: List<IconItem> =
    listOf(
        // Food
        IconItem("restaurant", "Restaurant", "Dine", Icons.Default.Restaurant, IconCategory.Food),
        IconItem("coffee", "Coffee", "Coffee", Icons.Default.Coffee, IconCategory.Food),
        IconItem("fastfood", "Burger", "Burger", Icons.Default.Fastfood, IconCategory.Food),
        IconItem("local_pizza", "Pizza", "Pizza", Icons.Default.LocalPizza, IconCategory.Food),
        IconItem("icecream", "Ice Cream", "Treats", Icons.Default.Icecream, IconCategory.Food),
        IconItem(
            "bakery_dining",
            "Bakery",
            "Bakery",
            Icons.Default.BakeryDining,
            IconCategory.Food,
        ),

        // Shopping
        IconItem(
            "shopping_cart",
            "Cart",
            "Cart",
            Icons.Default.ShoppingCart,
            IconCategory.Shopping,
        ),
        IconItem(
            "shopping_bag",
            "Retail",
            "Retail",
            Icons.Default.ShoppingBag,
            IconCategory.Shopping,
        ),
        IconItem("store", "Market", "Market", Icons.Default.Store, IconCategory.Shopping),
        IconItem("checkroom", "Apparel", "Apparel", Icons.Default.Checkroom, IconCategory.Shopping),
        IconItem("loyalty", "Perks", "Perks", Icons.Default.Loyalty, IconCategory.Shopping),

        // Transport
        IconItem(
            "directions_car",
            "Auto",
            "Auto",
            Icons.Default.DirectionsCar,
            IconCategory.Transport,
        ),
        IconItem(
            "local_gas_station",
            "Fuel",
            "Fuel",
            Icons.Default.LocalGasStation,
            IconCategory.Transport,
        ),
        IconItem(
            "directions_bus",
            "Transit",
            "Transit",
            Icons.Default.DirectionsBus,
            IconCategory.Transport,
        ),
        IconItem("flight", "Travel", "Travel", Icons.Default.Flight, IconCategory.Transport),
        IconItem("two_wheeler", "Moto", "Moto", Icons.Default.TwoWheeler, IconCategory.Transport),

        // Living
        IconItem("home", "Rent", "Rent", Icons.Default.Home, IconCategory.Living),
        IconItem("bolt", "Power", "Power", Icons.Default.Bolt, IconCategory.Living),
        IconItem("water_drop", "Water", "Water", Icons.Default.WaterDrop, IconCategory.Living),
        IconItem("wifi", "Internet", "Internet", Icons.Default.Wifi, IconCategory.Living),
        IconItem("pets", "Pets", "Pets", Icons.Default.Pets, IconCategory.Living),

        // Wellness
        IconItem("favorite", "Care", "Care", Icons.Default.Favorite, IconCategory.Wellness),
        IconItem("medication", "Rx", "Rx", Icons.Default.Medication, IconCategory.Wellness),
        IconItem(
            "fitness_center",
            "Gym",
            "Gym",
            Icons.Default.FitnessCenter,
            IconCategory.Wellness,
        ),
        IconItem("spa", "Spa", "Spa", Icons.Default.Spa, IconCategory.Wellness),
        IconItem("school", "Study", "Study", Icons.Default.School, IconCategory.Wellness),
        IconItem("palette", "Art", "Art", Icons.Default.Palette, IconCategory.Wellness),

        // Finance
        IconItem("payments", "Cash", "Cash", Icons.Default.Payments, IconCategory.Finance),
        IconItem("savings", "Savings", "Savings", Icons.Default.Savings, IconCategory.Finance),
        IconItem("credit_card", "Card", "Card", Icons.Default.CreditCard, IconCategory.Finance),
        IconItem("receipt_long", "Bills", "Bills", Icons.Default.ReceiptLong, IconCategory.Finance),
        IconItem(
            "account_balance",
            "Bank",
            "Bank",
            Icons.Default.AccountBalance,
            IconCategory.Finance,
        ),
    )

@Composable
fun IconPreviewBadge(
    icon: IconItem,
    accentColor: AccentColorOption,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier,
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerLow,
  ) {
    Row(
        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Surface(
          modifier = Modifier.size(48.dp),
          shape = CircleShape,
          color = accentColor.color,
          contentColor = accentColor.onColor,
          shadowElevation = 1.dp,
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
              imageVector = icon.icon,
              contentDescription = icon.name,
              modifier = Modifier.size(26.dp),
              tint = accentColor.onColor,
          )
        }
      }
      Column {
        Text(
            text = "Selected",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )
        Text(
            text = icon.name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
fun ColorPaletteTrack(
    colors: List<AccentColorOption>,
    selectedColor: AccentColorOption,
    onColorSelected: (AccentColorOption) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    colors.forEach { colorOption ->
      val isSelected = colorOption.name == selectedColor.name
      Surface(
          modifier = Modifier.size(40.dp),
          shape = CircleShape,
          color = colorOption.color,
          contentColor = colorOption.onColor,
          shadowElevation = if (isSelected) 2.dp else 0.dp,
          onClick = { onColorSelected(colorOption) },
      ) {
        Box(contentAlignment = Alignment.Center) {
          if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = colorOption.name,
                modifier = Modifier.size(18.dp),
                tint = colorOption.onColor,
            )
          }
        }
      }
    }
  }
}

@Composable
fun IconCategoryChips(
    categories: List<IconCategory>,
    selectedCategory: IconCategory,
    accentColor: AccentColorOption,
    onCategorySelected: (IconCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
  Row(
      modifier = modifier.horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    categories.forEach { category ->
      val isSelected = category == selectedCategory
      Surface(
          shape = CircleShape,
          color = if (isSelected) accentColor.color else MaterialTheme.colorScheme.surfaceContainer,
          contentColor =
              if (isSelected) accentColor.onColor else MaterialTheme.colorScheme.onSurfaceVariant,
          onClick = { onCategorySelected(category) },
      ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          Icon(
              imageVector = category.icon,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
          )
          Text(
              text = category.label,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
          )
        }
      }
    }
  }
}

@Composable
fun IconGridTile(
    icon: IconItem,
    isSelected: Boolean,
    accentColor: AccentColorOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      modifier = modifier,
      shape = RoundedCornerShape(16.dp),
      color =
          if (isSelected) MaterialTheme.colorScheme.surfaceContainer
          else MaterialTheme.colorScheme.surfaceContainerLow,
      onClick = onClick,
  ) {
    Column(
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Icon(
          imageVector = icon.icon,
          contentDescription = icon.name,
          modifier = Modifier.size(28.dp),
          tint = if (isSelected) accentColor.color else MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(6.dp))
      Text(
          text = icon.label,
          style = MaterialTheme.typography.labelSmall,
          color =
              if (isSelected) MaterialTheme.colorScheme.onSurface
              else MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
fun IconGrid(
    icons: List<IconItem>,
    selectedIcon: IconItem,
    accentColor: AccentColorOption,
    onIconSelected: (IconItem) -> Unit,
    modifier: Modifier = Modifier,
) {
  LazyVerticalGrid(
      columns = GridCells.Adaptive(minSize = 68.dp),
      modifier = modifier.height(280.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    items(icons, key = { it.id }) { iconItem ->
      IconGridTile(
          icon = iconItem,
          isSelected = iconItem.id == selectedIcon.id,
          accentColor = accentColor,
          onClick = { onIconSelected(iconItem) },
      )
    }
  }
}

@ComponentPreviews
@Composable
private fun IconPreviewBadgePreview() {
  val accentColors = defaultAccentColors()
  IconPreviewBadge(
      icon = defaultIcons.first(),
      accentColor = accentColors.first(),
  )
}

@ComponentPreviews
@Composable
private fun ColorPaletteTrackPreview() {
  val accentColors = defaultAccentColors()
  ColorPaletteTrack(
      colors = accentColors,
      selectedColor = accentColors.first(),
      onColorSelected = {},
  )
}

@ComponentPreviews
@Composable
private fun IconCategoryChipsPreview() {
  val accentColors = defaultAccentColors()
  IconCategoryChips(
      categories = IconCategory.entries,
      selectedCategory = IconCategory.Food,
      accentColor = accentColors.first(),
      onCategorySelected = {},
  )
}

@ComponentPreviews
@Composable
private fun IconGridTileSelectedPreview() {
  val accentColors = defaultAccentColors()
  IconGridTile(
      icon = defaultIcons.first(),
      isSelected = true,
      accentColor = accentColors.first(),
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun IconGridTileUnselectedPreview() {
  val accentColors = defaultAccentColors()
  IconGridTile(
      icon = defaultIcons.first(),
      isSelected = false,
      accentColor = accentColors.first(),
      onClick = {},
  )
}

@ComponentPreviews
@Composable
private fun IconGridPreview() {
  val accentColors = defaultAccentColors()
  IconGrid(
      icons = defaultIcons.take(12),
      selectedIcon = defaultIcons.first(),
      accentColor = accentColors.first(),
      onIconSelected = {},
      modifier = Modifier.fillMaxWidth(),
  )
}
