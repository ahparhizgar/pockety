package com.base.navigation

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

sealed interface NavigationAction {
  data class OpenAddExpense(
      val candidateExpenseId: Long,
      val initialAmount: String,
  ) : NavigationAction
}

interface NavigationActionBus {
  val events: Flow<NavigationAction>

  fun emit(action: NavigationAction)
}

class DefaultNavigationActionBus : NavigationActionBus {
  private val channel = Channel<NavigationAction>(capacity = Channel.BUFFERED)
  override val events: Flow<NavigationAction> = channel.receiveAsFlow()

  override fun emit(action: NavigationAction) {
    channel.trySend(action)
  }
}
