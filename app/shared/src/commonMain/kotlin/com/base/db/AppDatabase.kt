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
            CategoryEntity::class,
            PocketEntity::class,
        ],
    version = 7,
)
@ColumnTypeConverters(InstantConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun expenseDao(): ExpenseDao

  abstract fun candidateExpenseDao(): CandidateExpenseDao

  abstract fun categoryDao(): CategoryDao

  abstract fun pocketDao(): PocketDao
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

val MIGRATION_4_5 =
    object : Migration(4, 5) {
      override suspend fun migrate(connection: SQLiteConnection) {
        connection
            .prepare(
                """
                CREATE TABLE IF NOT EXISTS `categories` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `icon` TEXT NOT NULL,
                    `color` INTEGER NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """
                    .trimIndent(),
            )
            .use { it.step() }

        connection
            .prepare(
                """
                CREATE TABLE IF NOT EXISTS `transactions_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `amount` INTEGER NOT NULL,
                    `description` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `categoryId` INTEGER,
                    FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """
                    .trimIndent(),
            )
            .use { it.step() }

        connection
            .prepare(
                """
                INSERT INTO `transactions_new` (`id`, `amount`, `description`, `createdAt`, `categoryId`)
                SELECT `id`, `amount`, `description`, `createdAt`, NULL FROM `transactions`
                """
                    .trimIndent(),
            )
            .use { it.step() }

        connection.prepare("DROP TABLE `transactions`").use { it.step() }
        connection.prepare("ALTER TABLE `transactions_new` RENAME TO `transactions`").use {
          it.step()
        }
        connection
            .prepare(
                "CREATE INDEX IF NOT EXISTS `index_transactions_categoryId` ON `transactions` (`categoryId`)"
            )
            .use { it.step() }
      }
    }

val MIGRATION_5_6 =
    object : Migration(5, 6) {
      override suspend fun migrate(connection: SQLiteConnection) {
        connection
            .prepare(
                """
                CREATE TABLE IF NOT EXISTS `pockets` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `categoryTag` TEXT NOT NULL,
                    `budgetAmount` INTEGER NOT NULL,
                    `icon` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL
                )
                """
                    .trimIndent(),
            )
            .use { it.step() }

        connection
            .prepare(
                """
                CREATE TABLE IF NOT EXISTS `transactions_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `amount` INTEGER NOT NULL,
                    `description` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `categoryId` INTEGER,
                    `pocketId` INTEGER,
                    FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL,
                    FOREIGN KEY(`pocketId`) REFERENCES `pockets`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """
                    .trimIndent(),
            )
            .use { it.step() }

        connection
            .prepare(
                """
                INSERT INTO `transactions_new` (`id`, `amount`, `description`, `createdAt`, `categoryId`, `pocketId`)
                SELECT `id`, `amount`, `description`, `createdAt`, `categoryId`, NULL FROM `transactions`
                """
                    .trimIndent(),
            )
            .use { it.step() }

        connection.prepare("DROP TABLE `transactions`").use { it.step() }
        connection.prepare("ALTER TABLE `transactions_new` RENAME TO `transactions`").use {
          it.step()
        }
        connection
            .prepare(
                "CREATE INDEX IF NOT EXISTS `index_transactions_categoryId` ON `transactions` (`categoryId`)"
            )
            .use { it.step() }
        connection
            .prepare(
                "CREATE INDEX IF NOT EXISTS `index_transactions_pocketId` ON `transactions` (`pocketId`)"
            )
            .use { it.step() }
      }
    }

val MIGRATION_6_7 =
    object : Migration(6, 7) {
      override suspend fun migrate(connection: SQLiteConnection) {
        connection
            .prepare(
                """
                CREATE TABLE IF NOT EXISTS `pockets_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `categoryTag` TEXT NOT NULL,
                    `budgetAmount` INTEGER NOT NULL,
                    `icon` TEXT NOT NULL,
                    `createdAt` INTEGER NOT NULL,
                    `color` INTEGER NOT NULL,
                    `categoryId` INTEGER,
                    FOREIGN KEY(`categoryId`) REFERENCES `categories`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL
                )
                """
                    .trimIndent(),
            )
            .use { it.step() }

        connection
            .prepare(
                """
                INSERT INTO `pockets_new` (`id`, `name`, `categoryTag`, `budgetAmount`, `icon`, `createdAt`, `color`, `categoryId`)
                SELECT `id`, `name`, `categoryTag`, `budgetAmount`, `icon`, `createdAt`, 4281426754, NULL FROM `pockets`
                """
                    .trimIndent(),
            )
            .use { it.step() }

        connection.prepare("DROP TABLE `pockets`").use { it.step() }
        connection.prepare("ALTER TABLE `pockets_new` RENAME TO `pockets`").use { it.step() }
        connection
            .prepare(
                "CREATE INDEX IF NOT EXISTS `index_pockets_categoryId` ON `pockets` (`categoryId`)"
            )
            .use { it.step() }
      }
    }

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
    queryContext: CoroutineContext = Dispatchers.Default,
): AppDatabase {
  return builder
      .addMigrations(
          MIGRATION_1_2,
          MIGRATION_2_3,
          MIGRATION_3_4,
          MIGRATION_4_5,
          MIGRATION_5_6,
          MIGRATION_6_7,
      )
      .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
      .setQueryCoroutineContext(queryContext)
      .build()
}

interface AppDatabaseBuilderFactory {
  fun createBuilder(dataDir: DataDir): RoomDatabase.Builder<AppDatabase>
}
