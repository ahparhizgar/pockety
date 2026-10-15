package com.base.capability.adding_expense

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
import com.base.capability.adding_expense.add.AddExpenseComponent
import com.base.capability.adding_expense.add.AddExpenseComponentFactory
import com.base.capability.adding_expense.add.FakeAddExpenseComponent
import com.base.capability.category_management.AddCategoryComponent
import com.base.capability.category_management.AddCategoryComponentFactory
import com.base.capability.category_management.CategorySelectionComponent
import com.base.capability.category_management.CategorySelectionComponentFactory
import com.base.capability.root_navigation.TopLevelDestinationConfig
import com.base.component.CContext
import kotlinx.serialization.Serializable

@Serializable
sealed interface AddExpenseFlowChildConfig {
  @Serializable
  data class AddExpense(
      val initialAmount: String = "",
      val candidateExpenseId: Long? = null,
  ) : AddExpenseFlowChildConfig

  @Serializable
  data class CategorySelection(
      val currentCategoryId: Long? = null,
  ) : AddExpenseFlowChildConfig

  @Serializable data object AddCategory : AddExpenseFlowChildConfig
}

interface AddExpenseFlowComponent : TopLevelDestinationConfig {
  val stack: Value<ChildStack<AddExpenseFlowChildConfig, Child>>
  val backHandler: BackHandler

  fun onBack()

  sealed interface Child {
    class AddExpense(val component: AddExpenseComponent) : Child

    class CategorySelection(val component: CategorySelectionComponent) : Child

    class AddCategory(val component: AddCategoryComponent) : Child
  }

  @Serializable
  data class Config(
      val initialAmount: String = "",
      val candidateExpenseId: Long? = null,
  ) : TopLevelDestinationConfig
}

interface AddExpenseFlowComponentFactory {
  fun create(
      cContext: CContext,
      config: AddExpenseFlowComponent.Config = AddExpenseFlowComponent.Config(),
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseFlowComponent
}

class DefaultAddExpenseFlowComponent(
    cContext: CContext,
    private val addExpenseComponentFactory: AddExpenseComponentFactory,
    private val categorySelectionComponentFactory: CategorySelectionComponentFactory,
    private val addCategoryComponentFactory: AddCategoryComponentFactory,
    private val config: AddExpenseFlowComponent.Config = AddExpenseFlowComponent.Config(),
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
              ),
          handleBackButton = true,
          childFactory = ::createChild,
      )

  private fun createChild(
      childConfig: AddExpenseFlowChildConfig,
      context: CContext,
  ): AddExpenseFlowComponent.Child {
    return when (childConfig) {
      is AddExpenseFlowChildConfig.AddExpense ->
          AddExpenseFlowComponent.Child.AddExpense(
              addExpenseComponentFactory.create(
                  cContext = context,
                  config =
                      AddExpenseComponent.Config(
                          initialAmount = childConfig.initialAmount,
                          candidateExpenseId = childConfig.candidateExpenseId,
                      ),
                  onCompleted = onCompleted,
                  onCancelled = onCancelled,
              )
          )

      is AddExpenseFlowChildConfig.CategorySelection ->
          AddExpenseFlowComponent.Child.CategorySelection(
              categorySelectionComponentFactory.create(
                  cContext = context,
                  initialSelectedCategoryId = childConfig.currentCategoryId,
                  onCategorySelected = { category -> flowNavigation.pop() },
                  onCreateCategoryClicked = {
                    flowNavigation.pushNew(AddExpenseFlowChildConfig.AddCategory)
                  },
                  onBack = { flowNavigation.pop() },
              )
          )

      is AddExpenseFlowChildConfig.AddCategory ->
          AddExpenseFlowComponent.Child.AddCategory(
              addCategoryComponentFactory.create(
                  cContext = context,
                  onCompleted = { flowNavigation.pop() },
                  onCancelled = { flowNavigation.pop() },
              )
          )
    }
  }
}

class DefaultAddExpenseFlowComponentFactory(
    private val addExpenseComponentFactory: AddExpenseComponentFactory,
    private val categorySelectionComponentFactory: CategorySelectionComponentFactory,
    private val addCategoryComponentFactory: AddCategoryComponentFactory,
) : AddExpenseFlowComponentFactory {
  override fun create(
      cContext: CContext,
      config: AddExpenseFlowComponent.Config,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseFlowComponent =
      DefaultAddExpenseFlowComponent(
          cContext = cContext,
          addExpenseComponentFactory = addExpenseComponentFactory,
          categorySelectionComponentFactory = categorySelectionComponentFactory,
          addCategoryComponentFactory = addCategoryComponentFactory,
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
      config: AddExpenseFlowComponent.Config,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddExpenseFlowComponent = FakeAddExpenseFlowComponent()
}
