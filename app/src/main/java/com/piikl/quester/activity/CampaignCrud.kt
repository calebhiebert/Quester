package com.piikl.quester.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import com.mobsandgeeks.saripaar.annotation.Length
import com.mobsandgeeks.saripaar.annotation.NotEmpty
import com.piikl.quester.R

abstract class CampaignCrud : ValidatorActivity() {

    @NotEmpty
    @Length(min = 4, max = 255, trim = true)
    lateinit var nameInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_crud)

        nameInput = findViewById(R.id.edtCreateCampaignName)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_create_campaign, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.mnuCreateCampaignSave -> validator.validate()
        }

        return true
    }

    override fun onValidationSucceeded() { }
}
