package com.base.capability.pocket_management

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.capability.root_navigation.TopLevelDestinationConfig
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.PocketRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

data class PocketTransactionUiModel(
    val id: String,
    val title: String,
    val icon: String,
    val timestamp: String,
    val note: String,
    val amount: Long,
    val isTopUp: Boolean = false,
)

data class PocketDetailsState(
    val pocketId: Long = 0,
    val name: String = "",
    val balanceAmount: Long = 0,
    val icon: String = "coffee",
    val color: Long = 0xFF316342,
    val isLoading: Boolean = false,
    val isQuickTopUpVisible: Boolean = false,
    val quickTopUpCustomAmount: String = "",
    val confirmationMessage: String? = null,
    val transactions: List<PocketTransactionUiModel> = emptyList(),
)

interface PocketDetailsComponent : TopLevelDestinationConfig {
  val state: Value<PocketDetailsState>

  fun onBackClicked()

  fun onConfigureClicked()

  fun onTopUpClicked()

  fun onDismissQuickTopUp()

  fun onQuickTopUpPresetSelected(amount: Long)

  fun onQuickTopUpCustomAmountChanged(amount: String)

  fun onConfirmQuickTopUp(): Job

  fun onDismissConfirmation()

  fun onDeleteTransaction(id: String)

  @Serializable
  data class Config(
      val pocketId: Long,
  ) : TopLevelDestinationConfig
}

interface PocketDetailsComponentFactory {
  fun create(
      cContext: CContext,
      pocketId: Long,
      onBack: () -> Unit = {},
      onConfigure: (Long) -> Unit = {},
  ): PocketDetailsComponent
}

class DefaultPocketDetailsComponent(
    cContext: CContext,
    private val pocketRepository: PocketRepository,
    private val pocketId: Long,
    private val onBack: () -> Unit = {},
    private val onConfigure: (Long) -> Unit = {},
) : PocketDetailsComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state =
      MutableValue(
          PocketDetailsState(
              pocketId = pocketId,
              isLoading = true,
              transactions = defaultPocketTransactions(),
          )
      )
  override val state: Value<PocketDetailsState> = _state

  init {
    observePocket()
  }

  private fun observePocket(): Job =
      pocketRepository
          .observePockets()
          .onEach { pockets ->
            val pocket = pockets.firstOrNull { it.id == pocketId }
            if (pocket != null) {
              _state.update {
                it.copy(
                    pocketId = pocket.id,
                    name = pocket.name,
                    balanceAmount = pocket.remainingBalance,
                    icon = pocket.icon,
                    color = pocket.color,
                    isLoading = false,
                )
              }
            } else {
              _state.update { it.copy(isLoading = false) }
            }
          }
          .launchIn(scope)

  override fun onBackClicked() {
    onBack.invoke()
  }

  override fun onConfigureClicked() {
    onConfigure.invoke(pocketId)
  }

  override fun onTopUpClicked() {
    _state.update { it.copy(isQuickTopUpVisible = true, quickTopUpCustomAmount = "") }
  }

  override fun onDismissQuickTopUp() {
    _state.update { it.copy(isQuickTopUpVisible = false) }
  }

  override fun onQuickTopUpPresetSelected(amount: Long) {
    _state.update { it.copy(quickTopUpCustomAmount = amount.toString()) }
  }

  override fun onQuickTopUpCustomAmountChanged(amount: String) {
    _state.update { it.copy(quickTopUpCustomAmount = amount) }
  }

  override fun onConfirmQuickTopUp(): Job = scope.launch {
    val clean = _state.value.quickTopUpCustomAmount.replace(",", "").trim()
    val amount = clean.toLongOrNull() ?: 0L
    if (amount > 0) {
      pocketRepository.topUpPocket(pocketId, amount)
      _state.update {
        it.copy(
            isQuickTopUpVisible = false,
            quickTopUpCustomAmount = "",
            confirmationMessage = "Pocket topped up by +${formatAmount(amount)} T!",
        )
      }
    }
  }

  override fun onDismissConfirmation() {
    _state.update { it.copy(confirmationMessage = null) }
  }

  override fun onDeleteTransaction(id: String) {
    val tx = _state.value.transactions.firstOrNull { it.id == id }
    if (tx != null) {
      _state.update {
        it.copy(
            transactions = it.transactions.filterNot { item -> item.id == id },
            confirmationMessage = "Deleted ${tx.title}. +${formatAmount(tx.amount)} T restored!",
        )
      }
    }
  }

  private fun formatAmount(amount: Long): String {
    val chunkSize = 3
    return amount.toString().reversed().chunked(chunkSize).joinToString(",").reversed()
  }
}

class DefaultPocketDetailsComponentFactory(
    private val pocketRepository: PocketRepository,
) : PocketDetailsComponentFactory {
  override fun create(
      cContext: CContext,
      pocketId: Long,
      onBack: () -> Unit,
      onConfigure: (Long) -> Unit,
  ): PocketDetailsComponent =
      DefaultPocketDetailsComponent(
          cContext = cContext,
          pocketRepository = pocketRepository,
          pocketId = pocketId,
          onBack = onBack,
          onConfigure = onConfigure,
      )
}

