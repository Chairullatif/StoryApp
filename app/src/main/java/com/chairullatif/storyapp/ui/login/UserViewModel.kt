package com.chairullatif.storyapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chairullatif.storyapp.data.remote.ApiConfig
import com.chairullatif.storyapp.data.response.CommonResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel() {

    private val _commonResponse = MutableLiveData<CommonResponse>()
    val commonResponse: LiveData<CommonResponse> = _commonResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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

}