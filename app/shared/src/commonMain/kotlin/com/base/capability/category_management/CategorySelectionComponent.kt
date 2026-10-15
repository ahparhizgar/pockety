package com.base.capability.category_management

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.db.FakeCategoryFactory
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class CategorySelectionState(
    val categories: List<CategoryEntity> = emptyList(),
    val filteredCategories: List<CategoryEntity> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = false,
)

interface CategorySelectionComponent {
  val state: Value<CategorySelectionState>

  fun onSearchQueryChanged(query: String)

  fun onCategorySelected(category: CategoryEntity)

  fun onUncategorizedSelected()

  fun onCreateCategoryClicked()

  fun onBackClicked()
}

interface CategorySelectionComponentFactory {
  fun create(
      cContext: CContext,
      initialSelectedCategoryId: Long? = null,
      onCategorySelected: (CategoryEntity?) -> Unit,
      onCreateCategoryClicked: () -> Unit,
      onBack: () -> Unit,
  ): CategorySelectionComponent
}

class DefaultCategorySelectionComponent(
    cContext: CContext,
    private val categoryRepository: CategoryRepository,
    private val initialSelectedCategoryId: Long? = null,
    private val onCategorySelectedCallback: (CategoryEntity?) -> Unit,
    private val onCreateCategoryClicked: () -> Unit,
    private val onBack: () -> Unit,
) : CategorySelectionComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state =
      MutableValue(CategorySelectionState(selectedCategoryId = initialSelectedCategoryId))
  override val state: Value<CategorySelectionState> = _state

  init {
    observeCategories()
  }

  private fun observeCategories() {
    categoryRepository
        .observeCategories()
        .onEach { categories ->
          _state.update {
            it.copy(
                categories = categories,
                filteredCategories = filterCategories(categories, it.searchQuery),
            )
          }
        }
        .launchIn(scope)
  }

  private fun filterCategories(
      categories: List<CategoryEntity>,
      query: String,
  ): List<CategoryEntity> {
    if (query.isBlank()) return categories
    val trimmed = query.trim().lowercase()
    return categories.filter { it.name.lowercase().contains(trimmed) }
  }

  override fun onSearchQueryChanged(query: String) {
    _state.update {
      it.copy(
          searchQuery = query,
          filteredCategories = filterCategories(it.categories, query),
      )
    }
  }

  override fun onCategorySelected(category: CategoryEntity) {
    _state.update { it.copy(selectedCategoryId = category.id) }
    onCategorySelectedCallback(category)
  }

  override fun onUncategorizedSelected() {
    _state.update { it.copy(selectedCategoryId = null) }
    onCategorySelectedCallback(null)
  }

  override fun onCreateCategoryClicked() {
    onCreateCategoryClicked.invoke()
  }

  override fun onBackClicked() {
    onBack.invoke()
  }
}

class DefaultCategorySelectionComponentFactory(
    private val categoryRepository: CategoryRepository,
) : CategorySelectionComponentFactory {
  override fun create(
      cContext: CContext,
      initialSelectedCategoryId: Long?,
      onCategorySelected: (CategoryEntity?) -> Unit,
      onCreateCategoryClicked: () -> Unit,
      onBack: () -> Unit,
  ): CategorySelectionComponent =
      DefaultCategorySelectionComponent(
          cContext = cContext,
          categoryRepository = categoryRepository,
          initialSelectedCategoryId = initialSelectedCategoryId,
          onCategorySelectedCallback = onCategorySelected,
          onCreateCategoryClicked = onCreateCategoryClicked,
          onBack = onBack,
      )
}

@Fake
class FakeCategorySelectionComponent(
    initialState: CategorySelectionState =
        CategorySelectionState(categories = FakeCategoryFactory.createList(3)),
) : CategorySelectionComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<CategorySelectionState> = _state

  override fun onSearchQueryChanged(query: String) {
    _state.update { it.copy(searchQuery = query) }
  }

  override fun onCategorySelected(category: CategoryEntity) {
    _state.update { it.copy(selectedCategoryId = category.id) }
  }

  override fun onUncategorizedSelected() {
    _state.update { it.copy(selectedCategoryId = null) }
  }

  override fun onCreateCategoryClicked() {}

  override fun onBackClicked() {}
}

@Fake
class FakeCategorySelectionComponentFactory : CategorySelectionComponentFactory {
  override fun create(
      cContext: CContext,
      initialSelectedCategoryId: Long?,
      onCategorySelected: (CategoryEntity?) -> Unit,
      onCreateCategoryClicked: () -> Unit,
      onBack: () -> Unit,
  ): CategorySelectionComponent = FakeCategorySelectionComponent()
}
