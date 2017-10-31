package com.piikl.quester.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.piikl.quester.R

abstract class CustomActivity : AppCompatActivity() {

    protected fun openSettings() {
        val intent = Intent(this, PrefsActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(Menu.NONE, R.id.mnuSettings, Menu.NONE, R.string.settings)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mnuSettings -> openSettings()
        }

        return false
    }
}