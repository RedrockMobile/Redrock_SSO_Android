package com.redrock.sso

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import kotlin.coroutines.resume

/**
 * .
 *
 * @author 985892345
 * 2023/2/28 16:27
 */
object SSOUtils {
  
  data class Data(
    val session: String,
    val code: String
  )
  
  /**
   * ## 注意事项
   * - 虽然没有单独提供取消的方法，但在你需要取消协程时会自动取消
   *
   * @param parent 父布局，WebView 将会显示在该布局中，在登录成功或者协程被取消时移除 WebView
   */
  suspend fun login(context: Context, parent: ViewGroup): Data {
    val webView = createWebView(context, parent)
    try {
      val data = createServer()
      parent.removeView(webView)
      webView.destroy()
      return data
    } catch (e: Exception) {
      // 这里会包含协程被取消的异常
      parent.removeView(webView)
      webView.destroy()
      throw e
    }
  }
  
  private fun createWebView(context: Context, parent: ViewGroup): WebView {
    return WebView(context).apply {
      webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
          view: WebView,
          request: WebResourceRequest
        ): Boolean {
          val url = request.url.toString()
          if (!url.startsWith("http")) {
            try {
              context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
              return true
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
          return super.shouldOverrideUrlLoading(view, request)
        }
      }
      // 自适应屏幕
      settings.useWideViewPort = true
      settings.loadWithOverviewMode = true
      // 支持缩放
      settings.setSupportZoom(true)
      settings.builtInZoomControls = true
      // 隐藏缩放控件
      settings.displayZoomControls = false
      loadUrl("https://sso.redrock.team/auth/realms/master/protocol/openid-connect/auth?client_id=zscy&response_type=code&redirect_uri=http://localhost:53456/redrock")
      parent.addView(
        this,
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
      )
    }
  }
  
  private suspend fun createServer(): Data = withContext(Dispatchers.IO) {
    suspendCancellableCoroutine { continuation ->
      var server: NettyApplicationEngine? = null
      server = embeddedServer(Netty, port = 53456) {
        routing {
          get("/redrock") {
            call.respondText("Hello Redrocker", ContentType.Text.Plain)
            val session = call.parameters["session_state"]!!
            val code = call.parameters["code"]!!
            continuation.resume(Data(session, code))
            server?.stop(0, 0)
          }
        }
      }
      server.start()
      continuation.invokeOnCancellation {
        server.stop(0, 0)
      }
    }
  }
}