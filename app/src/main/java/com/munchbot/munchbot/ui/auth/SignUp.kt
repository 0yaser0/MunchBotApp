package com.munchbot.munchbot.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.munchbot.munchbot.R
import com.munchbot.munchbot.databinding.SignUpBinding

@Suppress("DEPRECATION")
class SignUp : AppCompatActivity() {
    private lateinit var binding: SignUpBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()
            val termsAccepted = binding.termsCheckBox.isChecked

            if (validateInput(email, password, confirmPassword, termsAccepted)) {
                authViewModel.createAccount(email, password)
            }
            binding.loaderLayout.visibility = View.VISIBLE
        }

        authViewModel.authError.observe(this) { error ->
            error?.let {
                binding.loaderLayout.visibility = View.GONE
                if (it == "The email address is already in use by another account.") {
                    Toast.makeText(this, "$it Please try logging in.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    @Suppress("DEPRECATION")
                    overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left)
                } else {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            }
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
