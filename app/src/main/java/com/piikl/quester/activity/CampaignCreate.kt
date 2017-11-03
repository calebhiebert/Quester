package com.piikl.quester.activity

import android.os.Bundle
import android.widget.Toast
import com.piikl.quester.api.Campaign
import com.piikl.quester.api.ErrorHandler
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

        MainActivity.questerService!!.createCampaign(campaign).enqueue(object : Callback<Campaign> {
            override fun onFailure(call: Call<Campaign>?, t: Throwable) {
                ErrorHandler.handleErrors(this@CampaignCreate, t)
            }

            override fun onResponse(call: Call<Campaign>?, response: Response<Campaign>) {
                when (response.code()) {
                    200 -> finish()
                    else -> ErrorHandler.handleErrors(this@CampaignCreate, response.errorBody()!!)
                }
            }
        })
    }
}
