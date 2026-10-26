package com.base.capability.pocket_management

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.FakePocketItemModelFactory
import com.base.db.PocketItemModel
import com.base.db.PocketRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class PocketsState(
    val pockets: List<PocketItemModel> = emptyList(),
    val isLoading: Boolean = false,
)

interface PocketsComponent {
  val state: Value<PocketsState>

  fun onPocketClick(pocketId: Long)

  fun onAddPocketClick()
}

interface PocketsComponentFactory {
  fun create(
      cContext: CContext,
      onPocketClick: (Long) -> Unit = {},
      onAddPocketClick: () -> Unit = {},
  ): PocketsComponent
}

class DefaultPocketsComponent(
    cContext: CContext,
    private val pocketRepository: PocketRepository,
    private val onPocketClick: (Long) -> Unit = {},
    private val onAddPocketClick: () -> Unit = {},
) : PocketsComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(PocketsState(isLoading = true))
  override val state: Value<PocketsState> = _state

  init {
    observePockets()
  }

  private fun observePockets(): Job =
      pocketRepository
          .observePockets()
          .onEach { items ->
            _state.update {
              it.copy(
                  pockets = items,
                  isLoading = false,
              )
            }
          }
          .launchIn(scope)

  override fun onPocketClick(pocketId: Long) {
    onPocketClick.invoke(pocketId)
  }

  override fun onAddPocketClick() {
    onAddPocketClick.invoke()
  }
}

class DefaultPocketsComponentFactory(
    private val pocketRepository: PocketRepository,
) : PocketsComponentFactory {
  override fun create(
      cContext: CContext,
      onPocketClick: (Long) -> Unit,
      onAddPocketClick: () -> Unit,
  ): PocketsComponent =
      DefaultPocketsComponent(
          cContext = cContext,
          pocketRepository = pocketRepository,
          onPocketClick = onPocketClick,
          onAddPocketClick = onAddPocketClick,
      )
}

@Fake
class FakePocketsComponent(
    initialState: PocketsState = PocketsState(pockets = FakePocketItemModelFactory.createList()),
) : PocketsComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<PocketsState> = _state

  override fun onPocketClick(pocketId: Long) {}

  override fun onAddPocketClick() {}
}

@Fake
class FakePocketsComponentFactory : PocketsComponentFactory {
  override fun create(
      cContext: CContext,
      onPocketClick: (Long) -> Unit,
      onAddPocketClick: () -> Unit,
  ): PocketsComponent = FakePocketsComponent()
}
