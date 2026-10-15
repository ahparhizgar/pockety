package com.base.capability.category_management

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Bolt
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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryColorOption(
    val name: String,
    val color: Color,
    val colorHex: Long,
    val onColor: Color = Color.White,
)

data class CategoryIconOption(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val categoryFilter: String = "All",
)

@Immutable data class CategoryColorsWrapper(val items: List<CategoryColorOption>)

@Immutable data class CategoryIconsWrapper(val items: List<CategoryIconOption>)

@Immutable data class CategoryFiltersWrapper(val items: List<String>)

val defaultCategoryColors =
    listOf(
        CategoryColorOption("Coral Red", Color(0xFFE57373), 0xFFE57373, Color.White),
        CategoryColorOption("Forest Green", Color(0xFF316342), 0xFF316342, Color.White),
        CategoryColorOption("Emerald", Color(0xFF4A7C59), 0xFF4A7C59, Color.White),
        CategoryColorOption("Sage", Color(0xFF9DD3AA), 0xFF9DD3AA, Color(0xFF00210E)),
        CategoryColorOption("Warm Amber", Color(0xFFB87D4B), 0xFFB87D4B, Color.White),
        CategoryColorOption("Deep Teal", Color(0xFF324C41), 0xFF324C41, Color.White),
        CategoryColorOption("Slate Muted", Color(0xFF4A6458), 0xFF4A6458, Color.White),
    )

val defaultIconCategoryFilters =
    listOf("All", "Food", "Shopping", "Transport", "Living", "Wellness", "Finance")

val defaultCategoryIcons =
    listOf(
        CategoryIconOption("restaurant", "Dining", Icons.Default.Restaurant, "Food"),
        CategoryIconOption("coffee", "Coffee", Icons.Default.Coffee, "Food"),
        CategoryIconOption("fastfood", "Burger", Icons.Default.Fastfood, "Food"),
        CategoryIconOption("local_pizza", "Pizza", Icons.Default.LocalPizza, "Food"),
        CategoryIconOption("icecream", "Treats", Icons.Default.Icecream, "Food"),
        CategoryIconOption("bakery_dining", "Bakery", Icons.Default.BakeryDining, "Food"),
        CategoryIconOption("shopping_cart", "Cart", Icons.Default.ShoppingCart, "Shopping"),
        CategoryIconOption("shopping_bag", "Retail", Icons.Default.ShoppingBag, "Shopping"),
        CategoryIconOption("store", "Market", Icons.Default.Store, "Shopping"),
        CategoryIconOption("checkroom", "Apparel", Icons.Default.Checkroom, "Shopping"),
        CategoryIconOption("loyalty", "Perks", Icons.Default.Loyalty, "Shopping"),
        CategoryIconOption("directions_car", "Auto", Icons.Default.DirectionsCar, "Transport"),
        CategoryIconOption("local_gas_station", "Fuel", Icons.Default.LocalGasStation, "Transport"),
        CategoryIconOption("directions_bus", "Transit", Icons.Default.DirectionsBus, "Transport"),
        CategoryIconOption("flight", "Travel", Icons.Default.Flight, "Transport"),
        CategoryIconOption("two_wheeler", "Moto", Icons.Default.TwoWheeler, "Transport"),
        CategoryIconOption("home", "Housing", Icons.Default.Home, "Living"),
        CategoryIconOption("bolt", "Power", Icons.Default.Bolt, "Living"),
        CategoryIconOption("water_drop", "Water", Icons.Default.WaterDrop, "Living"),
        CategoryIconOption("wifi", "Internet", Icons.Default.Wifi, "Living"),
        CategoryIconOption("pets", "Pets", Icons.Default.Pets, "Living"),
        CategoryIconOption("favorite", "Care", Icons.Default.Favorite, "Wellness"),
        CategoryIconOption("medication", "Rx", Icons.Default.Medication, "Wellness"),
        CategoryIconOption("medical_services", "Health", Icons.Default.MedicalServices, "Wellness"),
        CategoryIconOption("fitness_center", "Gym", Icons.Default.FitnessCenter, "Wellness"),
        CategoryIconOption("spa", "Spa", Icons.Default.Spa, "Wellness"),
        CategoryIconOption("school", "Study", Icons.Default.School, "Wellness"),
        CategoryIconOption("palette", "Art", Icons.Default.Palette, "Wellness"),
        CategoryIconOption("payments", "Income", Icons.Default.Payments, "Finance"),
        CategoryIconOption("savings", "Savings", Icons.Default.Savings, "Finance"),
        CategoryIconOption("credit_card", "Card", Icons.Default.CreditCard, "Finance"),
        CategoryIconOption(
            "receipt_long",
            "Bills",
            Icons.AutoMirrored.Filled.ReceiptLong,
            "Finance",
        ),
        CategoryIconOption("account_balance", "Bank", Icons.Default.AccountBalance, "Finance"),
    )

fun resolveCategoryIcon(iconId: String): ImageVector {
  return defaultCategoryIcons.firstOrNull { it.id == iconId }?.icon
      ?: Icons.AutoMirrored.Filled.Label
}
