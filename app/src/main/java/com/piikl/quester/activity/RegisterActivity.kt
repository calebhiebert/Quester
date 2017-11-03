package com.piikl.quester.activity

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.piikl.quester.R
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.QuesterService
import com.piikl.quester.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class RegisterActivity : ValidatorActivity() {

    @Length(min = 3, max = 255, trim = true)
    @NotEmpty(message = "Please enter a username")
    private lateinit var usernameInput: EditText

    @Length(min = 1, max = 255, trim = true)
    @NotEmpty(message = "Please enter a password")
    private lateinit var passwordInput: EditText

    private lateinit var registerButton: Button

    private lateinit var service: QuesterService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameInput = findViewById(R.id.txtUsername)
        passwordInput = findViewById(R.id.txtPassword)
        registerButton = findViewById(R.id.btnRegister)

        registerButton.setOnClickListener { validator.validate() }

        service = Retrofit.Builder()
                .baseUrl(PreferenceManager.getDefaultSharedPreferences(this).getString("api_url", null))
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(QuesterService::class.java)
    }

    override fun onValidationSucceeded() {
        val user = User()
        user.name = usernameInput.text.toString().trim()
        user.password = passwordInput.text.toString().trim()

        service.register(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>?, response: Response<User>) {
                when(response.code()) {
                    200 -> {
                        val prefs = PreferenceManager.getDefaultSharedPreferences(this@RegisterActivity)
                        val edit = prefs.edit()
                        edit.putString("username", user.name)
                        edit.putString("password", user.password)
                        edit.apply()

                        setResult(Activity.RESULT_OK)
                        finish()
                    }

                    406 -> {
                        usernameInput.error = "That username is already taken"
                    }

                    else -> ErrorHandler.handleErrors(this@RegisterActivity, response.errorBody()!!)
                }
            }

            override fun onFailure(call: Call<User>?, t: Throwable) {
                ErrorHandler.handleErrors(this@RegisterActivity, t)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        setResult(Activity.RESULT_CANCELED)
    }
}
