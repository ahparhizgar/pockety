package com.base.feature.category

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class CategoryOptionData(
    val name: String,
    val icon: ImageVector,
    val iconBackgroundColor: Color,
    val iconContentColor: Color,
    val description: String? = null,
    val isSelected: Boolean = false,
)
