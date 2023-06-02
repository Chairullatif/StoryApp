package com.chairullatif.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chairullatif.storyapp.ui.login.UserViewModel

class ViewModelFactory(
    private val context: Context
    ) : ViewModelProvider.Factory
{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            UserViewModel(
                context
            ) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}