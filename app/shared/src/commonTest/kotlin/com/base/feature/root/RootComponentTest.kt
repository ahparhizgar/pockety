package com.base.feature.root

import com.base.component.TestCContext
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler

class RootComponentTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("RootComponent initialization") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("initialized and idle") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("routes directly to MainComponent") { root.assertMainComponent() }
      }
    }
  }
}
