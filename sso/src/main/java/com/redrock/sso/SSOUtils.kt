package com.redrock.sso

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.gson.Gson

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
   * 获得用户token
   */
  suspend fun token(code : String) : Token? {
    val token = Server.getToken(code) ?: return null
    return Gson().fromJson(token,Token::class.java)
  }
  
  /**
   * ## 注意事项
   * - 虽然没有单独提供取消的方法，但在你需要取消协程时会自动取消
   *
   * @param parent 父布局，WebView 将会显示在该布局中，在登录成功或者协程被取消时移除 WebView
   */
  suspend fun login(parent: ViewGroup): Data {
    val webView = createWebView(parent.context, parent)
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
      // 支持JS
      true.also { settings.javaScriptEnabled = it }

      // 开启Dom存储功能
      settings.domStorageEnabled = true

      // 自适应屏幕
      settings.useWideViewPort = true
      settings.loadWithOverviewMode = true
      // 支持缩放
      settings.setSupportZoom(true)
      settings.builtInZoomControls = true
      // 隐藏缩放控件
      settings.displayZoomControls = false
      loadUrl("https://sso.redrock.team/auth/realms/master/protocol/openid-connect/auth?client_id=zscy&response_type=code&redirect_uri=http://localhost:53456")
      parent.addView(
        this,
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
      )
    }
  }
  
  private suspend fun createServer(): Data {
    return Server.get()
  }
}