package com.base.capability.auth

import com.base.config.JwtConfig
import org.koin.dsl.module

private const val BCRYPT_WORK_FACTOR = 12
private const val TEST_BCRYPT_WORK_FACTOR = 4

fun authKoinModule() = module {
  single { JwtConfig.fromEnvironment() }
  single<PasswordHasher> { BcryptPasswordHasher(BCRYPT_WORK_FACTOR) }
  single { JwtService(get()) }
  single<AuthRepository> { AuthRepositoryImpl(get()) }
  single { AuthService(get(), get(), get()) }
}

fun testAuthKoinModule() = module {
  single<PasswordHasher> { BcryptPasswordHasher(TEST_BCRYPT_WORK_FACTOR) }
}
