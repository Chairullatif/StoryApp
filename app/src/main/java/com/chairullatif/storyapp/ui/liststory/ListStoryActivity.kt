package com.chairullatif.storyapp.ui.liststory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityListStoryBinding
import com.chairullatif.storyapp.ui.liststory.addstory.AddStoryActivity
import com.chairullatif.storyapp.ui.liststory.detailstory.DetailStoryActivity

class ListStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            //btn menu
            btnMenu.setOnClickListener {
                showPopUpMenu()
            }

            //btn add story
            fabAddStory.setOnClickListener {
                val intent = Intent(this@ListStoryActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showPopUpMenu() {
        val popupMenu = androidx.appcompat.widget.PopupMenu(this, binding.btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_list_story, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    val intent = Intent(this, DetailStoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}