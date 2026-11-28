package com.base.db

/**
 * A category every fresh install starts with. The stored name follows the app
 * language at seed time; the icon is an id from the category icon set and the
 * color a value from the shared category palette.
 */
data class DefaultCategory(
    val nameFa: String,
    val nameEn: String,
    val icon: String,
    val color: Long,
)

/** The built-in categories, in display order (most-used first). */
val defaultCategories: List<DefaultCategory> =
    listOf(
        DefaultCategory("مواد غذایی", "Groceries", "local_grocery_store", 0xFF4A7C59),
        DefaultCategory("حمل‌ونقل", "Transport", "directions_car", 0xFF1E88E5),
        DefaultCategory("رستوران و کافه", "Restaurants & Cafes", "restaurant", 0xFFEF6C00),
        DefaultCategory("لوازم خانه", "Household", "chair", 0xFF6D4C41),
        DefaultCategory("بهداشت و زیبایی", "Health & Beauty", "spa", 0xFFEC407A),
        DefaultCategory("قبوض و ارتباطات", "Bills & Communications", "receipt_long", 0xFF4A6458),
        DefaultCategory("تفریح و سرگرمی", "Entertainment", "sports_esports", 0xFF8E24AA),
        DefaultCategory("سلامت و درمان", "Health & Medical", "medical_services", 0xFFE53935),
        DefaultCategory("پوشاک", "Clothing", "checkroom", 0xFF3949AB),
        DefaultCategory("مسکن", "Housing", "home", 0xFFB87D4B),
        DefaultCategory("آموزش", "Education", "school", 0xFF00ACC1),
        DefaultCategory("هدیه و کمک", "Gifts & Charity", "card_giftcard", 0xFFE57373),
        DefaultCategory("سفر", "Travel", "flight", 0xFFF9A825),
        DefaultCategory("بیمه", "Insurance", "account_balance", 0xFF324C41),
    )
