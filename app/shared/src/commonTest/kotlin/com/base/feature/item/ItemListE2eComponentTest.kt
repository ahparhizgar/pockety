package com.base.feature.item

import com.base.component.TestCContext
import com.base.dto.FakeItemDtoFactory
import com.base.feature.auth.assertAuth
import com.base.feature.auth.assertLogin
import com.base.feature.auth.assertMain
import com.base.feature.auth.assertSplash
import com.base.feature.auth.assertWelcome
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe

class ItemListE2eComponentTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())
    Given("app cold start navigation to main item list screen") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val itemApi by testValue { koin.get<ItemApi>() as FakeItemApi }
      var root by testValue { koin.get<RootComponentFactory>().create(context) }

      beforeEach { itemApi.items = FakeItemDtoFactory.createList(1).toMutableList() }

      Then("shows welcome screen") { root.assertSplash() }
      When("waiting") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }
        Then("shows auth screen") { root.assertAuth().assertWelcome() }
        And("click on login") {
          beforeEach { root.assertAuth().assertWelcome().onLoginClicked() }
          Then("shows login screen") { root.assertAuth().assertLogin() }
          And("entering valid credentials") {
            beforeEach {
              root.assertAuth().assertLogin().let {
                it.onEmailChanged("valid@example.com")
                it.onPasswordChanged("password123")
                it.onLoginClicked()
              }
              testCoroutineScheduler.advanceUntilIdle()
            }
            Then("navigates to MainComponent displaying item list") {
              val main = root.assertMain()
              main.itemListComponent.state.value.items.size shouldBe 1
            }
          }
        }
      }
    }
  }
}
