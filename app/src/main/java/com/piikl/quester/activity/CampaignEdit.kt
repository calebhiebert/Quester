package com.piikl.quester.activity

import android.os.Bundle
import android.widget.Toast
import com.piikl.quester.api.Campaign
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CampaignEdit : CampaignCrud() {

    private var campaign: Campaign? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Edit Campaign"

        val id = intent.getLongExtra("campaign_id", 0)
        val name = intent.getStringExtra("campaign_name")
        nameInput.setText(name)

        if(id == 0L)
            finish()
        else {
            loadData(id)
        }
    }


    override fun save() {

    }

    private fun onDataLoaded() {
        nameInput.setText(campaign?.name)
    }

    private fun loadData(id: Long) {
        MainActivity.questerService.getCampaign(id.toString()).enqueue(object : Callback<Campaign> {
            override fun onFailure(call: Call<Campaign>?, t: Throwable) {
                throw t
            }

            override fun onResponse(call: Call<Campaign>?, response: Response<Campaign>) {
                when (response.code()) {
                    200 -> {
                        campaign = response.body()
                        onDataLoaded()
                    }

                    404 -> {
                        finish()
                    }

                    else -> Toast.makeText(this@CampaignEdit, "Got code of ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
