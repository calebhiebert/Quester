package com.piikl.quester.activity

import android.os.Bundle
import android.widget.Toast
import com.piikl.quester.api.Campaign
import com.piikl.quester.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateCampaign : CampaignCrud() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Create Campaign"
    }

    override fun save() {
        validator.validate()
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
