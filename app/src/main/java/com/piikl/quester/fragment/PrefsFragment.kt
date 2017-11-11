package com.piikl.quester.fragment


import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v4.app.Fragment
import com.piikl.quester.R


/**
 * A simple [Fragment] subclass.
 */
class PrefsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)
    }

}
