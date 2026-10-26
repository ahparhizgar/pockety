package com.base.feature.pocket.selection

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.FakePocketItemModelFactory
import com.base.db.PocketItemModel
import com.base.db.PocketRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class SelectPocketState(
    val pockets: List<PocketItemModel> = emptyList(),
    val filteredPockets: List<PocketItemModel> = emptyList(),
    val searchQuery: String = "",
    val selectedPocketId: Long? = null,
    val isLoading: Boolean = false,
)

interface SelectPocketComponent {
  val state: Value<SelectPocketState>

  fun onSearchQueryChanged(query: String)

  fun onPocketSelected(pocket: PocketItemModel)

  fun onNoPocketSelected()

  fun onCreatePocketClicked()

  fun onBackClicked()
}

interface SelectPocketComponentFactory {
  fun create(
      cContext: CContext,
      initialSelectedPocketId: Long? = null,
      onPocketSelected: (PocketItemModel?) -> Unit,
      onCreatePocketClicked: () -> Unit,
      onBack: () -> Unit,
  ): SelectPocketComponent
}

class DefaultSelectPocketComponent(
    cContext: CContext,
    private val pocketRepository: PocketRepository,
    private val initialSelectedPocketId: Long? = null,
    private val onPocketSelectedCallback: (PocketItemModel?) -> Unit,
    private val onCreatePocketClicked: () -> Unit,
    private val onBack: () -> Unit,
) : SelectPocketComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(SelectPocketState(selectedPocketId = initialSelectedPocketId))
  override val state: Value<SelectPocketState> = _state

  init {
    observePockets()
  }

  private fun observePockets() {
    pocketRepository
        .observePockets()
        .onEach { pockets ->
          _state.update {
            it.copy(
                pockets = pockets,
                filteredPockets = filterPockets(pockets, it.searchQuery),
            )
          }
        }
        .launchIn(scope)
  }

  private fun filterPockets(
      pockets: List<PocketItemModel>,
      query: String,
  ): List<PocketItemModel> {
    if (query.isBlank()) return pockets
    val trimmed = query.trim().lowercase()
    return pockets.filter {
      it.name.lowercase().contains(trimmed) || it.categoryTag.lowercase().contains(trimmed)
    }
  }

  override fun onSearchQueryChanged(query: String) {
    _state.update {
      it.copy(
          searchQuery = query,
          filteredPockets = filterPockets(it.pockets, query),
      )
    }
  }

  override fun onPocketSelected(pocket: PocketItemModel) {
    _state.update { it.copy(selectedPocketId = pocket.id) }
    onPocketSelectedCallback(pocket)
  }

  override fun onNoPocketSelected() {
    _state.update { it.copy(selectedPocketId = null) }
    onPocketSelectedCallback(null)
  }

  override fun onCreatePocketClicked() {
    onCreatePocketClicked.invoke()
  }

  override fun onBackClicked() {
    onBack.invoke()
  }
}

class DefaultSelectPocketComponentFactory(
    private val pocketRepository: PocketRepository,
) : SelectPocketComponentFactory {
  override fun create(
      cContext: CContext,
      initialSelectedPocketId: Long?,
      onPocketSelected: (PocketItemModel?) -> Unit,
      onCreatePocketClicked: () -> Unit,
      onBack: () -> Unit,
  ): SelectPocketComponent =
      DefaultSelectPocketComponent(
          cContext = cContext,
          pocketRepository = pocketRepository,
          initialSelectedPocketId = initialSelectedPocketId,
          onPocketSelectedCallback = onPocketSelected,
          onCreatePocketClicked = onCreatePocketClicked,
          onBack = onBack,
      )
}

@Fake
class FakeSelectPocketComponent(
    initialState: SelectPocketState =
        SelectPocketState(pockets = FakePocketItemModelFactory.createList()),
    private val onPocketSelectedCallback: (PocketItemModel?) -> Unit = {},
    private val onCreatePocketClickedCallback: () -> Unit = {},
    private val onBackCallback: () -> Unit = {},
) : SelectPocketComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<SelectPocketState> = _state

  override fun onSearchQueryChanged(query: String) {
    _state.update {
      val trimmed = query.trim().lowercase()
      it.copy(
          searchQuery = query,
          filteredPockets =
              if (query.isBlank()) it.pockets
              else
                  it.pockets.filter { p ->
                    p.name.lowercase().contains(trimmed) ||
                        p.categoryTag.lowercase().contains(trimmed)
                  },
      )
    }
  }

  override fun onPocketSelected(pocket: PocketItemModel) {
    _state.update { it.copy(selectedPocketId = pocket.id) }
    onPocketSelectedCallback(pocket)
  }

  override fun onNoPocketSelected() {
    _state.update { it.copy(selectedPocketId = null) }
    onPocketSelectedCallback(null)
  }

  override fun onCreatePocketClicked() {
    onCreatePocketClickedCallback()
  }

  override fun onBackClicked() {
    onBackCallback()
  }
}

@Fake
class FakeSelectPocketComponentFactory : SelectPocketComponentFactory {
  override fun create(
      cContext: CContext,
      initialSelectedPocketId: Long?,
      onPocketSelected: (PocketItemModel?) -> Unit,
      onCreatePocketClicked: () -> Unit,
      onBack: () -> Unit,
  ): SelectPocketComponent =
      FakeSelectPocketComponent(
          initialState =
              SelectPocketState(
                  pockets = FakePocketItemModelFactory.createList(),
                  selectedPocketId = initialSelectedPocketId,
              ),
          onPocketSelectedCallback = onPocketSelected,
          onCreatePocketClickedCallback = onCreatePocketClicked,
          onBackCallback = onBack,
      )
}
