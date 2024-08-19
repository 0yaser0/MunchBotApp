package com.munchbot.munchbot.ui.main_view.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.LoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBarColor(window, R.color.status_bar_color)

        SetupUI.setupUI(binding.root)
        passwordVisibility(binding.passwordEditText, binding.passwordToggle)

        binding.logIn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            binding.loaderLayout.visibility = View.VISIBLE
            authViewModel.logIn(email, password)
        }

        binding.doesnTHav.setOnClickListener {
            binding.loaderLayout.visibility = View.GONE
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
        }

        authViewModel.user.observe(this) { user ->
            if (user != null) {
                binding.loaderLayout.visibility = View.GONE
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.authError.observe(this) { error ->
            error?.let {
                binding.loaderLayout.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun passwordVisibility(passwordEditText: EditText, passwordToggle: ImageView) {
        passwordToggle.setOnClickListener {
            if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_dog_eyes_close)
            } else {
                passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                passwordToggle.setImageResource(R.drawable.ic_dog_eyes_open)
            }
            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }
    }
}

