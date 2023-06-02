package com.chairullatif.storyapp.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPrefManager(private val context: Context) {

    private var sharedPref: SharedPreferences = context.getSharedPreferences(SP_APPS, MODE_PRIVATE)
    private var spEditor: SharedPreferences.Editor = sharedPref.edit()

    companion object {
        const val SP_APPS = "sp_storyapp"

        //login section
        const val SP_OBJECT_USER = "sp_object_user"
    }

    fun saveString(keySp: String, value: String?) {
        spEditor.putString(keySp, value)
        spEditor.commit()
    }

    fun getString(keySp: String): String? = sharedPref.getString(keySp, "")

    fun saveInt(keySp: String, value: Int) {
        spEditor.putInt(keySp, value)
        spEditor.commit()
    }

    fun getInt(keySp: String): Int = sharedPref.getInt(keySp, 0)

    fun saveBoolean(keySp: String, value: Boolean) {
        spEditor.putBoolean(keySp, value)
        spEditor.commit()
    }

    fun getBoolean(keySp: String): Boolean = sharedPref.getBoolean(keySp, false)
}