package com.base.db

import androidx.room3.Dao
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Transaction
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Long,
    val description: String,
    val createdAt: Instant,
)

@Dao
interface TransactionDao {
  @Transaction
  @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
  fun observeTransactions(): Flow<List<TransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity): Long
}

interface TransactionRepository {
  fun observeTransactions(): Flow<List<TransactionEntity>>

  suspend fun addTransaction(amount: Long, description: String): Long
}

class DefaultTransactionRepository(
    private val transactionDao: TransactionDao,
) : TransactionRepository {
  override fun observeTransactions(): Flow<List<TransactionEntity>> =
      transactionDao.observeTransactions()

  override suspend fun addTransaction(amount: Long, description: String): Long {
    val transaction =
        TransactionEntity(
            amount = amount,
            description = description,
            createdAt = Clock.System.now(),
        )
    return transactionDao.insertTransaction(transaction)
  }
}

object FakeTransactionFactory {
  fun create(
      id: Long = 1,
      amount: Long = 1000,
      description: String = "Test expense",
      createdAt: Instant = Clock.System.now(),
  ): TransactionEntity =
      TransactionEntity(
          id = id,
          amount = amount,
          description = description,
          createdAt = createdAt,
      )

  fun createList(count: Int = 3): List<TransactionEntity> =
      (1..count).map { index ->
        create(
            id = index.toLong(),
            amount = index * 1000L,
            description = "Expense $index",
        )
      }
}
