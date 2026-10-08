package com.base.database

import org.jetbrains.exposed.v1.core.Table

object Items : Table("items") {
  val id = varchar("id", 36)
  val name = varchar("name", 255)

  override val primaryKey = PrimaryKey(id)
}
