package com.chairullatif.storyapp.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chairullatif.storyapp.data.model.StoryModel

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<StoryModel>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, StoryModel>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}