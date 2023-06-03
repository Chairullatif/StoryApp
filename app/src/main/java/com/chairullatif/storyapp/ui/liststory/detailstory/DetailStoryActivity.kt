package com.chairullatif.storyapp.ui.liststory.detailstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityDetailStoryBinding
import com.chairullatif.storyapp.helper.GlideHelper.loadImage
import com.chairullatif.storyapp.ui.ViewModelFactory
import com.chairullatif.storyapp.ui.liststory.StoryViewModel

class DetailStoryActivity : AppCompatActivity() {

    private var idStory: String? = null
    private lateinit var binding: ActivityDetailStoryBinding
    private val storyViewModel: StoryViewModel by viewModels { ViewModelFactory(this) }

    companion object {
        const val EXTRA_ID_STORY = "extra_id_story"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idStory = intent.getStringExtra(EXTRA_ID_STORY)

        // init view
        initView()

        // view model
        viewModelAction()
    }

    private fun viewModelAction() {
        binding.apply {
            storyViewModel.apply {
                getStoryById(idStory)
                dataStory.observe(this@DetailStoryActivity) {
                    tvUserName.text = it.name
                    tvDesc.text = it.description
                    ivStory.loadImage(it.photoUrl)
                }
            }
        }
    }

    private fun initView() {
        binding.apply {
            //btn back
            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }
}