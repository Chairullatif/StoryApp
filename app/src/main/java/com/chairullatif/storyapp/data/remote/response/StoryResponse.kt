package com.chairullatif.storyapp.data.remote.response

import com.chairullatif.storyapp.data.model.StoryModel

data class StoryResponse(
    val error: Boolean,
    val story: StoryModel,
    val message: String
)