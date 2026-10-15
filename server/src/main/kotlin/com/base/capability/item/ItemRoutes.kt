package com.base.capability.item

import com.base.dto.CreateItemRequest
import com.base.dto.ItemDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.util.UUID
import org.koin.ktor.ext.inject

fun Application.itemRoutes() {
  val itemRepository by inject<ItemRepository>()

  routing {
    route("/items") {
      get {
        val items = itemRepository.getAllItems()
        call.respond(HttpStatusCode.OK, items)
      }

      post {
        val request = call.receive<CreateItemRequest>()
        val item = ItemDto(id = UUID.randomUUID().toString(), name = request.name)
        itemRepository.createItem(item)
        call.respond(HttpStatusCode.Created, item)
      }
    }
  }
}
