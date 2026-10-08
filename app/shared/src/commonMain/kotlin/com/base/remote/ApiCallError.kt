package com.base.remote

import base.app.shared.generated.resources.Res
import base.app.shared.generated.resources.error_client_generic
import base.app.shared.generated.resources.error_forbidden
import base.app.shared.generated.resources.error_invalid_data
import base.app.shared.generated.resources.error_invalid_request
import base.app.shared.generated.resources.error_network_disconnected
import base.app.shared.generated.resources.error_not_found
import base.app.shared.generated.resources.error_rate_limit
import base.app.shared.generated.resources.error_server_generic
import base.app.shared.generated.resources.error_unauthorized
import base.app.shared.generated.resources.error_unknown
import com.ahparhizgar.katch.ApiCallError
import com.ahparhizgar.katch.BadRequest
import com.ahparhizgar.katch.ClientError
import com.ahparhizgar.katch.Forbidden
import com.ahparhizgar.katch.InvalidDataError
import com.ahparhizgar.katch.NetworkError
import com.ahparhizgar.katch.NotFound
import com.ahparhizgar.katch.RateLimitReached
import com.ahparhizgar.katch.ServerError
import com.ahparhizgar.katch.Unauthorized
import com.base.usermessage.UiString

@Suppress("UNCHECKED_CAST")
val ApiCallError.fieldErrors: Map<String, String>
  get() = (payload as? Map<String, String>).orEmpty()

val ApiCallError.userMessage: UiString
  get() =
      when (this) {
        is BadRequest -> UiString(Res.string.error_invalid_request)
        is Unauthorized -> UiString(Res.string.error_unauthorized)
        is Forbidden -> UiString(Res.string.error_forbidden)
        is NotFound -> UiString(Res.string.error_not_found)
        is RateLimitReached -> UiString(Res.string.error_rate_limit)
        is ClientError ->
            userMessage?.let { UiString(it) } ?: UiString(Res.string.error_client_generic)
        is NetworkError -> UiString(Res.string.error_network_disconnected)
        is ServerError -> UiString(Res.string.error_server_generic)
        is InvalidDataError ->
            message?.let { UiString(it) } ?: UiString(Res.string.error_invalid_data)
        else -> message?.let { UiString(it) } ?: UiString(Res.string.error_unknown)
      }

val errorNetworkDisconnectedUiString: UiString = UiString(Res.string.error_network_disconnected)
