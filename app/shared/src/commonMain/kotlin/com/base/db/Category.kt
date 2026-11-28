package com.base.db

import androidx.room3.Dao
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Transaction
import com.base.Fake
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val createdAt: Instant,
    val sortOrder: Int,
)

@Dao
interface CategoryDao {
  @Transaction
  @Query("SELECT * FROM categories ORDER BY sortOrder ASC, createdAt DESC")
  fun observeCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity): Long

  @Query("SELECT COALESCE(MAX(sortOrder), -1) FROM categories") suspend fun maxSortOrder(): Int

  @Query("SELECT COUNT(*) FROM categories") suspend fun countCategories(): Int

  @Query("UPDATE categories SET sortOrder = :sortOrder WHERE id = :id")
  suspend fun updateSortOrder(id: Long, sortOrder: Int)

  @Transaction
  suspend fun reorderCategories(orderedIds: List<Long>) {
    orderedIds.forEachIndexed { index, id -> updateSortOrder(id, index) }
  }

  @Query("UPDATE transactions SET categoryId = NULL WHERE categoryId = :id")
  suspend fun clearTransactionsCategory(id: Long)

  @Query("DELETE FROM categories WHERE id = :id") suspend fun deleteCategoryById(id: Long)

  @Transaction
  suspend fun deleteCategory(id: Long) {
    clearTransactionsCategory(id)
    deleteCategoryById(id)
  }
}

interface CategoryRepository {
  fun observeCategories(): Flow<List<CategoryEntity>>

  suspend fun addCategory(name: String, icon: String, color: Long): Long

  /**
   * Inserts the built-in default categories in their fixed order when the user
   * has no categories yet. Returns true when the defaults were inserted.
   */
  suspend fun ensureDefaultCategories(isPersian: Boolean): Boolean

  suspend fun deleteCategory(id: Long)

  suspend fun reorderCategories(orderedIds: List<Long>)
}

class DefaultCategoryRepository(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
  override fun observeCategories(): Flow<List<CategoryEntity>> = categoryDao.observeCategories()

  override suspend fun addCategory(name: String, icon: String, color: Long): Long {
    val category =
        CategoryEntity(
            name = name,
            icon = icon,
            color = color,
            createdAt = Clock.System.now(),
            sortOrder = categoryDao.maxSortOrder() + 1,
        )
    return categoryDao.insertCategory(category)
  }

  override suspend fun ensureDefaultCategories(isPersian: Boolean): Boolean {
    if (categoryDao.countCategories() > 0) return false
    defaultCategories.forEachIndexed { index, category ->
      categoryDao.insertCategory(
          CategoryEntity(
              name = if (isPersian) category.nameFa else category.nameEn,
              icon = category.icon,
              color = category.color,
              createdAt = Clock.System.now(),
              sortOrder = index,
          )
      )
    }
    return true
  }

  override suspend fun reorderCategories(orderedIds: List<Long>) {
    categoryDao.reorderCategories(orderedIds)
  }

  override suspend fun deleteCategory(id: Long) {
    categoryDao.deleteCategory(id)
  }
}

@Fake
class FakeCategoryRepository(
    initialCategories: List<CategoryEntity> = emptyList(),
) : CategoryRepository {
  private val categoriesFlow = MutableStateFlow(initialCategories)
  private var nextId = (initialCategories.maxOfOrNull { it.id } ?: 0L) + 1L

  override fun observeCategories(): Flow<List<CategoryEntity>> = categoriesFlow

  override suspend fun addCategory(name: String, icon: String, color: Long): Long {
    val id = nextId++
    val newCategory =
        CategoryEntity(
            id = id,
            name = name,
            icon = icon,
            color = color,
            createdAt = Clock.System.now(),
            sortOrder = (categoriesFlow.value.maxOfOrNull { it.sortOrder } ?: -1) + 1,
        )
    categoriesFlow.update { it + newCategory }
    return id
  }

  override suspend fun ensureDefaultCategories(isPersian: Boolean): Boolean {
    if (categoriesFlow.value.isNotEmpty()) return false
    val seeded =
        defaultCategories.mapIndexed { index, category ->
          CategoryEntity(
              id = nextId + index,
              name = if (isPersian) category.nameFa else category.nameEn,
              icon = category.icon,
              color = category.color,
              createdAt = Clock.System.now(),
              sortOrder = index,
          )
        }
    nextId += seeded.size
    categoriesFlow.update { it + seeded }
    return true
  }

  override suspend fun reorderCategories(orderedIds: List<Long>) {
    categoriesFlow.update { current ->
      val byId = current.associateBy { it.id }
      val reordered = orderedIds.mapNotNull { byId[it] }
      val rest = current.filterNot { it.id in orderedIds.toSet() }
      (reordered + rest).mapIndexed { index, category -> category.copy(sortOrder = index) }
    }
  }

  override suspend fun deleteCategory(id: Long) {
    categoriesFlow.update { it.filterNot { cat -> cat.id == id } }
  }
}

object FakeCategoryFactory {
  @Suppress("LongParameterList")
  fun create(
      id: Long = 1,
      name: String = "Dining",
      icon: String = "restaurant",
      color: Long = 0xFFE57373,
      createdAt: Instant = Clock.System.now(),
      sortOrder: Int = 0,
  ): CategoryEntity =
      CategoryEntity(
          id = id,
          name = name,
          icon = icon,
          color = color,
          createdAt = createdAt,
          sortOrder = sortOrder,
      )

  fun createList(count: Int = 3): List<CategoryEntity> =
      (1..count).map { index ->
        create(
            id = index.toLong(),
            name = "Category $index",
            icon = "category_$index",
            color = 0xFFE57373 + index,
            sortOrder = index - 1,
        )
      }
}
