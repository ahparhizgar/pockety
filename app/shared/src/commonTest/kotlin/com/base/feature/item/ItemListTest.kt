package com.base.feature.item

import com.base.component.TestCContext
import com.base.dto.FakeItemDtoFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class ItemListTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())
    Given("an item list component") {
      val koin by integrationKoin()
      val cContext by testValue { TestCContext().resumed() }
      val itemApi by testValue { koin.get<ItemApi>() as FakeItemApi }

      When("no items exist") {
        var component by testValue { koin.get<ItemListComponentFactory>().create(cContext) }
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("state contains empty items and not loading") {
          component.state.value.isLoading shouldBe false
          component.state.value.items.shouldBeEmpty()
          component.state.value.errorMessage shouldBe null
        }
      }

      When("items exist from backend") {
        beforeEach { itemApi.items = FakeItemDtoFactory.createList(3).toMutableList() }
        var component by testValue { koin.get<ItemListComponentFactory>().create(cContext) }
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("state loads items successfully") {
          component.state.value.isLoading shouldBe false
          component.state.value.items shouldHaveSize 3
          component.state.value.items[0].name shouldBe "Test Item 1"
          component.state.value.errorMessage shouldBe null
        }
      }

      When("network is disconnected") {
        beforeEach { itemApi.disconnect() }
        var component by testValue { koin.get<ItemListComponentFactory>().create(cContext) }
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("state contains error message and not loading") {
          component.state.value.isLoading shouldBe false
          component.state.value.errorMessage shouldBe
              com.base.remote.errorNetworkDisconnectedUiString
        }
      }
    }
  }
}
