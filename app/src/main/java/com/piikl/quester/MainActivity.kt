package com.piikl.quester

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import com.piikl.quester.api.Campaign
import com.piikl.quester.api.QuesterService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var campaignRecyclerView: RecyclerView
    lateinit var adapter: CampaignListAdapter
    lateinit var loader: ProgressBar

    companion object {
        val questerService = Retrofit.Builder()
                .baseUrl("http://192.168.1.111:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(QuesterService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        campaignRecyclerView = findViewById(R.id.recMainCampaignDisplay)
        loader = findViewById(R.id.ldgMainLoader)

        val layoutManager = LinearLayoutManager(this)
        campaignRecyclerView.layoutManager = layoutManager

        adapter = CampaignListAdapter()
        campaignRecyclerView.adapter = adapter

        updateData()
    }

    override fun onResume() {
        super.onResume()
        updateData()
    }

    fun onCampaignSelected(campaign: Campaign) {
        val intent = Intent(this, CampaignView::class.java)
        intent.putExtra("campaign_id", campaign.id)

        startActivity(intent)
    }

    private fun updateData() {
        questerService.listCampaigns().enqueue(object: Callback<List<Campaign>> {
            override fun onResponse(call: Call<List<Campaign>>, response: Response<List<Campaign>>) {
                adapter.data = response.body()
                loader.visibility = View.GONE
            }

            override fun onFailure(call: Call<List<Campaign>>, t: Throwable) {
                throw t
            }
        })
    }
}
