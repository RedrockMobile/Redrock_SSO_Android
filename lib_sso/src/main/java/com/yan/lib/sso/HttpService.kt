package com.yan.lib.sso

import android.util.Log
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.BindException
import java.util.*

object HttpService {

    private var mOnRequestHandle : ((session : String?, code : String?) -> Unit)? = null

    private var mOnRequestFinish : (() -> Unit)? = null

    private val httpserver by lazy {
        embeddedServer(Netty, port = 53456){
            routing {
                get("/redrock"){
                    call.respondText("Hello Redrocker", ContentType.Text.Plain)
                    mOnRequestHandle?.invoke(call.parameters["session_state"],call.parameters["code"])
                    mOnRequestFinish?.invoke()
                }
            }
        }
    }

    fun setOnRequestHandle(onRequestHandler : ((session : String?,code : String?) -> Unit)?){
        mOnRequestHandle = onRequestHandler
    }

    fun setOnRequestFinish(onRequestFinish :()->Unit){
        mOnRequestFinish = onRequestFinish
    }

    fun start(){
        try {
            CoroutineScope(Dispatchers.IO).launch {
                Log.i("HttpService开启:","${Date(System.currentTimeMillis()).time}")
                httpserver.start(true)
            }

        }catch (_: BindException){
        }
    }

    fun stop(){
        httpserver.stop(1_000,3_000)
        Log.i("HttpService关闭:","${Date(System.currentTimeMillis()).time}")
    }


}