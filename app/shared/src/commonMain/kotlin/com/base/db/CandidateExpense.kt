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

enum class CandidateExpenseStatus {
  PENDING,
  REJECTED,
  ACCEPTED,
}

@Entity(tableName = "candidate_expenses")
data class CandidateExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,
    val rawBody: String,
    val amount: Long,
    val status: CandidateExpenseStatus,
    val receivedAt: Instant,
)

@Dao
interface CandidateExpenseDao {
  @Transaction
  @Query("SELECT * FROM candidate_expenses WHERE status = 'PENDING' ORDER BY receivedAt DESC")
  fun observePendingCandidates(): Flow<List<CandidateExpenseEntity>>

  @Query("SELECT * FROM candidate_expenses WHERE id = :id")
  suspend fun getCandidateById(id: Long): CandidateExpenseEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCandidate(candidate: CandidateExpenseEntity): Long

  @Query(
      "SELECT id FROM candidate_expenses WHERE sender = :sender AND rawBody = :body AND receivedAt = :receivedAt LIMIT 1"
  )
  suspend fun findExisting(sender: String, body: String, receivedAt: Instant): Long?

  @Transaction
  suspend fun insertIfAbsent(candidate: CandidateExpenseEntity): Long =
      findExisting(candidate.sender, candidate.rawBody, candidate.receivedAt)
          ?: insertCandidate(candidate)

  @Query("UPDATE candidate_expenses SET status = :status WHERE id = :id")
  suspend fun updateStatus(id: Long, status: CandidateExpenseStatus)
}

interface CandidateExpenseRepository {
  fun observePendingCandidates(): Flow<List<CandidateExpenseEntity>>

  suspend fun getCandidateById(id: Long): CandidateExpenseEntity?

  suspend fun addCandidate(
      sender: String,
      rawBody: String,
      amount: Long,
      receivedAt: Instant = Clock.System.now(),
  ): Long

  suspend fun updateStatus(id: Long, status: CandidateExpenseStatus)
}

class DefaultCandidateExpenseRepository(
    private val candidateExpenseDao: CandidateExpenseDao,
) : CandidateExpenseRepository {
  override fun observePendingCandidates(): Flow<List<CandidateExpenseEntity>> =
      candidateExpenseDao.observePendingCandidates()

  override suspend fun getCandidateById(id: Long): CandidateExpenseEntity? =
      candidateExpenseDao.getCandidateById(id)

  override suspend fun addCandidate(
      sender: String,
      rawBody: String,
      amount: Long,
      receivedAt: Instant,
  ): Long {
    val candidate =
        CandidateExpenseEntity(
            sender = sender,
            rawBody = rawBody,
            amount = amount,
            status = CandidateExpenseStatus.PENDING,
            receivedAt = receivedAt,
        )
    return candidateExpenseDao.insertIfAbsent(candidate)
  }

  override suspend fun updateStatus(id: Long, status: CandidateExpenseStatus) {
    candidateExpenseDao.updateStatus(id, status)
  }
}

object FakeCandidateExpenseEntityFactory {
  fun create(
      id: Long = 1,
      sender: String = "Bank Mellat",
      amount: Long = 3200000L,
      status: CandidateExpenseStatus = CandidateExpenseStatus.PENDING,
      receivedAt: Instant = Clock.System.now(),
  ): CandidateExpenseEntity =
      CandidateExpenseEntity(
          id = id,
          sender = sender,
          rawBody = "Transaction $id: $amount Rials",
          amount = amount,
          status = status,
          receivedAt = receivedAt,
      )

  fun createList(count: Int = 3): List<CandidateExpenseEntity> =
      (1..count).map { index ->
        create(
            id = index.toLong(),
            sender = "Bank $index",
            amount = index * 1000000L,
            status = CandidateExpenseStatus.PENDING,
        )
      }
}

@Fake
class FakeCandidateExpenseRepository(
    initialCandidates: List<CandidateExpenseEntity> = emptyList(),
) : CandidateExpenseRepository {
  private val _candidates = MutableStateFlow(initialCandidates)
  private var nextId = (initialCandidates.maxOfOrNull { it.id } ?: 0L) + 1L

  var lastUpdatedId: Long? = null
    private set

  var lastUpdatedStatus: CandidateExpenseStatus? = null
    private set

  override fun observePendingCandidates(): Flow<List<CandidateExpenseEntity>> =
      _candidates.map { list ->
        list
            .filter { it.status == CandidateExpenseStatus.PENDING }
            .sortedByDescending { it.receivedAt }
      }

  override suspend fun getCandidateById(id: Long): CandidateExpenseEntity? =
      _candidates.value.find { it.id == id }

  override suspend fun addCandidate(
      sender: String,
      rawBody: String,
      amount: Long,
      receivedAt: Instant,
  ): Long {
    val id = nextId++
    val candidate =
        CandidateExpenseEntity(
            id = id,
            sender = sender,
            rawBody = rawBody,
            amount = amount,
            status = CandidateExpenseStatus.PENDING,
            receivedAt = receivedAt,
        )
    _candidates.update { it + candidate }
    return id
  }

  override suspend fun updateStatus(id: Long, status: CandidateExpenseStatus) {
    lastUpdatedId = id
    lastUpdatedStatus = status
    _candidates.update { list ->
      list.map { candidate ->
        if (candidate.id == id) {
          candidate.copy(status = status)
        } else {
          candidate
        }
      }
    }
  }
}
