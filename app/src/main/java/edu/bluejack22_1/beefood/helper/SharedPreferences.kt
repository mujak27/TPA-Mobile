package edu.bluejack22_1.beefood.helper

import android.content.Context
import android.preference.PreferenceManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class SharedPreferences {
    companion object{
        private lateinit var sharedpreferences : DataStore<Preferences>

        fun addSP(context : Context, key : String, value : String){
//            sharedpreferences = context.createDataStore
        }
    }
}