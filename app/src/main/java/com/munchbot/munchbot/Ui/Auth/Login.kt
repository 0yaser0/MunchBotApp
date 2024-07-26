package com.munchbot.munchbot.Ui.Auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.utils.StatusBarUtils
import com.google.android.material.textfield.TextInputEditText
import com.munchbot.munchbot.R
import com.munchbot.munchbot.databinding.LoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: LoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBarColor(window, R.color.status_bar_color)

        setupUI(binding.root)
        passwordVisibility()

        binding.logIn.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            authViewModel.signIn(email, password)
        }

        authViewModel.user.observe(this, Observer { user ->
            if (user != null) {
                // Navigate to the next screen or update UI
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            }
        })

        authViewModel.authError.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun passwordVisibility() {
        val passwordEditText = binding.passwordEditText
        val passwordToggle = binding.passwordToggle

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

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI(view: View) {
        if (view !is TextInputEditText) {
            view.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    currentFocus?.clearFocus()
                }
                false
            }
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }
}
