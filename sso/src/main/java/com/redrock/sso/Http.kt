package com.redrock.sso

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Authenticator
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

/**
 * .
 *
 * @author 985892345
 * 2023/3/8 19:42
 */
internal object Http {

  val OkHttpClient = okhttp3.OkHttpClient.Builder()
    .connectTimeout(10,TimeUnit.SECONDS)
    .readTimeout(30,TimeUnit.SECONDS)
    .writeTimeout(30,TimeUnit.SECONDS)
    .addInterceptor(BasicAuthInterceptor())
    .build()

  val JSON: MediaType = "application/json".toMediaType()
  
  fun parse2request(input: InputStream): Request {
    val reader = BufferedReader(InputStreamReader(input, "UTF-8"))
    return decode(reader)
  }
  
  private fun decode(reader: BufferedReader): Request {
    // 解析状态行
    val status = reader.readLine().split(" ")
    val method = status[0]
    val uri = status[1]
    val version = status[2]
    
    // 解析 header
    val headers = hashMapOf<String, String>()
    var line = reader.readLine()
    while (line != "") {
      val kv = line.split(":")
      headers[kv[0].trim()] = kv[1].trim()
      line = reader.readLine()
    }
    
    // 解析正文
    val contentLen = headers.getOrDefault("Content-Length", "0").toInt()
    val message = if (contentLen == 0) null else {
      val charArray = CharArray(contentLen)
      reader.read(charArray)
      String(charArray)
    }
    
    return Request(method, uri, version, headers, message)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun okhttp3.Call.awaitResponse(): okhttp3.Response {

    return suspendCancellableCoroutine {

      it.invokeOnCancellation {
        //当协程被取消的时候，取消网络请求
        cancel()
      }

      enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: IOException) {
          it.resumeWithException(e)
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
          it.resume(response){

          }
        }
      })
    }
  }
}