package com.chairullatif.storyapp.di

import android.content.Context
import com.chairullatif.storyapp.data.SharedPrefManager
import com.chairullatif.storyapp.data.SharedPrefRepository
import com.chairullatif.storyapp.data.StoryRepository
import com.chairullatif.storyapp.data.database.StoryDatabase
import com.chairullatif.storyapp.data.remote.ApiConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }

    fun provideSharedPrefRepository(context: Context): SharedPrefRepository {
        val sharedPrefManager = SharedPrefManager(context)
        return SharedPrefRepository(sharedPrefManager)
    }
}