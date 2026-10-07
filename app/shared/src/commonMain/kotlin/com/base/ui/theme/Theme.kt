package com.base.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColorScheme(
    val success: ColorFamily,
    val warning: ColorFamily,
    val youOwe: ColorFamily,
    val youAreOwed: ColorFamily,
)

val lightScheme =
    lightColorScheme(
        primary = primaryLight,
        onPrimary = onPrimaryLight,
        primaryContainer = primaryContainerLight,
        onPrimaryContainer = onPrimaryContainerLight,
        secondary = secondaryLight,
        onSecondary = onSecondaryLight,
        secondaryContainer = secondaryContainerLight,
        onSecondaryContainer = onSecondaryContainerLight,
        tertiary = tertiaryLight,
        onTertiary = onTertiaryLight,
        tertiaryContainer = tertiaryContainerLight,
        onTertiaryContainer = onTertiaryContainerLight,
        error = errorLight,
        onError = onErrorLight,
        errorContainer = errorContainerLight,
        onErrorContainer = onErrorContainerLight,
        background = backgroundLight,
        onBackground = onBackgroundLight,
        surface = surfaceLight,
        onSurface = onSurfaceLight,
        surfaceVariant = surfaceVariantLight,
        onSurfaceVariant = onSurfaceVariantLight,
        outline = outlineLight,
        outlineVariant = outlineVariantLight,
        scrim = scrimLight,
        inverseSurface = inverseSurfaceLight,
        inverseOnSurface = inverseOnSurfaceLight,
        inversePrimary = inversePrimaryLight,
        surfaceDim = surfaceDimLight,
        surfaceBright = surfaceBrightLight,
        surfaceContainerLowest = surfaceContainerLowestLight,
        surfaceContainerLow = surfaceContainerLowLight,
        surfaceContainer = surfaceContainerLight,
        surfaceContainerHigh = surfaceContainerHighLight,
        surfaceContainerHighest = surfaceContainerHighestLight,
    )

val darkScheme =
    darkColorScheme(
        primary = primaryDark,
        onPrimary = onPrimaryDark,
        primaryContainer = primaryContainerDark,
        onPrimaryContainer = onPrimaryContainerDark,
        secondary = secondaryDark,
        onSecondary = onSecondaryDark,
        secondaryContainer = secondaryContainerDark,
        onSecondaryContainer = onSecondaryContainerDark,
        tertiary = tertiaryDark,
        onTertiary = onTertiaryDark,
        tertiaryContainer = tertiaryContainerDark,
        onTertiaryContainer = onTertiaryContainerDark,
        error = errorDark,
        onError = onErrorDark,
        errorContainer = errorContainerDark,
        onErrorContainer = onErrorContainerDark,
        background = backgroundDark,
        onBackground = onBackgroundDark,
        surface = surfaceDark,
        onSurface = onSurfaceDark,
        surfaceVariant = surfaceVariantDark,
        onSurfaceVariant = onSurfaceVariantDark,
        outline = outlineDark,
        outlineVariant = outlineVariantDark,
        scrim = scrimDark,
        inverseSurface = inverseSurfaceDark,
        inverseOnSurface = inverseOnSurfaceDark,
        inversePrimary = inversePrimaryDark,
        surfaceDim = surfaceDimDark,
        surfaceBright = surfaceBrightDark,
        surfaceContainerLowest = surfaceContainerLowestDark,
        surfaceContainerLow = surfaceContainerLowDark,
        surfaceContainer = surfaceContainerDark,
        surfaceContainerHigh = surfaceContainerHighDark,
        surfaceContainerHighest = surfaceContainerHighestDark,
    )

val extendedLight =
    ExtendedColorScheme(
        success =
            ColorFamily(
                successLight,
                onSuccessLight,
                successContainerLight,
                onSuccessContainerLight,
            ),
        warning =
            ColorFamily(
                warningLight,
                onWarningLight,
                warningContainerLight,
                onWarningContainerLight,
            ),
        youOwe =
            ColorFamily(
                youOweLight,
                onYouOweLight,
                youOweContainerLight,
                onYouOweContainerLight,
            ),
        youAreOwed =
            ColorFamily(
                youAreOwedLight,
                onYouAreOwedLight,
                youAreOwedContainerLight,
                onYouAreOwedContainerLight,
            ),
    )

