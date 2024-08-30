package com.munchbot.munchbot.ui.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.viewmodel.SignUpSharedViewModel
import com.munchbot.munchbot.databinding.SignUp5Binding
import com.munchbot.munchbot.ui.main_view.auth.SignUp

class SignUpStep5Fragment : MunchBotFragments() {
    private lateinit var binding: SignUp5Binding
    private lateinit var sharedViewModel: SignUpSharedViewModel
    private lateinit var signUp: SignUp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUp5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.secondColor)
        SetupUI.setupUI(binding.root)

        setupViewModel()

        signUp = activity as SignUp
    }

    private fun setupViewModel() {
        sharedViewModel = ViewModelProvider(requireActivity())[SignUpSharedViewModel::class.java]
    }

    fun saveAllData() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        android.util.Log.d("FinalSignUpFragment", "UserID: $userID")

        userID?.let { uid ->
            showLoader()

            sharedViewModel.saveUserAndPet(uid)

           binding.root.postDelayed({
                if (isAdded) {
                    hideLoader()
                    Toast.makeText(requireContext(), "All data saved successfully!", Toast.LENGTH_LONG).show()
                } else {
                    android.util.Log.e("FinalSignUpFragment", "Fragment is not attached to context, cannot show Toast.")
                }
            }, 2000)
        } ?: run {
            android.util.Log.e("FinalSignUpFragment", "UserID is null.")
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
}