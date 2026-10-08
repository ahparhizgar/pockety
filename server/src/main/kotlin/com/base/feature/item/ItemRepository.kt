package com.base.feature.item

import com.base.database.Items
import com.base.dto.ItemDto
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

interface ItemRepository {
  fun getAllItems(): List<ItemDto>

  fun createItem(item: ItemDto)
}

class ItemRepositoryImpl(private val database: Database) : ItemRepository {
  override fun getAllItems(): List<ItemDto> =
      transaction(database) { Items.selectAll().map { it.toItemDto() } }

  override fun createItem(item: ItemDto) {
    transaction(database) {
      Items.insert {
        it[Items.id] = item.id
        it[Items.name] = item.name
      }
    }
  }

  private fun ResultRow.toItemDto(): ItemDto =
      ItemDto(
          id = get(Items.id),
          name = get(Items.name),
      )
}

class FakeItemRepository : ItemRepository {
  val items = mutableListOf<ItemDto>()

  override fun getAllItems(): List<ItemDto> = items.toList()

  override fun createItem(item: ItemDto) {
    items.add(item)
  }
}
