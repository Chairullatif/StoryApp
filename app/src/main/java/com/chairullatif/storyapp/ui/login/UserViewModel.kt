package com.chairullatif.storyapp.ui.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chairullatif.storyapp.data.SharedPrefManager
import com.chairullatif.storyapp.data.model.UserModel
import com.chairullatif.storyapp.data.remote.ApiConfig
import com.chairullatif.storyapp.data.remote.response.CommonResponse
import com.chairullatif.storyapp.data.remote.response.LoginResponse
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel(context: Context) : ViewModel() {

    private val sharedPrefManager: SharedPrefManager = SharedPrefManager(context)

    private val _commonResponse = MutableLiveData<CommonResponse>()
    val commonResponse: LiveData<CommonResponse> = _commonResponse

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse = _loginResponse

    private val _currentUser = MutableLiveData<UserModel>()
    val currentUser: LiveData<UserModel> = _currentUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "UserViewModel"
    }

    fun register(
        name: String,
        email: String,
        password: String
    ) {
        _isLoading.value = true
        val client = ApiConfig.getApiService()

        client.register(name, email, password).enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful) {
                    _commonResponse.value = response.body()
                } else {
                    _commonResponse.value = CommonResponse(false, response.message())
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                _commonResponse.value = CommonResponse(false, t.message.toString())
                _isLoading.value = false
            }

        })
    }

    fun login(
        email: String,
        password: String
    ) {
        _isLoading.value = true
        val client = ApiConfig.getApiService()

        client.login(email, password).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val gson = Gson()
                    val jsonUser = gson.toJson(response.body()?.loginResult ?: "")
                    sharedPrefManager.saveString(SharedPrefManager.SP_OBJECT_USER, jsonUser)
                    Log.d(TAG, "onResponse: $jsonUser")
                    _loginResponse.value = response.body()
                } else {
                    if (response.code() == 401) {
                        _loginResponse.value = LoginResponse(true, null, "Email or password was wrong")
                    } else {
                        _loginResponse.value = LoginResponse(true, null, response.message())
                    }
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResponse.value = LoginResponse(true, null, t.message.toString())
                _isLoading.value = false
            }
        })
    }

    fun getCurrentUser() {
        //get section
        val gson = Gson()
        val jsonUser = sharedPrefManager.getString(SharedPrefManager.SP_OBJECT_USER)
        val user = gson.fromJson(jsonUser, UserModel::class.java)

        _currentUser.value = user
    }

    fun logout() {
        sharedPrefManager.saveString(SharedPrefManager.SP_OBJECT_USER, null)
    }

}