package com.piikl.quester.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity

open class CustomActivity : AppCompatActivity() {

    protected fun openSettings() {
        val intent = Intent(this, PrefsActivity::class.java)
        startActivity(intent)
    }
}