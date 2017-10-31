package com.piikl.quester.activity

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Length
import com.piikl.quester.R
import com.piikl.quester.api.ErrorHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class LoginActivity : CustomActivity(), Validator.ValidationListener {

    @Length(min = 3, max = 255, trim = true)
    lateinit var username: EditText

    @Length(min = 1, max = 255, trim = true)
    lateinit var password: EditText

    lateinit var loginButton: Button

    private val validator = Validator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.txtUsername)
        password = findViewById(R.id.txtPassword)
        loginButton = findViewById(R.id.btnLogIn)
        validator.setValidationListener(this)

        loginButton.setOnClickListener({
            validator.validate()
        })
    }

    private fun login() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val uname = username.text.toString().trim()
        val pword = password.text.toString().trim()

        MainActivity.createApiClient(prefs.getString("api_url", null), uname, pword)

        MainActivity.questerService!!.ping().enqueue(object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>?, t: Throwable) {
                ErrorHandler.handleErrors(this@LoginActivity, t)
            }

            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>) {
                when (response.code()) {
                    200 -> {
                        val edit = prefs.edit()
                        edit.putString("username", uname)
                        edit.putString("password", pword)
                        edit.commit()
                        finish()
                    }

                    401 -> {
                        username.error = "Incorrect username or password"
                        password.error = "Incorrect username or password"
                    }

                    else -> {
                        Toast.makeText(this@LoginActivity, "Login failed with code ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>?) {
        username.error = "Bad Login"
        password.error = "Bad Login"
    }

    override fun onValidationSucceeded() {
        login()
    }
}
