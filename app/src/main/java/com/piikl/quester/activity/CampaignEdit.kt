package com.piikl.quester.activity

import android.os.Bundle
import android.widget.Toast
import com.piikl.quester.api.Campaign
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CampaignEdit : CampaignCrud() {

    lateinit protected var campaign: Campaign

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Edit Campaign"

        val json = intent.getStringExtra("campaign_json")
        campaign = MainActivity.mapper.readValue(json, Campaign::class.java)
        onDataLoaded()

        if(campaign.id == 0L)
            finish()
    }

    private fun onDataLoaded() {
        nameInput.setText(campaign.name)
    }

    override fun onValidationSucceeded() {
        val c = Campaign()
        c.name = nameInput.text.toString().trim()

        MainActivity.questerService.editCampaign(campaign.id, c).enqueue(object : Callback<Campaign> {
            override fun onResponse(call: Call<Campaign>?, response: Response<Campaign>) {
                when(response.code()) {
                    200 -> {
                        finish()
                    }

                    else -> Toast.makeText(this@CampaignEdit, "Got code of ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Campaign>?, t: Throwable) {
                throw t
            }
        })
    }
}
