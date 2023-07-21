package com.chairullatif.storyapp

import com.chairullatif.storyapp.data.model.StoryModel

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryModel> {
        val items: MutableList<StoryModel> = arrayListOf()
        for (i in 0..100) {
            val story = StoryModel(
                i.toString(),
                i.toDouble(),
                i.toDouble(),
                "name $i",
                "description $i",
                "photoUrl $i",
                "createdAt $i"
            )
            items.add(story)
        }
        return items
    }

}