package com.chairullatif.storyapp.data.remote

import com.chairullatif.storyapp.data.response.AllStoriesResponse
import com.chairullatif.storyapp.data.response.CommonResponse
import com.chairullatif.storyapp.data.response.LoginResponse
import com.chairullatif.storyapp.data.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<CommonResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?,
        @Query("location") location: Boolean
    ): Call<AllStoriesResponse>

    @GET("stories/{id_story}")
    fun getStoryById(
        @Header("Authorization") authorization: String,
        @Path("id_story") id_story: String?
    ): Call<StoryResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") authorization: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Call<CommonResponse>
}