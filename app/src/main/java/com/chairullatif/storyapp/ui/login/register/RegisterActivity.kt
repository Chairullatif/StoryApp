package com.chairullatif.storyapp.ui.login.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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