package com.chairullatif.storyapp.ui.login.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityRegisterBinding
import com.chairullatif.storyapp.ui.login.UserViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // observe
        viewModelObserve()

        // init view
        initView()
    }

    private fun viewModelObserve() {
        binding.apply {
            // loading
            userViewModel.isLoading.observe(this@RegisterActivity) {
                if (it) {
                    btnRegister.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                } else {
                    btnRegister.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }

            // register response
            userViewModel.commonResponse.observe(this@RegisterActivity) {
                Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()
                if (!it.error) {
                    finish()
                }
            }

        }
    }

    private fun register() {
        binding.apply {
            userViewModel.register(
                edtUserName.text.toString(),
                edtEmail.text.toString(),
                edtPassword.text.toString()
            )
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

            //btn back
            btnBack.setOnClickListener {
                onBackPressed()
            }

            //btn register
            btnRegister.setOnClickListener {
                if ((edtUserName.text?.length ?: 0) < 1) {
                    edtUserName.error = getString(R.string.username_is_required)
                } else {
                    edtUserName.error = null
                    //register
                    register()
                }
            }

        }
    }

    private fun setButtonEnable() {
        binding.apply {
            btnRegister.isEnabled =
                ((Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()
                        && (edtPassword.text?.length ?: 0) >= 8
                        && (edtUserName.text?.length ?: 0) >= 0
                        ))
        }
    }
}