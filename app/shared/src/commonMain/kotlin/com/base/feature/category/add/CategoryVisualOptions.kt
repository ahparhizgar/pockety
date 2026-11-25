package com.base.feature.category.add

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.jetbrains.compose.resources.StringResource
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.category_color_amber
import pockety.app.shared.generated.resources.category_color_blue
import pockety.app.shared.generated.resources.category_color_brown
import pockety.app.shared.generated.resources.category_color_coral
import pockety.app.shared.generated.resources.category_color_cyan
import pockety.app.shared.generated.resources.category_color_emerald
import pockety.app.shared.generated.resources.category_color_forest
import pockety.app.shared.generated.resources.category_color_gold
import pockety.app.shared.generated.resources.category_color_graphite
import pockety.app.shared.generated.resources.category_color_indigo
import pockety.app.shared.generated.resources.category_color_orange
import pockety.app.shared.generated.resources.category_color_pink
import pockety.app.shared.generated.resources.category_color_red
import pockety.app.shared.generated.resources.category_color_sage
import pockety.app.shared.generated.resources.category_color_slate
import pockety.app.shared.generated.resources.category_color_teal
import pockety.app.shared.generated.resources.category_color_violet
import pockety.app.shared.generated.resources.category_filter_all
import pockety.app.shared.generated.resources.category_filter_finance
import pockety.app.shared.generated.resources.category_filter_food
import pockety.app.shared.generated.resources.category_filter_leisure
import pockety.app.shared.generated.resources.category_filter_living
import pockety.app.shared.generated.resources.category_filter_shopping
import pockety.app.shared.generated.resources.category_filter_transport
import pockety.app.shared.generated.resources.category_filter_wellness
import pockety.app.shared.generated.resources.category_icon_account_balance
import pockety.app.shared.generated.resources.category_icon_bakery_dining
import pockety.app.shared.generated.resources.category_icon_beach_access
import pockety.app.shared.generated.resources.category_icon_bolt
import pockety.app.shared.generated.resources.category_icon_build
import pockety.app.shared.generated.resources.category_icon_card_giftcard
import pockety.app.shared.generated.resources.category_icon_chair
import pockety.app.shared.generated.resources.category_icon_checkroom
import pockety.app.shared.generated.resources.category_icon_child_care
import pockety.app.shared.generated.resources.category_icon_cleaning_services
import pockety.app.shared.generated.resources.category_icon_coffee
import pockety.app.shared.generated.resources.category_icon_content_cut
import pockety.app.shared.generated.resources.category_icon_credit_card
import pockety.app.shared.generated.resources.category_icon_devices
import pockety.app.shared.generated.resources.category_icon_directions_bike
import pockety.app.shared.generated.resources.category_icon_directions_bus
import pockety.app.shared.generated.resources.category_icon_directions_car
import pockety.app.shared.generated.resources.category_icon_fastfood
import pockety.app.shared.generated.resources.category_icon_favorite
import pockety.app.shared.generated.resources.category_icon_fitness_center
import pockety.app.shared.generated.resources.category_icon_flight
import pockety.app.shared.generated.resources.category_icon_home
import pockety.app.shared.generated.resources.category_icon_hotel
import pockety.app.shared.generated.resources.category_icon_icecream
import pockety.app.shared.generated.resources.category_icon_local_florist
import pockety.app.shared.generated.resources.category_icon_local_gas_station
import pockety.app.shared.generated.resources.category_icon_local_grocery_store
import pockety.app.shared.generated.resources.category_icon_local_pizza
import pockety.app.shared.generated.resources.category_icon_local_taxi
import pockety.app.shared.generated.resources.category_icon_loyalty
import pockety.app.shared.generated.resources.category_icon_medical_services
import pockety.app.shared.generated.resources.category_icon_medication
import pockety.app.shared.generated.resources.category_icon_menu_book
import pockety.app.shared.generated.resources.category_icon_movie
import pockety.app.shared.generated.resources.category_icon_music_note
import pockety.app.shared.generated.resources.category_icon_palette
import pockety.app.shared.generated.resources.category_icon_payments
import pockety.app.shared.generated.resources.category_icon_pets
import pockety.app.shared.generated.resources.category_icon_phone_android
import pockety.app.shared.generated.resources.category_icon_receipt_long
import pockety.app.shared.generated.resources.category_icon_restaurant
import pockety.app.shared.generated.resources.category_icon_savings
import pockety.app.shared.generated.resources.category_icon_school
import pockety.app.shared.generated.resources.category_icon_shopping_bag
import pockety.app.shared.generated.resources.category_icon_shopping_cart
import pockety.app.shared.generated.resources.category_icon_spa
import pockety.app.shared.generated.resources.category_icon_sports_esports
import pockety.app.shared.generated.resources.category_icon_sports_soccer
import pockety.app.shared.generated.resources.category_icon_store
import pockety.app.shared.generated.resources.category_icon_train
import pockety.app.shared.generated.resources.category_icon_two_wheeler
import pockety.app.shared.generated.resources.category_icon_volunteer_activism
import pockety.app.shared.generated.resources.category_icon_water_drop
import pockety.app.shared.generated.resources.category_icon_wifi
import pockety.app.shared.generated.resources.category_icon_work

