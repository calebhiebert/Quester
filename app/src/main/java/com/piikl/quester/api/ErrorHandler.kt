package com.piikl.quester.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.ResponseBody
import java.io.IOException
import java.net.SocketTimeoutException

class ErrorHandler {
    companion object {

        private val mapper = ObjectMapper()

        fun handleErrors(ctx: Context, t: Throwable) {
            when (t) {
                is SocketTimeoutException -> Toast.makeText(ctx, "Could not connect to the api", Toast.LENGTH_LONG).show()

                is IOException -> {}

                else -> throw t
            }
        }

        fun handleErrors(ctx: Context, errorBody: ResponseBody): ApiError? {
            val json = errorBody.string()
            Log.d("Error response", json)
            val err = try { mapper.readValue(json, ApiError::class.java) } catch (e: Exception) { e.printStackTrace(); null }

            if(err != null)
                err.makeToast(ctx)

            return err
        }

        fun ApiError?.makeToast(ctx: Context) {
            Toast.makeText(ctx, "${this?.status}: ${this?.message}", Toast.LENGTH_LONG).show()
        }
    }
}