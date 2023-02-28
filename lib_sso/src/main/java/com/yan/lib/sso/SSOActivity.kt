package com.yan.lib.sso

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup.LayoutParams
import android.webkit.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import io.ktor.utils.io.*

class SSOActivity : AppCompatActivity(){

    companion object{
        fun startSSOActivity(context: Context,onRequestHandle : ((session : String?,code : String?) -> Unit)?){
            context.startActivity(Intent(context,SSOActivity::class.java))
            HttpService.setOnRequestHandle(onRequestHandle)
        }
    }


    private lateinit var webView: WebView
    private val ssoUrl = "https://sso.redrock.team/auth/realms/master/protocol/openid-connect/auth?client_id=zscy&response_type=code&redirect_uri=http://localhost:53456/redrock"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootView = ConstraintLayout(this)
        rootView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
        webView = WebView(this)
        rootView.addView(webView,LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT))
        setContentView(rootView)

        initWebView()
        webView.apply {
            loadUrl(ssoUrl)
        }
        HttpService.setOnRequestFinish {
            this.finish()
        }

    }
    /**
     * 对WebView的初始化操作
     */
    private fun initWebView(){
        val settings: WebSettings = webView.settings

        // 支持JS
        true.also { settings.javaScriptEnabled = it }

        // 自适应屏幕
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        // 支持缩放
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true

        // 隐藏缩放控件
        settings.displayZoomControls = false

        // 开启Dom存储功能
        settings.domStorageEnabled = true

        // 开启文件访问
        settings.allowFileAccess = true
        settings.loadsImagesAutomatically = true
        settings.javaScriptCanOpenWindowsAutomatically = true

        // 设置缓存策略
        if (isConnected(this)) {
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        } else {
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }

        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            // 页面开始加载时调用，如显示加载圈
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            // 页面加载完成时调用，如隐藏加载圈
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            // 页面出错时调用，可在此时加载失败时的页面
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
            }

            //重定向更改
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val hit = webView.hitTestResult
                //hit.getExtra()为null或者hit.getType() == 0都表示即将加载的URL会发生重定向，需要做拦截处理
                if (TextUtils.isEmpty(hit.extra) || hit.type == 0) {
                    //通过判断开头协议就可解决大部分重定向问题了，有另外的需求可以在此判断下操作
                    Log.e("重定向", "重定向: " + hit.type + " && EXTRA（）" + hit.extra + "------")
                    Log.e("重定向", "GetURL: " + view?.url + "\n" +"getOriginalUrl()"+ webView.originalUrl)
                    Log.d("重定向", "URL: $url")
                }

                if (url!!.startsWith("http://") || url.startsWith("https://")) { //加载的url是http/https协议地址
                    webView.loadUrl(url)
                    return false; //返回false表示此url默认由系统处理,url未加载完成，会继续往下走

                } else { //加载的url是自定义协议地址
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                    } catch (e : java.lang.Exception
                    ) {
                        e.printStackTrace()
                    }
                    return true
                }
            }

        }

        webView.webChromeClient = object : WebChromeClient() {

          //获得WebView的标题
          override fun onReceivedTitle(view: WebView, webTitle: String) {
            super.onReceivedTitle(view, webTitle)
          }

          // 获取页面加载的进度
          override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

          }
        }
    }

    /**
     * 检查网络是否连接-
     * @param context
     * @return
     */
    private fun isConnected(context: Context): Boolean {
        var isNetUsable = false
        val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        if (networkCapabilities != null) {
            isNetUsable =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }
        return isNetUsable
    }

}
