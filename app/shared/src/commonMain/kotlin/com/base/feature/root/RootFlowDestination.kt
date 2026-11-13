package com.base.feature.root

import com.base.feature.expense.AddExpenseFlowComponent
import com.base.feature.home.HomeComponent
import com.base.feature.main.MainComponent
import com.base.feature.pocket.PocketsComponent
import com.base.feature.pocket.details.PocketDetailsComponent
import com.base.feature.pocket.settings.PocketSettingsFlowComponent
import com.base.feature.privacypolicy.PrivacyPolicyComponent
import com.base.feature.sms.inbox.InboxComponent
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
  data object PrivacyPolicy : RootFlowDestination {
    data class Instance(val component: PrivacyPolicyComponent) : RootFlowDestination.Instance
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
      val expenseId: Long? = null,
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
