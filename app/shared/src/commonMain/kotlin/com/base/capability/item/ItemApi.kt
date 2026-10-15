package com.base.capability.item

import com.base.dto.ItemDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

interface ItemApi {
  suspend fun getItems(): List<ItemDto>
}

class KtorItemApi(
    private val client: HttpClient,
) : ItemApi {
  override suspend fun getItems(): List<ItemDto> = client.get("/items").body()
}

class FakeItemApi(
    var items: List<ItemDto> = emptyList(),
) : ItemApi {
  override suspend fun getItems(): List<ItemDto> = items
}
