package com.piikl.quester.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.piikl.quester.R
import com.piikl.quester.adapter.QuestListAdapter
import com.piikl.quester.api.Campaign
import com.piikl.quester.api.Quest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CampaignView : AppCompatActivity() {

    private lateinit var titleView: TextView
    private lateinit var creatorView: TextView
    private lateinit var questListAdapter: QuestListAdapter
    private lateinit var questListView: RecyclerView
    private lateinit var loadingWheel: ProgressBar

    private var campaign: Campaign? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_view)

        val id = intent.getLongExtra("campaign_id", 0)

        if(id == 0L) {
            finish()
        } else {
            questListAdapter = QuestListAdapter()

            titleView = findViewById(R.id.txtCampaignViewTitle)
            creatorView = findViewById(R.id.txtCampaignViewCreator)
            questListView = findViewById(R.id.recCampaignViewQuestList)
            loadingWheel = findViewById(R.id.ldgCampaignViewLoader)

            questListView.layoutManager = LinearLayoutManager(this)
            questListView.adapter = questListAdapter

            titleView.visibility = View.INVISIBLE
            creatorView.visibility = View.INVISIBLE

            loadData(id)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_campaign_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuCampaignViewDelete -> {
                if(campaign != null) {
                    MainActivity.questerService.deleteCampaign(campaign!!.id.toString()).enqueue(object : Callback<Campaign> {
                        override fun onResponse(call: Call<Campaign>?, response: Response<Campaign>) {
                            if(response.code() == 200) {
                                finish()
                            } else {
                                Toast.makeText(this@CampaignView, response.errorBody().toString(), Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<Campaign>?, t: Throwable) {
                            Toast.makeText(this@CampaignView, t.message, Toast.LENGTH_LONG).show()
                        }
                    })
                }
            }
        }

        return true
    }

    private fun onDataUpdated(campaign: Campaign) {
        this.campaign = campaign

        titleView.text = campaign.name
        creatorView.text = "Created by ${campaign.creator?.name}"

        titleView.visibility = View.VISIBLE
        creatorView.visibility = View.VISIBLE
        loadingWheel.visibility = View.GONE

        questListAdapter.questList = campaign.quests
    }

    fun onQuestSelected(quest: Quest) {
        val intent = Intent(this, QuestView::class.java)
        intent.putExtra("quest_id", quest.id)

        startActivity(intent)
    }

    private fun loadData(id: Long) {
        MainActivity.questerService.getCampaign(id.toString()).enqueue(object: retrofit2.Callback<Campaign> {
            override fun onResponse(call: Call<Campaign>, response: Response<Campaign>) {

                when(response.code()) {
                    200 -> onDataUpdated(response.body()!!)
                    404 -> finish()

                    else -> { Toast.makeText(this@CampaignView, "Got code ${response.code()} when loading campaign data", Toast.LENGTH_SHORT).show() }
                }
            }

            override fun onFailure(call: Call<Campaign>, t: Throwable) {
                throw t
            }
        })
    }
}
