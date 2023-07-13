package com.chairullatif.storyapp.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {

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

}