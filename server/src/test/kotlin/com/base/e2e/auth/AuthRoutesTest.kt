package com.base.e2e.auth

import com.base.dto.AuthResult
import com.base.dto.ErrorResponse
import com.base.dto.SignInRequest
import com.base.dto.SignUpRequest
import com.base.util.testOpenSplit
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthRoutesTest {
  @Test
  fun signInRejectsInvalidData() = testOpenSplit {
    val response = client.post("/tokens") { setBody(SignInRequest("bad", "short")) }

    assertEquals(HttpStatusCode.BadRequest, response.status)
    val error: ErrorResponse = response.body()
    assertNotNull(error.errors["email"])
  }

  @Test
  fun cannotSignUpUsingTheSameEmail() = testOpenSplit {
    val signUp =
        client.post("/users") { setBody(SignUpRequest("new@example.com", "password123", "Amir")) }

    assertEquals(HttpStatusCode.Created, signUp.status)

    val secondSignUp =
        client.post("/users") {
          setBody(SignUpRequest("new@example.com", "newPassword123", "Amir"))
        }

    assertEquals(HttpStatusCode.Conflict, secondSignUp.status)
  }

  @Test
  fun cannotSignInUsingWrongPassword() = testOpenSplit {
    val signUp =
        client.post("/users") {
          setBody(SignUpRequest("wrong-pass@example.com", "password123", "Amir"))
        }

    assertEquals(HttpStatusCode.Created, signUp.status)

    val signIn =
        client.post("/tokens") { setBody(SignInRequest("wrong-pass@example.com", "incorrect")) }

    assertEquals(HttpStatusCode.Unauthorized, signIn.status)
    val error: ErrorResponse = signIn.body()
    assertEquals("Invalid email or password", error.generalError)
  }

  @Test
  fun canSignInAfterSignUp() = testOpenSplit {
    val email = "fresh@example.com"
    val password = "password123"
    val name = "Amir"

    val signUp = client.post("/users") { setBody(SignUpRequest(email, password, name)) }

    assertEquals(HttpStatusCode.Created, signUp.status)

    val signIn = client.post("/tokens") { setBody(SignInRequest(email, password)) }

    assertEquals(HttpStatusCode.OK, signIn.status)
    val session: AuthResult = signIn.body()
    assertEquals(email, session.email)
    assertNotNull(session.accessToken)
    assertTrue(session.accessToken.isNotBlank())
  }
}
