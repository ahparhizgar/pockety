package com.base.util

import com.base.capability.adding_expense.AddExpenseFlowComponent
import com.base.capability.adding_expense.add.AddExpenseComponent
import com.base.capability.category_management.AddCategoryComponent
import com.base.capability.category_management.CategorySelectionComponent
import io.kotest.matchers.types.shouldBeInstanceOf

val AddExpenseFlowComponent.activeInstance
  get() = stack.value.active.instance

fun AddExpenseFlowComponent.assertAddExpenseComponent(): AddExpenseComponent {
  return activeInstance.shouldBeInstanceOf<AddExpenseFlowComponent.Child.AddExpense>().component
}

fun AddExpenseFlowComponent.assertCategorySelectionComponent(): CategorySelectionComponent {
  return activeInstance
      .shouldBeInstanceOf<AddExpenseFlowComponent.Child.CategorySelection>()
      .component
}

fun AddExpenseFlowComponent.assertAddCategoryComponent(): AddCategoryComponent {
  return activeInstance.shouldBeInstanceOf<AddExpenseFlowComponent.Child.AddCategory>().component
}
