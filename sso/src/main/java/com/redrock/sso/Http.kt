package com.redrock.sso

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * .
 *
 * @author 985892345
 * 2023/3/8 19:42
 */
internal object Http {
  
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
}