data class CategoryColorOption(
    val name: StringResource,
    val color: Color,
    val colorHex: Long,
    val onColor: Color = Color.White,
)

data class CategoryIconOption(
    val id: String,
    val label: StringResource,
    val icon: ImageVector,
    val categoryFilter: CategoryIconFilter,
)

@Immutable data class CategoryColorsWrapper(val items: List<CategoryColorOption>)

@Immutable data class CategoryIconsWrapper(val items: List<CategoryIconOption>)

@Immutable data class CategoryFiltersWrapper(val items: List<CategoryIconFilter>)

val defaultCategoryColors =
    listOf(
        CategoryColorOption(
            Res.string.category_color_coral,
            Color(0xFFE57373),
            0xFFE57373,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_red,
            Color(0xFFE53935),
            0xFFE53935,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_orange,
            Color(0xFFEF6C00),
            0xFFEF6C00,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_amber,
            Color(0xFFB87D4B),
            0xFFB87D4B,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_gold,
            Color(0xFFF9A825),
            0xFFF9A825,
            Color(0xFF3E2723),
        ),
        CategoryColorOption(
            Res.string.category_color_brown,
            Color(0xFF6D4C41),
            0xFF6D4C41,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_forest,
            Color(0xFF316342),
            0xFF316342,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_emerald,
            Color(0xFF4A7C59),
            0xFF4A7C59,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_sage,
            Color(0xFF9DD3AA),
            0xFF9DD3AA,
            Color(0xFF00210E),
        ),
        CategoryColorOption(
            Res.string.category_color_teal,
            Color(0xFF324C41),
            0xFF324C41,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_slate,
            Color(0xFF4A6458),
            0xFF4A6458,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_cyan,
            Color(0xFF00ACC1),
            0xFF00ACC1,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_blue,
            Color(0xFF1E88E5),
            0xFF1E88E5,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_indigo,
            Color(0xFF3949AB),
            0xFF3949AB,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_violet,
            Color(0xFF8E24AA),
            0xFF8E24AA,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_pink,
            Color(0xFFEC407A),
            0xFFEC407A,
            Color.White,
        ),
        CategoryColorOption(
            Res.string.category_color_graphite,
            Color(0xFF757575),
            0xFF757575,
            Color.White,
        ),
    )

enum class CategoryIconFilter(val label: StringResource) {
  All(Res.string.category_filter_all),
  Food(Res.string.category_filter_food),
  Shopping(Res.string.category_filter_shopping),
  Transport(Res.string.category_filter_transport),
  Living(Res.string.category_filter_living),
  Wellness(Res.string.category_filter_wellness),
  Finance(Res.string.category_filter_finance),
  Leisure(Res.string.category_filter_leisure),
}

val defaultIconCategoryFilters = CategoryIconFilter.entries

