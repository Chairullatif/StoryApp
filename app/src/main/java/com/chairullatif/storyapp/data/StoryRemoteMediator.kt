package com.chairullatif.storyapp.data

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.chairullatif.storyapp.data.database.RemoteKeys
import com.chairullatif.storyapp.data.database.StoryDatabase
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val authorization: String,
): RemoteMediator<Int, StoryModel>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryModel>
    ): MediatorResult = withContext(Dispatchers.IO) {

        val page = when (loadType) {
            LoadType.REFRESH ->{
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return@withContext MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return@withContext MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            Log.d(TAG, "load to try")
            val response =
                apiService.getStories(
                    authorization,
                    page,
                    state.config.pageSize,
                    0
                ).execute()

            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                Log.d(TAG, "stories loaded: $stories")
                val endOfPaginationReached = stories.isEmpty()

                //save to database
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAll()
                    }

                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = stories.map {
                        RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }

                    database.remoteKeysDao().insertAll(keys)
                    database.storyDao().insertStory(stories)
                }

                return@withContext MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

            } else {
                return@withContext  MediatorResult.Error(Exception("Error ${response.code()}"))
            }


        } catch (exception: Exception) {
            Log.d(TAG, "load to error: ${exception.message}")
            return@withContext  MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        const val TAG = "StoryRemoteMediator"
    }

}