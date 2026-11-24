package com.base

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.DefaultDesktopComposePreviewTester
import com.github.takahirom.roborazzi.DesktopComposePreviewTester
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.InternalRoborazziApi
import com.github.takahirom.roborazzi.advanceMainClockFor
import io.github.takahirom.roborazzi.captureRoboImage

/**
 * Capturer that can also screenshot previews which show a dialog.
 *
 * The default capturer calls [onRoot], which requires exactly one semantics root. On
 * desktop a dialog composes into its own window with its own root, so a dialog preview
 * has two roots and the single-root lookup throws. When more than one root exists, the
 * last root belongs to the dialog window, so capture that one instead.
 */
@OptIn(ExperimentalRoborazziApi::class, InternalRoborazziApi::class, ExperimentalTestApi::class)
class DialogAwareCapturer : DefaultDesktopComposePreviewTester.Capturer {

  override fun ComposeUiTest.capture(
      parameter: DefaultDesktopComposePreviewTester.CaptureParameter
  ) {
    setContent(parameter.content)
    advanceMainClockFor(parameter)
    val roots = onAllNodes(isRoot())
    val rootCount = roots.fetchSemanticsNodes().size
    val target = if (rootCount <= 1) onRoot() else roots[rootCount - 1]
    target.captureRoboImage(
        filePath = parameter.filePath,
        roborazziOptions = parameter.roborazziOptions,
    )
  }
}

/**
 * Desktop preview tester that captures dialog previews instead of failing on them.
 * Everything except capturing delegates to [DefaultDesktopComposePreviewTester].
 */
@OptIn(ExperimentalRoborazziApi::class, InternalRoborazziApi::class)
class DialogAwareDesktopComposePreviewTester :
    DesktopComposePreviewTester by
        DefaultDesktopComposePreviewTester(capturer = DialogAwareCapturer())
