package com.piikl.quester

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.piikl.quester.api.Campaign
import retrofit2.Call
import retrofit2.Response

class CampaignView : AppCompatActivity() {

    lateinit var titleView: TextView
    lateinit var creatorView: TextView
    lateinit var questListAdapter: QuestListAdapter
    lateinit var questListView: RecyclerView
    lateinit var loadingWheel: ProgressBar

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

    private fun onDataUpdated(campaign: Campaign) {
        titleView.text = campaign.name
        creatorView.text = "Created by ${campaign.creator?.name}"

        titleView.visibility = View.VISIBLE
        creatorView.visibility = View.VISIBLE
        loadingWheel.visibility = View.GONE
    }

    private fun loadData(id: Long) {
        MainActivity.questerService.getCampaign(id.toString()).enqueue(object: retrofit2.Callback<Campaign> {
            override fun onResponse(call: Call<Campaign>, response: Response<Campaign>) {
                if(response.code() == 200) {
                    onDataUpdated(response.body()!!)
                } else {
                    throw Error("Response was ${response.errorBody().toString()}")
                }
            }

            override fun onFailure(call: Call<Campaign>, t: Throwable) {
                throw t
            }
        })
    }
}
