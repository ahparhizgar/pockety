package com.base.db

import androidx.room3.Dao
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey val id: String,
    val name: String,
)

@Dao
interface ItemDao {
  @Transaction @Query("SELECT * FROM items") fun getItems(): Flow<List<ItemEntity>>
}