val extendedDark =
    ExtendedColorScheme(
        success =
            ColorFamily(
                successDark,
                onSuccessDark,
                successContainerDark,
                onSuccessContainerDark,
            ),
        warning =
            ColorFamily(
                warningDark,
                onWarningDark,
                warningContainerDark,
                onWarningContainerDark,
            ),
        youOwe =
            ColorFamily(
                youOweDark,
                onYouOweDark,
                youOweContainerDark,
                onYouOweContainerDark,
            ),
        youAreOwed =
            ColorFamily(
                youAreOwedDark,
                onYouAreOwedDark,
                youAreOwedContainerDark,
                onYouAreOwedContainerDark,
            ),
    )

val extendedLightMediumContrast =
    ExtendedColorScheme(
        success =
            ColorFamily(
                successLightMediumContrast,
                onSuccessLightMediumContrast,
                successContainerLightMediumContrast,
                onSuccessContainerLightMediumContrast,
            ),
        warning =
            ColorFamily(
                warningLightMediumContrast,
                onWarningLightMediumContrast,
                warningContainerLightMediumContrast,
                onWarningContainerLightMediumContrast,
            ),
        youOwe =
            ColorFamily(
                youOweLightMediumContrast,
                onYouOweLightMediumContrast,
                youOweContainerLightMediumContrast,
                onYouOweContainerLightMediumContrast,
            ),
        youAreOwed =
            ColorFamily(
                youAreOwedLightMediumContrast,
                onYouAreOwedLightMediumContrast,
                youAreOwedContainerLightMediumContrast,
                onYouAreOwedContainerLightMediumContrast,
            ),
    )

val extendedLightHighContrast =
    ExtendedColorScheme(
        success =
            ColorFamily(
                successLightHighContrast,
                onSuccessLightHighContrast,
                successContainerLightHighContrast,
                onSuccessContainerLightHighContrast,
            ),
        warning =
            ColorFamily(
                warningLightHighContrast,
                onWarningLightHighContrast,
                warningContainerLightHighContrast,
                onWarningContainerLightHighContrast,
            ),
        youOwe =
            ColorFamily(
                youOweLightHighContrast,
                onYouOweLightHighContrast,
                youOweContainerLightHighContrast,
                onYouOweContainerLightHighContrast,
            ),
        youAreOwed =
            ColorFamily(
                youAreOwedLightHighContrast,
                onYouAreOwedLightHighContrast,
                youAreOwedContainerLightHighContrast,
                onYouAreOwedContainerLightHighContrast,
            ),
    )

val extendedDarkMediumContrast =
    ExtendedColorScheme(
        success =
            ColorFamily(
                successDarkMediumContrast,
                onSuccessDarkMediumContrast,
                successContainerDarkMediumContrast,
                onSuccessContainerDarkMediumContrast,
            ),
        warning =
            ColorFamily(
                warningDarkMediumContrast,
                onWarningDarkMediumContrast,
                warningContainerDarkMediumContrast,
                onWarningContainerDarkMediumContrast,
            ),
        youOwe =
            ColorFamily(
                youOweDarkMediumContrast,
                onYouOweDarkMediumContrast,
                youOweContainerDarkMediumContrast,
                onYouOweContainerDarkMediumContrast,
            ),
        youAreOwed =
            ColorFamily(
                youAreOwedDarkMediumContrast,
                onYouAreOwedDarkMediumContrast,
                youAreOwedContainerDarkMediumContrast,
                onYouAreOwedContainerDarkMediumContrast,
            ),
    )

val extendedDarkHighContrast =
    ExtendedColorScheme(
        success =
            ColorFamily(
                successDarkHighContrast,
                onSuccessDarkHighContrast,
                successContainerDarkHighContrast,
                onSuccessContainerDarkHighContrast,
            ),
        warning =
            ColorFamily(
                warningDarkHighContrast,
                onWarningDarkHighContrast,
                warningContainerDarkHighContrast,
                onWarningContainerDarkHighContrast,
            ),
        youOwe =
            ColorFamily(
                youOweDarkHighContrast,
                onYouOweDarkHighContrast,
                youOweContainerDarkHighContrast,
                onYouOweContainerDarkHighContrast,
            ),
        youAreOwed =
            ColorFamily(
                youAreOwedDarkHighContrast,
                onYouAreOwedDarkHighContrast,
                youAreOwedContainerDarkHighContrast,
                onYouAreOwedContainerDarkHighContrast,
            ),
    )

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
)

val unspecified_scheme =
    ColorFamily(Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified)
