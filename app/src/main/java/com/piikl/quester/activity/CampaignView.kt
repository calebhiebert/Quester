package com.piikl.quester.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
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
import com.piikl.quester.setVisibility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CampaignView : CustomActivity() {

    private lateinit var titleView: TextView
    private lateinit var creatorView: TextView
    private lateinit var questListAdapter: QuestListAdapter
    private lateinit var questListView: RecyclerView
    private lateinit var loadingWheel: ProgressBar
    private lateinit var createQuest: FloatingActionButton

    lateinit var campaign: Campaign

    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_view)

        val id = intent.getLongExtra("campaign_id", 0)
        campaign = Campaign()

        if (id == 0L) {
            finish()
        } else {
            questListAdapter = QuestListAdapter()

            titleView = findViewById(R.id.txtCampaignViewTitle)
            creatorView = findViewById(R.id.txtCampaignViewCreator)
            questListView = findViewById(R.id.recCampaignViewQuestList)
            loadingWheel = findViewById(R.id.ldgCampaignViewLoader)
            createQuest = findViewById(R.id.fabCreateQuest)

            questListView.layoutManager = LinearLayoutManager(this)
            questListView.adapter = questListAdapter

            setVisibility(View.INVISIBLE, titleView, creatorView, questListView, createQuest)
            setVisibility(View.VISIBLE, loadingWheel)

            loadData(id)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_campaign_view, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        loadData(campaign.id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuCampaignViewDelete -> {

                MainActivity.questerService.deleteCampaign(campaign.id.toString()).enqueue(object : Callback<Campaign> {
                    override fun onResponse(call: Call<Campaign>?, response: Response<Campaign>) {
                        if (response.code() == 200) {
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

            R.id.mnuCampaignViewEdit -> {
                val intent = Intent(this, CampaignEdit::class.java)
                intent.putExtra("campaign_json", MainActivity.mapper.writeValueAsString(campaign))
                startActivity(intent)
            }

            R.id.mnuCampaignViewRefresh -> {
                setVisibility(View.INVISIBLE, titleView, creatorView, questListView, createQuest)
                setVisibility(View.VISIBLE, loadingWheel)

                loadData(campaign.id)
            }

            R.id.mnuSettings -> openSettings()
        }

        return true
    }

    private fun onDataUpdated(campaign: Campaign) {
        this.campaign = campaign

        createQuest.setOnClickListener({
            val intent = Intent(this, QuestCreate::class.java)
            intent.putExtra("campaign_id", campaign.id)
            startActivity(intent)
        })

        titleView.text = campaign.name
        creatorView.text = "Created by ${campaign.creator?.name}"

        setVisibility(View.VISIBLE, titleView, creatorView, questListView, createQuest)
        loadingWheel.visibility = View.GONE

        questListAdapter.questList = campaign.quests
    }

    fun onQuestSelected(quest: Quest) {
        val intent = Intent(this, QuestView::class.java)
        intent.putExtra("quest_json", MainActivity.mapper.writeValueAsString(quest))

        startActivity(intent)
    }

    private fun loadData(id: Long) {
        if(!loading) {
            loading = true

            MainActivity.questerService.getCampaign(id).enqueue(object : retrofit2.Callback<Campaign> {
                override fun onResponse(call: Call<Campaign>, response: Response<Campaign>) {
                    loading = false

                    when (response.code()) {
                        200 -> onDataUpdated(response.body()!!)
                        404 -> finish()

                        else -> {
                            Toast.makeText(this@CampaignView, "Got code ${response.code()} when loading campaign data", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<Campaign>, t: Throwable) {
                    loading = false
                    throw t
                }
            })
        }
    }
}
