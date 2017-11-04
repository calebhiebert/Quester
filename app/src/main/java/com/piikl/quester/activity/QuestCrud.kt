package com.piikl.quester.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.piikl.quester.R
import com.piikl.quester.api.Campaign
import com.piikl.quester.api.Quest
import com.piikl.quester.fragment.SelectQuestFragment

abstract class QuestCrud : ValidatorActivity(), SelectQuestFragment.QuestSelectionListener {

    @NotEmpty
    @Length(min = 3, max = 255)
    protected lateinit var nameInput: EditText

    @NotEmpty
    @Length(min = 3, max = 16000)
    protected lateinit var detailsInput: EditText

    @Length(min = 0, max = 255, trim = true)
    protected lateinit var locationObtainedInput: EditText

    @Length(min = 0, max = 255, trim = true)
    protected lateinit var questGiverInput: EditText

    protected lateinit var unlockModeInput: Spinner

    protected lateinit var unlockedByLabel: TextView
    protected lateinit var unlockedByView: TextView

    protected lateinit var rdoAvailable: RadioButton
    protected lateinit var rdoLocked: RadioButton
    protected lateinit var rdoHidden: RadioButton

    protected lateinit var unlockedBy: MutableList<Quest>
    protected lateinit var quests: List<Quest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.crud_quest)

        nameInput = findViewById(R.id.txtNameInput)
        detailsInput = findViewById(R.id.txtDetailsInput)
        locationObtainedInput = findViewById(R.id.txtLocationObtainedInput)
        questGiverInput = findViewById(R.id.txtQuestGiverInput)
        rdoAvailable = findViewById(R.id.rdoAvailable)
        rdoLocked = findViewById(R.id.rdoLocked)
        rdoHidden = findViewById(R.id.rdoHidden)
        unlockModeInput = findViewById(R.id.spnUnlockMode)
        unlockedByLabel = findViewById(R.id.txtUnlockedByLabel)
        unlockedByView = findViewById(R.id.txtUnlockedBy)

        unlockModeInput.adapter = ArrayAdapter<Quest.UnlockMode>(this, android.R.layout.simple_spinner_dropdown_item, Quest.UnlockMode.values())

        val campaign = intent.getParcelableExtra<Campaign>("campaign")
        quests = campaign.quests!!

        unlockedByLabel.setOnClickListener { openUnlockedByDialog() }
        unlockedByView.setOnClickListener { openUnlockedByDialog() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.mnu_quest_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId) {
            R.id.mnuSave -> validator.validate()
        }

        return true
    }

    override fun selectionUpdated(selected: MutableList<Quest>) {
        unlockedBy = selected

        var ulByView = ""

        selected.forEach {
            ulByView += "${it.name}, "
        }

        unlockedByView.text = ulByView
    }

    private fun openUnlockedByDialog() {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag("dialog")

        if(prev != null)
            ft.remove(prev)

        ft.addToBackStack(null)

        val frag = SelectQuestFragment.newInstance(quests, unlockedBy)
        frag.show(ft, "dialog")
    }

    override fun onValidationSucceeded() { }
}