package com.redrock.sso

/**
 * .
 *
 * @author 985892345
 * 2023/3/8 19:40
 */
internal data class Request(
  val method: String,
  val uri: String,
  val version: String,
  val headers: Map<String, String>,
  val message: String?
)
