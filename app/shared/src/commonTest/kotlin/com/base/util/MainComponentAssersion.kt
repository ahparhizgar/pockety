package com.base.util

import com.base.capability.adding_expense.add.AddExpenseComponent
import com.base.capability.home.HomeComponent
import com.base.capability.main.MainComponent
import io.kotest.matchers.types.shouldBeInstanceOf

val MainComponent.activeInstance
  get() = stack.value.active.instance

fun MainComponent.assertHomeComponent(): HomeComponent {
  return activeInstance.shouldBeInstanceOf<MainComponent.Child.Home>().component
}

fun MainComponent.assertAddExpenseComponent(): AddExpenseComponent {
  return activeInstance.shouldBeInstanceOf<MainComponent.Child.AddExpense>().component
}