@Suppress("MagicNumber")
fun defaultPocketTransactions(): List<PocketTransactionUiModel> =
    listOf(
        PocketTransactionUiModel(
            id = "tx-1",
            title = "Sam Cafe Vanak",
            icon = "coffee",
            timestamp = "Today, 10:15 AM",
            note = "Meeting with Ali",
            amount = 85000,
        ),
        PocketTransactionUiModel(
            id = "tx-2",
            title = "Lamiz Coffee Jordan",
            icon = "local_cafe",
            timestamp = "Yesterday, 4:30 PM",
            note = "Iced Latte & Cookie",
            amount = 120000,
        ),
        PocketTransactionUiModel(
            id = "tx-3",
            title = "Monthly Pocket Allocation",
            icon = "arrow_downward_alt",
            timestamp = "23 Oct, 09:00 AM",
            note = "Auto-deposit",
            amount = 500000,
            isTopUp = true,
        ),
        PocketTransactionUiModel(
            id = "tx-4",
            title = "Reza Bakery & Espresso",
            icon = "bakery_dining",
            timestamp = "22 Oct, 11:20 AM",
            note = "Morning Americano",
            amount = 65000,
        ),
        PocketTransactionUiModel(
            id = "tx-5",
            title = "Cafe Kariz Garden",
            icon = "cloud_sync",
            timestamp = "20 Oct, 6:00 PM",
            note = "Herbal tea & cheesecake",
            amount = 95000,
        ),
        PocketTransactionUiModel(
            id = "tx-6",
            title = "Bread & Salt Bakery",
            icon = "shopping_bag",
            timestamp = "18 Oct, 08:10 AM",
            note = "Sourdough & filter roast",
            amount = 45000,
        ),
    )

@Fake
class FakePocketDetailsComponent(
    initialState: PocketDetailsState = FakePocketDetailsStateFactory.create(),
) : PocketDetailsComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<PocketDetailsState> = _state

  override fun onBackClicked() {}

  override fun onConfigureClicked() {}

  override fun onTopUpClicked() {
    _state.update { it.copy(isQuickTopUpVisible = true) }
  }

  override fun onDismissQuickTopUp() {
    _state.update { it.copy(isQuickTopUpVisible = false) }
  }

  override fun onQuickTopUpPresetSelected(amount: Long) {
    _state.update { it.copy(quickTopUpCustomAmount = amount.toString()) }
  }

  override fun onQuickTopUpCustomAmountChanged(amount: String) {
    _state.update { it.copy(quickTopUpCustomAmount = amount) }
  }

  override fun onConfirmQuickTopUp(): Job = Job()

  override fun onDismissConfirmation() {
    _state.update { it.copy(confirmationMessage = null) }
  }

  override fun onDeleteTransaction(id: String) {
    _state.update { it.copy(transactions = it.transactions.filterNot { item -> item.id == id }) }
  }
}

@Fake
class FakePocketDetailsComponentFactory : PocketDetailsComponentFactory {
  override fun create(
      cContext: CContext,
      pocketId: Long,
      onBack: () -> Unit,
      onConfigure: (Long) -> Unit,
  ): PocketDetailsComponent = FakePocketDetailsComponent()
}

object FakePocketDetailsStateFactory {
  @Suppress("LongParameterList")
  fun create(
      pocketId: Long = 1L,
      name: String = "Daily Cafe & Coffee",
      balanceAmount: Long = 850000L,
      icon: String = "coffee",
      color: Long = 0xFF316342,
      isLoading: Boolean = false,
      isQuickTopUpVisible: Boolean = false,
      quickTopUpCustomAmount: String = "",
      confirmationMessage: String? = null,
      transactions: List<PocketTransactionUiModel> = defaultPocketTransactions(),
  ): PocketDetailsState =
      PocketDetailsState(
          pocketId = pocketId,
          name = name,
          balanceAmount = balanceAmount,
          icon = icon,
          color = color,
          isLoading = isLoading,
          isQuickTopUpVisible = isQuickTopUpVisible,
          quickTopUpCustomAmount = quickTopUpCustomAmount,
          confirmationMessage = confirmationMessage,
          transactions = transactions,
      )
}

object FakePocketTransactionUiModelFactory {
  @Suppress("LongParameterList")
  fun create(
      id: String = "tx-1",
      title: String = "Sam Cafe Vanak",
      icon: String = "coffee",
      timestamp: String = "Today, 10:15 AM",
      note: String = "Meeting with Ali",
      amount: Long = 85000,
      isTopUp: Boolean = false,
  ): PocketTransactionUiModel =
      PocketTransactionUiModel(
          id = id,
          title = title,
          icon = icon,
          timestamp = timestamp,
          note = note,
          amount = amount,
          isTopUp = isTopUp,
      )
}
