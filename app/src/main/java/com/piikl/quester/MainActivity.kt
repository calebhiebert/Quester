package com.piikl.quester

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        campaignRecyclerView = findViewById(R.id.recMainCampaignDisplay)

        val layoutManager = LinearLayoutManager(this)
        campaignRecyclerView.layoutManager = layoutManager

        adapter = CampaignListAdapter()
        campaignRecyclerView.adapter = adapter

        val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.1.111:8080")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()

        val service = retrofit.create(QuesterService::class.java)

        service.listCampaigns().enqueue(object: Callback<List<Campaign>> {
            override fun onResponse(call: Call<List<Campaign>>, response: Response<List<Campaign>>) {
                response.body()!!.forEach {
                    Log.d("QuestData", "Got quest ${it.name} ${it.id}")
                }

                adapter.data = response.body()
            }

            override fun onFailure(call: Call<List<Campaign>>, t: Throwable) {
                throw t
            }
        })
    }

    fun onCampaignSelected(campaign: Campaign) {
        Toast.makeText(this, "Campaign selected ${campaign.id}", Toast.LENGTH_SHORT).show()
    }
}
