package com.chairullatif.storyapp.ui.liststory.detailstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            //btn back
            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }
}