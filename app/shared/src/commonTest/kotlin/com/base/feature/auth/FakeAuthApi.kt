package com.base.feature.auth

import com.base.dto.FakeAuthResultFactory
import com.base.util.FakeService

class FakeAuthApi : AuthApi, FakeService {
  override var errorToThrow: Exception? = null

  override suspend fun signUp(email: String, password: String): AuthSubmissionResult = fakeApiCall {
    AuthSubmissionResult(
        session =
            FakeAuthResultFactory.create(
                userId = email,
                email = email,
            ),
    )
  }

  override suspend fun signIn(email: String, password: String): AuthSubmissionResult = fakeApiCall {
    signUp(email, password)
  }
}
