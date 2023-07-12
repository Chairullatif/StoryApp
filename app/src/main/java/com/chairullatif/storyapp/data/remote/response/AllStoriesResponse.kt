package com.chairullatif.storyapp.data.remote.response

import com.chairullatif.storyapp.data.model.StoryModel

data class AllStoriesResponse(
    val error: Boolean,
    val listStory: List<StoryModel>,
    val message: String
)