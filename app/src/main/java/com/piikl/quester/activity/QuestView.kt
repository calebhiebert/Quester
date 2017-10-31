package com.piikl.quester.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.piikl.quester.R
import com.piikl.quester.api.Quest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestView : CustomActivity() {

    val EDIT_QUEST = 1

    lateinit var title: TextView
    lateinit var details: TextView
    lateinit var loader: ProgressBar
    lateinit var questIcon: ImageView

    private lateinit var quest: Quest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quest_view)

        title = findViewById(R.id.txtQuestViewQuestTitle)
        details = findViewById(R.id.txtQuestViewDetailView)
        loader = findViewById(R.id.ldgQuestViewLoader)
        questIcon = findViewById(R.id.imgQuestViewQuestIcon)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_quest_view, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mnuSettings -> openSettings()

            R.id.mnuQuestViewEdit -> {
                val intent = Intent(this, QuestEdit::class.java)
                intent.putExtra("quest_json", MainActivity.mapper.writeValueAsString(quest))
                startActivityForResult(intent, EDIT_QUEST)
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
    }

    private fun loadData(id: Long) {
        MainActivity.questerService.getQuest(id).enqueue(object : Callback<Quest> {
            override fun onFailure(call: Call<Quest>?, t: Throwable) {
                Toast.makeText(applicationContext, "There was an error loading this quest $t", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<Quest>?, response: Response<Quest>) {
                if(response.code() == 200) {
                    onDataLoaded(response.body()!!)
                }
            }
        })
    }
}
