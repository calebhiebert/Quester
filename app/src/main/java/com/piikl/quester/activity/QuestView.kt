package com.piikl.quester.activity

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.piikl.quester.R
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.Quest
import com.piikl.quester.setMenuVisibility
import com.piikl.quester.setVisibility
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
    lateinit var questStatusFab: FloatingActionButton

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
        questStatusFab = findViewById(R.id.fabQuestStatus)

        setVisibility(View.INVISIBLE, title, details, questStatusFab, questIcon, questGiverLayout, locationObtainedLayout)

        val json = intent.getStringExtra("quest_json")
        quest = MainActivity.mapper.readValue(json, Quest::class.java)

        if(quest.id == 0L) {
            finish()
        } else {
            onDataLoaded(quest)
            loadData(quest.id)
        }

        questStatusFab.setOnClickListener {
            setVisibility(View.INVISIBLE, title, details, questIcon, questGiverLayout, locationObtainedLayout)
            setVisibility(View.VISIBLE, loader)

            val quest = Quest()

            quest.status = when(this.quest.status) {
                Quest.Status.COMPLETE, Quest.Status.HIDDEN, Quest.Status.LOCKED, null -> Quest.Status.INCOMPLETE
                Quest.Status.INCOMPLETE -> Quest.Status.IN_PROGRESS
                Quest.Status.IN_PROGRESS -> Quest.Status.COMPLETE
            }

            MainActivity.questerService!!.editQuest(this.quest.id, quest).enqueue(object : Callback<Quest> {
                override fun onFailure(call: Call<Quest>?, t: Throwable) {
                    ErrorHandler.handleErrors(this@QuestView, t)
                }

                override fun onResponse(call: Call<Quest>?, response: Response<Quest>) {
                    when(response.code()) {
                        200 -> {
                            this@QuestView.quest = response.body()!!
                            this@QuestView.onDataLoaded(this@QuestView.quest)
                        }

                        else -> ErrorHandler.handleErrors(this@QuestView, response.errorBody()!!)
                    }
                }
            })
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

                            else -> ErrorHandler.handleErrors(this@QuestView, response.errorBody()!!)
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

        setVisibility(View.VISIBLE, title, details, questIcon, questGiverLayout, locationObtainedLayout)

        if(questIsMine()) {
            questStatusFab.visibility = View.VISIBLE

            when (quest.status) {
                Quest.Status.COMPLETE -> {
                    questStatusFab.setImageResource(R.drawable.ic_times)
                    questStatusFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_red_light))
                }

                Quest.Status.INCOMPLETE -> {
                    questStatusFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                    questStatusFab.setImageResource(R.drawable.ic_arrow_right)
                }

                Quest.Status.IN_PROGRESS -> {
                    questStatusFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_green_light))
                    questStatusFab.setImageResource(R.drawable.ic_check_white)
                }

                Quest.Status.LOCKED -> {
                    questStatusFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_green_light))
                    questStatusFab.setImageResource(R.drawable.ic_unlock_white)
                }

                Quest.Status.HIDDEN -> {
                    questStatusFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.holo_green_light))
                    questStatusFab.setImageResource(R.drawable.ic_eye)
                }
            }

        } else {
            questStatusFab.visibility = View.INVISIBLE
        }

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
