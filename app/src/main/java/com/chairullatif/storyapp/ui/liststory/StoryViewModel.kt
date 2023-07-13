package com.chairullatif.storyapp.ui.liststory

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.data.SharedPrefManager
import com.chairullatif.storyapp.data.StoryRepository
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.data.model.UserModel
import com.chairullatif.storyapp.data.remote.response.AllStoriesResponse
import com.chairullatif.storyapp.data.remote.response.CommonResponse
import com.chairullatif.storyapp.data.remote.response.StoryResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class StoryViewModel(
    private val context: Context,
    private val storyRepository: StoryRepository
    ): ViewModel() {

    private val sharedPreManager = SharedPrefManager(context)

    private val _dataStories = MutableLiveData<List<StoryModel>>()
    val dataStories: LiveData<List<StoryModel>> = _dataStories

    private val _dataPagedStories = MutableLiveData<PagingData<StoryModel>>()
    val dataPagedStories: LiveData<PagingData<StoryModel>> = _dataPagedStories

    private val _dataStory = MutableLiveData<StoryModel>()
    val dataStory: LiveData<StoryModel> = _dataStory

    private val _commonResponse = MutableLiveData<CommonResponse>()
    val commonResponse: LiveData<CommonResponse> = _commonResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "StoryViewModel"
    }

    fun getStoriesWithPaging() {
        val gson = Gson()
        val json = sharedPreManager.getString(SharedPrefManager.SP_OBJECT_USER)
        val user = gson.fromJson(json, UserModel::class.java)
        val token = user?.token
        val authorization = "Bearer $token"

        storyRepository.getStories(authorization).cachedIn(viewModelScope).observeForever {
            _dataPagedStories.value = it
        }
    }

    fun getStoriesWithLocation() {
        val gson = Gson()
        val json = sharedPreManager.getString(SharedPrefManager.SP_OBJECT_USER)
        val user = gson.fromJson(json, UserModel::class.java)
        val token = user?.token
        val authorization = "Bearer $token"

        val client = storyRepository.getStoriesWithLocation(authorization)
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

    fun getStoryById(idStory: String?) {
        val gson = Gson()
        val json = sharedPreManager.getString(SharedPrefManager.SP_OBJECT_USER)
        val user = gson.fromJson(json, UserModel::class.java)
        val token = user?.token
        val authorization = "Bearer $token"

        val client = storyRepository.getStoryById(authorization, idStory)
        client.enqueue(object : Callback<StoryResponse> {

            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _dataStory.value = body.story
                    }
                } else {
                    val errorBody = response.errorBody().toString()
                    Log.d(TAG, "onResponse error: $errorBody")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {

            }

        })
    }

    fun addStory(
        description: String,
        latitude: Double? = 0.0,
        longitude: Double? = 0.0,
        image: File,
    ) {
        val gson = Gson()
        val json = sharedPreManager.getString(SharedPrefManager.SP_OBJECT_USER)
        val user = gson.fromJson(json, UserModel::class.java)
        val token = user?.token
        val authorization = "Bearer $token"

        val bodyDesc = description.toRequestBody("text/plain".toMediaTypeOrNull())
        val bodyLat = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val bodyLon = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
        val bodyImage = MultipartBody.Part.createFormData("photo", image.name, requestImageFile)

        Log.d(TAG, "addStory: description: $description")
        Log.d(TAG, "addStory: image: $image")

        _isLoading.value = true
        val client = storyRepository
            .addStory(
                authorization,
                bodyDesc,
                bodyLat,
                bodyLon,
                bodyImage,
            )
        client.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful) {
                    _commonResponse.value = response.body()
                } else {
                    val errorBody = response.errorBody().toString()
                    Log.d(TAG, "onResponse error: $errorBody")
                    _commonResponse.value = CommonResponse(true, context.getString(R.string.something_went_wrong))
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                _commonResponse.value = CommonResponse(true, t.message.toString())
                _isLoading.value = false
            }

        })
    }

}