package com.base.database

import org.jetbrains.exposed.v1.core.Table

object Users : Table("users") {
  val id = varchar("id", 36)
  val name = varchar("name", 255)
  val email = varchar("email", 255).uniqueIndex()
  val passwordHash = varchar("password_hash", 64)

  override val primaryKey = PrimaryKey(id)
}
