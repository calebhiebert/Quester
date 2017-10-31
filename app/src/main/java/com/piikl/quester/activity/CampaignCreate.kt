package com.piikl.quester.activity

import android.os.Bundle
import android.widget.Toast
import com.piikl.quester.api.Campaign
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CampaignCreate : CampaignCrud() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Create Campaign"
    }

    override fun onValidationSucceeded() {
        val campaign = Campaign()
        campaign.name = nameInput.text.toString()

        MainActivity.questerService.createCampaign(campaign).enqueue(object : Callback<Campaign> {
            override fun onFailure(call: Call<Campaign>?, t: Throwable) {
                throw t
            }

            override fun onResponse(call: Call<Campaign>?, response: Response<Campaign>) {
                when(response.code()) {
                    200 -> finish()
                    else -> Toast.makeText(this@CampaignCreate, "Creating campaign failed with code ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
