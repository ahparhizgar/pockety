package com.base.feature.privacypolicy

import com.base.component.TestCContext
import com.base.feature.root.RootComponentFactory
import com.base.feature.root.RootFlowDestination
import com.base.feature.settings.SettingsComponent
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertMainComponent
import com.base.util.assertPrivacyPolicy
import com.base.util.assertSettingsComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class PrivacyPolicyFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("app initialized at MainComponent with Settings tab selected") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      beforeEach {
        testCoroutineScheduler.advanceUntilIdle()
        val main = root.assertMainComponent()
        main.onTabSelect(PocketyTab.Settings)
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("user clicks Privacy Policy navigation row") {
        beforeEach {
          val settings = root.assertMainComponent().assertSettingsComponent()
          settings.onPrivacyPolicyClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("active child in RootComponent becomes RootFlowDestination.PrivacyPolicy") {
          root.childStack.value.active.configuration shouldBe RootFlowDestination.PrivacyPolicy
          root.assertPrivacyPolicy().shouldBeInstanceOf<PrivacyPolicyComponent>()
        }

        And("user triggers back navigation from Privacy Policy") {
          beforeEach {
            root.onBack()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then(
              "active child in RootComponent returns to RootFlowDestination.Main with Settings active"
          ) {
            root.childStack.value.active.configuration shouldBe RootFlowDestination.Main
            root
                .assertMainComponent()
                .assertSettingsComponent()
                .shouldBeInstanceOf<SettingsComponent>()
          }
        }
      }
    }
  }
}
