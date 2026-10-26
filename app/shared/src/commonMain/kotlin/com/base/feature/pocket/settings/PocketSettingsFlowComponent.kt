package com.base.feature.pocket.settings

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
import com.base.feature.root.RootFlowDestination
import kotlinx.serialization.Serializable

@Serializable
sealed interface PocketSettingsFlowChildConfig {
  @Serializable
  data class PocketSettings(
      val pocketId: Long? = null,
  ) : PocketSettingsFlowChildConfig

  @Serializable
  data class CategorySelection(
      val currentCategoryId: Long? = null,
  ) : PocketSettingsFlowChildConfig

  @Serializable data object AddCategory : PocketSettingsFlowChildConfig
}

interface PocketSettingsFlowComponent {
  val stack: Value<ChildStack<PocketSettingsFlowChildConfig, Child>>
  val backHandler: BackHandler

  fun onBack()

  sealed interface Child {
    class PocketSettings(val component: PocketSettingsComponent) : Child

    class CategorySelection(val component: CategorySelectionComponent) : Child

    class AddCategory(val component: AddCategoryComponent) : Child
  }
}

interface PocketSettingsFlowComponentFactory {
  fun create(
      cContext: CContext,
      config: RootFlowDestination.PocketSettingsFlow = RootFlowDestination.PocketSettingsFlow(),
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
      onDeleted: () -> Unit,
  ): PocketSettingsFlowComponent
}

data class PocketSettingsFlowChildFactories(
    val pocketSettingsComponentFactory: PocketSettingsComponentFactory,
    val categorySelectionComponentFactory: CategorySelectionComponentFactory,
    val addCategoryComponentFactory: AddCategoryComponentFactory,
)

class DefaultPocketSettingsFlowComponent(
    cContext: CContext,
    private val factories: PocketSettingsFlowChildFactories,
    private val config: RootFlowDestination.PocketSettingsFlow =
        RootFlowDestination.PocketSettingsFlow(),
    private val onCompleted: () -> Unit,
    private val onCancelled: () -> Unit,
    private val onDeleted: () -> Unit,
) : PocketSettingsFlowComponent, CContext by cContext {
  private val flowNavigation = StackNavigation<PocketSettingsFlowChildConfig>()
  override val backHandler: BackHandler = cContext.backHandler

  override fun onBack() {
    flowNavigation.pop()
  }

  override val stack:
      Value<ChildStack<PocketSettingsFlowChildConfig, PocketSettingsFlowComponent.Child>> =
      childStack(
          source = flowNavigation,
          serializer = null,
          initialConfiguration =
              PocketSettingsFlowChildConfig.PocketSettings(
                  pocketId = config.pocketId,
              ),
          handleBackButton = true,
          childFactory = ::createChild,
      )

  private var pocketSettingsComponent: PocketSettingsComponent? = null

  private fun createChild(
      childConfig: PocketSettingsFlowChildConfig,
      context: CContext,
  ): PocketSettingsFlowComponent.Child {
    return when (childConfig) {
      is PocketSettingsFlowChildConfig.PocketSettings -> {
        val settingsComp =
            factories.pocketSettingsComponentFactory.create(
                cContext = context,
                pocketId = childConfig.pocketId,
                onSelectCategory = {
                  val currentCatId = pocketSettingsComponent?.state?.value?.selectedCategory?.id
                  flowNavigation.pushNew(
                      PocketSettingsFlowChildConfig.CategorySelection(
                          currentCategoryId = currentCatId
                      )
                  )
                },
                onCompleted = { onCompleted() },
                onCancelled = { onCancelled() },
                onDeleted = { onDeleted() },
            )
        pocketSettingsComponent = settingsComp
        PocketSettingsFlowComponent.Child.PocketSettings(settingsComp)
      }

      is PocketSettingsFlowChildConfig.CategorySelection ->
          PocketSettingsFlowComponent.Child.CategorySelection(
              factories.categorySelectionComponentFactory.create(
                  cContext = context,
                  initialSelectedCategoryId = childConfig.currentCategoryId,
                  onCategorySelected = { category ->
                    pocketSettingsComponent?.onCategorySelected(category)
                    flowNavigation.pop()
                  },
                  onCreateCategoryClicked = {
                    flowNavigation.pushNew(PocketSettingsFlowChildConfig.AddCategory)
                  },
                  onBack = { flowNavigation.pop() },
              )
          )

      is PocketSettingsFlowChildConfig.AddCategory ->
          PocketSettingsFlowComponent.Child.AddCategory(
              factories.addCategoryComponentFactory.create(
                  cContext = context,
                  onCompleted = { newCategory ->
                    pocketSettingsComponent?.onCategorySelected(newCategory)
                    flowNavigation.pop()
                    flowNavigation.pop()
                  },
                  onCancelled = { flowNavigation.pop() },
              )
          )
    }
  }
}

class DefaultPocketSettingsFlowComponentFactory(
    private val pocketSettingsComponentFactory: PocketSettingsComponentFactory,
    private val categorySelectionComponentFactory: CategorySelectionComponentFactory,
    private val addCategoryComponentFactory: AddCategoryComponentFactory,
) : PocketSettingsFlowComponentFactory {
  override fun create(
      cContext: CContext,
      config: RootFlowDestination.PocketSettingsFlow,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
      onDeleted: () -> Unit,
  ): PocketSettingsFlowComponent =
      DefaultPocketSettingsFlowComponent(
          cContext = cContext,
          factories =
              PocketSettingsFlowChildFactories(
                  pocketSettingsComponentFactory = pocketSettingsComponentFactory,
                  categorySelectionComponentFactory = categorySelectionComponentFactory,
                  addCategoryComponentFactory = addCategoryComponentFactory,
              ),
          config = config,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
          onDeleted = onDeleted,
      )
}

@Fake
class FakePocketSettingsFlowComponent(
    override val stack:
        Value<ChildStack<PocketSettingsFlowChildConfig, PocketSettingsFlowComponent.Child>> =
        MutableValue(
            ChildStack(
                active =
                    Child.Created(
                        PocketSettingsFlowChildConfig.PocketSettings(),
                        PocketSettingsFlowComponent.Child.PocketSettings(
                            FakePocketSettingsComponent()
                        ),
                    ),
                backStack = emptyList(),
            )
        )
) : PocketSettingsFlowComponent {
  override val backHandler: BackHandler = BackDispatcher()

  override fun onBack() {}
}

@Fake
class FakePocketSettingsFlowComponentFactory : PocketSettingsFlowComponentFactory {
  override fun create(
      cContext: CContext,
      config: RootFlowDestination.PocketSettingsFlow,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
      onDeleted: () -> Unit,
  ): PocketSettingsFlowComponent = FakePocketSettingsFlowComponent()
}
