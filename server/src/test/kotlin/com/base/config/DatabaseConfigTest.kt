package com.base.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DatabaseConfigTest {

  @Test
  fun testConfig() {
    val testConfig = DatabaseConfig.test()
    assertEquals("sa", testConfig.username)
    assertEquals("org.h2.Driver", testConfig.driverClassName)

    val envConfig = DatabaseConfig.fromEnvironment()
    assertNotNull(envConfig.jdbcUrl)
    assertNotNull(envConfig.driverClassName)
  }
}
