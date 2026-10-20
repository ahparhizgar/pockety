package com.base.capability.pocket_management

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.db.PocketRepository
import com.base.usermessage.UiString
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.error_empty_pocket_name

data class PocketSettingsState(
    val pocketId: Long? = null,
    val nameText: String = "",
    val nameError: UiString? = null,
    val selectedColor: Long = 0xFF316342,
    val selectedIcon: String = "coffee",
    val selectedCategory: CategoryEntity? = null,
    val balanceAmount: Long = 0,
    val topUpAmountText: String = "",
    val isSaving: Boolean = false,
)

interface PocketSettingsComponent {
  val state: Value<PocketSettingsState>

  fun onNameChanged(name: String)

  fun onColorSelected(color: Long)

  fun onIconSelected(icon: String)

  fun onCategorySelected(category: CategoryEntity?)

  fun onCategoryChanged(categoryId: Long?)

  fun onCategoryCleared()

  fun onTopUpAmountChanged(amount: String)

  fun onSelectCategoryClicked()

  fun onSaveClicked(): Job

  fun onDeleteClicked(): Job

  fun onBackClicked()
}

interface PocketSettingsComponentFactory {
  @Suppress("LongParameterList")
  fun create(
      cContext: CContext,
      pocketId: Long? = null,
      onSelectCategory: () -> Unit = {},
      onCompleted: (Long) -> Unit = {},
      onCancelled: () -> Unit = {},
      onDeleted: () -> Unit = {},
  ): PocketSettingsComponent
}

@Suppress("LongParameterList")
class DefaultPocketSettingsComponent(
    cContext: CContext,
    private val pocketRepository: PocketRepository,
    private val categoryRepository: CategoryRepository,
    private val pocketId: Long? = null,
    private val onSelectCategory: () -> Unit = {},
    private val onCompleted: (Long) -> Unit = {},
    private val onCancelled: () -> Unit = {},
    private val onDeleted: () -> Unit = {},
) : PocketSettingsComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(PocketSettingsState(pocketId = pocketId))
  override val state: Value<PocketSettingsState> = _state

  init {
    if (pocketId != null) {
      scope.launch {
        val pocket = pocketRepository.getPocketById(pocketId)
        if (pocket != null) {
          val category =
              pocket.categoryId?.let { catId ->
                categoryRepository.observeCategories().first().firstOrNull { it.id == catId }
              }
          _state.update {
            it.copy(
                pocketId = pocket.id,
                nameText = pocket.name,
                selectedColor = pocket.color,
                selectedIcon = pocket.icon,
                selectedCategory = category,
            )
          }
        }
      }
    }
  }

  override fun onNameChanged(name: String) {
    _state.update {
      it.copy(
          nameText = name,
          nameError = null,
      )
    }
  }

  override fun onColorSelected(color: Long) {
    _state.update { it.copy(selectedColor = color) }
  }

  override fun onIconSelected(icon: String) {
    _state.update { it.copy(selectedIcon = icon) }
  }

  override fun onCategorySelected(category: CategoryEntity?) {
    _state.update { it.copy(selectedCategory = category) }
  }

  override fun onCategoryChanged(categoryId: Long?) {
    if (categoryId == null) {
      onCategoryCleared()
    } else {
      scope.launch {
        val category =
            categoryRepository.observeCategories().first().firstOrNull { it.id == categoryId }
        _state.update { it.copy(selectedCategory = category) }
      }
    }
  }

  override fun onCategoryCleared() {
    _state.update { it.copy(selectedCategory = null) }
  }

  override fun onTopUpAmountChanged(amount: String) {
    _state.update { it.copy(topUpAmountText = amount) }
  }

  override fun onSelectCategoryClicked() {
    onSelectCategory()
  }

  override fun onSaveClicked(): Job = scope.launch {
    val currentState = _state.value
    val trimmedName = currentState.nameText.trim()
    if (trimmedName.isEmpty()) {
      _state.update { it.copy(nameError = UiString(Res.string.error_empty_pocket_name)) }
      return@launch
    }

    _state.update { it.copy(isSaving = true) }
    try {
      val categoryTag = currentState.selectedCategory?.name ?: "General"
      val existingId = currentState.pocketId
      if (existingId != null) {
        pocketRepository.updatePocket(
            id = existingId,
            name = trimmedName,
            categoryTag = categoryTag,
            icon = currentState.selectedIcon,
            color = currentState.selectedColor,
            categoryId = currentState.selectedCategory?.id,
        )
        onCompleted(existingId)
      } else {
        val newId =
            pocketRepository.addPocket(
                name = trimmedName,
                categoryTag = categoryTag,
                budgetAmount = currentState.balanceAmount,
                icon = currentState.selectedIcon,
                color = currentState.selectedColor,
                categoryId = currentState.selectedCategory?.id,
            )
        onCompleted(newId)
      }
    } finally {
      _state.update { it.copy(isSaving = false) }
    }
  }

  override fun onDeleteClicked(): Job = scope.launch {
    val id = pocketId ?: return@launch
    pocketRepository.deletePocket(id)
    onDeleted()
  }

  override fun onBackClicked() {
    onCancelled()
  }
}

