package com.base.util

import com.base.feature.category.add.AddCategoryComponent
import com.base.feature.category.selection.CategorySelectionComponent
import com.base.feature.expense.AddExpenseFlowComponent
import com.base.feature.main.MainComponent
import com.base.feature.pocket.PocketsComponent
import com.base.feature.pocket.details.PocketDetailsComponent
import com.base.feature.pocket.settings.PocketSettingsComponent
import com.base.feature.pocket.settings.PocketSettingsFlowComponent
import com.base.feature.root.RootComponent
import com.base.feature.root.RootFlowDestination
import com.base.feature.sms.inbox.InboxComponent
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
