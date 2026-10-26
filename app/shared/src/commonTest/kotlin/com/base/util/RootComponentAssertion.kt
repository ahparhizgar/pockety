package com.base.util

import com.base.capability.adding_expense.AddExpenseFlowComponent
import com.base.capability.category_management.AddCategoryComponent
import com.base.capability.category_management.CategorySelectionComponent
import com.base.capability.main.MainComponent
import com.base.capability.pocket_management.PocketDetailsComponent
import com.base.capability.pocket_management.PocketSettingsComponent
import com.base.capability.pocket_management.PocketSettingsFlowComponent
import com.base.capability.pocket_management.PocketsComponent
import com.base.capability.root_navigation.RootComponent
import com.base.capability.root_navigation.RootFlowDestination
import com.base.capability.sms_inbox.InboxComponent
import io.kotest.matchers.types.shouldBeInstanceOf

val RootComponent.activeConfig
  get() = childStack.value.active.configuration

val RootComponent.activeInstance
  get() = childStack.value.active.instance

fun RootComponent.assertMainComponent(): MainComponent {
  return activeInstance.shouldBeInstanceOf<RootFlowDestination.Main.Instance>().component
}

fun RootComponent.assertInbox(): InboxComponent {
  return assertMainComponent().assertInboxComponent()
}

fun RootComponent.assertPockets(): PocketsComponent {
  return assertMainComponent().assertPocketsComponent()
}

fun RootComponent.assertAddExpenseFlow(): AddExpenseFlowComponent {
  return activeInstance.shouldBeInstanceOf<RootFlowDestination.AddExpenseFlow.Instance>().component
}

fun RootComponent.assertPocketSettingsFlow(): PocketSettingsFlowComponent {
  return activeInstance
      .shouldBeInstanceOf<RootFlowDestination.PocketSettingsFlow.Instance>()
      .component
}

fun PocketSettingsFlowComponent.assertPocketSettings(): PocketSettingsComponent {
  return stack.value.active.instance
      .shouldBeInstanceOf<PocketSettingsFlowComponent.Child.PocketSettings>()
      .component
}

fun PocketSettingsFlowComponent.assertCategorySelection(): CategorySelectionComponent {
  return stack.value.active.instance
      .shouldBeInstanceOf<PocketSettingsFlowComponent.Child.CategorySelection>()
      .component
}

fun PocketSettingsFlowComponent.assertAddCategory(): AddCategoryComponent {
  return stack.value.active.instance
      .shouldBeInstanceOf<PocketSettingsFlowComponent.Child.AddCategory>()
      .component
}

fun RootComponent.assertPocketDetails(): PocketDetailsComponent {
  return activeInstance.shouldBeInstanceOf<RootFlowDestination.PocketDetails.Instance>().component
}
