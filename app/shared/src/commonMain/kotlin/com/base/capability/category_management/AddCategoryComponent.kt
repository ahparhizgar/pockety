package com.base.capability.category_management

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.base.Fake
import com.base.component.CContext
import com.base.component.componentScope
import com.base.db.CategoryRepository
import com.base.usermessage.UiString
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class AddCategoryState(
    val nameText: String = "",
    val nameError: UiString? = null,
    val selectedColor: Long = 0xFFE57373,
    val selectedIcon: String = "restaurant",
    val isSaving: Boolean = false,
)

interface AddCategoryComponent {
  val state: Value<AddCategoryState>

  fun onNameChanged(name: String)

  fun onColorSelected(color: Long)

  fun onIconSelected(icon: String)

  fun onSaveClicked(): Job

  fun onBackClicked()
}

interface AddCategoryComponentFactory {
  fun create(
      cContext: CContext,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddCategoryComponent
}

class DefaultAddCategoryComponent(
    cContext: CContext,
    private val categoryRepository: CategoryRepository,
    private val onCompleted: () -> Unit,
    private val onCancelled: () -> Unit,
) : AddCategoryComponent, CContext by cContext {
  private val scope = componentScope()
  private val _state = MutableValue(AddCategoryState())
  override val state: Value<AddCategoryState> = _state

  override fun onNameChanged(name: String) {
    TODO("Not implemented yet - TDD Red Phase")
  }

  override fun onColorSelected(color: Long) {
    TODO("Not implemented yet - TDD Red Phase")
  }

  override fun onIconSelected(icon: String) {
    TODO("Not implemented yet - TDD Red Phase")
  }

  override fun onSaveClicked(): Job = scope.launch { TODO("Not implemented yet - TDD Red Phase") }

  override fun onBackClicked() {
    onCancelled()
  }
}

class DefaultAddCategoryComponentFactory(
    private val categoryRepository: CategoryRepository,
) : AddCategoryComponentFactory {
  override fun create(
      cContext: CContext,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddCategoryComponent =
      DefaultAddCategoryComponent(
          cContext = cContext,
          categoryRepository = categoryRepository,
          onCompleted = onCompleted,
          onCancelled = onCancelled,
      )
}

@Fake
class FakeAddCategoryComponent(
    initialState: AddCategoryState = AddCategoryState(),
) : AddCategoryComponent {
  private val _state = MutableValue(initialState)
  override val state: Value<AddCategoryState> = _state

  override fun onNameChanged(name: String) {
    _state.update { it.copy(nameText = name) }
  }

  override fun onColorSelected(color: Long) {
    _state.update { it.copy(selectedColor = color) }
  }

  override fun onIconSelected(icon: String) {
    _state.update { it.copy(selectedIcon = icon) }
  }

  override fun onSaveClicked(): Job = Job().apply { complete() }

  override fun onBackClicked() {}
}

@Fake
class FakeAddCategoryComponentFactory : AddCategoryComponentFactory {
  override fun create(
      cContext: CContext,
      onCompleted: () -> Unit,
      onCancelled: () -> Unit,
  ): AddCategoryComponent = FakeAddCategoryComponent()
}
