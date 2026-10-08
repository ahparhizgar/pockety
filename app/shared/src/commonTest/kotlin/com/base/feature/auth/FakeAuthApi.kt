package com.base.feature.auth

import com.base.dto.AuthResult
import com.base.util.FakeService

class FakeAuthApi : AuthApi, FakeService {
  override var errorToThrow: Exception? = null

  override suspend fun signUp(email: String, password: String): AuthSubmissionResult = fakeApiCall {
    AuthSubmissionResult(
        session = AuthResult(userId = "user-1", email = email, accessToken = "token-user-1-$email"),
    )
  }

  override suspend fun signIn(email: String, password: String): AuthSubmissionResult = fakeApiCall {
    signUp(email, password)
  }
}
