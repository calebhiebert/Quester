package com.piikl.quester.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.RadioButton
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.mobsandgeeks.saripaar.annotation.Optional
import com.piikl.quester.R

abstract class QuestCrud : ValidatorActivity() {

    @NotEmpty
    @Length(min = 3, max = 255)
    protected lateinit var nameInput: EditText

    @NotEmpty
    @Length(min = 3, max = 16000)
    protected lateinit var detailsInput: EditText

    @Optional
    @Length(min = 1, max = 255)
    protected lateinit var locationObtainedInput: EditText

    @Optional
    @Length(min = 3, max = 255)
    protected lateinit var questGiverInput: EditText

    protected lateinit var rdoAvailable: RadioButton
    protected lateinit var rdoLocked: RadioButton
    protected lateinit var rdoHidden: RadioButton

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.crud_quest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mnu_quest_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mnuSettings -> openSettings()
            R.id.mnuSave -> validator.validate()
        }

        return true
    }

    override fun onValidationSucceeded() { }
}