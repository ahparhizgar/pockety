package com.base.capability.root_navigation

import com.base.navigation.DefaultNavigationActionBus
import com.base.navigation.NavigationAction
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first

class NavigationActionBusTest : BehaviorSpec() {
  init {
    Given("a DefaultNavigationActionBus") {
      val bus = DefaultNavigationActionBus()

      When("an action is emitted before collection (replay test)") {
        val action =
            NavigationAction.OpenAddExpense(candidateExpenseId = 42L, initialAmount = "50000")
        bus.emit(action)

        Then("a new collector receives the replayed event") {
          val received = bus.events.first()
          received shouldBe action
        }
      }
    }
  }
}
