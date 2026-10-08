package com.base.usermessage

import androidx.compose.material3.SnackbarResult
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch

interface MessageShower {
  suspend fun showSnackbarForResult(message: SnackbarMessage): SnackbarResult

  suspend fun showSnackbar(message: SnackbarMessage)
}

class MessageHolder : MessageShower {
  private val messages: Channel<Request> = Channel(capacity = Channel.RENDEZVOUS)

  override suspend fun showSnackbarForResult(message: SnackbarMessage): SnackbarResult {
    val request =
        Request(
            input = message,
            response = CompletableDeferred(),
            job = currentCoroutineContext()[Job],
        )
    messages.send(request)
    return request.response.await()
  }

  override suspend fun showSnackbar(message: SnackbarMessage) {
    messages.send(Request(input = message, response = CompletableDeferred(), job = null))
  }

  suspend fun showAll(block: suspend (SnackbarMessage) -> SnackbarResult) {
    coroutineScope {
      var message: Request? = null
      while (true) {
        message =
            message
                ?: run {
                  messages.receive()
                }
        val currentMessage =
            checkNotNull(message) { "Message request is required before processing snackbar." }
        var a: Deferred<Unit>? = null
        val jobScope =
            CoroutineScope(
                currentCoroutineContext() +
                    if (currentMessage.job != null) currentMessage.job else EmptyCoroutineContext
            )
        val worker =
            jobScope.launch(start = CoroutineStart.UNDISPATCHED) {
              try {
                val result = block(currentMessage.input)
                currentMessage.response.complete(result)
                a?.cancel()
              } catch (e: CancellationException) {
                a?.cancelAndJoin()
                throw e
              } finally {
                message = null
              }
            }

        a =
            async(start = CoroutineStart.UNDISPATCHED) {
              val next = messages.receive()
              if (next.input.message == currentMessage.input.message) {
                try {
                  currentMessage.response.cancel(CancellationException("Cancelled by next message"))
                  worker.cancelAndJoin()
                } finally {
                  message = next.apply { input.shouldShake = true }
                }
              }
            }

        worker.join()
        a.join()
      }
    }
  }
}

data class Request(
    val input: SnackbarMessage,
    val response: CompletableDeferred<SnackbarResult>,
    val job: Job?,
)
