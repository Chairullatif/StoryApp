package com.chairullatif.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoriesPagingSource(
    private val apiService: ApiService,
    private val authorization: String,
) : PagingSource<Int, StoryModel>() {

    override fun getRefreshKey(state: PagingState<Int, StoryModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryModel> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX

            val response = withContext(Dispatchers.IO) {
                apiService.getStories(
                    authorization,
                    position,
                    params.loadSize,
                    0
                ).execute()
            }

            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = stories,
                    prevKey = if (position == 1) null else position - 1,
                    nextKey = if (stories.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("Error ${response.code()}"))
            }

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}