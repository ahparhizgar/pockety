package com.base.feature.category.management

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CategoryEntity
import com.base.db.CategoryRepository
import com.base.feature.category.add.AddCategoryComponent
import com.base.feature.category.add.AddCategoryComponentFactory
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

data class CategoryManagementState(
    val categories: List<CategoryEntity> = emptyList(),
    val filteredCategories: List<CategoryEntity> = emptyList(),
    val searchQuery: String = "",
    val categoryPendingDeletion: CategoryEntity? = null,
)

interface CategoryManagementComponent {
  val state: Value<CategoryManagementState>

  fun onSearchQueryChanged(query: String)

  fun onDeleteClicked(category: CategoryEntity)

  fun onDeleteConfirmed()

  fun onDeleteDismissed()

  fun onAddClicked()

  fun onBackClicked()
}

interface CategoryManagementFlowComponent {
  val stack: Value<ChildStack<CategoryManagementChildConfig, Child>>
  val backHandler: BackHandler

  fun onBack()

  sealed interface Child {
    class Browse(val component: CategoryManagementComponent) : Child

    class Add(val component: AddCategoryComponent) : Child
  }
}

@Serializable
sealed interface CategoryManagementChildConfig {
  @Serializable data object Browse : CategoryManagementChildConfig

  @Serializable data object Add : CategoryManagementChildConfig
}

interface CategoryManagementComponentFactory {
  fun create(cContext: CContext, onAdd: () -> Unit, onBack: () -> Unit): CategoryManagementComponent
}

interface CategoryManagementFlowComponentFactory {
  fun create(cContext: CContext, onBack: () -> Unit): CategoryManagementFlowComponent
}

class DefaultCategoryManagementComponent(
    cContext: CContext,
    private val categoryRepository: CategoryRepository,
    private val onAdd: () -> Unit,
    private val onBack: () -> Unit,
) : CategoryManagementComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(CategoryManagementState())
  override val state: Value<CategoryManagementState> = _state

  init {
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

  override fun onDeleteClicked(category: CategoryEntity) {
    _state.update { it.copy(categoryPendingDeletion = category) }
  }

  override fun onDeleteConfirmed() {
    val category = _state.value.categoryPendingDeletion ?: return
    _state.update { it.copy(categoryPendingDeletion = null) }
    scope.launch { categoryRepository.deleteCategory(category.id) }
  }

  override fun onDeleteDismissed() {
    _state.update { it.copy(categoryPendingDeletion = null) }
  }

  override fun onAddClicked() = onAdd()

  override fun onBackClicked() = onBack()
}

class DefaultCategoryManagementComponentFactory(
    private val categoryRepository: CategoryRepository,
) : CategoryManagementComponentFactory {
  override fun create(
      cContext: CContext,
      onAdd: () -> Unit,
      onBack: () -> Unit,
  ): CategoryManagementComponent =
      DefaultCategoryManagementComponent(
          cContext = cContext,
          categoryRepository = categoryRepository,
          onAdd = onAdd,
          onBack = onBack,
      )
}

class DefaultCategoryManagementFlowComponent(
    cContext: CContext,
    private val browseFactory: CategoryManagementComponentFactory,
    private val addFactory: AddCategoryComponentFactory,
    private val onBackFromFlow: () -> Unit,
) : CategoryManagementFlowComponent, CContext by cContext {
  private val flowNavigation = StackNavigation<CategoryManagementChildConfig>()
  override val backHandler: BackHandler = cContext.backHandler

  override fun onBack() {
    if (stack.value.backStack.isNotEmpty()) flowNavigation.pop() else onBackFromFlow()
  }

  override val stack:
      Value<ChildStack<CategoryManagementChildConfig, CategoryManagementFlowComponent.Child>> =
      childStack(
          source = flowNavigation,
          serializer = null,
          initialConfiguration = CategoryManagementChildConfig.Browse,
          handleBackButton = true,
          childFactory = ::createChild,
      )

  private fun createChild(
      config: CategoryManagementChildConfig,
      context: CContext,
  ): CategoryManagementFlowComponent.Child =
      when (config) {
        CategoryManagementChildConfig.Browse ->
            CategoryManagementFlowComponent.Child.Browse(
                browseFactory.create(
                    cContext = context,
                    onAdd = { flowNavigation.pushNew(CategoryManagementChildConfig.Add) },
                    onBack = onBackFromFlow,
                )
            )
        CategoryManagementChildConfig.Add ->
            CategoryManagementFlowComponent.Child.Add(
                addFactory.create(
                    cContext = context,
                    onCompleted = { flowNavigation.pop() },
                    onCancelled = { flowNavigation.pop() },
                )
            )
      }
}

class DefaultCategoryManagementFlowComponentFactory(
    private val browseFactory: CategoryManagementComponentFactory,
    private val addFactory: AddCategoryComponentFactory,
) : CategoryManagementFlowComponentFactory {
  override fun create(cContext: CContext, onBack: () -> Unit): CategoryManagementFlowComponent =
      DefaultCategoryManagementFlowComponent(cContext, browseFactory, addFactory, onBack)
}

@Fake
class FakeCategoryManagementComponent(
    override val state: Value<CategoryManagementState> = MutableValue(CategoryManagementState()),
) : CategoryManagementComponent {
  override fun onSearchQueryChanged(query: String) {}

  override fun onDeleteClicked(category: CategoryEntity) {}

  override fun onDeleteConfirmed() {}

  override fun onDeleteDismissed() {}

  override fun onAddClicked() {}

  override fun onBackClicked() {}
}

@Fake
class FakeCategoryManagementFlowComponent : CategoryManagementFlowComponent {
  override val backHandler: BackHandler = BackDispatcher()
  override val stack:
      Value<ChildStack<CategoryManagementChildConfig, CategoryManagementFlowComponent.Child>> =
      MutableValue(
          ChildStack(
              active =
                  Child.Created(
                      CategoryManagementChildConfig.Browse,
                      CategoryManagementFlowComponent.Child.Browse(
                          FakeCategoryManagementComponent()
                      ),
                  ),
              backStack = emptyList(),
          )
      )

  override fun onBack() {}
}
