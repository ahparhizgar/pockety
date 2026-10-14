package com.base.usermessage

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import pockety.app.shared.generated.resources.Res
import pockety.app.shared.generated.resources.save_action

class UiStringTest {

  @Test
  fun testEmptyString() {
    val empty = UiString("")
    assertEquals(UiString.EmptyString, empty)
  }

  @Test
  fun testStringLiteral() {
    val literal = UiString("hello")
    assertEquals(UiString.StringLiteral("hello"), literal)
    assertEquals("hello", literal.toString())
  }

  @Test
  fun testStringRes() {
    val res = UiString(Res.string.save_action)
    assertEquals(UiString.StringRes(Res.string.save_action), res)
  }

  @Test
  fun testStringResWithArgument() {
    val argRes1 = UiString(Res.string.save_action, "arg1", 123)
    val argRes2 = UiString(Res.string.save_action, "arg1", 123)
    val argRes3 = UiString(Res.string.save_action, "arg2", 456)

    assertEquals(argRes1, argRes2)
    assertNotEquals(argRes1, argRes3)
    assertEquals(argRes1.hashCode(), argRes2.hashCode())
  }

  @Test
  fun testOperators() {
    val empty = UiString.EmptyString
    val s1 = UiString("Hello")
    val s2 = UiString(" World")

    assertEquals(s1, empty + s1)
    assertEquals(s1, s1 + empty)

    val combined = s1 + s2
    val expected = UiString.Concat(listOf(s1, s2))
    assertEquals(expected, combined)

    val combinedWithStr = s1 + " World"
    assertEquals(expected, combinedWithStr)

    val concatMore = combined + UiString("!")
    val expected3 = UiString.Concat(listOf(s1, s2, UiString("!")))
    assertEquals(expected3, concatMore)

    val emptyConcat = empty + empty
    assertEquals(UiString.EmptyString, emptyConcat)
  }
}
