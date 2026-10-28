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
import kotlinx.coroutines.flow.combine
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
    val icon: String,
    val createdAt: Instant,
    val color: Long = 0xFF316342,
    val categoryId: Long? = null,
)

@Entity(
    tableName = "pocket_top_ups",
    foreignKeys =
        [
            ForeignKey(
                entity = PocketEntity::class,
                parentColumns = ["id"],
                childColumns = ["pocketId"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
    indices = [Index("pocketId")],
)
data class PocketTopUpEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pocketId: Long,
    val amount: Long,
    val description: String? = null,
    val createdAt: Instant,
)

data class PocketWithSpent(
    val id: Long,
    val name: String,
    val categoryTag: String,
    val icon: String,
    val createdAt: Instant,
    val color: Long,
    val categoryId: Long?,
    val totalTopUpAmount: Long,
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
          p.icon, 
          p.createdAt,
          p.color,
          p.categoryId,
          COALESCE(topups.totalTopUp, 0) AS totalTopUpAmount,
          COALESCE(expenses.totalSpent, 0) AS spentAmount
      FROM pockets p
      LEFT JOIN (
          SELECT pocketId, SUM(amount) AS totalTopUp 
          FROM pocket_top_ups 
          GROUP BY pocketId
      ) topups ON topups.pocketId = p.id
      LEFT JOIN (
          SELECT pocketId, SUM(amount) AS totalSpent 
          FROM transactions 
          GROUP BY pocketId
      ) expenses ON expenses.pocketId = p.id
      ORDER BY p.createdAt DESC
      """
  )
  fun observePocketsWithSpent(): Flow<List<PocketWithSpent>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPocket(pocket: PocketEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTopUp(topUp: PocketTopUpEntity): Long

  @Query("SELECT * FROM pockets WHERE id = :id") suspend fun getPocketById(id: Long): PocketEntity?

  @Query("SELECT * FROM pocket_top_ups WHERE pocketId = :pocketId ORDER BY createdAt DESC")
  fun observePocketTopUps(pocketId: Long): Flow<List<PocketTopUpEntity>>

  @Query("SELECT * FROM transactions WHERE pocketId = :pocketId ORDER BY createdAt DESC")
  fun observePocketExpenses(pocketId: Long): Flow<List<ExpenseEntity>>

  @Suppress("LongParameterList")
  @Query(
      """
      UPDATE pockets 
      SET name = :name, categoryTag = :categoryTag, icon = :icon, color = :color, categoryId = :categoryId 
      WHERE id = :id
      """
  )
  suspend fun updatePocket(
      id: Long,
      name: String,
      categoryTag: String,
      icon: String,
      color: Long,
      categoryId: Long?,
  )

  @Query("UPDATE transactions SET pocketId = NULL WHERE id = :id")
  suspend fun unlinkExpense(id: Long)

  @Query("DELETE FROM pockets WHERE id = :id") suspend fun deletePocket(id: Long)
}

interface PocketRepository {
  fun observePockets(): Flow<List<PocketItemModel>>

  fun observePocketTopUps(pocketId: Long): Flow<List<PocketTopUpEntity>>

  fun observePocketExpenses(pocketId: Long): Flow<List<ExpenseEntity>>

  @Suppress("LongParameterList")
  suspend fun addPocket(
      name: String,
      categoryTag: String,
      budgetAmount: Long,
      icon: String,
      color: Long = 0xFF316342,
      categoryId: Long? = null,
  ): Long

  @Suppress("LongParameterList")
  suspend fun updatePocket(
      id: Long,
      name: String,
      categoryTag: String,
      icon: String,
      color: Long = 0xFF316342,
      categoryId: Long? = null,
  )

  suspend fun getPocketById(id: Long): PocketEntity?

  suspend fun topUpPocket(id: Long, amount: Long, description: String? = null)

  suspend fun unlinkExpense(id: Long)

  suspend fun deletePocket(id: Long)
}

class DefaultPocketRepository(
    private val pocketDao: PocketDao,
) : PocketRepository {
  override fun observePockets(): Flow<List<PocketItemModel>> =
      pocketDao.observePocketsWithSpent().map { list ->
        list.map { item ->
          val remainingBalance = item.totalTopUpAmount - item.spentAmount
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
              budgetAmount = item.totalTopUpAmount,
              remainingBalance = remainingBalance,
              icon = item.icon,
              color = item.color,
              categoryId = item.categoryId,
              isOverBudget = isOverBudget,
              warningMessage = warningMessage,
          )
        }
      }

  override fun observePocketTopUps(pocketId: Long): Flow<List<PocketTopUpEntity>> =
      pocketDao.observePocketTopUps(pocketId)

  override fun observePocketExpenses(pocketId: Long): Flow<List<ExpenseEntity>> =
      pocketDao.observePocketExpenses(pocketId)

  @Suppress("LongParameterList")
  override suspend fun addPocket(
      name: String,
      categoryTag: String,
      budgetAmount: Long,
      icon: String,
      color: Long,
      categoryId: Long?,
  ): Long {
    val now = Clock.System.now()
    val pocket =
        PocketEntity(
            name = name,
            categoryTag = categoryTag,
            icon = icon,
            color = color,
            categoryId = categoryId,
            createdAt = now,
        )
    val pocketId = pocketDao.insertPocket(pocket)
    if (budgetAmount > 0) {
      val initialTopUp =
          PocketTopUpEntity(
              pocketId = pocketId,
              amount = budgetAmount,
              description = "Initial Balance",
              createdAt = now,
          )
      pocketDao.insertTopUp(initialTopUp)
    }
    return pocketId
  }

  @Suppress("LongParameterList")
  override suspend fun updatePocket(
      id: Long,
      name: String,
      categoryTag: String,
      icon: String,
      color: Long,
      categoryId: Long?,
  ) {
    pocketDao.updatePocket(
        id = id,
        name = name,
        categoryTag = categoryTag,
        icon = icon,
        color = color,
        categoryId = categoryId,
    )
  }

  override suspend fun getPocketById(id: Long): PocketEntity? {
    return pocketDao.getPocketById(id)
  }

  override suspend fun topUpPocket(id: Long, amount: Long, description: String?) {
    val topUp =
        PocketTopUpEntity(
            pocketId = id,
            amount = amount,
            description = description,
            createdAt = Clock.System.now(),
        )
    pocketDao.insertTopUp(topUp)
  }

  override suspend fun unlinkExpense(id: Long) {
    pocketDao.unlinkExpense(id)
  }

  override suspend fun deletePocket(id: Long) {
    pocketDao.deletePocket(id)
  }

  private fun formatAmount(amount: Long): String {
    val formatted = amount.toString().reversed().chunked(3).joinToString(",").reversed()
    return formatted
  }
}

@Fake
@Suppress("TooManyFunctions")
class FakePocketRepository(
    initialPockets: List<PocketEntity> = emptyList(),
    initialExpenses: List<ExpenseEntity> = emptyList(),
    initialTopUps: List<PocketTopUpEntity> = emptyList(),
) : PocketRepository {
  private val pocketsFlow = MutableStateFlow(initialPockets)
  private val expensesFlow = MutableStateFlow(initialExpenses)
  private val topUpsFlow = MutableStateFlow(initialTopUps)
  private var nextPocketId = (initialPockets.maxOfOrNull { it.id } ?: 0L) + 1L
  private var nextTopUpId = (initialTopUps.maxOfOrNull { it.id } ?: 0L) + 1L

  fun updateExpenses(expenses: List<ExpenseEntity>) {
    expensesFlow.value = expenses
  }

  fun updateTopUps(topUps: List<PocketTopUpEntity>) {
    topUpsFlow.value = topUps
  }

  override fun observePockets(): Flow<List<PocketItemModel>> =
      combine(pocketsFlow, expensesFlow, topUpsFlow) { pockets, expenses, topUps ->
        pockets.map { pocket ->
          val totalTopUp = topUps.filter { it.pocketId == pocket.id }.sumOf { it.amount }
          val spent = expenses.filter { it.pocketId == pocket.id }.sumOf { it.amount }
          val remaining = totalTopUp - spent
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
              budgetAmount = totalTopUp,
              remainingBalance = remaining,
              icon = pocket.icon,
              color = pocket.color,
              categoryId = pocket.categoryId,
              isOverBudget = isOverBudget,
              warningMessage = warningMessage,
          )
        }
      }

  override fun observePocketTopUps(pocketId: Long): Flow<List<PocketTopUpEntity>> =
      topUpsFlow.map { list ->
        list.filter { it.pocketId == pocketId }
      }

  override fun observePocketExpenses(pocketId: Long): Flow<List<ExpenseEntity>> =
      expensesFlow.map { list ->
        list.filter { it.pocketId == pocketId }
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
    val id = nextPocketId++
    val now = Clock.System.now()
    val newPocket =
        PocketEntity(
            id = id,
            name = name,
            categoryTag = categoryTag,
            icon = icon,
            color = color,
            categoryId = categoryId,
            createdAt = now,
        )
    pocketsFlow.update { listOf(newPocket) + it }
    if (budgetAmount > 0) {
      val initialTopUp =
          PocketTopUpEntity(
              id = nextTopUpId++,
              pocketId = id,
              amount = budgetAmount,
              description = "Initial Balance",
              createdAt = now,
          )
      topUpsFlow.update { listOf(initialTopUp) + it }
    }
    return id
  }

  @Suppress("LongParameterList")
  override suspend fun updatePocket(
      id: Long,
      name: String,
      categoryTag: String,
      icon: String,
      color: Long,
      categoryId: Long?,
  ) {
    pocketsFlow.update { list ->
      list.map { pocket ->
        if (pocket.id == id) {
          pocket.copy(
              name = name,
              categoryTag = categoryTag,
              icon = icon,
              color = color,
              categoryId = categoryId,
          )
        } else {
          pocket
        }
      }
    }
  }

  override suspend fun getPocketById(id: Long): PocketEntity? {
    return pocketsFlow.value.firstOrNull { it.id == id }
  }

  override suspend fun topUpPocket(id: Long, amount: Long, description: String?) {
    val topUp =
        PocketTopUpEntity(
            id = nextTopUpId++,
            pocketId = id,
            amount = amount,
            description = description,
            createdAt = Clock.System.now(),
        )
    topUpsFlow.update { listOf(topUp) + it }
  }

  override suspend fun unlinkExpense(id: Long) {
    expensesFlow.update { list ->
      list.map { expense ->
        if (expense.id == id) {
          expense.copy(pocketId = null)
        } else {
          expense
        }
      }
    }
  }

  override suspend fun deletePocket(id: Long) {
    pocketsFlow.update { list -> list.filterNot { it.id == id } }
    topUpsFlow.update { list -> list.filterNot { it.pocketId == id } }
    expensesFlow.update { list ->
      list.map { expense ->
        if (expense.pocketId == id) {
          expense.copy(pocketId = null)
        } else {
          expense
        }
      }
    }
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
      icon: String = "shopping_bag",
      createdAt: Instant = Clock.System.now(),
      color: Long = 0xFF316342,
      categoryId: Long? = null,
  ): PocketEntity =
      PocketEntity(
          id = id,
          name = name,
          categoryTag = categoryTag,
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

object FakePocketTopUpEntityFactory {
  fun create(
      id: Long = 1,
      pocketId: Long = 1,
      amount: Long = 500_000,
      description: String? = "Initial Balance",
      createdAt: Instant = Clock.System.now(),
  ): PocketTopUpEntity =
      PocketTopUpEntity(
          id = id,
          pocketId = pocketId,
          amount = amount,
          description = description,
          createdAt = createdAt,
      )

  fun createList(pocketId: Long = 1, count: Int = 3): List<PocketTopUpEntity> =
      (1..count).map { index ->
        create(
            id = index.toLong(),
            pocketId = pocketId,
            amount = index * 100_000L,
            description = "Top-up $index",
        )
      }
}
