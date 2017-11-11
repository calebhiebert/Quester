package com.piikl.quester.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.piikl.quester.fragment.PrefsFragment

class PrefsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, PrefsFragment())
                .commit()
    }
}
