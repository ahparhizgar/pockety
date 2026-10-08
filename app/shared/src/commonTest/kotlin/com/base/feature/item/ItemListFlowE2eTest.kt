package com.base.feature.item

import com.base.component.TestCContext
import com.base.dto.FakeItemDtoFactory
import com.base.feature.auth.assertAuth
import com.base.feature.auth.assertLogin
import com.base.feature.auth.assertMain
import com.base.feature.auth.assertWelcome
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class ItemListFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("authenticated user navigating to ItemListScreen") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val itemApi by testValue { koin.get<ItemApi>() as FakeItemApi }
      var root by testValue { koin.get<RootComponentFactory>().create(context) }

      beforeEach { itemApi.items = FakeItemDtoFactory.createList(2).toMutableList() }

      When("cold start and logging in") {
        beforeEach {
          testCoroutineScheduler.advanceUntilIdle()
          root.assertAuth().assertWelcome().onLoginClicked()
          with(root.assertAuth().assertLogin()) {
            onEmailChanged("user@example.com")
            onPasswordChanged("password123")
            onLoginClicked()
          }
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("navigates to MainComponent and loads items") {
          val main = root.assertMain()
          main.itemListComponent.state.value.isLoading shouldBe false
          main.itemListComponent.state.value.items shouldHaveSize 2
          main.itemListComponent.state.value.items[0].name shouldBe "Test Item 1"
        }
      }
    }
  }
}
