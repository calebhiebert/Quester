package com.piikl.quester.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.Quest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketTimeoutException

class QuestEdit : QuestCrud() {

    lateinit var quest: Quest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val json = intent.getStringExtra("quest_json")
        quest = MainActivity.mapper.readValue(json, Quest::class.java)

        nameInput.setText(quest.name)
        detailsInput.setText(quest.details)
        locationObtainedInput.setText(quest.locationObtained)
        questGiverInput.setText(quest.questGiver)

        when(quest.status) {
            Quest.Status.LOCKED -> rdoLocked.isChecked = true
            Quest.Status.HIDDEN -> rdoHidden.isChecked = true
            else -> rdoAvailable.isChecked = true
        }
    }

    override fun onValidationSucceeded() {

        val newQuest = Quest()
        newQuest.name = nameInput.text.toString().trim()
        newQuest.details = detailsInput.text.toString().trim()

        if(locationObtainedInput.text.toString().trim().isNotEmpty())
            newQuest.locationObtained = locationObtainedInput.text.toString().trim()

        if(questGiverInput.text.toString().trim().isNotEmpty())
            newQuest.questGiver = questGiverInput.text.toString().trim()

        newQuest.status = when {
            rdoLocked.isChecked -> Quest.Status.LOCKED
            rdoHidden.isChecked -> Quest.Status.HIDDEN

            rdoAvailable.isChecked && quest.status != Quest.Status.LOCKED && quest.status != Quest.Status.HIDDEN -> quest.status
            else -> Quest.Status.INCOMPLETE
        }

        MainActivity.questerService!!.editQuest(quest.id, newQuest).enqueue(object : Callback<Quest> {
            override fun onFailure(call: Call<Quest>?, t: Throwable) {
                ErrorHandler.handleErrors(this@QuestEdit, t)
            }

            override fun onResponse(call: Call<Quest>?, response: Response<Quest>) {
                when(response.code()) {
                    200 -> {
                        val result = Intent()
                        result.putExtra("quest_json", MainActivity.mapper.writeValueAsString(response.body()))
                        setResult(Activity.RESULT_OK, result)
                        finish()
                    }

                    else -> {
                        Toast.makeText(this@QuestEdit, "Saving quest failed with code ${response.code()}", Toast.LENGTH_LONG).show()
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }
}
