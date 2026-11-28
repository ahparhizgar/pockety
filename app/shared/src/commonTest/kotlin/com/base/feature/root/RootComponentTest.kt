package com.base.feature.root

import com.base.component.TestCContext
import com.base.feature.settings.AppLanguage
import com.base.feature.settings.UserSettingsRepository
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.assertMainComponent
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking

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

    Given("RootComponent created with a persisted language") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }

      When("the saved language is Persian") {
        Then("the root starts in Persian immediately, without waiting for the settings flow") {
          runBlocking {
            koin.get<UserSettingsRepository>().setAppLanguage(AppLanguage.PERSIAN)
          }
          val root = koin.get<RootComponentFactory>().create(context)
          root.appLanguage.value shouldBe AppLanguage.PERSIAN
        }
      }
    }
  }
}
