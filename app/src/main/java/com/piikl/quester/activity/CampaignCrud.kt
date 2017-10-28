package com.piikl.quester.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.mobsandgeeks.saripaar.ValidationError
import com.mobsandgeeks.saripaar.Validator
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.piikl.quester.R

abstract class CampaignCrud : AppCompatActivity(), Validator.ValidationListener {

    @NotEmpty
    @Length(min = 4, max = 255, trim = true)
    lateinit var nameInput: EditText

    lateinit protected var validator: Validator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_crud)
        validator = Validator(this)
        validator.setValidationListener(this)

        nameInput = findViewById(R.id.edtCreateCampaignName)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_campaign, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.mnuCreateCampaignSave -> save()
        }

        return true
    }

    abstract fun save()

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

    override fun onValidationSucceeded() {

    }
}
