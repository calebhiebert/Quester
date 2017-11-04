package com.piikl.quester.activity

import android.app.Activity
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
import com.fasterxml.jackson.databind.ObjectMapper
import com.piikl.quester.AuthInterceptor
import com.piikl.quester.R
import com.piikl.quester.adapter.CampaignListAdapter
import com.piikl.quester.api.Campaign
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.QuesterService
import com.piikl.quester.api.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class MainActivity : CustomActivity() {

    private val DEFAULT_API_URL = "https://questerv1.piikl.com"
    private val LOGIN_REQUEST_CODE = 1

    lateinit var campaignRecyclerView: RecyclerView
    lateinit var adapter: CampaignListAdapter
    lateinit var loader: ProgressBar

    companion object {
        var questerService: QuesterService? = null

        val mapper = ObjectMapper()

        fun createApiClient(url: String?, username: String, password: String): QuesterService? {
            if (url != null) {
                val client = OkHttpClient.Builder()
                        .addInterceptor(AuthInterceptor(username, password))
                        .build()

                val retrofit = Retrofit.Builder()
                        .client(client)
                        .baseUrl(url)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build().create(QuesterService::class.java)

                questerService = retrofit

                return retrofit
            }

            return null
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
        var apiUrl = getApiUrl(prefs)

        if(apiUrl == null) {
            val edit = prefs.edit()
            edit.putString("api_url", DEFAULT_API_URL)
            edit.commit()
            apiUrl = DEFAULT_API_URL
        }

        val user = getUsernamePass(prefs)
        if(user == null) {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
            return
        }

        createApiClient(apiUrl, user.name, user.password)

        prefs.registerOnSharedPreferenceChangeListener({ pref, value ->
            when (value) {
                "api_url" -> {
                    if(pref.getString("username", null) != null && pref.getString("password", null) != null)
                        createApiClient(pref.getString("api_url", null), pref.getString("username", null), pref.getString("password", null))
                }
            }
        })

        if(prefs.getBoolean("reopen_last_campaign", false)) {
            val lastCampaignId = prefs.getLong("last_campaign_id", 0)

            if(lastCampaignId != 0L) {
                val intent = Intent(this, CampaignView::class.java)
                intent.putExtra("campaign_id", lastCampaignId)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        if (questerService != null) {
            val item = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, R.string.log_out)
            item.setOnMenuItemClickListener {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val edit = prefs.edit()
                edit.remove("username")
                edit.remove("password")
                edit.commit()
                questerService = null
                recreate()
                true
            }
        }

        menuInflater.inflate(R.menu.menu_campaign_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == LOGIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            recreate()
        }
    }

    override fun onResume() {
        super.onResume()

        if (questerService == null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val user = getUsernamePass(prefs)
            val apiUrl = getApiUrl(prefs)

            if(user != null && apiUrl != null) {
                createApiClient(apiUrl, user.name, user.password)
            }
        }

        loader.visibility = View.VISIBLE
        updateData()
    }

    private fun getUsernamePass(prefs: SharedPreferences): User? {
        val username: String? = try {
            prefs.getString("username", null)
        } catch (e: Exception) {
            e.printStackTrace(); null
        }
        val password: String? = try {
            prefs.getString("password", null)
        } catch (e: Exception) {
            e.printStackTrace(); null
        }

        if(!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            val user = User()
            user.name = username!!
            user.password = password!!
            return user
        }

        return null
    }

    private fun getApiUrl(prefs: SharedPreferences): String? {
        return try {
            prefs.getString("api_url", null)
        } catch (e: Exception) {
            e.printStackTrace(); null
        }
    }

    private fun updateData() {
        if (questerService == null)
            return

        questerService!!.listCampaigns().enqueue(object : Callback<List<Campaign>> {
            override fun onResponse(call: Call<List<Campaign>>, response: Response<List<Campaign>>) {
                when (response.code()) {
                    200 -> {
                        adapter.data = response.body()
                        loader.visibility = View.GONE
                        campaignRecyclerView.visibility = View.VISIBLE
                    }

                    else -> ErrorHandler.handleErrors(this@MainActivity, response.errorBody()!!)
                }
            }

            override fun onFailure(call: Call<List<Campaign>>, t: Throwable) {
                ErrorHandler.handleErrors(this@MainActivity, t)
            }
        })
    }
}