package com.base.feature.privacypolicy

import com.base.component.TestCContext
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class PrivacyPolicyTest : BehaviorSpec() {
  init {
    Given("PrivacyPolicyComponent initialized with back navigation callback") {
      var backClicked = false
      val factory = DefaultPrivacyPolicyComponentFactory()
      val component =
          factory.create(
              cContext = TestCContext(),
              onBack = { backClicked = true },
          )

      When("user invokes onBack()") {
        component.onBack()

        Then("the registered back callback is executed") { backClicked shouldBe true }
      }
    }
  }
}
