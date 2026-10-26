package com.base.capability.root_navigation

import com.base.capability.adding_expense.AddExpenseFlowComponent
import com.base.capability.home.HomeComponent
import com.base.capability.main.MainComponent
import com.base.capability.pocket_management.PocketDetailsComponent
import com.base.capability.pocket_management.PocketSettingsFlowComponent
import com.base.capability.pocket_management.PocketsComponent
import com.base.capability.sms_inbox.InboxComponent
import kotlinx.serialization.Serializable

@Serializable
sealed interface RootFlowDestination {
  interface Instance

  @Serializable
  data object Splash : RootFlowDestination {
    data object Instance : RootFlowDestination.Instance
  }

  @Serializable
  data object Main : RootFlowDestination {
    data class Instance(val component: MainComponent) : RootFlowDestination.Instance
  }

  @Serializable
  data object Home : RootFlowDestination {
    data class Instance(val component: HomeComponent) : RootFlowDestination.Instance
  }

  @Serializable
  data object Inbox : RootFlowDestination {
    data class Instance(val component: InboxComponent) : RootFlowDestination.Instance
  }

  @Serializable
  data object Pockets : RootFlowDestination {
    data class Instance(val component: PocketsComponent) : RootFlowDestination.Instance
  }

  @Serializable
  data class AddExpenseFlow(
      val initialAmount: String = "",
      val candidateExpenseId: Long? = null,
  ) : RootFlowDestination {
    data class Instance(val component: AddExpenseFlowComponent) : RootFlowDestination.Instance
  }

  @Serializable
  data class PocketSettingsFlow(
      val pocketId: Long? = null,
  ) : RootFlowDestination {
    data class Instance(val component: PocketSettingsFlowComponent) : RootFlowDestination.Instance
  }

  @Serializable
  data class PocketDetails(
      val pocketId: Long,
  ) : RootFlowDestination {
    data class Instance(val component: PocketDetailsComponent) : RootFlowDestination.Instance
  }
}
