package com.redrock.sso

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class BasicAuthInterceptor : Interceptor {
    private val credentials: String = Credentials.basic("zscy", "YaE1eoBZrYrfoDCM1mllsgmPhR1eJRR6")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: okhttp3.Request = chain.request()
        val authenticatedRequest: okhttp3.Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}