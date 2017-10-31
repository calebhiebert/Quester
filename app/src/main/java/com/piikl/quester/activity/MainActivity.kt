package com.piikl.quester.activity

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.fasterxml.jackson.databind.ObjectMapper
import com.piikl.quester.AuthInterceptor
import com.piikl.quester.R
import com.piikl.quester.adapter.CampaignListAdapter
import com.piikl.quester.api.Campaign
import com.piikl.quester.api.QuesterService
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class MainActivity : CustomActivity() {

    lateinit var campaignRecyclerView: RecyclerView
    lateinit var adapter: CampaignListAdapter
    lateinit var loader: ProgressBar

    companion object {
        private val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor("Panchem", "Tapemeasure1"))
                .build()

        lateinit var questerService: QuesterService

        val mapper = ObjectMapper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiUrl: String? = try { PreferenceManager.getDefaultSharedPreferences(this).getString("api_url", "http://10.0.2.2:8080") } catch (e: Exception) { e.printStackTrace(); null }
        if(apiUrl != null) {
            questerService = Retrofit.Builder()
                    .client(client)
                    .baseUrl(apiUrl)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build().create(QuesterService::class.java)

            campaignRecyclerView = findViewById(R.id.recMainCampaignDisplay)
            loader = findViewById(R.id.ldgMainLoader)

            val layoutManager = LinearLayoutManager(this)
            campaignRecyclerView.layoutManager = layoutManager

            adapter = CampaignListAdapter()
            campaignRecyclerView.adapter = adapter

            updateData()
        } else {
            Toast.makeText(this, "Could not get api url", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_campaign_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mnuCampaignListCreateNew -> {
                val intent = Intent(this, CampaignCreate::class.java)
                startActivity(intent)
            }

            R.id.mnuCampaignListRefresh -> {
                loader.visibility = View.VISIBLE
                campaignRecyclerView.visibility = View.INVISIBLE
                updateData()
            }

            R.id.mnuSettings -> openSettings()
        }

        return true
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
                when (response.code()) {
                    200 -> {
                        adapter.data = response.body()
                        loader.visibility = View.GONE
                        campaignRecyclerView.visibility = View.VISIBLE
                    }

                    else -> {
                        Toast.makeText(this@MainActivity, "Failed to load campaigns with code ${response.code()}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<List<Campaign>>, t: Throwable) {
                throw t
            }
        })
    }
}
