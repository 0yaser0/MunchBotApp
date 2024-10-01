package com.munchbot.munchbot.ui.main_view.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.munchbot.munchbot.MunchBotActivity
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.LoginBinding
import com.munchbot.munchbot.ui.main_view.Home

class Login : MunchBotActivity() {
    private lateinit var binding: LoginBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBarColor(window, R.color.secondColor)

        SetupUI.setupUI(binding.root)
        passwordVisibility(binding.passwordEditText)

        binding.logIn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            binding.loaderLayout.visibility = View.VISIBLE
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                binding.loaderLayout.visibility = View.GONE
                return@setOnClickListener
            }else{
                authViewModel.logIn(email, password)
            }

        }

        binding.doesnTHav.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
        }

        authViewModel.user.observe(this) { user ->
            if (user != null) {
                val intent = Intent(this, Home::class.java)
                startActivity(intent)
                @Suppress("DEPRECATION")
                overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
            }
        }

        authViewModel.authError.observe(this) { error ->
            error?.let {
                binding.loaderLayout.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.authComplete.observe(this) { isComplete ->
            if (isComplete) {
                binding.loaderLayout.visibility = View.GONE
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun passwordVisibility(passwordEditText: TextInputEditText) {
        passwordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = passwordEditText.compoundDrawablesRelative[2]
                if (drawableEnd != null && event.rawX >= (passwordEditText.right - drawableEnd.bounds.width())) {
                    if (passwordEditText.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_icon_password, 0, R.drawable.ic_dog_eyes_close, 0)
                    } else {
                        passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        passwordEditText.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_icon_password, 0, R.drawable.ic_dog_eyes_open, 0)
                    }
                    passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }


}

