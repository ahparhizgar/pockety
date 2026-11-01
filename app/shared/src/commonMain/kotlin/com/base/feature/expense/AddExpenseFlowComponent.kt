package com.base.feature.expense

import com.arkivanov.decompose.Child
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.backhandler.BackHandler
import com.base.Fake
import com.base.component.CContext
import com.base.feature.category.add.AddCategoryComponent
import com.base.feature.category.add.AddCategoryComponentFactory
import com.base.feature.category.selection.CategorySelectionComponent
import com.base.feature.category.selection.CategorySelectionComponentFactory
import com.base.feature.expense.add.AddExpenseComponent
import com.base.feature.expense.add.AddExpenseComponentFactory
import com.base.feature.expense.add.FakeAddExpenseComponent
import com.base.feature.pocket.selection.SelectPocketComponent
import com.base.feature.pocket.selection.SelectPocketComponentFactory
import com.base.feature.root.RootFlowDestination
import kotlinx.serialization.Serializable

@Serializable
sealed interface AddExpenseFlowChildConfig {
  @Serializable
  data class AddExpense(
      val initialAmount: String = "",
      val candidateExpenseId: Long? = null,
      val expenseId: Long? = null,
  ) : AddExpenseFlowChildConfig

  @Serializable
  data class CategorySelection(
      val currentCategoryId: Long? = null,
  ) : AddExpenseFlowChildConfig

  @Serializable
  data class PocketSelection(
      val currentPocketId: Long? = null,
  ) : AddExpenseFlowChildConfig

  @Serializable data object AddCategory : AddExpenseFlowChildConfig
}

interface AddExpenseFlowComponent {
  val stack: Value<ChildStack<AddExpenseFlowChildConfig, Child>>
  val backHandler: BackHandler

  fun onBack()

  sealed interface Child {
    class AddExpense(val component: AddExpenseComponent) : Child

    class CategorySelection(val component: CategorySelectionComponent) : Child

    class AddCategory(val component: AddCategoryComponent) : Child

    class PocketSelection(val component: SelectPocketComponent) : Child
  }
}

