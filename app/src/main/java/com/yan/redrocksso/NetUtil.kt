package com.yan.redrocksso

import android.util.Log
import okhttp3.*
import java.io.IOException

/**
 * 建立网络工具类
 */

//class NetUtil {
//    fun urlGet(url : String){
//        val client = OkHttpClient()
//        val request = Request.Builder()
//            .url(url)
//            .get()
//            .build()
//        client.newCall(request).enqueue(object : Callback{
//            override fun onFailure(call: Call, e: IOException) {
//                Log.e("fail",call.toString())
//                Log.e("fail",e.toString())
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//                Log.e("success",call.toString())
//                Log.e("success",response.toString())
//            }
//
//        })
//    }
//}