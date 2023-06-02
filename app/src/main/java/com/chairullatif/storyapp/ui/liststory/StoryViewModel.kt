package com.chairullatif.storyapp.ui.liststory

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chairullatif.storyapp.data.SharedPrefManager
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.data.model.UserModel
import com.chairullatif.storyapp.data.remote.ApiConfig
import com.chairullatif.storyapp.data.response.AllStoriesResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryViewModel(context: Context): ViewModel() {

    private val sharedPreManager = SharedPrefManager(context)

    private val _dataStories = MutableLiveData<List<StoryModel>>()
    val dataStories: LiveData<List<StoryModel>> = _dataStories

    companion object {
        private const val TAG = "StoryViewModel"
    }

    fun getStories() {
        val gson = Gson()
        val json = sharedPreManager.getString(SharedPrefManager.SP_OBJECT_USER)
        val user = gson.fromJson(json, UserModel::class.java)
        val token = user?.token
        val authorization = "Bearer $token"

        val client = ApiConfig.getApiService().getStories(authorization, null, null, false)
        client.enqueue(object : Callback<AllStoriesResponse> {
            override fun onResponse(
                call: Call<AllStoriesResponse>,
                response: Response<AllStoriesResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _dataStories.value = body.listStory
                    }
                } else {
                    val errorBody = response.errorBody().toString()
                    Log.d(TAG, "onResponse error: $errorBody")
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {

            }

        })
    }

}