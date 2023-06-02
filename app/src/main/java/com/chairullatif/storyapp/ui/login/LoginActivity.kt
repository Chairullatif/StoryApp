package com.chairullatif.storyapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.chairullatif.storyapp.R
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

}