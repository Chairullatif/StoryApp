package com.chairullatif.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chairullatif.storyapp.data.StoryRepository
import com.chairullatif.storyapp.ui.liststory.StoryViewModel
import com.chairullatif.storyapp.ui.login.UserViewModel

class ViewModelFactory(
    private val context: Context,
    private val repository: Any
    ) : ViewModelProvider.Factory
{
    constructor(context: Context) : this(context = context, Any())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            UserViewModel(
                context
            ) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            StoryViewModel(
                context,
                repository as StoryRepository
            ) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}