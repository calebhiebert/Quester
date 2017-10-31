package com.piikl.quester.api

import android.content.Context
import android.widget.Toast
import java.net.SocketTimeoutException

class ErrorHandler {
    companion object {
        fun handleErrors(ctx: Context, t: Throwable) {
            when (t) {
                is SocketTimeoutException -> Toast.makeText(ctx, "Could not connect to the api", Toast.LENGTH_LONG).show()
                else -> throw t
            }
        }
    }
}