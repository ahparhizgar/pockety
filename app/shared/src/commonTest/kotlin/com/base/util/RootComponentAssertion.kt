package com.base.util

import com.base.capability.adding_expense.add.AddExpenseComponent
import com.base.capability.main.MainComponent
import com.base.capability.root_navigation.RootComponent
import com.base.capability.sms_inbox.InboxComponent
import io.kotest.matchers.types.shouldBeInstanceOf

val RootComponent.activeConfig
  get() = childStack.value.active.configuration

val RootComponent.activeInstance
  get() = childStack.value.active.instance

fun RootComponent.assertMainComponent(): MainComponent {
  return activeInstance.shouldBeInstanceOf<MainComponent>()
}

fun RootComponent.assertAddExpense(): AddExpenseComponent {
  return activeInstance.shouldBeInstanceOf<AddExpenseComponent>()
}

fun RootComponent.assertInbox(): InboxComponent {
  return activeInstance.shouldBeInstanceOf<InboxComponent>()
}

fun RootComponent.assertAddExpenseFlow():
    com.base.capability.adding_expense.AddExpenseFlowComponent {
  return activeInstance.shouldBeInstanceOf<
      com.base.capability.adding_expense.AddExpenseFlowComponent
  >()
}
