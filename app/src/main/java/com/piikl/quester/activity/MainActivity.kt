package com.piikl.quester.activity

import android.content.Intent
import android.content.SharedPreferences
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
import com.piikl.quester.api.ErrorHandler
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
        var questerService: QuesterService? = null

        val mapper = ObjectMapper()

        fun createApiClient(url: String, username: String, password: String) {
            val client = OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(username, password))
                    .build()

            questerService = Retrofit.Builder()
                    .client(client)
                    .baseUrl(url)
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build().create(QuesterService::class.java)
        }
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

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        createApi(prefs)

        prefs.registerOnSharedPreferenceChangeListener({ pref, value ->
            when(value) {
                "api_url" -> {
                    createApiClient(pref.getString("api_url", null), pref.getString("username", null), pref.getString("password", null))
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        if(questerService != null) {
            val item = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.log_out)
            item.setOnMenuItemClickListener {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val edit = prefs.edit()
                edit.remove("username")
                edit.remove("password")
                edit.commit()
                recreate()
                true
            }
        }

        menuInflater.inflate(R.menu.menu_campaign_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

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
        }

        return true
    }

    fun onCampaignSelected(campaign: Campaign) {
        val intent = Intent(this, CampaignView::class.java)
        intent.putExtra("campaign_id", campaign.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()

        if(questerService == null) {
            createApi(PreferenceManager.getDefaultSharedPreferences(this))
        }

        loader.visibility = View.VISIBLE
        updateData()
    }

    fun createApi(prefs: SharedPreferences) {
        val apiUrl: String? = try { prefs.getString("api_url", null) } catch (e: Exception) { e.printStackTrace(); null }
        val username: String? = try { prefs.getString("username", null) } catch (e: Exception) { e.printStackTrace(); null }
        val password: String? = try { prefs.getString("password", null) } catch (e: Exception) { e.printStackTrace(); null }

        if(password.isNullOrEmpty() || username.isNullOrEmpty()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            return
        }

        if(apiUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Api url is not set", Toast.LENGTH_LONG).show()
            return
        }

        createApiClient(apiUrl!!, username!!, password!!)
    }

    private fun updateData() {
        if(questerService == null)
            return

        questerService!!.listCampaigns().enqueue(object: Callback<List<Campaign>> {
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
                ErrorHandler.handleErrors(this@MainActivity, t)
            }
        })
    }
}
