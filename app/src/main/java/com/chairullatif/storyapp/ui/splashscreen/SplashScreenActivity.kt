package com.chairullatif.storyapp.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.chairullatif.storyapp.databinding.ActivitySplashScreenBinding
import com.chairullatif.storyapp.ui.ViewModelFactory
import com.chairullatif.storyapp.ui.liststory.ListStoryActivity
import com.chairullatif.storyapp.ui.login.LoginActivity
import com.chairullatif.storyapp.ui.login.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val userViewModel: UserViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // observe viewModel
        viewModelObserve()

    }

    private fun viewModelObserve() {
        binding.apply {
            userViewModel.apply {
                // get current user
                getCurrentUser()

                // current user observe
                currentUser.observe(this@SplashScreenActivity) {

                    CoroutineScope(Dispatchers.IO).launch {
                        Thread.sleep(1000)
                        if (it != null) {
                            val intent = Intent(this@SplashScreenActivity, ListStoryActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }

                }
            }
        }
    }
}