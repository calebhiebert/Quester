package com.piikl.quester.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RadioButton
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.piikl.quester.R

abstract class QuestCrud : ValidatorActivity() {

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

    protected lateinit var rdoAvailable: RadioButton
    protected lateinit var rdoLocked: RadioButton
    protected lateinit var rdoHidden: RadioButton

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

    override fun onValidationSucceeded() { }
}