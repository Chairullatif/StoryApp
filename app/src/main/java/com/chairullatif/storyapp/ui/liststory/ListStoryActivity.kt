package com.chairullatif.storyapp.ui.liststory

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.adapter.LoadingStateAdapter
import com.chairullatif.storyapp.adapter.StoriesAdapter
import com.chairullatif.storyapp.databinding.ActivityListStoryBinding
import com.chairullatif.storyapp.helper.rotateFile
import com.chairullatif.storyapp.ui.ViewModelFactory
import com.chairullatif.storyapp.ui.liststory.addstory.AddStoryActivity
import com.chairullatif.storyapp.ui.liststory.storymap.StoryMapsActivity
import com.chairullatif.storyapp.ui.login.LoginActivity
import com.chairullatif.storyapp.ui.login.UserViewModel
import kotlinx.coroutines.launch
import java.io.File

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding
    private val userViewModel: UserViewModel by viewModels { ViewModelFactory(this) }
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }
    private lateinit var adapter: StoriesAdapter

    // launcher intent add story
    private val launcherIntentAddStory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            adapter.refresh()
            lifecycleScope.launch {
                adapter.loadStateFlow
                    .collect {
                        binding.rvListStory.smoothScrollToPosition(0)
                    }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init view
        initView()

        // view model
        viewModelAction()

    }

    private fun viewModelAction() {
        storyViewModel.dataPagedStories.observe(this@ListStoryActivity) {
            Log.d(TAG, "viewModelAction stories: $it")
            adapter.submitData(lifecycle, it)
        }
    }

    private fun initView() {
        binding.apply {
            //btn menu
            btnMenu.setOnClickListener {
                showPopUpMenu()
            }

            //btn add story
            fabAddStory.setOnClickListener {
                launcherIntentAddStory.launch(
                    Intent(
                        this@ListStoryActivity,
                        AddStoryActivity::class.java
                    )
                )
            }

            //btn map
            btnMap.setOnClickListener {
                launchActivity(StoryMapsActivity::class.java)
            }

            // rv stories
            adapter = StoriesAdapter()
            rvListStory.layoutManager = LinearLayoutManager(this@ListStoryActivity)
            rvListStory.setHasFixedSize(true)
            rvListStory.adapter = adapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    adapter.retry()
                }
            )
        }
    }

    private fun launchActivity(activityClass: Class<*>) {
        val intent = Intent(this@ListStoryActivity, activityClass)
        startActivity(intent)
    }

    private fun showPopUpMenu() {
        val popupMenu = androidx.appcompat.widget.PopupMenu(this, binding.btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_list_story, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    userViewModel.logout()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    true
                }
                R.id.menu_change_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    companion object {
        private const val TAG = "ListStoryActivity"
    }

}