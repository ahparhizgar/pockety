package com.base

data class DataDir(val dir: String) {
  companion object {
    const val DEFAULT = "app-data/A"
    val MEMORY = DataDir(":memory:")
  }
}
