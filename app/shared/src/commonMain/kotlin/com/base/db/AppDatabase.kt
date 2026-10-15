package com.base.db

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import com.base.DataDir
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

@Database(
    entities =
        [
            ExpenseEntity::class,
            CandidateExpenseEntity::class,
        ],
    version = 4,
)
@ColumnTypeConverters(InstantConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun expenseDao(): ExpenseDao

  abstract fun candidateExpenseDao(): CandidateExpenseDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
  override fun initialize(): AppDatabase
}

val MIGRATION_1_2 =
    object : Migration(1, 2) {
      override suspend fun migrate(connection: SQLiteConnection) {
        connection
            .prepare(
                """
                CREATE TABLE IF NOT EXISTS `transactions` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `amount` INTEGER NOT NULL,
                    `description` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """
                    .trimIndent(),
            )
            .use { it.step() }
      }
    }

val MIGRATION_2_3 =
    object : Migration(2, 3) {
      override suspend fun migrate(connection: SQLiteConnection) {
        connection
            .prepare(
                """
                CREATE TABLE IF NOT EXISTS `candidate_expenses` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `sender` TEXT NOT NULL,
                    `rawBody` TEXT NOT NULL,
                    `amount` INTEGER NOT NULL,
                    `status` TEXT NOT NULL,
                    `receivedAt` INTEGER NOT NULL
                )
                """
                    .trimIndent(),
            )
            .use { it.step() }
      }
    }

val MIGRATION_3_4 =
    object : Migration(3, 4) {
      override suspend fun migrate(connection: SQLiteConnection) {
        connection.prepare("DROP TABLE IF EXISTS `items`").use { it.step() }
      }
    }

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
    queryContext: CoroutineContext = Dispatchers.Default,
): AppDatabase {
  return builder
      .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
      .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
      .setQueryCoroutineContext(queryContext)
      .build()
}

interface AppDatabaseBuilderFactory {
  fun createBuilder(dataDir: DataDir): RoomDatabase.Builder<AppDatabase>
}
