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
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

@Entity(
    tableName = "transactions",
    foreignKeys =
        [
            ForeignKey(
                entity = CategoryEntity::class,
                parentColumns = ["id"],
                childColumns = ["categoryId"],
                onDelete = ForeignKey.SET_NULL,
            )
        ],
    indices = [Index("categoryId")],
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Long,
    val description: String,
    val createdAt: Instant,
    val categoryId: Long? = null,
)

@Dao
interface ExpenseDao {
  @Transaction
  @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
  fun observeExpenses(): Flow<List<ExpenseEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertExpense(expense: ExpenseEntity): Long
}

interface ExpenseRepository {
  fun observeExpenses(): Flow<List<ExpenseEntity>>

  suspend fun addExpense(amount: Long, description: String, categoryId: Long? = null): Long
}

class DefaultExpenseRepository(
    private val expenseDao: ExpenseDao,
) : ExpenseRepository {
  override fun observeExpenses(): Flow<List<ExpenseEntity>> = expenseDao.observeExpenses()

  override suspend fun addExpense(amount: Long, description: String, categoryId: Long?): Long {
    val expense =
        ExpenseEntity(
            amount = amount,
            description = description,
            createdAt = Clock.System.now(),
            categoryId = categoryId,
        )
    return expenseDao.insertExpense(expense)
  }
}

object FakeExpenseFactory {
  fun create(
      id: Long = 1,
      amount: Long = 1000,
      description: String = "Test expense",
      createdAt: Instant = Clock.System.now(),
      categoryId: Long? = null,
  ): ExpenseEntity =
      ExpenseEntity(
          id = id,
          amount = amount,
          description = description,
          createdAt = createdAt,
          categoryId = categoryId,
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
