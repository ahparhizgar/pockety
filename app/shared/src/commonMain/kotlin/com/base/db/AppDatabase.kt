package com.base.db

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.base.DataDir
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

@Database(
    entities =
        [
            ItemEntity::class,
        ],
    version = 1,
)
@ColumnTypeConverters(InstantConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun itemDao(): ItemDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
  override fun initialize(): AppDatabase
}

// THIS IS TEMPLATE -- change it when needed
// val MIGRATION_1_2 =
//    object : Migration(1, 2) {
//      override suspend fun migrate(connection: SQLiteConnection) {
//        connection
//            .prepare(
//                // Query
//            )
//            .use { it.step() }
//      }
//    }

fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
    queryContext: CoroutineContext = Dispatchers.Default,
): AppDatabase {
  return builder
      //      .addMigrations(MIGRATION_1_2)
      .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
      .setQueryCoroutineContext(queryContext)
      .build()
}

interface AppDatabaseBuilderFactory {
  fun createBuilder(dataDir: DataDir): RoomDatabase.Builder<AppDatabase>
}
