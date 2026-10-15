package com.base.feature.root

import com.arkivanov.decompose.router.stack.pushNew
import com.base.component.TestCContext
import com.base.db.FakeCandidateExpenseEntityFactory
import com.base.feature.expense.add.AddExpenseComponent
import com.base.feature.inbox.InboxComponent
import com.base.feature.main.MainComponent
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class RootComponentTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("RootComponent initialization") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("initialized and idle") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("routes directly to MainComponent") {
          val activeInstance = root.childStack.value.active.instance
          activeInstance.shouldBeInstanceOf<MainComponent>()
        }

        Then("navigates to InboxComponent when navigating to InboxComponent.Config") {
          context.navigation.pushNew(InboxComponent.Config)
          testCoroutineScheduler.advanceUntilIdle()

          val activeInstance = root.childStack.value.active.instance
          activeInstance.shouldBeInstanceOf<InboxComponent>()
        }

        Then("navigates to AddExpenseComponent when selecting candidate from InboxComponent") {
          context.navigation.pushNew(InboxComponent.Config)
          testCoroutineScheduler.advanceUntilIdle()

          val inbox = root.childStack.value.active.instance as InboxComponent
          val candidate = FakeCandidateExpenseEntityFactory.create(amount = 25000L)
          inbox.onSelectCandidate(candidate)
          testCoroutineScheduler.advanceUntilIdle()

          val activeInstance = root.childStack.value.active.instance
          activeInstance.shouldBeInstanceOf<AddExpenseComponent>()
          activeInstance.state.value.amountText shouldBe "25000"
        }
      }
    }
  }
}
