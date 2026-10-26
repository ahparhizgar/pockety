package com.base.util

import com.base.feature.category.add.AddCategoryComponent
import com.base.feature.category.selection.CategorySelectionComponent
import com.base.feature.expense.AddExpenseFlowComponent
import com.base.feature.expense.add.AddExpenseComponent
import com.base.feature.pocket.selection.SelectPocketComponent
import io.kotest.matchers.types.shouldBeInstanceOf

private val AddExpenseFlowComponent.activeInstance
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
  return activeInstance
      .shouldBeInstanceOf<AddExpenseFlowComponent.Child.AddCategory>()
      .component
}

fun AddExpenseFlowComponent.assertSelectPocketComponent(): SelectPocketComponent {
  return activeInstance
      .shouldBeInstanceOf<AddExpenseFlowComponent.Child.PocketSelection>()
      .component
}
