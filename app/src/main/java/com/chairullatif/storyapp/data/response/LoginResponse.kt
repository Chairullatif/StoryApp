package com.chairullatif.storyapp.data.response

import com.chairullatif.storyapp.data.model.UserModel

data class LoginResponse(
    val error: Boolean,
    val loginResult: UserModel?,
    val message: String
)
