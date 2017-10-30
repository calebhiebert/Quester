package com.piikl.quester.activity

import android.os.Bundle

class PrefsActivity : CustomActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction()
                .replace(android.R.id.content, PrefsFragment())
                .commit()
    }
}