val defaultCategoryIcons =
    listOf(
        CategoryIconOption(
            "restaurant",
            Res.string.category_icon_restaurant,
            Icons.Default.Restaurant,
            CategoryIconFilter.Food,
        ),
        CategoryIconOption(
            "coffee",
            Res.string.category_icon_coffee,
            Icons.Default.Coffee,
            CategoryIconFilter.Food,
        ),
        CategoryIconOption(
            "fastfood",
            Res.string.category_icon_fastfood,
            Icons.Default.Fastfood,
            CategoryIconFilter.Food,
        ),
        CategoryIconOption(
            "local_pizza",
            Res.string.category_icon_local_pizza,
            Icons.Default.LocalPizza,
            CategoryIconFilter.Food,
        ),
        CategoryIconOption(
            "icecream",
            Res.string.category_icon_icecream,
            Icons.Default.Icecream,
            CategoryIconFilter.Food,
        ),
        CategoryIconOption(
            "bakery_dining",
            Res.string.category_icon_bakery_dining,
            Icons.Default.BakeryDining,
            CategoryIconFilter.Food,
        ),
        CategoryIconOption(
            "shopping_cart",
            Res.string.category_icon_shopping_cart,
            Icons.Default.ShoppingCart,
            CategoryIconFilter.Shopping,
        ),
        CategoryIconOption(
            "shopping_bag",
            Res.string.category_icon_shopping_bag,
            Icons.Default.ShoppingBag,
            CategoryIconFilter.Shopping,
        ),
        CategoryIconOption(
            "store",
            Res.string.category_icon_store,
            Icons.Default.Store,
            CategoryIconFilter.Shopping,
        ),
        CategoryIconOption(
            "checkroom",
            Res.string.category_icon_checkroom,
            Icons.Default.Checkroom,
            CategoryIconFilter.Shopping,
        ),
        CategoryIconOption(
            "loyalty",
            Res.string.category_icon_loyalty,
            Icons.Default.Loyalty,
            CategoryIconFilter.Shopping,
        ),
        CategoryIconOption(
            "directions_car",
            Res.string.category_icon_directions_car,
            Icons.Default.DirectionsCar,
            CategoryIconFilter.Transport,
        ),
        CategoryIconOption(
            "local_gas_station",
            Res.string.category_icon_local_gas_station,
            Icons.Default.LocalGasStation,
            CategoryIconFilter.Transport,
        ),
        CategoryIconOption(
            "directions_bus",
            Res.string.category_icon_directions_bus,
            Icons.Default.DirectionsBus,
            CategoryIconFilter.Transport,
        ),
        CategoryIconOption(
            "flight",
            Res.string.category_icon_flight,
            Icons.Default.Flight,
            CategoryIconFilter.Transport,
        ),
        CategoryIconOption(
            "two_wheeler",
            Res.string.category_icon_two_wheeler,
            Icons.Default.TwoWheeler,
            CategoryIconFilter.Transport,
        ),
        CategoryIconOption(
            "home",
            Res.string.category_icon_home,
            Icons.Default.Home,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "bolt",
            Res.string.category_icon_bolt,
            Icons.Default.Bolt,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "water_drop",
            Res.string.category_icon_water_drop,
            Icons.Default.WaterDrop,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "wifi",
            Res.string.category_icon_wifi,
            Icons.Default.Wifi,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "pets",
            Res.string.category_icon_pets,
            Icons.Default.Pets,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "favorite",
            Res.string.category_icon_favorite,
            Icons.Default.Favorite,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "medication",
            Res.string.category_icon_medication,
            Icons.Default.Medication,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "medical_services",
            Res.string.category_icon_medical_services,
            Icons.Default.MedicalServices,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "fitness_center",
            Res.string.category_icon_fitness_center,
            Icons.Default.FitnessCenter,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "spa",
            Res.string.category_icon_spa,
            Icons.Default.Spa,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "school",
            Res.string.category_icon_school,
            Icons.Default.School,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "palette",
            Res.string.category_icon_palette,
            Icons.Default.Palette,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "payments",
            Res.string.category_icon_payments,
            Icons.Default.Payments,
            CategoryIconFilter.Finance,
        ),
        CategoryIconOption(
            "savings",
            Res.string.category_icon_savings,
            Icons.Default.Savings,
            CategoryIconFilter.Finance,
        ),
        CategoryIconOption(
            "credit_card",
            Res.string.category_icon_credit_card,
            Icons.Default.CreditCard,
            CategoryIconFilter.Finance,
        ),
        CategoryIconOption(
            "receipt_long",
            Res.string.category_icon_receipt_long,
            Icons.AutoMirrored.Filled.ReceiptLong,
            CategoryIconFilter.Finance,
        ),
        CategoryIconOption(
            "account_balance",
            Res.string.category_icon_account_balance,
            Icons.Default.AccountBalance,
            CategoryIconFilter.Finance,
        ),
        CategoryIconOption(
            "local_grocery_store",
            Res.string.category_icon_local_grocery_store,
            Icons.Default.LocalGroceryStore,
            CategoryIconFilter.Shopping,
        ),
        CategoryIconOption(
            "devices",
            Res.string.category_icon_devices,
            Icons.Default.Devices,
            CategoryIconFilter.Shopping,
        ),
        CategoryIconOption(
            "card_giftcard",
            Res.string.category_icon_card_giftcard,
            Icons.Default.CardGiftcard,
            CategoryIconFilter.Shopping,
        ),
        CategoryIconOption(
            "directions_bike",
            Res.string.category_icon_directions_bike,
            Icons.AutoMirrored.Filled.DirectionsBike,
            CategoryIconFilter.Transport,
        ),
        CategoryIconOption(
            "train",
            Res.string.category_icon_train,
            Icons.Default.Train,
            CategoryIconFilter.Transport,
        ),
        CategoryIconOption(
            "local_taxi",
            Res.string.category_icon_local_taxi,
            Icons.Default.LocalTaxi,
            CategoryIconFilter.Transport,
        ),
        CategoryIconOption(
            "build",
            Res.string.category_icon_build,
            Icons.Default.Build,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "cleaning_services",
            Res.string.category_icon_cleaning_services,
            Icons.Default.CleaningServices,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "chair",
            Res.string.category_icon_chair,
            Icons.Default.Chair,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "phone_android",
            Res.string.category_icon_phone_android,
            Icons.Default.PhoneAndroid,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "child_care",
            Res.string.category_icon_child_care,
            Icons.Default.ChildCare,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "local_florist",
            Res.string.category_icon_local_florist,
            Icons.Default.LocalFlorist,
            CategoryIconFilter.Living,
        ),
        CategoryIconOption(
            "sports_soccer",
            Res.string.category_icon_sports_soccer,
            Icons.Default.SportsSoccer,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "menu_book",
            Res.string.category_icon_menu_book,
            Icons.AutoMirrored.Filled.MenuBook,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "content_cut",
            Res.string.category_icon_content_cut,
            Icons.Default.ContentCut,
            CategoryIconFilter.Wellness,
        ),
        CategoryIconOption(
            "movie",
            Res.string.category_icon_movie,
            Icons.Default.Movie,
            CategoryIconFilter.Leisure,
        ),
        CategoryIconOption(
            "sports_esports",
            Res.string.category_icon_sports_esports,
            Icons.Default.SportsEsports,
            CategoryIconFilter.Leisure,
        ),
        CategoryIconOption(
            "music_note",
            Res.string.category_icon_music_note,
            Icons.Default.MusicNote,
            CategoryIconFilter.Leisure,
        ),
        CategoryIconOption(
            "hotel",
            Res.string.category_icon_hotel,
            Icons.Default.Hotel,
            CategoryIconFilter.Leisure,
        ),
        CategoryIconOption(
            "beach_access",
            Res.string.category_icon_beach_access,
            Icons.Default.BeachAccess,
            CategoryIconFilter.Leisure,
        ),
        CategoryIconOption(
            "volunteer_activism",
            Res.string.category_icon_volunteer_activism,
            Icons.Default.VolunteerActivism,
            CategoryIconFilter.Finance,
        ),
        CategoryIconOption(
            "work",
            Res.string.category_icon_work,
            Icons.Default.Work,
            CategoryIconFilter.Finance,
        ),
    )

fun resolveCategoryIcon(iconId: String): ImageVector {
  return defaultCategoryIcons.firstOrNull { it.id == iconId }?.icon
      ?: Icons.AutoMirrored.Filled.Label
}
