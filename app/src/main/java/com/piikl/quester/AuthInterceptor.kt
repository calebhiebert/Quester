package com.piikl.quester

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val username: String, private val password: String) : Interceptor {

    private var credentials = Credentials.basic(username, password)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val authRequest = request.newBuilder()
                .header("Authorization", credentials).build()

        return chain.proceed(authRequest)
    }
}