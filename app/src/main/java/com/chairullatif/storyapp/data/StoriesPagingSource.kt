package com.chairullatif.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.data.remote.ApiService

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

            val response = apiService.getStories(
                authorization,
                position,
                params.loadSize,
                0
            ).execute()

            LoadResult.Page(
                data = response.body()?.listStory ?: emptyList(),
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (response.body()?.listStory.isNullOrEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}