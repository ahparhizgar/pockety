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
)

@Dao
interface CategoryDao {
  @Transaction
  @Query("SELECT * FROM categories ORDER BY createdAt DESC")
  fun observeCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity): Long

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

  suspend fun deleteCategory(id: Long)
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
        )
    return categoryDao.insertCategory(category)
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
        )
    categoriesFlow.update { listOf(newCategory) + it }
    return id
  }

  override suspend fun deleteCategory(id: Long) {
    categoriesFlow.update { it.filterNot { cat -> cat.id == id } }
  }
}

object FakeCategoryFactory {
  fun create(
      id: Long = 1,
      name: String = "Dining",
      icon: String = "restaurant",
      color: Long = 0xFFE57373,
      createdAt: Instant = Clock.System.now(),
  ): CategoryEntity =
      CategoryEntity(
          id = id,
          name = name,
          icon = icon,
          color = color,
          createdAt = createdAt,
      )

  fun createList(count: Int = 3): List<CategoryEntity> =
      (1..count).map { index ->
        create(
            id = index.toLong(),
            name = "Category $index",
            icon = "category_$index",
            color = 0xFFE57373 + index,
        )
      }
}
