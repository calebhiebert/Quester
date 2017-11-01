package com.piikl.quester.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.piikl.quester.R
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.Quest
import com.piikl.quester.setMenuVisibility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestView : CustomActivity() {

    val EDIT_QUEST = 1

    lateinit var title: TextView
    lateinit var details: TextView
    lateinit var loader: ProgressBar
    lateinit var questIcon: ImageView
    lateinit var locationObtained: TextView
    lateinit var questGiver: TextView
    lateinit var questGiverLayout: LinearLayout
    lateinit var locationObtainedLayout: LinearLayout

    private lateinit var quest: Quest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_view)

        title = findViewById(R.id.txtQuestViewQuestTitle)
        details = findViewById(R.id.txtQuestViewDetailView)
        loader = findViewById(R.id.ldgQuestViewLoader)
        questIcon = findViewById(R.id.imgQuestViewQuestIcon)
        locationObtained = findViewById(R.id.txtLocationObtained)
        questGiver = findViewById(R.id.txtQuestGiver)
        questGiverLayout = findViewById(R.id.lltQuestGiver)
        locationObtainedLayout = findViewById(R.id.lltLocationObtained)

        title.visibility = View.INVISIBLE
        details.visibility = View.INVISIBLE
        questIcon.visibility = View.GONE

        val json = intent.getStringExtra("quest_json")
        quest = MainActivity.mapper.readValue(json, Quest::class.java)

        if(quest.id == 0L) {
            finish()
        } else {
            onDataLoaded(quest)
            loadData(quest.id)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_quest_view, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if(questIsMine()) {
            setMenuVisibility(true, menu,
                    R.id.mnuQuestViewEdit,
                    R.id.mnuDeleteQuest)
        } else {
            setMenuVisibility(false, menu,
                    R.id.mnuQuestViewEdit,
                    R.id.mnuDeleteQuest)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId) {
            R.id.mnuQuestViewEdit -> {
                val intent = Intent(this, QuestEdit::class.java)
                intent.putExtra("quest_json", MainActivity.mapper.writeValueAsString(quest))
                startActivityForResult(intent, EDIT_QUEST)
            }

            R.id.mnuDeleteQuest -> {
                MainActivity.questerService!!.deleteQuest(quest.id).enqueue(object : Callback<Quest> {
                    override fun onFailure(call: Call<Quest>?, t: Throwable) {
                        ErrorHandler.handleErrors(this@QuestView, t)
                    }

                    override fun onResponse(call: Call<Quest>?, response: Response<Quest>) {
                        when(response.code()) {
                            200, 404 -> { finish() }
                            else -> Toast.makeText(this@QuestView, "Could not delete quest because of code ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                })
            }
        }

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == EDIT_QUEST) {
            if(resultCode == Activity.RESULT_OK && data != null) {
                val json = data.getStringExtra("quest_json")
                Log.d("Got quest", json)
                quest = MainActivity.mapper.readValue(json, Quest::class.java)
                onDataLoaded(quest)
            }
        }
    }

    private fun onDataLoaded(quest: Quest) {
        this.quest = quest

        title.text = quest.name
        details.text = quest.details

        loader.visibility = View.INVISIBLE
        title.visibility = View.VISIBLE
        details.visibility = View.VISIBLE

        val resource = quest.getIconDrawble()

        if(resource != null) {
            questIcon.setImageResource(resource)
            questIcon.visibility = View.VISIBLE
        } else {
            questIcon.visibility = View.GONE
        }
        
        if(quest.questGiver != null) {
            questGiverLayout.visibility = View.VISIBLE
            questGiver.text = quest.questGiver
        } else questGiverLayout.visibility = View.GONE

        if(quest.locationObtained != null) {
            locationObtainedLayout.visibility = View.VISIBLE
            locationObtained.text = quest.locationObtained
        } else locationObtainedLayout.visibility = View.GONE

        invalidateOptionsMenu()
    }

    private fun loadData(id: Long) {
        MainActivity.questerService!!.getQuest(id).enqueue(object : Callback<Quest> {
            override fun onFailure(call: Call<Quest>?, t: Throwable) {
                ErrorHandler.handleErrors(this@QuestView, t)
            }

            override fun onResponse(call: Call<Quest>?, response: Response<Quest>) {
                if(response.code() == 200) {
                    onDataLoaded(response.body()!!)
                }
            }
        })
    }

    private fun questIsMine(): Boolean {
        return if(quest.campaign != null && quest.campaign!!.creator != null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val creator = quest.campaign!!.creator!!
            creator.name == prefs.getString("username", null)
        } else {
            false
        }
    }
}