class DefaultPocketSettingsComponentFactory(
    private val pocketRepository: PocketRepository,
    private val categoryRepository: CategoryRepository,
) : PocketSettingsComponentFactory {
  override fun create(
      cContext: CContext,
      pocketId: Long?,
      onSelectCategory: () -> Unit,
      onCompleted: (Long) -> Unit,
      onCancelled: () -> Unit,
      onDeleted: () -> Unit,
  ): PocketSettingsComponent =
      DefaultPocketSettingsComponent(
          cContext = cContext,
          pocketRepository = pocketRepository,
          categoryRepository = categoryRepository,
          pocketId = pocketId,
          onSelectCategory = onSelectCategory,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
          onDeleted = onDeleted,
      )
}

object FakePocketSettingsStateFactory {
  @Suppress("LongParameterList")
  fun create(
      pocketId: Long? = null,
      nameText: String = "",
      nameError: UiString? = null,
      selectedColor: Long = 0xFF316342,
      selectedIcon: String = "coffee",
      selectedCategory: CategoryEntity? = null,
      balanceAmount: Long = 0,
      topUpAmountText: String = "",
      isSaving: Boolean = false,
  ): PocketSettingsState =
      PocketSettingsState(
          pocketId = pocketId,
          nameText = nameText,
          nameError = nameError,
          selectedColor = selectedColor,
          selectedIcon = selectedIcon,
          selectedCategory = selectedCategory,
          balanceAmount = balanceAmount,
          topUpAmountText = topUpAmountText,
          isSaving = isSaving,
      )
}

@Fake
class FakePocketSettingsComponent(
    initialState: PocketSettingsState = FakePocketSettingsStateFactory.create(),
) : PocketSettingsComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<PocketSettingsState> = _state

  override fun onNameChanged(name: String) {
    _state.update { it.copy(nameText = name, nameError = null) }
  }

  override fun onColorSelected(color: Long) {
    _state.update { it.copy(selectedColor = color) }
  }

  override fun onIconSelected(icon: String) {
    _state.update { it.copy(selectedIcon = icon) }
  }

  override fun onCategorySelected(category: CategoryEntity?) {
    _state.update { it.copy(selectedCategory = category) }
  }

  override fun onCategoryChanged(categoryId: Long?) {}

  override fun onCategoryCleared() {
    _state.update { it.copy(selectedCategory = null) }
  }

  override fun onTopUpAmountChanged(amount: String) {
    _state.update { it.copy(topUpAmountText = amount) }
  }

  override fun onSelectCategoryClicked() {}

  override fun onSaveClicked(): Job = Job()

  override fun onDeleteClicked(): Job = Job()

  override fun onBackClicked() {}
}

@Fake
class FakePocketSettingsComponentFactory : PocketSettingsComponentFactory {
  override fun create(
      cContext: CContext,
      pocketId: Long?,
      onSelectCategory: () -> Unit,
      onCompleted: (Long) -> Unit,
      onCancelled: () -> Unit,
      onDeleted: () -> Unit,
  ): PocketSettingsComponent = FakePocketSettingsComponent()
}
