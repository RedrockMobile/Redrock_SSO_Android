package com.redrock.sso

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import java.util.*

object Jwt {

    @SuppressLint("NewApi")
    fun parseToken(token : String) : SsoAccount{
        val chunks: List<String> = token.split(".")
        val decoder: Base64.Decoder = Base64.getUrlDecoder()
        val payload: String = String(decoder.decode(chunks[1]))
        return Gson().fromJson(payload,SsoAccount::class.java)
    }
}