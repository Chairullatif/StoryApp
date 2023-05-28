package com.chairullatif.storyapp.ui.liststory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityListStoryBinding

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
        }
    }

    private fun showPopUpMenu() {
        val popupMenu = androidx.appcompat.widget.PopupMenu(this, binding.btnMenu)
        popupMenu.menuInflater.inflate(R.menu.menu_list_story, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_logout -> {
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
}