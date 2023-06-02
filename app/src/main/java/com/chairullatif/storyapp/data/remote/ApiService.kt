package com.chairullatif.storyapp.data.remote

import com.chairullatif.storyapp.data.response.AllStoriesResponse
import com.chairullatif.storyapp.data.response.CommonResponse
import com.chairullatif.storyapp.data.response.LoginResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
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

}