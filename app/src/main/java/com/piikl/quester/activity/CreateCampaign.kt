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
import com.piikl.quester.api.Campaign
import com.piikl.quester.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateCampaign : AppCompatActivity(), Validator.ValidationListener {

    @NotEmpty
    @Length(min = 4, max = 255, trim = true)
    lateinit var nameInput: EditText

    lateinit var validator: Validator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campaign)
        title = "Create Campaign"
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

    private fun save() {
        validator.validate()
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

    override fun onValidationSucceeded() {
        Toast.makeText(this, "Validation passes", Toast.LENGTH_SHORT).show()

        val campaign = Campaign()
        campaign.name = nameInput.text.toString()

        val creator = User()
        creator.id = 1

        campaign.creator = creator

        MainActivity.questerService.createCampaign(campaign.name!!).enqueue(object : Callback<Campaign> {
            override fun onFailure(call: Call<Campaign>?, t: Throwable) {
                throw t
            }

            override fun onResponse(call: Call<Campaign>?, response: Response<Campaign>) {
                if(response.code() == 200) {
                    finish()
                } else {
                    Toast.makeText(this@CreateCampaign, response.message(), Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
