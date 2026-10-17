package com.base.db

import androidx.room3.Dao
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
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

@Entity(
    tableName = "pockets",
    foreignKeys =
        [
            ForeignKey(
                entity = CategoryEntity::class,
                parentColumns = ["id"],
                childColumns = ["categoryId"],
                onDelete = ForeignKey.SET_NULL,
            ),
        ],
    indices = [Index("categoryId")],
)
data class PocketEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryTag: String,
    val budgetAmount: Long,
    val icon: String,
    val createdAt: Instant,
    val color: Long = 0xFF316342,
    val categoryId: Long? = null,
)

data class PocketWithSpent(
    val id: Long,
    val name: String,
    val categoryTag: String,
    val budgetAmount: Long,
    val icon: String,
    val createdAt: Instant,
    val color: Long,
    val categoryId: Long?,
    val spentAmount: Long,
)

data class PocketItemModel(
    val id: Long,
    val name: String,
    val categoryTag: String,
    val budgetAmount: Long,
    val remainingBalance: Long,
    val icon: String,
    val color: Long = 0xFF316342,
    val categoryId: Long? = null,
    val isOverBudget: Boolean,
    val warningMessage: String? = null,
)

@Dao
interface PocketDao {
  @Transaction
  @Query(
      """
      SELECT 
          p.id, 
          p.name, 
          p.categoryTag, 
          p.budgetAmount, 
          p.icon, 
          p.createdAt,
          p.color,
          p.categoryId,
          COALESCE(SUM(t.amount), 0) AS spentAmount
      FROM pockets p
      LEFT JOIN transactions t ON t.pocketId = p.id
      GROUP BY p.id
      ORDER BY p.createdAt DESC
      """
  )
  fun observePocketsWithSpent(): Flow<List<PocketWithSpent>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPocket(pocket: PocketEntity): Long

  @Query("SELECT * FROM pockets WHERE id = :id") suspend fun getPocketById(id: Long): PocketEntity?
}

interface PocketRepository {
  fun observePockets(): Flow<List<PocketItemModel>>

  @Suppress("LongParameterList")
  suspend fun addPocket(
      name: String,
      categoryTag: String,
      budgetAmount: Long,
      icon: String,
      color: Long = 0xFF316342,
      categoryId: Long? = null,
  ): Long

  suspend fun getPocketById(id: Long): PocketEntity?
}

class DefaultPocketRepository(
    private val pocketDao: PocketDao,
) : PocketRepository {
  override fun observePockets(): Flow<List<PocketItemModel>> =
      pocketDao.observePocketsWithSpent().map { list ->
        list.map { item ->
          val remainingBalance = item.budgetAmount - item.spentAmount
          val isOverBudget = remainingBalance < 0
          val warningMessage =
              if (isOverBudget) {
                "Over budget by ${formatAmount(-remainingBalance)} T"
              } else {
                null
              }
          PocketItemModel(
              id = item.id,
              name = item.name,
              categoryTag = item.categoryTag,
              budgetAmount = item.budgetAmount,
              remainingBalance = remainingBalance,
              icon = item.icon,
              color = item.color,
              categoryId = item.categoryId,
              isOverBudget = isOverBudget,
              warningMessage = warningMessage,
          )
        }
      }

  @Suppress("LongParameterList")
  override suspend fun addPocket(
      name: String,
      categoryTag: String,
      budgetAmount: Long,
      icon: String,
      color: Long,
      categoryId: Long?,
  ): Long {
    val pocket =
        PocketEntity(
            name = name,
            categoryTag = categoryTag,
            budgetAmount = budgetAmount,
            icon = icon,
            color = color,
            categoryId = categoryId,
            createdAt = Clock.System.now(),
        )
    return pocketDao.insertPocket(pocket)
  }

  override suspend fun getPocketById(id: Long): PocketEntity? {
    return pocketDao.getPocketById(id)
  }

  private fun formatAmount(amount: Long): String {
    val formatted = amount.toString().reversed().chunked(3).joinToString(",").reversed()
    return formatted
  }
}

