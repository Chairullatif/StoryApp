package com.chairullatif.storyapp.ui.liststory

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.chairullatif.storyapp.DataDummy
import com.chairullatif.storyapp.MainDispatcherRule
import com.chairullatif.storyapp.adapter.StoriesAdapter
import com.chairullatif.storyapp.data.SharedPrefRepository
import com.chairullatif.storyapp.data.StoryRepository
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    val dummyStory = DataDummy.generateDummyStoryResponse()
    val data: PagingData<StoryModel> = StoriesPagingSource.snapshot(dummyStory)
    val emptyData: PagingData<StoryModel> = PagingData.from(emptyList())
    val expectedStory = MutableLiveData<PagingData<StoryModel>>()
    val expectedEmptyStory = MutableLiveData<PagingData<StoryModel>>()

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var emptyStoryRepository: StoryRepository

    @Mock
    private lateinit var sharedPrefRepository: SharedPrefRepository

    @Before
    fun setUp() {
        expectedStory.value = data
        expectedEmptyStory.value = emptyData
        Mockito.`when`(sharedPrefRepository.getString(Mockito.anyString())).thenReturn("")
        Mockito.`when`(storyRepository.getStories(Mockito.anyString())).thenReturn(expectedStory)
        Mockito.`when`(emptyStoryRepository.getStories(Mockito.anyString())).thenReturn(expectedEmptyStory)
    }

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val storyViewModel = StoryViewModel(sharedPrefRepository, storyRepository)
        val actualStory: PagingData<StoryModel> = storyViewModel.dataPagedStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        
        differ.submitData(actualStory)
        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val storyViewModel = StoryViewModel(sharedPrefRepository, emptyStoryRepository)
        val actualStory: PagingData<StoryModel> = storyViewModel.dataPagedStories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )

        differ.submitData(actualStory)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoriesPagingSource : PagingSource<Int, LiveData<List<StoryModel>>>() {
    companion object {
        fun snapshot(items: List<StoryModel>): PagingData<StoryModel> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryModel>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryModel>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}