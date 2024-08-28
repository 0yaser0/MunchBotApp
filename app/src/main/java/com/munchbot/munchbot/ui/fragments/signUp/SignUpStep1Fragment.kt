package com.munchbot.munchbot.ui.fragments.signUp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.databinding.SignUp1Binding
import com.munchbot.munchbot.ui.main_view.auth.AuthViewModel
import com.munchbot.munchbot.ui.main_view.auth.Login
import com.munchbot.munchbot.ui.main_view.auth.SignUp

@Suppress("DEPRECATION")
class SignUpStep1Fragment : MunchBotFragments() {
    private var _binding: SignUp1Binding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()
    private var verificationDialog: AlertDialog? = null
    private lateinit var signUp: SignUp

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
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.secondColor)
        SetupUI.setupUI(binding.root)

        signUp = requireActivity() as SignUp

        binding.passwordEditText.togglePasswordVisibility(binding.passwordToggle)
        binding.confirmPasswordEditText.togglePasswordVisibility(binding.ConfirmPasswordToggle)

        authViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                hideLoader()
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
                hideLoader()
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.showVerificationAlert.observe(viewLifecycleOwner) { show ->
            if (show) {
                showVerificationAlertDialog()
            }
        }

        binding.termsCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                @Suppress("DEPRECATION")
                binding.termsCheckBox.setTextColor(resources.getColor(R.color.p))
            } else {
                @Suppress("DEPRECATION")
                binding.termsCheckBox.setTextColor(resources.getColor(R.color.red))
            }
        }
    }

    fun signUpValidate() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val confirmPassword = binding.confirmPasswordEditText.text.toString()
        val termsAccepted = binding.termsCheckBox.isChecked

        if (validateInput(email, password, confirmPassword, termsAccepted)) {
            showLoader()
            authViewModel.signUp(email, password)
            saveEmailForValidation(email)
        }
    }

    private fun showLoader() {
        (activity as? SignUp)?.showLoader(true)
        binding.root.isClickable = false
        binding.root.isEnabled = false
    }

    private fun hideLoader() {
        (activity as? SignUp)?.showLoader(false)
        binding.root.isClickable = true
        binding.root.isEnabled = true
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

    private var isTermsAccepted = false

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
            Toast.makeText(
                requireContext(),
                "You must accept the terms and conditions",
                Toast.LENGTH_LONG
            ).show()
            isValid = false
        } else {
            @Suppress("DEPRECATION")
            binding.termsCheckBox.setTextColor(resources.getColor(R.color.p))
            isTermsAccepted = true
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
                startResendButtonTimer(resendButton)
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                authViewModel.checkEmailVerification { isVerified ->
                    if (isVerified) {
                        dialog.dismiss()
                        (activity as? SignUp)?.let {
                            Toast.makeText(
                                requireContext(),
                                "Account created successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            hideLoader()
                            it.adapter.navigateToFragment(it.viewPager, 1)
                            val progressStates = arrayOf(0, 50, 80, 100)
                            signUp.updateProgress(progressStates[1])
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Email not verified yet.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            startResendButtonTimer(resendButton)
        }

        authViewModel.resendButtonState.observe(viewLifecycleOwner) { state ->
            verificationDialog?.let { dialog ->
                val resendButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                resendButton.isEnabled = (state == AuthViewModel.ButtonState.CLICKABLE)
                resendButton.setTextColor(
                    if (state == AuthViewModel.ButtonState.CLICKABLE)
                        resources.getColor(R.color.firstColor)
                    else
                        resources.getColor(R.color.p)
                )
            }
        }
    }

    private fun startResendButtonTimer(resendButton: Button) {
        resendButton.isEnabled = false
        resendButton.setTextColor(resources.getColor(R.color.p))
        object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                resendButton.text = "Resend (${millisUntilFinished / 1000}s)"
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                resendButton.text = "Resend"
                resendButton.isEnabled = true
                resendButton.setTextColor(resources.getColor(R.color.firstColor))
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        verificationDialog?.dismiss()
        verificationDialog = null
    }

    fun saveEmailForValidation(email: String) {
        val emailData = hashMapOf(
            "email" to email,
            "timestamp" to System.currentTimeMillis(),
            "validated" to false
        )

        FirebaseFirestore.getInstance().collection("pendingEmails")
            .add(emailData)
            .addOnSuccessListener { documentReference ->
                Log.d("SignUp", "Email saved for validation with ID: ${documentReference.id}")
                scheduleEmailDeletion(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w("SignUp", "Error adding email for validation", e)
            }
    }

    private fun scheduleEmailDeletion(documentId: String) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            deleteEmailIfNotValidated(documentId)
        }
        //  1 hour katsawi (3600000 milliseconds)
        handler.postDelayed(runnable, 300000)
    }

    private fun deleteEmailIfNotValidated(documentId: String) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("pendingEmails").document(documentId)

        documentRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val validated = document.getBoolean("validated") ?: false
                if (!validated) {
                    documentRef.delete()
                        .addOnSuccessListener {
                            Log.d("SignUp", "Expired email deleted successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.w("SignUp", "Error deleting expired email", e)
                        }
                }
            }
        }.addOnFailureListener { e ->
            Log.w("SignUp", "Error fetching email for deletion", e)
        }
    }

}