@Fake
class FakePocketRepository(
    initialPockets: List<PocketEntity> = emptyList(),
    initialExpenses: List<ExpenseEntity> = emptyList(),
) : PocketRepository {
  private val pocketsFlow = MutableStateFlow(initialPockets)
  private val expensesFlow = MutableStateFlow(initialExpenses)
  private var nextId = (initialPockets.maxOfOrNull { it.id } ?: 0L) + 1L

  fun updateExpenses(expenses: List<ExpenseEntity>) {
    expensesFlow.value = expenses
  }

  override fun observePockets(): Flow<List<PocketItemModel>> = pocketsFlow.map { pockets ->
    val expenses = expensesFlow.value
    pockets.map { pocket ->
      val spent = expenses.filter { it.pocketId == pocket.id }.sumOf { it.amount }
      val remaining = pocket.budgetAmount - spent
      val isOverBudget = remaining < 0
      val warningMessage =
          if (isOverBudget) {
            "Over budget by ${formatAmount(-remaining)} T"
          } else {
            null
          }
      PocketItemModel(
          id = pocket.id,
          name = pocket.name,
          categoryTag = pocket.categoryTag,
          budgetAmount = pocket.budgetAmount,
          remainingBalance = remaining,
          icon = pocket.icon,
          color = pocket.color,
          categoryId = pocket.categoryId,
          isOverBudget = isOverBudget,
          warningMessage = warningMessage,
      )
    }
  }

  @Suppress("LongParameterList")
  override suspend fun addPocket(
      name: String,
      categoryTag: String,
      budgetAmount: Long,
      icon: String,
      color: Long,
      categoryId: Long?,
  ): Long {
    val id = nextId++
    val newPocket =
        PocketEntity(
            id = id,
            name = name,
            categoryTag = categoryTag,
            budgetAmount = budgetAmount,
            icon = icon,
            color = color,
            categoryId = categoryId,
            createdAt = Clock.System.now(),
        )
    pocketsFlow.update { listOf(newPocket) + it }
    return id
  }

  override suspend fun getPocketById(id: Long): PocketEntity? {
    return pocketsFlow.value.firstOrNull { it.id == id }
  }

  private fun formatAmount(amount: Long): String {
    return amount.toString().reversed().chunked(3).joinToString(",").reversed()
  }
}

object FakePocketEntityFactory {
  @Suppress("LongParameterList")
  fun create(
      id: Long = 1,
      name: String = "Groceries & Household",
      categoryTag: String = "Groceries",
      budgetAmount: Long = 1_000_000,
      icon: String = "shopping_bag",
      createdAt: Instant = Clock.System.now(),
      color: Long = 0xFF316342,
      categoryId: Long? = null,
  ): PocketEntity =
      PocketEntity(
          id = id,
          name = name,
          categoryTag = categoryTag,
          budgetAmount = budgetAmount,
          icon = icon,
          createdAt = createdAt,
          color = color,
          categoryId = categoryId,
      )

  fun createList(count: Int = 3): List<PocketEntity> =
      (1..count).map { index ->
        create(
            id = index.toLong(),
            name = "Pocket $index",
            categoryTag = "Category $index",
            budgetAmount = index * 500_000L,
            icon = "icon_$index",
            color = 0xFF316342,
            categoryId = null,
        )
      }
}

object FakePocketItemModelFactory {
  @Suppress("LongParameterList")
  fun create(
      id: Long = 1,
      name: String = "Groceries & Household",
      categoryTag: String = "Groceries",
      budgetAmount: Long = 1_000_000,
      remainingBalance: Long = 850_000,
      icon: String = "shopping_bag",
      color: Long = 0xFF316342,
      categoryId: Long? = null,
      isOverBudget: Boolean = false,
      warningMessage: String? = null,
  ): PocketItemModel =
      PocketItemModel(
          id = id,
          name = name,
          categoryTag = categoryTag,
          budgetAmount = budgetAmount,
          remainingBalance = remainingBalance,
          icon = icon,
          color = color,
          categoryId = categoryId,
          isOverBudget = isOverBudget,
          warningMessage = warningMessage,
      )

  fun createList(): List<PocketItemModel> =
      listOf(
          create(
              id = 1,
              name = "Groceries & Household",
              categoryTag = "Groceries",
              budgetAmount = 1_000_000,
              remainingBalance = 850_000,
              icon = "shopping_bag",
              color = 0xFF316342,
              categoryId = null,
              isOverBudget = false,
              warningMessage = null,
          ),
          create(
              id = 2,
              name = "Daily Cafe & Coffee",
              categoryTag = "Coffee & Dining",
              budgetAmount = 500_000,
              remainingBalance = -65_000,
              icon = "local_cafe",
              color = 0xFFB87D4B,
              categoryId = null,
              isOverBudget = true,
              warningMessage = "Over budget by 65,000 T",
          ),
          create(
              id = 3,
              name = "Entertainment & Gaming",
              categoryTag = "Entertainment",
              budgetAmount = 1_200_000,
              remainingBalance = 1_200_000,
              icon = "sports_esports",
              color = 0xFF4A7C59,
              categoryId = null,
              isOverBudget = false,
              warningMessage = null,
          ),
      )
}
