package com.base.feature.transaction

import com.base.component.TestCContext
import com.base.db.TransactionRepository
import com.base.feature.transaction.add.AddTransactionComponent
import com.base.feature.transaction.add.DefaultAddTransactionComponent
import com.base.testValue
import com.base.usermessage.UiString
import com.base.util.FakeCallback
import com.base.util.MainDispatcherExtension
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.first
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.error_empty_description
import pockety.app.shared.generated.resources.error_invalid_amount

class AddTransactionTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("AddTransactionComponent with repository") {
      val koin by integrationKoin()
      val repository by testValue { koin.get<TransactionRepository>() }
      val completedCallback by testValue { FakeCallback<Unit>() }
      val cancelledCallback by testValue { FakeCallback<Unit>() }
      val component: AddTransactionComponent by testValue {
        DefaultAddTransactionComponent(
            cContext = TestCContext(),
            transactionRepository = repository,
            onCompleted = completedCallback,
            onCancelled = cancelledCallback,
        )
      }

      When("submitting with empty amount and empty description") {
        beforeEach {
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("shows amount and description validation errors") {
          val state = component.state.value
          state.amountError shouldBe UiString(Res.string.error_invalid_amount)
          state.descriptionError shouldBe UiString(Res.string.error_empty_description)
          completedCallback.shouldNotBeCalled()
        }
      }

      When("submitting with zero amount") {
        beforeEach {
          component.onAmountChanged("0")
          component.onDescriptionChanged("Coffee")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("shows amount validation error") {
          val state = component.state.value
          state.amountError shouldBe UiString(Res.string.error_invalid_amount)
          state.descriptionError.shouldBeNull()
          completedCallback.shouldNotBeCalled()
        }
      }

      When("submitting with negative amount") {
        beforeEach {
          component.onAmountChanged("-500")
          component.onDescriptionChanged("Coffee")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("shows amount validation error") {
          val state = component.state.value
          state.amountError shouldBe UiString(Res.string.error_invalid_amount)
          completedCallback.shouldNotBeCalled()
        }
      }

      When("submitting with valid amount and description") {
        beforeEach {
          component.onAmountChanged("15000")
          component.onDescriptionChanged("Groceries")
          component.onSaveClicked()
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("saves transaction to repository and calls onCompleted") {
          val state = component.state.value
          state.amountError.shouldBeNull()
          state.descriptionError.shouldBeNull()
          completedCallback.shouldBeCalled()

          val transactions = repository.observeTransactions().first()
          transactions shouldHaveSize 1
          transactions[0].amount shouldBe 15000
          transactions[0].description shouldBe "Groceries"
        }
      }

      When("cancel is clicked") {
        beforeEach { component.onCancelClicked() }

        Then("calls onCancelled") { cancelledCallback.shouldBeCalled() }
      }
    }
  }
}
