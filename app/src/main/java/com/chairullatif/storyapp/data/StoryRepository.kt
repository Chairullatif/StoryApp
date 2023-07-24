package com.chairullatif.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.chairullatif.storyapp.data.database.StoryDatabase
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.data.remote.ApiService
import com.chairullatif.storyapp.data.remote.response.AllStoriesResponse
import com.chairullatif.storyapp.data.remote.response.CommonResponse
import com.chairullatif.storyapp.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {

    fun getStories(authorization: String): LiveData<PagingData<StoryModel>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5,
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, authorization),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoriesWithLocation(
        authorization: String
    ): Call<AllStoriesResponse> {
        return apiService.getStoriesWithLocation(authorization, null, null, 1)
    }

    fun getStoryById(
        authorization: String,
        idStory: String?
    ): Call<StoryResponse> {
        return apiService.getStoryById(authorization, idStory)
    }

    fun addStory(
        authorization: String,
        bodyDesc: RequestBody,
        bodyLat: RequestBody,
        bodyLon: RequestBody,
        bodyImage: MultipartBody.Part
    ): Call<CommonResponse> {
        return apiService.addStory(
            authorization,
            bodyDesc,
            bodyLat,
            bodyLon,
            bodyImage,
        )
    }
}