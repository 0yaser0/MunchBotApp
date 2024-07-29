package com.munchbot.munchbot.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.databinding.SignUpBinding

class SignUp : AppCompatActivity() {
    private lateinit var binding: SignUpBinding
    private val authViewModel: AuthViewModel by viewModels()

    private val progressStates = arrayOf(0, 50, 90, 100)
    private var currentStateIndex = 0
    companion object {
        private const val ANIMATION_DURATION = 1000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateProgress(progressStates[currentStateIndex])

        binding.updateProgressButton.setOnClickListener {
            currentStateIndex = (currentStateIndex + 1) % progressStates.size
            updateProgress(progressStates[currentStateIndex])
        }

        StatusBarUtils.setStatusBarColor(window, R.color.status_bar_color)

        SetupUI.setupUI(binding.root)
        binding.passwordEditText.togglePasswordVisibility(binding.passwordToggle)
        binding.confirmPasswordEditText.togglePasswordVisibility(binding.ConfirmPasswordToggle)

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val termsAccepted = binding.termsCheckBox.isChecked

            if (validateInput(email, password, confirmPassword, termsAccepted)) {
                authViewModel.signUp(email, password)
            }
            binding.loaderLayout.visibility = View.VISIBLE
        }

        authViewModel.toastMessage.observe(this) { message ->
            message?.let {
                binding.loaderLayout.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.navigateToLogin.observe(this) { navigate ->
            if (navigate) {
                redirectToLoginPage()
            }
        }

        authViewModel.authError.observe(this) { error ->
            error?.let {
                binding.loaderLayout.visibility = View.GONE
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            updateProgress(progressStates[currentStateIndex])
        }
    }

    private fun updateProgress(progress: Int) {
        val progressBarWidth = binding.progressBar.width
        val indicatorWidth = binding.progressIndicator.width
        val indicatorPosition = progressBarWidth * (progress / 100f) - indicatorWidth / 2f

        android.animation.ObjectAnimator.ofInt(binding.progressBar, "progress", progress).apply {
            duration = ANIMATION_DURATION
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        binding.progressIndicator.animate()
            .translationX(indicatorPosition)
            .setDuration(ANIMATION_DURATION)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }


    private fun redirectToLoginPage() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        @Suppress("DEPRECATION")
        overridePendingTransition(R.animator.slide_in_left, R.animator.slide_out_right)
    }

    fun EditText.togglePasswordVisibility(toggleImageView: ImageView) {
        var isPasswordVisible = false
        toggleImageView.setOnClickListener {
            if (isPasswordVisible) {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggleImageView.setImageResource(R.drawable.ic_dog_eyes_close)
            } else {
                inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                toggleImageView.setImageResource(R.drawable.ic_dog_eyes_open)
            }
            setSelection(text?.length ?: 0)
            isPasswordVisible = !isPasswordVisible
        }
    }

    private fun validateInput(
        email: String,
        password: String,
        confirmPassword: String,
        termsAccepted: Boolean
    ): Boolean {
        var isValid = true

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.error = "Invalid email address"
            isValid = false
        }

        val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
        if (!password.matches(passwordPattern.toRegex())) {
            binding.passwordEditText.error =
                "Password must be at least 8 characters long and contain letters and numbers"
            isValid = false
        }

        if (password != confirmPassword) {
            binding.confirmPasswordEditText.error = "Passwords do not match"
            isValid = false
        }

        if (!termsAccepted) {
            @Suppress("DEPRECATION")
            binding.termsCheckBox.setTextColor(resources.getColor(R.color.red))
            Toast.makeText(this, "You must accept the terms and conditions", Toast.LENGTH_LONG)
                .show()
            isValid = false
        }

        return isValid
    }
}
