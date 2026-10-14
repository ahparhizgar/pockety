package com.base.feature.home

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.FakeTransactionFactory
import com.base.db.TransactionEntity
import com.base.db.TransactionRepository
import com.base.feature.root.TopLevelDestinationConfig
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.Serializable

data class HomeState(
    val transactions: List<TransactionEntity> = emptyList(),
    val isLoading: Boolean = false,
)

interface HomeComponent : TopLevelDestinationConfig {
  val state: Value<HomeState>

  fun onAddTransactionClicked()

  @Serializable data object Config : TopLevelDestinationConfig
}

interface HomeComponentFactory {
  fun create(
      cContext: CContext,
      onAddTransaction: () -> Unit,
  ): HomeComponent
}

class DefaultHomeComponent(
    cContext: CContext,
    private val transactionRepository: TransactionRepository,
    private val onAddTransaction: () -> Unit,
) : HomeComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(HomeState(isLoading = true))
  override val state: Value<HomeState> = _state

  init {
    observeTransactions()
  }

  private fun observeTransactions(): Job =
      transactionRepository
          .observeTransactions()
          .onEach { items ->
            _state.update {
              it.copy(
                  transactions = items,
                  isLoading = false,
              )
            }
          }
          .launchIn(scope)

  override fun onAddTransactionClicked() {
    onAddTransaction()
  }
}

class DefaultHomeComponentFactory(
    private val transactionRepository: TransactionRepository,
) : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddTransaction: () -> Unit,
  ): HomeComponent =
      DefaultHomeComponent(
          cContext = cContext,
          transactionRepository = transactionRepository,
          onAddTransaction = onAddTransaction,
      )
}

@Fake
class FakeHomeComponent(
    initialState: HomeState = HomeState(transactions = FakeTransactionFactory.createList(3)),
) : HomeComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<HomeState> = _state

  override fun onAddTransactionClicked() {}
}

@Fake
class FakeHomeComponentFactory : HomeComponentFactory {
  override fun create(
      cContext: CContext,
      onAddTransaction: () -> Unit,
  ): HomeComponent = FakeHomeComponent()
}
