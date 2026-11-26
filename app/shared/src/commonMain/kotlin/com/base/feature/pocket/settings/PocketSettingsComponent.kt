package com.base.feature.pocket.settings

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
    val categories: List<CategoryEntity> = emptyList(),
    val manualMode: Boolean = false,
    val showManualConfirm: Boolean = false,
    val linkedCategoryIds: Set<Long> = emptySet(),
    val balanceAmount: Long = 0,
    val topUpAmountText: String = "",
    val monthlyTopUpAmount: Long = 0L,
    val autoTopUpEnabled: Boolean = false,
    val isSaving: Boolean = false,
)

@Suppress("TooManyFunctions")
interface PocketSettingsComponent {
  val state: Value<PocketSettingsState>

  fun onNameChanged(name: String)

  fun onColorSelected(color: Long)

  fun onIconSelected(icon: String)

  fun onCategorySelected(category: CategoryEntity?)

  fun onCategoryChanged(categoryId: Long?)

  fun onCategoryCleared()

  fun onManualSetupClicked()

  fun onManualConfirmConfirmed()

  fun onManualConfirmDismissed()

  fun onLinkedCategoryToggled(category: CategoryEntity)

  fun onTopUpAmountChanged(amount: String)

  fun onMonthlyTopUpAmountChanged(amount: Long)

