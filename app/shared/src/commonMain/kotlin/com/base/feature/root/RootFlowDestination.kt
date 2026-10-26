package com.base.feature.root

import com.base.feature.auth.AuthComponent
import com.base.feature.item.ItemListComponent
import com.base.feature.main.MainComponent
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
  data object Auth : RootFlowDestination {
    data class Instance(val component: AuthComponent) : RootFlowDestination.Instance
  }

  @Serializable
  data object ItemList : RootFlowDestination {
    data class Instance(val component: ItemListComponent) : RootFlowDestination.Instance
  }
}
