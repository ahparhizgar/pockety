package com.base.feature.transaction

import com.base.component.TestCContext
import com.base.feature.main.MainComponent
import com.base.feature.root.RootComponentFactory
import com.base.testValue
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class AddViewTransactionFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("user launches application") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }

      When("cold start completes") {
        beforeEach { testCoroutineScheduler.advanceUntilIdle() }

        Then("displays HomeScreen with empty transactions") {
          val main = root.childStack.value.active.instance.shouldBeInstanceOf<MainComponent>()
          val homeChild =
              main.stack.value.active.instance.shouldBeInstanceOf<MainComponent.Child.Home>()
          homeChild.component.state.value.transactions shouldHaveSize 0
        }

        And("user navigates to add transaction and saves an expense") {
          beforeEach {
            val main = root.childStack.value.active.instance.shouldBeInstanceOf<MainComponent>()
            val homeChild =
                main.stack.value.active.instance.shouldBeInstanceOf<MainComponent.Child.Home>()
            homeChild.component.onAddTransactionClicked()

            val addTransactionChild =
                main.stack.value.active.instance.shouldBeInstanceOf<
                    MainComponent.Child.AddTransaction
                >()
            addTransactionChild.component.onAmountChanged("320000")
            addTransactionChild.component.onDescriptionChanged("Coffee and croissant")
            addTransactionChild.component.onSaveClicked()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("navigates back to HomeScreen displaying the recorded transaction") {
            val main = root.childStack.value.active.instance.shouldBeInstanceOf<MainComponent>()
            val homeChild =
                main.stack.value.active.instance.shouldBeInstanceOf<MainComponent.Child.Home>()
            val transactions = homeChild.component.state.value.transactions

            transactions shouldHaveSize 1
            transactions[0].amount shouldBe 320000
            transactions[0].description shouldBe "Coffee and croissant"
          }
        }
      }
    }
  }
}
