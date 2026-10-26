package com.base.feature.pocket

import com.base.component.TestCContext
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertHomeComponent
import com.base.util.assertMainComponent
import com.base.util.assertPockets
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize

class PocketListFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("application on Home tab") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      beforeEach { testCoroutineScheduler.advanceUntilIdle() }

      When("user selects the Pockets tab") {
        beforeEach {
          val main = root.assertMainComponent()
          main.onTabSelect(PocketyTab.Pockets)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("active child in MainComponent is Pockets with PocketsComponent instance") {
          root.assertPockets()
          root.assertMainComponent().stack.value.backStack shouldHaveSize 1
        }

        And("pressing back returns to Home tab with empty back stack") {
          beforeEach {
            root.assertMainComponent().onBack()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("returns to Home tab") {
            root.assertMainComponent().assertHomeComponent()
            root.assertMainComponent().stack.value.backStack shouldHaveSize 0
          }
        }
      }
    }
  }
}
