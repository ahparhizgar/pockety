package com.base.design.ui

import androidx.compose.ui.tooling.preview.AndroidUiModes
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewWrapper

@Preview(name = "Light", uiMode = AndroidUiModes.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", uiMode = AndroidUiModes.UI_MODE_NIGHT_YES)
@PreviewWrapper(PocketyThemeWrapper::class)
annotation class ComponentPreviews

@Preview(
    name = "Mobile Dark",
    uiMode = AndroidUiModes.UI_MODE_NIGHT_YES,
    widthDp = 390,
    heightDp = 844,
)
@Preview(
    name = "Mobile Light",
    uiMode = AndroidUiModes.UI_MODE_NIGHT_NO,
    widthDp = 390,
    heightDp = 844,
)
@Preview(
    name = "Mobile Landscape",
    widthDp = 844,
    heightDp = 390,
)
@Preview(
    name = "Desktop",
    widthDp = 1280,
    heightDp = 800,
)
@PreviewWrapper(PocketyThemeWrapper::class)
annotation class ScreenPreviews

@Preview(
    widthDp = 3048,
    heightDp = 2024,
)
@PreviewWrapper(PocketyThemeWrapper::class)
annotation class FigmaBoardPreviews
