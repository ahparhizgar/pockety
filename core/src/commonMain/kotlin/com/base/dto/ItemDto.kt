package com.base.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: String,
    val name: String,
)

@Serializable
data class CreateItemRequest(
    val name: String,
)

object FakeItemDtoFactory {
  fun create(
      id: String = "item-1",
      name: String = "Test Item 1",
  ) = ItemDto(id = id, name = name)

  fun createList(count: Int = 3): List<ItemDto> =
      (1..count).map { index -> create(id = "item-$index", name = "Test Item $index") }
}

object FakeCreateItemRequestFactory {
  fun create(name: String = "Test Item") = CreateItemRequest(name = name)
}
