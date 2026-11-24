package com.base.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.vazirmatn_bold
import pockety.app.shared.generated.resources.vazirmatn_medium
import pockety.app.shared.generated.resources.vazirmatn_regular
import pockety.app.shared.generated.resources.vazirmatn_semibold

/** Vazirmatn (OFL) — the app's font family for Persian and Latin text. */
@Composable
fun vazirmatnFontFamily(): FontFamily =
    FontFamily(
        Font(Res.font.vazirmatn_regular, FontWeight.Normal),
        Font(Res.font.vazirmatn_medium, FontWeight.Medium),
        Font(Res.font.vazirmatn_semibold, FontWeight.SemiBold),
        Font(Res.font.vazirmatn_bold, FontWeight.Bold),
    )

private val baseTypography =
    Typography(
      displayLarge =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Bold,
              fontSize = 36.sp,
              lineHeight = 44.sp,
              letterSpacing = (-0.02).sp,
          ),
      headlineLarge =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 32.sp,
              lineHeight = 40.sp,
              letterSpacing = (-0.02).sp,
          ),
      headlineMedium =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 24.sp,
              lineHeight = 32.sp,
              letterSpacing = (-0.01).sp,
          ),
      headlineSmall =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 20.sp,
              lineHeight = 28.sp,
              letterSpacing = 0.sp,
          ),
      titleLarge =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 18.sp,
              lineHeight = 26.sp,
              letterSpacing = 0.sp,
          ),
      titleMedium =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium,
              fontSize = 16.sp,
              lineHeight = 24.sp,
              letterSpacing = 0.01.sp,
          ),
      titleSmall =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium,
              fontSize = 14.sp,
              lineHeight = 20.sp,
              letterSpacing = 0.01.sp,
          ),
      bodyLarge =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Normal,
              fontSize = 16.sp,
              lineHeight = 24.sp,
              letterSpacing = 0.sp,
          ),
      bodyMedium =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Normal,
              fontSize = 14.sp,
              lineHeight = 20.sp,
              letterSpacing = 0.01.sp,
          ),
      bodySmall =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Normal,
              fontSize = 12.sp,
              lineHeight = 18.sp,
              letterSpacing = 0.02.sp,
          ),
      labelLarge =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp,
              lineHeight = 20.sp,
              letterSpacing = 0.01.sp,
          ),
      labelMedium =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
              lineHeight = 16.sp,
              letterSpacing = 0.02.sp,
          ),
      labelSmall =
          TextStyle(
              fontFamily = FontFamily.SansSerif,
              fontWeight = FontWeight.Medium,
              fontSize = 11.sp,
              lineHeight = 14.sp,
              letterSpacing = 0.04.sp,
          ),
  )

/** The app's typography with every style set in the Vazirmatn family. */
@Composable
fun appTypography(): Typography {
  val vazirmatn = vazirmatnFontFamily()
  return with(baseTypography) {
    Typography(
        displayLarge = displayLarge.copy(fontFamily = vazirmatn),
        headlineLarge = headlineLarge.copy(fontFamily = vazirmatn),
        headlineMedium = headlineMedium.copy(fontFamily = vazirmatn),
        headlineSmall = headlineSmall.copy(fontFamily = vazirmatn),
        titleLarge = titleLarge.copy(fontFamily = vazirmatn),
        titleMedium = titleMedium.copy(fontFamily = vazirmatn),
        titleSmall = titleSmall.copy(fontFamily = vazirmatn),
        bodyLarge = bodyLarge.copy(fontFamily = vazirmatn),
        bodyMedium = bodyMedium.copy(fontFamily = vazirmatn),
        bodySmall = bodySmall.copy(fontFamily = vazirmatn),
        labelLarge = labelLarge.copy(fontFamily = vazirmatn),
        labelMedium = labelMedium.copy(fontFamily = vazirmatn),
        labelSmall = labelSmall.copy(fontFamily = vazirmatn),
    )
  }
}
