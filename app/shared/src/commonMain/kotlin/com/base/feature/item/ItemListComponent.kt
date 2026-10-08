package com.base.feature.item

import com.ahparhizgar.katch.ApiCallError
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.dto.FakeItemDtoFactory
import com.base.dto.ItemDto
import com.base.feature.root.TopLevelDestinationConfig
import com.base.remote.userMessage
import com.base.usermessage.UiString
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface ItemListComponent : TopLevelDestinationConfig {
  val state: Value<ItemListState>

  fun loadItems(): Job

  @Serializable data object Config : TopLevelDestinationConfig
}

interface ItemListComponentFactory {
  fun create(cContext: CContext): ItemListComponent
}

data class ItemListState(
    val items: List<ItemDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: UiString? = null,
)

class DefaultItemListComponent(
    context: CContext,
    private val itemApi: ItemApi,
) : ItemListComponent, CContext by context {
  private val scope = componentScope()
  private val _state = MutableValue(ItemListState(isLoading = true))
  override val state: Value<ItemListState> = _state

  init {
    loadItems()
  }

  override fun loadItems(): Job = scope.launch {
    try {
      _state.update { it.copy(isLoading = true, errorMessage = null) }
      val fetchedItems = itemApi.getItems()
      _state.update { it.copy(items = fetchedItems, isLoading = false) }
    } catch (e: ApiCallError) {
      _state.update {
        it.copy(
            isLoading = false,
            errorMessage = e.userMessage,
        )
      }
    }
  }
}

class DefaultItemListComponentFactory(
    private val itemApi: ItemApi,
) : ItemListComponentFactory {
  override fun create(cContext: CContext): ItemListComponent =
      DefaultItemListComponent(cContext, itemApi)
}

@Fake
class FakeItemListComponent(
    initialState: ItemListState = ItemListState(items = FakeItemDtoFactory.createList(3)),
) : ItemListComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<ItemListState> = _state

  override fun loadItems(): Job = Job().apply { complete() }
}

@Fake
class FakeItemListComponentFactory : ItemListComponentFactory {
  override fun create(cContext: CContext): ItemListComponent = FakeItemListComponent()
}
