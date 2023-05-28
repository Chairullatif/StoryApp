package com.chairullatif.storyapp.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityLoginBinding
import com.chairullatif.storyapp.ui.liststory.ListStoryActivity
import com.chairullatif.storyapp.ui.login.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            //
            textView.setOnClickListener {
                val intent = Intent(this@LoginActivity, ListStoryActivity::class.java)
                startActivity(intent)
            }
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