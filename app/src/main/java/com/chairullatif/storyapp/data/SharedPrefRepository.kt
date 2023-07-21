package com.chairullatif.storyapp.data

class SharedPrefRepository(private val sharedPrefManager: SharedPrefManager) {

    companion object {
        const val SP_OBJECT_USER = SharedPrefManager.SP_OBJECT_USER
    }

    fun saveString(keySp: String, value: String?) {
        sharedPrefManager.saveString(keySp, value)
    }

    fun getString(keySp: String): String? = sharedPrefManager.getString(keySp)
}