package com.base.database

import kotlin.time.Instant
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.datetime.InstantColumnType

class KotlinInstantColumnType : InstantColumnType<Instant>() {
  override fun toInstant(value: Instant): Instant = value

  override fun fromInstant(instant: Instant): Instant = instant
}

fun Table.timestamp(name: String): Column<Instant> = registerColumn(name, KotlinInstantColumnType())
