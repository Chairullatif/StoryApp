package com.chairullatif.storyapp.ui.liststory.addstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}