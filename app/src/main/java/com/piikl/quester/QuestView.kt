package com.piikl.quester

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.piikl.quester.api.Quest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestView : AppCompatActivity() {

    lateinit var title: TextView
    lateinit var details: TextView
    lateinit var loader: ProgressBar
    lateinit var questIcon: ImageView

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

        val id = intent.getLongExtra("quest_id", 0)

        if(id == 0L) {
            finish()
        } else {
            loadData(id)
        }
    }

    private fun onDataLoaded(quest: Quest) {
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
        MainActivity.questerService.getQuest(id.toString()).enqueue(object : Callback<Quest> {
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
