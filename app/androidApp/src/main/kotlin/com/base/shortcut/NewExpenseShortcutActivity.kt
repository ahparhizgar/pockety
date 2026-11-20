package com.base.shortcut

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.base.MainActivity

/** Keeps the launcher's static-shortcut task reset away from the user's current draft. */
class NewExpenseShortcutActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    startActivity(
        Intent(this, MainActivity::class.java).apply {
          action = MainActivity.ACTION_NEW_EXPENSE
          flags =
              Intent.FLAG_ACTIVITY_NEW_TASK or
                  Intent.FLAG_ACTIVITY_CLEAR_TOP or
                  Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
    )
    finish()
  }
}
