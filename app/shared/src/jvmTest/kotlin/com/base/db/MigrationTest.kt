package com.base.db

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MigrationTest : BehaviorSpec() {
  init {
    Given("SQLite database at version 2") {
      val driver = BundledSQLiteDriver()
      val connection = driver.open(":memory:")

      connection.execSQL(
          "CREATE TABLE IF NOT EXISTS `items` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, PRIMARY KEY(`id`))"
      )
      connection.execSQL(
          "CREATE TABLE IF NOT EXISTS `transactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `amount` INTEGER NOT NULL, `description` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)"
      )

      When("MIGRATION_2_3 is executed") {
        MIGRATION_2_3.migrate(connection)

        Then("candidate_expenses table exists with correct columns") {
          connection.execSQL(
              """
              INSERT INTO `candidate_expenses` (`id`, `sender`, `rawBody`, `amount`, `status`, `receivedAt`)
              VALUES (1, 'Bank Mellat', 'Withdrawal 1000', 1000, 'PENDING', 1700000000000)
              """
                  .trimIndent()
          )

          val statement =
              connection.prepare(
                  "SELECT sender, amount, status FROM candidate_expenses WHERE id = 1"
              )
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getText(0) shouldBe "Bank Mellat"
          statement.getLong(1) shouldBe 1000L
          statement.getText(2) shouldBe "PENDING"
          statement.close()
        }
      }

      When("MIGRATION_3_4 is executed") {
        MIGRATION_3_4.migrate(connection)

        Then("items table is dropped") {
          val statement =
              connection.prepare(
                  "SELECT count(*) FROM sqlite_master WHERE type='table' AND name='items'"
              )
          statement.step() shouldBe true
          statement.getLong(0) shouldBe 0L
          statement.close()
        }
      }

      afterSpec { connection.close() }
    }
  }
}
