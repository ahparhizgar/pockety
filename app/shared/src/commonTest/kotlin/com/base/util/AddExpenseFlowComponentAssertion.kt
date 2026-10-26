package com.base.util

import com.base.feature.category.add.AddCategoryComponent
import com.base.feature.category.selection.CategorySelectionComponent
import com.base.feature.expense.AddExpenseFlowComponent
import com.base.feature.expense.add.AddExpenseComponent
import io.kotest.matchers.types.shouldBeInstanceOf

private val AddExpenseFlowComponent.flowActiveInstance
  get() = stack.value.active.instance

fun AddExpenseFlowComponent.assertAddExpenseComponent(): AddExpenseComponent {
  return flowActiveInstance.shouldBeInstanceOf<AddExpenseFlowComponent.Child.AddExpense>().component
}

fun AddExpenseFlowComponent.assertCategorySelectionComponent(): CategorySelectionComponent {
  return flowActiveInstance
      .shouldBeInstanceOf<AddExpenseFlowComponent.Child.CategorySelection>()
      .component
}

fun AddExpenseFlowComponent.assertAddCategoryComponent(): AddCategoryComponent {
  return flowActiveInstance
      .shouldBeInstanceOf<AddExpenseFlowComponent.Child.AddCategory>()
      .component
}
