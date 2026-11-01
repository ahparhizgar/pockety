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
import androidx.room3.Update
import com.base.Fake
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Entity(
    tableName = "transactions",
    foreignKeys =
        [
            ForeignKey(
                entity = CategoryEntity::class,
                parentColumns = ["id"],
                childColumns = ["categoryId"],
                onDelete = ForeignKey.SET_NULL,
            ),
            ForeignKey(
                entity = PocketEntity::class,
                parentColumns = ["id"],
                childColumns = ["pocketId"],
                onDelete = ForeignKey.SET_NULL,
            ),
        ],
    indices = [Index("categoryId"), Index("pocketId")],
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Long,
    val description: String,
    val createdAt: Instant,
    val categoryId: Long? = null,
    val pocketId: Long? = null,
)

@Dao
interface ExpenseDao {
  @Transaction
  @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
  fun observeExpenses(): Flow<List<ExpenseEntity>>

  @Query("SELECT * FROM transactions WHERE id = :id LIMIT 1")
  suspend fun getExpenseById(id: Long): ExpenseEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: ExpenseEntity): Long

  @Update suspend fun updateExpense(expense: ExpenseEntity)

  @Query("DELETE FROM transactions WHERE id = :id") suspend fun deleteExpense(id: Long)
}

interface ExpenseRepository {
  fun observeExpenses(): Flow<List<ExpenseEntity>>

  suspend fun getExpenseById(id: Long): ExpenseEntity?

  suspend fun addExpense(
      amount: Long,
      description: String,
      categoryId: Long? = null,
      pocketId: Long? = null,
  ): Long

  suspend fun updateExpense(expense: ExpenseEntity)

  suspend fun deleteExpense(id: Long)
}

class DefaultExpenseRepository(
    private val expenseDao: ExpenseDao,
) : ExpenseRepository {
  override fun observeExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeExpenses()

  override suspend fun getExpenseById(id: Long): ExpenseEntity? = expenseDao.getExpenseById(id)

  override suspend fun addExpense(
      amount: Long,
      description: String,
      categoryId: Long?,
      pocketId: Long?,
  ): Long {
    val expense =
        ExpenseEntity(
            amount = amount,
            description = description,
            createdAt = Clock.System.now(),
            categoryId = categoryId,
            pocketId = pocketId,
        )
    return expenseDao.insertExpense(expense)
  }

  override suspend fun updateExpense(expense: ExpenseEntity) {
    expenseDao.updateExpense(expense)
  }

  override suspend fun deleteExpense(id: Long) {
    expenseDao.deleteExpense(id)
  }
}

@Fake
class FakeExpenseRepository(
    initialExpenses: List<ExpenseEntity> = emptyList(),
) : ExpenseRepository {
  private val expensesFlow = MutableStateFlow(initialExpenses)
  private var nextId = (initialExpenses.maxOfOrNull { it.id } ?: 0L) + 1L

  override fun observeExpenses(): Flow<List<ExpenseEntity>> = expensesFlow

  override suspend fun getExpenseById(id: Long): ExpenseEntity? =
      expensesFlow.value.firstOrNull { it.id == id }

  override suspend fun addExpense(
      amount: Long,
      description: String,
      categoryId: Long?,
      pocketId: Long?,
  ): Long {
    val id = nextId++
    val newExpense =
        ExpenseEntity(
            id = id,
            amount = amount,
            description = description,
            createdAt = Clock.System.now(),
            categoryId = categoryId,
            pocketId = pocketId,
        )
    expensesFlow.update { listOf(newExpense) + it }
    return id
  }

  override suspend fun updateExpense(expense: ExpenseEntity) {
    expensesFlow.update { list -> list.map { if (it.id == expense.id) expense else it } }
  }

  override suspend fun deleteExpense(id: Long) {
    expensesFlow.update { list -> list.filterNot { it.id == id } }
  }
}

object FakeExpenseFactory {
  @Suppress("LongParameterList")
  fun create(
      id: Long = 1,
      amount: Long = 1000,
      description: String = "Test expense",
      createdAt: Instant = Clock.System.now(),
      categoryId: Long? = null,
      pocketId: Long? = null,
  ): ExpenseEntity =
      ExpenseEntity(
          id = id,
          amount = amount,
          description = description,
          createdAt = createdAt,
          categoryId = categoryId,
          pocketId = pocketId,
      )

  fun createList(count: Int = 3): List<ExpenseEntity> =
      (1..count).map { index ->
        create(
            id = index.toLong(),
            amount = index * 1000L,
            description = "Expense $index",
        )
      }
}
