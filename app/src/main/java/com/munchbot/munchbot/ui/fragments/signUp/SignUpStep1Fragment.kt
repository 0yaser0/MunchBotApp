package com.munchbot.munchbot.ui.fragments.signUp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.SignUp1Binding
import com.munchbot.munchbot.ui.main_view.auth.AuthViewModel
import com.munchbot.munchbot.ui.main_view.auth.Login
import com.munchbot.munchbot.ui.main_view.auth.SignUp

class SignUpStep1Fragment : Fragment() {
    private var _binding: SignUp1Binding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SignUp1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)
        SetupUI.setupUI(binding.root)

        binding.passwordEditText.togglePasswordVisibility(binding.passwordToggle)
        binding.confirmPasswordEditText.togglePasswordVisibility(binding.ConfirmPasswordToggle)

        binding.signUp.setOnClickListener {
            signUpValidate()
        }

        authViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                binding.loaderLayout.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.navigateToLogin.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                redirectToLoginPage()
            }
        }

        authViewModel.authError.observe(viewLifecycleOwner) { error ->
            error?.let {
                binding.loaderLayout.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.showVerificationAlert.observe(viewLifecycleOwner) { show ->
            if (show) {
                showVerificationAlertDialog()
            }
        }

        authViewModel.resendButtonState.observe(viewLifecycleOwner) { state ->
            // Update UI based on resend button state
            // This code assumes you have a reference to the resend button in the alert dialog
            // You'll need to update the dialog button state here
        }
    }

     fun signUpValidate() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()
        val termsAccepted = binding.termsCheckBox.isChecked
        if (validateInput(email, password, confirmPassword, termsAccepted)) {
            authViewModel.signUp(email, password)
        }
        binding.loaderLayout.visibility = View.VISIBLE
    }

    private fun redirectToLoginPage() {
        val intent = Intent(requireContext(), Login::class.java)
        startActivity(intent)
        @Suppress("DEPRECATION")
        requireActivity().overridePendingTransition(
            R.animator.slide_in_left,
            R.animator.slide_out_right
        )
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
            binding.loaderLayout.visibility = View.GONE
            @Suppress("DEPRECATION")
            binding.termsCheckBox.setTextColor(resources.getColor(R.color.red))
            Toast.makeText(
                requireContext(),
                "You must accept the terms and conditions",
                Toast.LENGTH_LONG
            )
                .show()
            isValid = false
        }

        return isValid
    }

    private fun EditText.togglePasswordVisibility(toggleImageView: ImageView) {
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

    private var verificationDialog: AlertDialog? = null

    private fun showVerificationAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Email Verification")
        builder.setMessage("Verification email sent. Please check your inbox.")

        builder.setPositiveButton("Resend", null)
        builder.setNegativeButton("Go", null)

        verificationDialog = builder.create().apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }

        verificationDialog?.show()

        verificationDialog?.let { dialog ->
            val resendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            resendButton.setOnClickListener {
                authViewModel.resendVerificationEmail()
                startResendButtonTimer()
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                authViewModel.checkEmailVerification { isVerified ->
                    if (isVerified) {
                        dialog.dismiss()
                        (activity as? SignUp)?.let {
                            Toast.makeText(requireContext(), "Account created successfully!", Toast.LENGTH_SHORT).show()
                            it.adapter.navigateToFragment(it.viewPager, 1)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Email not verified yet.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            startResendButtonTimer()
        }

        authViewModel.resendButtonState.observe(viewLifecycleOwner) { state ->
            verificationDialog?.let { dialog ->
                val resendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                resendButton.isEnabled = (state == AuthViewModel.ButtonState.CLICKABLE)
                resendButton.setTextColor(
                    if (state == AuthViewModel.ButtonState.CLICKABLE)
                        resources.getColor(R.color.circle_color_selected)
                    else
                        resources.getColor(R.color.p)
                )
            }
        }
    }


    private fun startResendButtonTimer() {
        verificationDialog?.let { dialog ->
            val resendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            resendButton.isEnabled = false
            resendButton.setTextColor(resources.getColor(R.color.p))

            object : CountDownTimer(60000, 1000) { // 60 seconds timer
                override fun onTick(millisUntilFinished: Long) {
                    "Resend (${millisUntilFinished / 1000}s)".also { resendButton.text = it }
                }

                override fun onFinish() {
                    "Resend".also { resendButton.text = it }
                    resendButton.isEnabled = true
                    resendButton.setTextColor(resources.getColor(R.color.circle_color_selected))
                }
            }.start()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        verificationDialog?.dismiss()
        verificationDialog = null
    }
}