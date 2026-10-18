package com.base.capability.pocket_management

import com.base.capability.root_navigation.RootComponentFactory
import com.base.component.TestCContext
import com.base.db.PocketRepository
import com.base.testValue
import com.base.ui.components.PocketyTab
import com.base.util.MainDispatcherExtension
import com.base.util.assertMainComponent
import com.base.util.assertPocketDetails
import com.base.util.assertPocketSettings
import com.base.util.assertPocketSettingsFlow
import com.base.util.assertPockets
import com.base.util.integrationKoin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.testCoroutineScheduler
import io.kotest.matchers.shouldBe

class PocketDetailsFlowE2eTest : BehaviorSpec() {
  init {
    extensions(MainDispatcherExtension())

    Given("application with existing pockets on the Pockets tab") {
      val koin by integrationKoin()
      val context by testValue { TestCContext() }
      val root by testValue { koin.get<RootComponentFactory>().create(context) }
      val pocketRepository by testValue { koin.get<PocketRepository>() }
      var pocketId: Long by testValue { 0 }

      beforeEach {
        pocketId =
            pocketRepository.addPocket(
                name = "Daily Cafe & Coffee",
                categoryTag = "Drinks",
                budgetAmount = 850000,
                icon = "coffee",
                color = 0xFF4A7C59,
            )
        testCoroutineScheduler.advanceUntilIdle()

        val main = root.assertMainComponent()
        main.onTabSelect(PocketyTab.Pockets)
        testCoroutineScheduler.advanceUntilIdle()
      }

      When("user clicks a pocket card in Pockets list") {
        beforeEach {
          val pockets = root.assertPockets()
          pockets.onPocketClick(pocketId)
          testCoroutineScheduler.advanceUntilIdle()
        }

        Then("active child in RootComponent becomes PocketDetailsComponent") {
          val details = root.assertPocketDetails()
          details.state.value.pocketId shouldBe pocketId
          details.state.value.name shouldBe "Daily Cafe & Coffee"
          details.state.value.balanceAmount shouldBe 850000L
        }

        And("user opens quick top-up and confirms an addition of 100,000 T") {
          beforeEach {
            val details = root.assertPocketDetails()
            details.onTopUpClicked()
            details.onQuickTopUpPresetSelected(100000)
            details.onConfirmQuickTopUp()
            testCoroutineScheduler.advanceUntilIdle()
          }

          Then("pocket balance reflects the new total") {
            val details = root.assertPocketDetails()
            details.state.value.balanceAmount shouldBe 950000L
          }

          And("user taps Configure") {
            beforeEach {
              val details = root.assertPocketDetails()
              details.onConfigureClicked()
              testCoroutineScheduler.advanceUntilIdle()
            }

            Then("RootComponent pushes PocketSettingsFlowComponent with pocketId in edit mode") {
              val flow = root.assertPocketSettingsFlow()
              val settings = flow.assertPocketSettings()
              settings.state.value.pocketId shouldBe pocketId
            }

            And("user presses back from pocket settings") {
              beforeEach {
                root.onBack()
                testCoroutineScheduler.advanceUntilIdle()
              }

              Then("RootComponent returns to PocketDetailsComponent") {
                val details = root.assertPocketDetails()
                details.state.value.pocketId shouldBe pocketId
              }

              And("user presses back from pocket details") {
                beforeEach {
                  val details = root.assertPocketDetails()
                  details.onBackClicked()
                  testCoroutineScheduler.advanceUntilIdle()
                }

                Then("RootComponent returns to MainComponent on Pockets tab with updated balance") {
                  root.assertMainComponent()
                  val pockets = root.assertPockets()
                  val pocket = pockets.state.value.pockets.first { it.id == pocketId }
                  pocket.remainingBalance shouldBe 950000L
                }
              }
            }
          }
        }
      }
    }
  }
}
