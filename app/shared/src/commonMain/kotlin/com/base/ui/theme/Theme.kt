package com.base.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColorScheme(
    val success: ColorFamily,
    val warning: ColorFamily,
    val primaryFixed: Color = com.base.ui.theme.primaryFixed,
    val primaryFixedDim: Color = com.base.ui.theme.primaryFixedDim,
    val onPrimaryFixed: Color = com.base.ui.theme.onPrimaryFixed,
    val onPrimaryFixedVariant: Color = com.base.ui.theme.onPrimaryFixedVariant,
    val secondaryFixed: Color = com.base.ui.theme.secondaryFixed,
    val secondaryFixedDim: Color = com.base.ui.theme.secondaryFixedDim,
    val onSecondaryFixed: Color = com.base.ui.theme.onSecondaryFixed,
    val onSecondaryFixedVariant: Color = com.base.ui.theme.onSecondaryFixedVariant,
    val tertiaryFixed: Color = com.base.ui.theme.tertiaryFixed,
    val tertiaryFixedDim: Color = com.base.ui.theme.tertiaryFixedDim,
    val onTertiaryFixed: Color = com.base.ui.theme.onTertiaryFixed,
    val onTertiaryFixedVariant: Color = com.base.ui.theme.onTertiaryFixedVariant,
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color,
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
                onSuccessWarningContainer,
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
    )
