package com.redrock.sso

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

/**
 * .
 *
 * @author 985892345
 * 2023/3/8 19:37
 */
internal object Server {
  suspend fun get(): SSOUtils.Data {
    return withContext(Dispatchers.IO) {
      suspendCancellableCoroutine { continuation ->
        val serverSocket = ServerSocket(53456)
        continuation.invokeOnCancellation {
          serverSocket.close()
        }
        try {
          val socket = serverSocket.accept()
          socket.close()
          serverSocket.close()
          continuation.resumeWith(Result.success(getData(socket)))
        } catch (_: SocketException) {
          // socket 被 serverSocket cancel 时抛出的异常
        }
      }
    }
  }

  private fun getData(socket: Socket): SSOUtils.Data {
    val request = Http.parse2request(socket.getInputStream())
    val uriParam = request.uri
      .substring(2, request.uri.length) // 去掉前面的 [表情]
      .split("&")
      .associate {
        val index = it.indexOf("=") // 等号的索引
        it.substring(0, index) to it.substring(index + 1, it.length)
      }
    return SSOUtils.Data(uriParam.getValue("session_state"), uriParam.getValue("code"))
  }
}