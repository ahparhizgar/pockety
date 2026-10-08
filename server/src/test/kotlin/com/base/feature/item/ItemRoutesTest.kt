package com.base.feature.item

import com.base.dto.CreateItemRequest
import com.base.dto.ItemDto
import com.base.util.KtorBehaviorSpec
import com.base.util.testValue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

class ItemRoutesTest : KtorBehaviorSpec() {
  init {
    Given("items endpoint") {
      When("getting items initially") {
        val response by testValue { client.get("/items") }
        Then("returns empty list with 200 OK") {
          response.status shouldBe HttpStatusCode.OK
          val items: List<ItemDto> = response.body()
          items.shouldBeEmpty()
        }
      }

      When("items are created via POST") {
        beforeEach {
          val createFirst = client.post("/items") { setBody(CreateItemRequest(name = "Coffee")) }
          createFirst.status shouldBe HttpStatusCode.Created

          val createSecond = client.post("/items") { setBody(CreateItemRequest(name = "Tea")) }
          createSecond.status shouldBe HttpStatusCode.Created
        }
        val response by testValue { client.get("/items") }
        Then("returns list of items") {
          response.status shouldBe HttpStatusCode.OK
          val items: List<ItemDto> = response.body()
          items shouldHaveSize 2
          items[0].name shouldBe "Coffee"
          items[1].name shouldBe "Tea"
        }
      }
    }
  }
}
