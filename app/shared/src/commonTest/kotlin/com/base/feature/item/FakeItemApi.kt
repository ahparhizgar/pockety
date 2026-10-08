package com.base.feature.item

import com.base.dto.ItemDto
import com.base.util.FakeService

class FakeItemApi : ItemApi, FakeService {
  override var errorToThrow: Exception? = null
  var items = mutableListOf<ItemDto>()

  override suspend fun getItems(): List<ItemDto> = fakeApiCall { items.toList() }
}
