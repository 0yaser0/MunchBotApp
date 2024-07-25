package com.munchbot.munchbot.Ui.Auth

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.utils.StatusBarUtils
import com.google.android.material.textfield.TextInputEditText
import com.munchbot.munchbot.R
import com.munchbot.munchbot.databinding.LoginBinding

class Login : AppCompatActivity() {
    private lateinit var binding: LoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBarColor(window, R.color.status_bar_color)

        setupUI(binding.root)

        passwordVisibility()
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
