package com.base.util

import io.kotest.assertions.fail

class FakeCallback<T> : () -> Unit {
  var called = false

  override fun invoke() {
    called = true
  }

  fun shouldBeCalled() {
    if (!called) fail("Expected callback to be called, but it was not.")
  }

  fun shouldNotBeCalled() {
    if (called) fail("Expected callback not to be called, but it was.")
  }
}

class FakeCallback1<T> : (T) -> Unit {
  var called = false

  override fun invoke(p1: T) {
    called = true
  }

  fun shouldBeCalled() {
    if (!called) fail("Expected callback to be called, but it was not.")
  }

  fun shouldNotBeCalled() {
    if (called) fail("Expected callback not to be called, but it was.")
  }
}
