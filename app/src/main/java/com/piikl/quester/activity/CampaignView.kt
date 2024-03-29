package com.piikl.quester.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
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
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.Quest
import com.piikl.quester.fragment.InviteUserFragment
import com.piikl.quester.setMenuVisibility
import com.piikl.quester.setVisibility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CampaignView : CustomActivity(), InviteUserFragment.OnFragmentInteractionListener {

    private lateinit var titleView: TextView
    private lateinit var creatorView: TextView
    private lateinit var questListAdapter: QuestListAdapter
    private lateinit var questListView: RecyclerView
    private lateinit var loadingWheel: ProgressBar
    private lateinit var createQuest: FloatingActionButton

    lateinit var campaign: Campaign

    private var netCall: Call<Campaign>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_view)

        val id = intent.getLongExtra("campaign_id", 0)
        campaign = Campaign()
        campaign.id = id

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

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val edit = prefs.edit()
            edit.putLong("last_campaign_id", id)
            edit.apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_campaign_view, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if(campaignIsMine()) {
            setMenuVisibility(true, menu,
                    R.id.mnuCampaignViewEdit,
                    R.id.mnuCampaignViewDelete,
                    R.id.mnuAddUsers)
        } else {
            setMenuVisibility(false, menu,
                    R.id.mnuCampaignViewEdit,
                    R.id.mnuCampaignViewDelete,
                    R.id.mnuAddUsers)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onResume() {
        super.onResume()
        loadData(campaign.id)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            R.id.mnuCampaignViewDelete -> MainActivity.questerService!!.deleteCampaign(campaign.id.toString()).enqueue(object : Callback<Campaign> {
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

            R.id.mnuAddUsers -> {
                val transaction: android.support.v4.app.FragmentTransaction = supportFragmentManager.beginTransaction()
                val prev = supportFragmentManager.findFragmentByTag("dialog")

                if(prev != null)
                    transaction.remove(prev)

                transaction.addToBackStack(null)

                val newFrag = InviteUserFragment.newInstance(campaign.id)
                newFrag.show(transaction, "dialog")
            }
        }

        return true
    }

    private fun onDataUpdated(campaign: Campaign) {
        this.campaign = campaign

        createQuest.setOnClickListener({
            val intent = Intent(this, QuestCreate::class.java)
            intent.putExtra("campaign", campaign)
            startActivity(intent)
        })

        titleView.text = campaign.name
        creatorView.text = "Created by ${campaign.creator?.name}"

        setVisibility(View.VISIBLE, titleView, creatorView, questListView, createQuest)
        loadingWheel.visibility = View.GONE

        questListAdapter.questList = campaign.quests?.sortedBy { it.id }

        if(campaignIsMine())
            createQuest.visibility = View.VISIBLE
        else
            createQuest.visibility = View.GONE

        invalidateOptionsMenu()
    }

    fun onQuestSelected(quest: Quest) {
        val intent = Intent(this, QuestView::class.java)
        intent.putExtra("quest", quest)
        intent.putExtra("campaign", campaign)
        intent.putExtra("user", campaign.creator)

        startActivity(intent)
    }

    override fun onFragmentInteraction(uri: Uri) { }

    private fun loadData(id: Long) {
        if(netCall != null)
            netCall?.cancel()

        netCall = MainActivity.questerService!!.getCampaign(id)

        netCall!!.enqueue(object : retrofit2.Callback<Campaign> {
            override fun onResponse(call: Call<Campaign>, response: Response<Campaign>) {
                when (response.code()) {
                    200 -> onDataUpdated(response.body()!!)
                    404 -> finish()

                    else -> ErrorHandler.handleErrors(this@CampaignView, response.errorBody()!!)
                }
            }

            override fun onFailure(call: Call<Campaign>, t: Throwable) {
                ErrorHandler.handleErrors(this@CampaignView, t)
            }
        })
    }

    private fun campaignIsMine(): Boolean {
        if(campaign.creator != null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val creator = campaign.creator!!
            return creator.name == prefs.getString("username", null)
        } else {
            return false
        }
    }
}
