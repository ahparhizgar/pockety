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
    private val onCategorySelected: (CategoryEntity?) -> Unit,
    private val onCreateCategoryClicked: () -> Unit,
    private val onBack: () -> Unit,
) : CategorySelectionComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state =
      MutableValue(CategorySelectionState(selectedCategoryId = initialSelectedCategoryId))
  override val state: Value<CategorySelectionState> = _state

  override fun onSearchQueryChanged(query: String) {
    TODO("Not implemented yet - TDD Red Phase")
  }

  override fun onCategorySelected(category: CategoryEntity) {
    TODO("Not implemented yet - TDD Red Phase")
  }

  override fun onUncategorizedSelected() {
    TODO("Not implemented yet - TDD Red Phase")
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
          onCategorySelected = onCategorySelected,
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
