package com.piikl.quester.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator

abstract class ValidatorActivity : CustomActivity(), Validator.ValidationListener {

    protected lateinit var validator: Validator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        validator = Validator(this)
        validator.setValidationListener(this)
    }

    override fun onValidationFailed(errors: MutableList<ValidationError>) {
        errors.forEach {
            val message = it.getCollatedErrorMessage(this)

            if(it.view is EditText) {
                (it.view as EditText).error = message
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onValidationSucceeded() { }
}