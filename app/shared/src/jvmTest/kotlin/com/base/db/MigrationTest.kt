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

      When("MIGRATION_4_5 is executed") {
        MIGRATION_4_5.migrate(connection)

        Then("categories table exists with required columns") {
          connection.execSQL(
              """
              INSERT INTO `categories` (`id`, `name`, `icon`, `color`, `createdAt`)
              VALUES (1, 'Dining', 'restaurant', 4293227379, 1700000000000)
              """
                  .trimIndent()
          )

          val statement =
              connection.prepare("SELECT name, icon, color FROM categories WHERE id = 1")
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getText(0) shouldBe "Dining"
          statement.getText(1) shouldBe "restaurant"
          statement.getLong(2) shouldBe 4293227379L
          statement.close()
        }

        Then("transactions table contains nullable categoryId column") {
          connection.execSQL(
              """
              INSERT INTO `transactions` (`id`, `amount`, `description`, `createdAt`, `categoryId`)
              VALUES (101, 5000, 'Dinner', 1700000000000, 1)
              """
                  .trimIndent()
          )

          val statement =
              connection.prepare("SELECT amount, categoryId FROM transactions WHERE id = 101")
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getLong(0) shouldBe 5000L
          statement.getLong(1) shouldBe 1L
          statement.close()
        }

        Then("transactions table has categoryId index and foreign key") {
          val indexStmt = connection.prepare("PRAGMA index_list(`transactions`)")
          var foundIndex = false
          while (indexStmt.step()) {
            if (indexStmt.getText(1) == "index_transactions_categoryId") {
              foundIndex = true
            }
          }
          indexStmt.close()
          foundIndex shouldBe true

          val fkStmt = connection.prepare("PRAGMA foreign_key_list(`transactions`)")
          var foundFk = false
          while (fkStmt.step()) {
            if (fkStmt.getText(2) == "categories") {
              foundFk = true
            }
          }
          fkStmt.close()
          foundFk shouldBe true
        }
      }

      When("MIGRATION_5_6 is executed") {
        MIGRATION_5_6.migrate(connection)

        Then("pockets table exists with required columns") {
          connection.execSQL(
              """
              INSERT INTO `pockets` (`id`, `name`, `categoryTag`, `budgetAmount`, `icon`, `createdAt`)
              VALUES (1, 'Groceries', 'Food', 1000000, 'shopping_bag', 1700000000000)
              """
                  .trimIndent()
          )

          val statement =
              connection.prepare(
                  "SELECT name, categoryTag, budgetAmount, icon FROM pockets WHERE id = 1"
              )
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getText(0) shouldBe "Groceries"
          statement.getText(1) shouldBe "Food"
          statement.getLong(2) shouldBe 1000000L
          statement.getText(3) shouldBe "shopping_bag"
          statement.close()
        }

        Then("transactions table contains nullable pocketId column") {
          connection.execSQL(
              """
              INSERT INTO `transactions` (`id`, `amount`, `description`, `createdAt`, `categoryId`, `pocketId`)
              VALUES (102, 3000, 'Supermarket', 1700000000000, 1, 1)
              """
                  .trimIndent()
          )

          val statement =
              connection.prepare(
                  "SELECT amount, categoryId, pocketId FROM transactions WHERE id = 102"
              )
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getLong(0) shouldBe 3000L
          statement.getLong(1) shouldBe 1L
          statement.getLong(2) shouldBe 1L
          statement.close()
        }

        Then("transactions table has pocketId index and foreign key") {
          val indexStmt = connection.prepare("PRAGMA index_list(`transactions`)")
          var foundIndex = false
          while (indexStmt.step()) {
            if (indexStmt.getText(1) == "index_transactions_pocketId") {
              foundIndex = true
            }
          }
          indexStmt.close()
          foundIndex shouldBe true

          val fkStmt = connection.prepare("PRAGMA foreign_key_list(`transactions`)")
          var foundFk = false
          while (fkStmt.step()) {
            if (fkStmt.getText(2) == "pockets") {
              foundFk = true
            }
          }
          fkStmt.close()
          foundFk shouldBe true
        }
      }

      When("MIGRATION_6_7 is executed") {
        MIGRATION_6_7.migrate(connection)

        Then("pockets table contains color and categoryId columns with defaults preserved") {
          val statement =
              connection.prepare("SELECT name, color, categoryId FROM pockets WHERE id = 1")
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getText(0) shouldBe "Groceries"
          statement.getLong(1) shouldBe 4281426754L
          statement.isNull(2) shouldBe true
          statement.close()
        }

        Then("pockets table allows setting categoryId and color") {
          connection.execSQL(
              """
              INSERT INTO `pockets` (`id`, `name`, `categoryTag`, `budgetAmount`, `icon`, `createdAt`, `color`, `categoryId`)
              VALUES (2, 'Dining Out', 'Food', 500000, 'restaurant', 1700000000000, 4293227379, 1)
              """
                  .trimIndent()
          )

          val statement =
              connection.prepare("SELECT name, color, categoryId FROM pockets WHERE id = 2")
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getText(0) shouldBe "Dining Out"
          statement.getLong(1) shouldBe 4293227379L
          statement.getLong(2) shouldBe 1L
          statement.close()
        }

        Then("pockets table has categoryId index and foreign key") {
          val indexStmt = connection.prepare("PRAGMA index_list(`pockets`)")
          var foundIndex = false
          while (indexStmt.step()) {
            if (indexStmt.getText(1) == "index_pockets_categoryId") {
              foundIndex = true
            }
          }
          indexStmt.close()
          foundIndex shouldBe true

          val fkStmt = connection.prepare("PRAGMA foreign_key_list(`pockets`)")
          var foundFk = false
          while (fkStmt.step()) {
            if (fkStmt.getText(2) == "categories") {
              foundFk = true
            }
          }
          fkStmt.close()
          foundFk shouldBe true
        }
      }

      When("MIGRATION_7_8 is executed") {
        MIGRATION_7_8.migrate(connection)

        Then("pockets table does not contain budgetAmount column") {
          val pragmaStmt = connection.prepare("PRAGMA table_info(`pockets`)")
          var hasBudgetAmount = false
          while (pragmaStmt.step()) {
            if (pragmaStmt.getText(1) == "budgetAmount") {
              hasBudgetAmount = true
            }
          }
          pragmaStmt.close()
          hasBudgetAmount shouldBe false
        }

        Then("pocket_top_ups table contains migrated initial balance rows") {
          val statement =
              connection.prepare(
                  "SELECT pocketId, amount, description FROM pocket_top_ups WHERE pocketId = 1"
              )
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getLong(0) shouldBe 1L
          statement.getLong(1) shouldBe 1000000L
          statement.getText(2) shouldBe "Initial Balance"
          statement.close()
        }

        Then("pocket_top_ups table has pocketId index and foreign key") {
          val indexStmt = connection.prepare("PRAGMA index_list(`pocket_top_ups`)")
          var foundIndex = false
          while (indexStmt.step()) {
            if (indexStmt.getText(1) == "index_pocket_top_ups_pocketId") {
              foundIndex = true
            }
          }
          indexStmt.close()
          foundIndex shouldBe true

          val fkStmt = connection.prepare("PRAGMA foreign_key_list(`pocket_top_ups`)")
          var foundFk = false
          while (fkStmt.step()) {
            if (fkStmt.getText(2) == "pockets") {
              foundFk = true
            }
          }
          fkStmt.close()
          foundFk shouldBe true
        }
      }

      When("MIGRATION_8_9 is executed") {
        MIGRATION_8_9.migrate(connection)

        Then(
            "pockets table includes monthlyTopUpAmount, autoTopUpEnabled, and lastTopUpMonth columns with defaults"
        ) {
          val statement =
              connection.prepare(
                  "SELECT monthlyTopUpAmount, autoTopUpEnabled, lastTopUpMonth FROM pockets WHERE id = 1"
              )
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getLong(0) shouldBe 0L
          statement.getLong(1) shouldBe 0L
          statement.isNull(2) shouldBe true
          statement.close()
        }

        Then(
            "pockets table allows setting monthlyTopUpAmount, autoTopUpEnabled, and lastTopUpMonth"
        ) {
          connection.execSQL(
              """
              INSERT INTO `pockets` (`id`, `name`, `categoryTag`, `icon`, `createdAt`, `color`, `categoryId`, `monthlyTopUpAmount`, `autoTopUpEnabled`, `lastTopUpMonth`)
              VALUES (3, 'Travel', 'Trip', 'flight', 1700000000000, 4281426754, NULL, 500000, 1, '2026-11')
              """
                  .trimIndent()
          )

          val statement =
              connection.prepare(
                  "SELECT monthlyTopUpAmount, autoTopUpEnabled, lastTopUpMonth FROM pockets WHERE id = 3"
              )
          val hasRow = statement.step()
          hasRow shouldBe true
          statement.getLong(0) shouldBe 500000L
          statement.getLong(1) shouldBe 1L
          statement.getText(2) shouldBe "2026-11"
          statement.close()
        }
      }

      afterSpec { connection.close() }
    }
    Given("a version 9 categories table") {
      val driver = BundledSQLiteDriver()
      val connection = driver.open(":memory:")

      connection.execSQL(
          "CREATE TABLE IF NOT EXISTS `categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `icon` TEXT NOT NULL, `color` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)"
      )
      // At version 9 the list was displayed newest first: Groceries, Bills, Dining.
      connection.execSQL(
          "INSERT INTO `categories` (`id`, `name`, `icon`, `color`, `createdAt`) VALUES (1, 'Dining', 'restaurant', 1, 1000)"
      )
      connection.execSQL(
          "INSERT INTO `categories` (`id`, `name`, `icon`, `color`, `createdAt`) VALUES (2, 'Groceries', 'shopping_cart', 2, 3000)"
      )
      connection.execSQL(
          "INSERT INTO `categories` (`id`, `name`, `icon`, `color`, `createdAt`) VALUES (3, 'Bills', 'receipt', 3, 2000)"
      )

      When("MIGRATION_9_10 is executed") {
        MIGRATION_9_10.migrate(connection)

        Then("sortOrder preserves the previously displayed order") {
          val statement =
              connection.prepare("SELECT `id`, `sortOrder` FROM `categories` ORDER BY `sortOrder` ASC")
          val ids = mutableListOf<Long>()
          val sortOrders = mutableListOf<Long>()
          while (statement.step()) {
            ids.add(statement.getLong(0))
            sortOrders.add(statement.getLong(1))
          }
          statement.close()
          ids shouldBe listOf(2L, 3L, 1L)
          sortOrders shouldBe listOf(0L, 1L, 2L)
        }
      }

      afterSpec { connection.close() }
    }
  }
}