interface AddExpenseFlowComponentFactory {
  fun create(
      cContext: CContext,
      config: RootFlowDestination.AddExpenseFlow = RootFlowDestination.AddExpenseFlow(),
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseFlowComponent
}

data class AddExpenseFlowChildFactories(
    val addExpenseComponentFactory: AddExpenseComponentFactory,
    val categorySelectionComponentFactory: CategorySelectionComponentFactory,
    val addCategoryComponentFactory: AddCategoryComponentFactory,
    val selectPocketComponentFactory: SelectPocketComponentFactory,
)

class DefaultAddExpenseFlowComponent(
    cContext: CContext,
    private val factories: AddExpenseFlowChildFactories,
    private val config: RootFlowDestination.AddExpenseFlow = RootFlowDestination.AddExpenseFlow(),
    private val onCompleted: () -> Unit,
    private val onCancelled: () -> Unit,
) : AddExpenseFlowComponent, CContext by cContext {
  private val flowNavigation = StackNavigation<AddExpenseFlowChildConfig>()
  override val backHandler: BackHandler = cContext.backHandler

  override fun onBack() {
    flowNavigation.pop()
  }

  override val stack: Value<ChildStack<AddExpenseFlowChildConfig, AddExpenseFlowComponent.Child>> =
      childStack(
          source = flowNavigation,
          serializer = null,
          initialConfiguration =
              AddExpenseFlowChildConfig.AddExpense(
                  initialAmount = config.initialAmount,
                  candidateExpenseId = config.candidateExpenseId,
                  expenseId = config.expenseId,
              ),
          handleBackButton = true,
          childFactory = ::createChild,
      )

  private var addExpenseComponent: AddExpenseComponent? = null

  private fun createChild(
      childConfig: AddExpenseFlowChildConfig,
      context: CContext,
  ): AddExpenseFlowComponent.Child =
      when (childConfig) {
        is AddExpenseFlowChildConfig.AddExpense -> createAddExpenseChild(childConfig, context)
        is AddExpenseFlowChildConfig.PocketSelection ->
            createPocketSelectionChild(childConfig, context)
        is AddExpenseFlowChildConfig.CategorySelection ->
            createCategorySelectionChild(childConfig, context)
        is AddExpenseFlowChildConfig.AddCategory -> createAddCategoryChild(context)
      }

  private fun createAddExpenseChild(
      childConfig: AddExpenseFlowChildConfig.AddExpense,
      context: CContext,
  ): AddExpenseFlowComponent.Child {
    val expenseComp =
        factories.addExpenseComponentFactory.create(
            cContext = context,
            config =
                AddExpenseComponent.Config(
                    initialAmount = childConfig.initialAmount,
                    candidateExpenseId = childConfig.candidateExpenseId,
                    expenseId = childConfig.expenseId,
                ),
            onSelectCategory = {
              val currentCatId = addExpenseComponent?.state?.value?.selectedCategory?.id
              flowNavigation.pushNew(
                  AddExpenseFlowChildConfig.CategorySelection(currentCategoryId = currentCatId)
              )
            },
            onSelectPocket = {
              val currentPocketId = addExpenseComponent?.state?.value?.selectedPocket?.id
              flowNavigation.pushNew(
                  AddExpenseFlowChildConfig.PocketSelection(currentPocketId = currentPocketId)
              )
            },
            onCompleted = onCompleted,
            onCancelled = onCancelled,
        )
    addExpenseComponent = expenseComp
    return AddExpenseFlowComponent.Child.AddExpense(expenseComp)
  }

  private fun createPocketSelectionChild(
      childConfig: AddExpenseFlowChildConfig.PocketSelection,
      context: CContext,
  ): AddExpenseFlowComponent.Child =
      AddExpenseFlowComponent.Child.PocketSelection(
          factories.selectPocketComponentFactory.create(
              cContext = context,
              initialSelectedPocketId = childConfig.currentPocketId,
              onPocketSelected = { pocket ->
                addExpenseComponent?.onPocketSelected(pocket)
                flowNavigation.pop()
              },
              onCreatePocketClicked = { flowNavigation.pop() },
              onBack = { flowNavigation.pop() },
          )
      )

  private fun createCategorySelectionChild(
      childConfig: AddExpenseFlowChildConfig.CategorySelection,
      context: CContext,
  ): AddExpenseFlowComponent.Child =
      AddExpenseFlowComponent.Child.CategorySelection(
          factories.categorySelectionComponentFactory.create(
              cContext = context,
              initialSelectedCategoryId = childConfig.currentCategoryId,
              onCategorySelected = { category ->
                addExpenseComponent?.onCategorySelected(category)
                flowNavigation.pop()
              },
              onCreateCategoryClicked = {
                flowNavigation.pushNew(AddExpenseFlowChildConfig.AddCategory)
              },
              onBack = { flowNavigation.pop() },
          )
      )

  private fun createAddCategoryChild(
      context: CContext,
  ): AddExpenseFlowComponent.Child =
      AddExpenseFlowComponent.Child.AddCategory(
          factories.addCategoryComponentFactory.create(
              cContext = context,
              onCompleted = { newCategory ->
                addExpenseComponent?.onCategorySelected(newCategory)
                flowNavigation.pop()
                flowNavigation.pop()
              },
              onCancelled = { flowNavigation.pop() },
          )
      )
}

class DefaultAddExpenseFlowComponentFactory(
    private val addExpenseComponentFactory: AddExpenseComponentFactory,
    private val categorySelectionComponentFactory: CategorySelectionComponentFactory,
    private val addCategoryComponentFactory: AddCategoryComponentFactory,
    private val selectPocketComponentFactory: SelectPocketComponentFactory,
) : AddExpenseFlowComponentFactory {
  override fun create(
      cContext: CContext,
      config: RootFlowDestination.AddExpenseFlow,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseFlowComponent =
      DefaultAddExpenseFlowComponent(
          cContext = cContext,
          factories =
              AddExpenseFlowChildFactories(
                  addExpenseComponentFactory = addExpenseComponentFactory,
                  categorySelectionComponentFactory = categorySelectionComponentFactory,
                  addCategoryComponentFactory = addCategoryComponentFactory,
                  selectPocketComponentFactory = selectPocketComponentFactory,
              ),
          config = config,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
      )
}

@Fake
class FakeAddExpenseFlowComponent(
    override val stack:
        Value<ChildStack<AddExpenseFlowChildConfig, AddExpenseFlowComponent.Child>> =
        MutableValue(
            ChildStack(
                active =
                    Child.Created(
                        AddExpenseFlowChildConfig.AddExpense(),
                        AddExpenseFlowComponent.Child.AddExpense(FakeAddExpenseComponent()),
                    ),
                backStack = emptyList(),
            )
        )
) : AddExpenseFlowComponent {
  override val backHandler: BackHandler = BackDispatcher()

  override fun onBack() {}
}

@Fake
class FakeAddExpenseFlowComponentFactory : AddExpenseFlowComponentFactory {
  override fun create(
      cContext: CContext,
      config: RootFlowDestination.AddExpenseFlow,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseFlowComponent = FakeAddExpenseFlowComponent()
}
