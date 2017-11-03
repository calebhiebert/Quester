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

class QuestCreate : QuestCrud() {

    private var campaignId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        campaignId = intent.getLongExtra("campaign_id", 0)

        if(campaignId == 0L)
            finish()
        else {

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
            else -> Quest.Status.INCOMPLETE
        }

        newQuest.unlockMode = unlockModeInput.selectedItem as Quest.UnlockMode

        MainActivity.questerService!!.createQuest(campaignId, newQuest).enqueue(object : Callback<Quest> {
            override fun onFailure(call: Call<Quest>?, t: Throwable) {
                ErrorHandler.handleErrors(this@QuestCreate, t)
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
                        ErrorHandler.handleErrors(this@QuestCreate, response.errorBody()!!)
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            }
        })
    }
}
