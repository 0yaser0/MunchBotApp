package com.munchbot.munchbot.ui.fragments.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
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
                    Glide.with(this)
                        .load(it.petProfileImage)
                        .error(R.drawable.ic_error)
                        .into(petImageView)
                }
            }
        }
    }

    private fun setupGetter(){
        val userId = getUserId()
        Log.d(TAG, "User ID $userId")

        if (userId != null) {
            getPetId(userId) { petId ->
                if (petId != null) {
                    petViewModel.loadPet(userId, petId)
                    Log.d(TAG, "Pet ID $petId")
                } else {
                    Log.d(TAG, "Pet ID not found")
                }
            }
            userViewModel.loadUser(userId)
        }
    }

    private fun base64ToBitmap(base64: String): Bitmap? {
        return try {
            val decodedString = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: Exception) {
            Log.e("Home1Fragment", "Error decoding Base64 string to Bitmap", e)
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}