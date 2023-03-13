package com.redrock.sso

import android.util.Log
import com.redrock.sso.Http.awaitResponse
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.Request
import java.io.IOException
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
        var socket : Socket? = null
        try {
          socket = serverSocket.accept()
          continuation.resumeWith(Result.success(getData(socket)))
        } catch (_: SocketException) {
          // socket 被 serverSocket cancel 时抛出的异常
        } finally {
          socket?.close()
          serverSocket.close()
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

  suspend fun getToken(code : String) : String?{
    var token : String? = null
    val formBody: RequestBody = FormBody.Builder()
      .add("grant_type","authorization_code")
      .add("redirect_uri", "http://localhost:53456")
      .add("code", code)
      .build()

    val request: Request = Request.Builder()
      .url("https://sso.redrock.team/auth/realms/master/protocol/openid-connect/token")
      .post(formBody)
      .build()

    val response = Http.OkHttpClient.newCall(request).awaitResponse()
    token = response.body?.string()
    return token
  }
}