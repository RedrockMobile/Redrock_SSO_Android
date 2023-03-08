package com.redrock.sso

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.ServerSocket

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
        val socket = serverSocket.accept()
        val request = Http.parse2request(socket.getInputStream())
        socket.close()
        serverSocket.close()
        val uriParam = request.uri
          .substring(2, request.uri.length) // 去掉前面的 /?
          .split("&")
          .associate {
            val index = it.indexOf("=") // 等号的索引
            it.substring(0, index) to it.substring(index + 1, it.length)
          }
        val data = SSOUtils.Data(uriParam.getValue("session_state"), uriParam.getValue("code"))
        continuation.resumeWith(Result.success(data))
        continuation.invokeOnCancellation {
          socket.close()
          serverSocket.close()
        }
      }
    }
  }
}