  fun onAutoTopUpEnabledChanged(enabled: Boolean)

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

@Suppress("LongParameterList", "TooManyFunctions")
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
    scope.launch {
      categoryRepository.observeCategories().collect { categories ->
        _state.update { it.copy(categories = categories) }
      }
    }
    if (pocketId != null) {
      scope.launch {
        val pocket = pocketRepository.getPocketById(pocketId)
        if (pocket != null) {
          val category =
              pocket.categoryId?.let { catId ->
                categoryRepository.observeCategories().first().firstOrNull { it.id == catId }
              }
          val linkedIds = pocketRepository.getPocketCategoryIds(pocketId).toSet()
          _state.update {
            it.copy(
                pocketId = pocket.id,
                nameText = pocket.name,
                selectedColor = pocket.color,
                selectedIcon = pocket.icon,
                selectedCategory = category,
                linkedCategoryIds = linkedIds.ifEmpty { listOfNotNull(pocket.categoryId).toSet() },
                monthlyTopUpAmount = pocket.monthlyTopUpAmount,
                autoTopUpEnabled = pocket.autoTopUpEnabled,
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
    _state.update { state ->
      // In the simple creation mode the pocket identity is live-linked to the
      // category: picking one fills name, icon and color, picking another
      // re-syncs all three. Edit mode and manual mode keep the identity
      // independent.
      val inherit = pocketId == null && !state.manualMode && category != null
      state.copy(
          selectedCategory = category,
          linkedCategoryIds = listOfNotNull(category?.id).toSet(),
          nameText = if (inherit) category.name else state.nameText,
          selectedIcon = if (inherit) category.icon else state.selectedIcon,
          selectedColor = if (inherit) category.color else state.selectedColor,
      )
    }
  }

  override fun onCategoryChanged(categoryId: Long?) {
    if (categoryId == null) {
      onCategoryCleared()
    } else {
      scope.launch {
        val category =
            categoryRepository.observeCategories().first().firstOrNull { it.id == categoryId }
        onCategorySelected(category)
      }
    }
  }

  override fun onCategoryCleared() {
    _state.update { it.copy(selectedCategory = null, linkedCategoryIds = emptySet()) }
  }

  override fun onManualSetupClicked() {
    _state.update { it.copy(showManualConfirm = true) }
  }

  override fun onManualConfirmConfirmed() {
    _state.update { it.copy(manualMode = true, showManualConfirm = false) }
  }

  override fun onManualConfirmDismissed() {
    _state.update { it.copy(showManualConfirm = false) }
  }

  override fun onLinkedCategoryToggled(category: CategoryEntity) {
    _state.update { state ->
      state.copy(
          linkedCategoryIds =
              if (category.id in state.linkedCategoryIds) {
                state.linkedCategoryIds - category.id
              } else {
                state.linkedCategoryIds + category.id
              },
      )
    }
  }

  override fun onTopUpAmountChanged(amount: String) {
    _state.update { it.copy(topUpAmountText = amount) }
  }

  override fun onMonthlyTopUpAmountChanged(amount: Long) {
    _state.update { it.copy(monthlyTopUpAmount = amount) }
  }

  override fun onAutoTopUpEnabledChanged(enabled: Boolean) {
    _state.update { it.copy(autoTopUpEnabled = enabled) }
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
      // Links are saved in category-list order so the first one is a stable
      // primary (it becomes pockets.categoryId and the card badge).
      val orderedLinks =
          currentState.categories
              .filter { it.id in currentState.linkedCategoryIds }
              .map { it.id }
              .ifEmpty { currentState.linkedCategoryIds.toList() }
      val primaryCategory =
          currentState.categories.firstOrNull { it.id == orderedLinks.firstOrNull() }
      val categoryTag = primaryCategory?.name ?: currentState.selectedCategory?.name ?: "General"
      val existingId = currentState.pocketId
      if (existingId != null) {
        pocketRepository.updatePocket(
            id = existingId,
            name = trimmedName,
            categoryTag = categoryTag,
            icon = currentState.selectedIcon,
            color = currentState.selectedColor,
            categoryIds = orderedLinks,
            monthlyTopUpAmount = currentState.monthlyTopUpAmount,
            autoTopUpEnabled = currentState.autoTopUpEnabled,
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
                categoryIds = orderedLinks,
                monthlyTopUpAmount = currentState.monthlyTopUpAmount,
                autoTopUpEnabled = currentState.autoTopUpEnabled,
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
    val currentState = _state.value
    if (pocketId == null && currentState.manualMode) {
      // Before saving, Back leaves manual mode and returns to the simple
      // setup: the category stays selected and the identity follows it
      // again, dropping any manual tweaks and extra links.
      _state.update { state ->
        val category = state.selectedCategory
        state.copy(
            manualMode = false,
            nameText = category?.name ?: state.nameText,
            selectedIcon = category?.icon ?: state.selectedIcon,
            selectedColor = category?.color ?: state.selectedColor,
            linkedCategoryIds = listOfNotNull(category?.id).toSet(),
        )
      }
    } else {
      onCancelled()
    }
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
      categories: List<CategoryEntity> = emptyList(),
      manualMode: Boolean = false,
      showManualConfirm: Boolean = false,
      linkedCategoryIds: Set<Long> = listOfNotNull(selectedCategory?.id).toSet(),
      balanceAmount: Long = 0,
      topUpAmountText: String = "",
      monthlyTopUpAmount: Long = 0L,
      autoTopUpEnabled: Boolean = false,
      isSaving: Boolean = false,
  ): PocketSettingsState =
      PocketSettingsState(
          pocketId = pocketId,
          nameText = nameText,
          nameError = nameError,
          selectedColor = selectedColor,
          selectedIcon = selectedIcon,
          selectedCategory = selectedCategory,
          categories = categories,
          manualMode = manualMode,
          showManualConfirm = showManualConfirm,
          linkedCategoryIds = linkedCategoryIds,
          balanceAmount = balanceAmount,
          topUpAmountText = topUpAmountText,
          monthlyTopUpAmount = monthlyTopUpAmount,
          autoTopUpEnabled = autoTopUpEnabled,
          isSaving = isSaving,
      )
}

@Fake
@Suppress("TooManyFunctions")
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
    _state.update { state ->
      val inherit = state.pocketId == null && !state.manualMode && category != null
      state.copy(
          selectedCategory = category,
          linkedCategoryIds = listOfNotNull(category?.id).toSet(),
          nameText = if (inherit) category.name else state.nameText,
          selectedIcon = if (inherit) category.icon else state.selectedIcon,
          selectedColor = if (inherit) category.color else state.selectedColor,
      )
    }
  }

  override fun onCategoryChanged(categoryId: Long?) {}

  override fun onCategoryCleared() {
    _state.update { it.copy(selectedCategory = null, linkedCategoryIds = emptySet()) }
  }

  override fun onManualSetupClicked() {
    _state.update { it.copy(showManualConfirm = true) }
  }

  override fun onManualConfirmConfirmed() {
    _state.update { it.copy(manualMode = true, showManualConfirm = false) }
  }

  override fun onManualConfirmDismissed() {
    _state.update { it.copy(showManualConfirm = false) }
  }

  override fun onLinkedCategoryToggled(category: CategoryEntity) {
    _state.update { state ->
      state.copy(
          linkedCategoryIds =
              if (category.id in state.linkedCategoryIds) {
                state.linkedCategoryIds - category.id
              } else {
                state.linkedCategoryIds + category.id
              },
      )
    }
  }

  override fun onTopUpAmountChanged(amount: String) {
    _state.update { it.copy(topUpAmountText = amount) }
  }

  override fun onMonthlyTopUpAmountChanged(amount: Long) {
    _state.update { it.copy(monthlyTopUpAmount = amount) }
  }

  override fun onAutoTopUpEnabledChanged(enabled: Boolean) {
    _state.update { it.copy(autoTopUpEnabled = enabled) }
  }

  override fun onSelectCategoryClicked() {}

  override fun onSaveClicked(): Job = Job()

  override fun onDeleteClicked(): Job = Job()

  override fun onBackClicked() {
    _state.update { state ->
      if (state.pocketId == null && state.manualMode) {
        val category = state.selectedCategory
        state.copy(
            manualMode = false,
            nameText = category?.name ?: state.nameText,
            selectedIcon = category?.icon ?: state.selectedIcon,
            selectedColor = category?.color ?: state.selectedColor,
            linkedCategoryIds = listOfNotNull(category?.id).toSet(),
        )
      } else {
        state
      }
    }
  }
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
