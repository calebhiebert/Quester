package com.piikl.quester.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.piikl.quester.api.ErrorHandler
import com.piikl.quester.api.Quest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuestEdit : QuestCrud() {

    lateinit var quest: Quest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        quest = intent.getParcelableExtra("quest")

        nameInput.setText(quest.name)
        detailsInput.setText(quest.details)
        locationObtainedInput.setText(quest.locationObtained)
        questGiverInput.setText(quest.questGiver)

        unlockModeInput.setSelection(quest.unlockMode!!.ordinal)

        if(quest.unlockedBy != null) {
            unlockedBy = quest.unlockedBy!!
            unlockedBy.remove(quest)
        } else {
            unlockedBy = mutableListOf()
        }

        selectionUpdated(unlockedBy)

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

        newQuest.unlockMode = unlockModeInput.selectedItem as Quest.UnlockMode
        newQuest.unlockedBy = unlockedBy

        val result = Intent()
        result.putExtra("quest", newQuest)
        setResult(Activity.RESULT_OK, result)

        MainActivity.questerService!!.editQuest(quest.id, newQuest).enqueue(object : Callback<Quest> {
            override fun onFailure(call: Call<Quest>?, t: Throwable) {
                ErrorHandler.handleErrors(this@QuestEdit, t)
            }

            override fun onResponse(call: Call<Quest>?, response: Response<Quest>) {
                if(response.code() != 200) {
                    ErrorHandler.handleErrors(applicationContext, response.errorBody()!!)
                }
            }
        })

        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }
}
