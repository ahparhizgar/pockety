package com.base.design

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.runDesktopComposeUiTest
import com.base.design.figma.FigmaScreenItem
import com.base.design.figma.figmaPageSections
import com.base.design.ui.theme.PocketyTheme
import com.github.takahirom.roborazzi.RoborazziOptions
import io.github.takahirom.roborazzi.captureRoboImage
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class CatalogScreenshotTest(private val screenItem: FigmaScreenItem) {

  @OptIn(ExperimentalTestApi::class)
  @Test
  fun testScreen() =
      runDesktopComposeUiTest(width = 390, height = 844) {
        setContent { PocketyTheme(darkTheme = false, content = screenItem.content) }
        val safeTitle = screenItem.title.replace("/", "-")
        // A screen showing a dialog has two semantics roots (the dialog lives in its
        // own window); capture the last root, which is the dialog window.
        val roots = onAllNodes(isRoot())
        val rootCount = roots.fetchSemanticsNodes().size
        val target = if (rootCount <= 1) onRoot() else roots[rootCount - 1]
        target
            .captureRoboImage(
                filePath = "screenshots/${safeTitle}.png",
                roborazziOptions =
                    RoborazziOptions(
                        compareOptions =
                            RoborazziOptions.CompareOptions(
                                outputDirectoryPath = "screenshots/compare/"
                            )
                    ),
            )
      }

  companion object {
    @JvmStatic
    @Parameterized.Parameters(name = "{0}")
    fun data(): Collection<FigmaScreenItem> {
      return figmaPageSections.values.flatten().flatMap { it.screens }
    }
  }
}
