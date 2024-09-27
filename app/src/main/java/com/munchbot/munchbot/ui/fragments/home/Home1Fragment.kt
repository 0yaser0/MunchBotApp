package com.munchbot.munchbot.ui.fragments.home

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.getPetId
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.viewmodel.PetViewModel
import com.munchbot.munchbot.data.viewmodel.SignUpSharedViewModel
import com.munchbot.munchbot.data.viewmodel.UserViewModel
import com.munchbot.munchbot.databinding.Home1Binding
import com.munchbot.munchbot.ui.main_view.auth.AuthViewModel
import com.munchbot.munchbot.ui.main_view.auth.Login

@GlideModule
class Home1Fragment : MunchBotFragments() {
    private var _binding: Home1Binding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SignUpSharedViewModel
    private lateinit var authViewModel: AuthViewModel
    private val userViewModel: UserViewModel by viewModels()
    private val petViewModel: PetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Home1Binding.inflate(inflater, container, false)

        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[SignUpSharedViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.black)

        binding.bluetooth.setOnClickListener {
            Log.d("Home1Fragment", "clicked button Bluetooth ")
            authViewModel.signOut()
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        setupGetter()

        userViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                val helloUserText = getString(R.string.helloUser, it.username)
                val spannableString = SpannableString(helloUserText)
                val usernameStart = helloUserText.indexOf(it.username)
                val usernameEnd = usernameStart + it.username.length
                spannableString.setSpan(
                    ForegroundColorSpan(Color.parseColor("#0491E9")),
                    usernameStart,
                    usernameEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    usernameStart,
                    usernameEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                binding.helloUser.text = spannableString
            }
        }

        petViewModel.petLiveData.observe(viewLifecycleOwner) { pet ->
            pet?.let {
                val petNameTextView = binding.petName
                petNameTextView.text = it.petName

                val petImageView = binding.PetImageHome
                if (it.petProfileImage.isNotEmpty()) {
                    Log.d("Home1Fragment", "Loading image URL: ${it.petProfileImage}")
                    Glide.with(this)
                        .load(it.petProfileImage)
                        .error(R.drawable.ic_error)
                        .into(petImageView)
                } else {
                    petImageView.setImageResource(R.drawable.ic_launcher_round)
                }
            }
        }
    }

    private fun setupGetter() {
        val userId = getUserId()
        Log.d("Home1Fragment", "User ID $userId")

        if (userId != null) {
            val petId = getPetId(userId)
            Log.d("Home1Fragment", "Pet ID $petId")
            userViewModel.loadUser(userId)
            petViewModel.loadPet(userId, petId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}