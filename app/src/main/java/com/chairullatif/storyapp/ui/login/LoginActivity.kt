package com.chairullatif.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.chairullatif.storyapp.databinding.ActivityLoginBinding
import com.chairullatif.storyapp.ui.ViewModelFactory
import com.chairullatif.storyapp.ui.liststory.ListStoryActivity
import com.chairullatif.storyapp.ui.login.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val userViewModel: UserViewModel by viewModels { ViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // observe viewModel
        viewModelObserve()

        // init view
        initView()

    }

    private fun playAnimation() {
        val tvEmailAnim =
            ObjectAnimator.ofFloat(binding.tvEmail, View.TRANSLATION_X, -200f, 0f).setDuration(1000)
        val tvPasswordAnim =
            ObjectAnimator.ofFloat(binding.tvPassword, View.TRANSLATION_X, -200f, 0f).setDuration(1000)
        val edtEmailAnim =
            ObjectAnimator.ofFloat(binding.edtEmail, View.TRANSLATION_X, 200f, 0f).setDuration(1000)
        val edtPasswordAnim =
            ObjectAnimator.ofFloat(binding.edtPassword, View.TRANSLATION_X, 200f, 0f).setDuration(1000)
        val btnLoginAnim =
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA,  1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(tvEmailAnim, tvPasswordAnim, edtEmailAnim, edtPasswordAnim)
        }

        AnimatorSet().apply {
            playSequentially(together, btnLoginAnim)
            start()
        }
    }

    private fun viewModelObserve() {
        binding.apply {
            userViewModel.apply {
                // loading
                isLoading.observe(this@LoginActivity) {
                    if (it) {
                        btnLogin.visibility = View.GONE
                        progressBar.visibility = View.VISIBLE
                        tvRegister.visibility = View.GONE
                    } else {
                        btnLogin.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        tvRegister.visibility = View.VISIBLE
                    }
                }

                // login response
                loginResponse.observe(this@LoginActivity) {
                    Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()
                    if (!it.error) {
                        val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun initView() {
        binding.apply {

            //email
            edtEmail.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    setButtonEnable()
                }

            })

            //password
            edtPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun afterTextChanged(p0: Editable?) {
                    setButtonEnable()
                }

            })

            //tvRegister
            tvRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

            //btnLogin
            btnLogin.setOnClickListener {
                login()
            }

            textView.setOnClickListener {
                val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun login() {
        binding.apply {
            userViewModel.login(
                edtEmail.text.toString(),
                edtPassword.text.toString()
            )
        }
    }

    private fun setButtonEnable() {
        binding.apply {
            btnLogin.isEnabled =
                (Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()
                        && (edtPassword.text?.length ?: 0) >= 8)
        }
    }

    override fun onResume() {
        super.onResume()
        // set animation
        playAnimation()
    }
}