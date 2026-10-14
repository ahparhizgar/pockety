package com.base.permission

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class FakePermissionControllerTest {

  @Test
  fun initialPermissionStateIsRespected() {
    val defaultController = FakePermissionController()
    assertFalse(defaultController.isSmsPermissionGranted.value)

    val grantedController = FakePermissionController(initialSmsPermissionGranted = true)
    assertTrue(grantedController.isSmsPermissionGranted.value)
  }

  @Test
  fun updatingPermissionStateEmitsNewValue() {
    val controller = FakePermissionController(initialSmsPermissionGranted = false)
    assertFalse(controller.isSmsPermissionGranted.value)

    controller.setSmsPermissionGranted(true)
    assertTrue(controller.isSmsPermissionGranted.value)

    controller.setSmsPermissionGranted(false)
    assertFalse(controller.isSmsPermissionGranted.value)
  }

  @Test
  fun requestPermissionIncrementsCallCountAndReturnsResult() = runTest {
    val controller = FakePermissionController()
    var callbackInvoked = false
    controller.onRequestReceiveSmsPermission = {
      callbackInvoked = true
      true
    }

    assertEquals(0, controller.requestReceiveSmsPermissionCallCount)
    assertFalse(callbackInvoked)

    val result = controller.requestReceiveSmsPermission()

    assertEquals(1, controller.requestReceiveSmsPermissionCallCount)
    assertTrue(callbackInvoked)
    assertTrue(result)
    assertTrue(controller.isSmsPermissionGranted.value)
  }
}
