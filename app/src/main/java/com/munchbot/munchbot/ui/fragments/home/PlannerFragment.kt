package com.munchbot.munchbot.ui.fragments.home

import android.content.ContentValues.TAG
import android.os.Bundle
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
import com.munchbot.munchbot.databinding.FragmentPlannerBinding
import com.munchbot.munchbot.ui.main_view.auth.AuthViewModel

class PlannerFragment : MunchBotFragments() {
    private var _binding: FragmentPlannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SignUpSharedViewModel
    private lateinit var authViewModel: AuthViewModel
    private val userViewModel: UserViewModel by viewModels()
    private val petViewModel: PetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlannerBinding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        viewModel = ViewModelProvider(requireActivity())[SignUpSharedViewModel::class.java]
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.black)

        setupGetter()

        petViewModel.petLiveData.observe(viewLifecycleOwner) { pet ->
            pet?.let {
                val petName = getString(R.string.Take_a_look_at_how_pet_s_day_was_planned, it.petName)
                binding.TakeALookAtHowPetSDayWasPlanned.text = petName

                val petImageView = binding.petImagePlanner
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

}