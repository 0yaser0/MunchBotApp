package com.munchbot.munchbot.ui.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.SignUpDataStoreManager
import com.munchbot.munchbot.data.repository.SignUpDataRepository
import com.munchbot.munchbot.data.viewmodel.MyViewModelDataFactory
import com.munchbot.munchbot.data.viewmodel.SignUpSharedViewModel
import com.munchbot.munchbot.data.viewmodel.SignUpViewModelData
import com.munchbot.munchbot.databinding.SignUp5Binding
import com.munchbot.munchbot.ui.adapters.SignUpAdapter
import com.munchbot.munchbot.ui.main_view.auth.SignUp

class SignUpStep5Fragment : MunchBotFragments() {
    private lateinit var binding: SignUp5Binding
    private val sharedViewModel: SignUpSharedViewModel by activityViewModels()
    private lateinit var viewModel: SignUpViewModelData
    private lateinit var adapter: SignUpAdapter
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
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)
        SetupUI.setupUI(binding.root)

        setupViewModel()

        signUp = activity as SignUp
        adapter = signUp.adapter

    }

    private fun setupViewModel() {
        val repository = SignUpDataRepository(SignUpDataStoreManager(requireContext()))
        val factory = MyViewModelDataFactory(repository)
        viewModel = ViewModelProvider(this, factory)[SignUpViewModelData::class.java]
    }

    fun saveAllData() {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        android.util.Log.d("FinalSignUpFragment", "UserID: $userID")

        userID?.let { uid ->
            showLoader()

            sharedViewModel.username.observe(viewLifecycleOwner) { username ->
                username?.let {
                    viewModel.saveUsername(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Username: $it")
                }
            }
            sharedViewModel.status.observe(viewLifecycleOwner) { status ->
                status?.let {
                    viewModel.saveStatus(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Status: $it")
                }
            }
            sharedViewModel.bio.observe(viewLifecycleOwner) { bio ->
                bio?.let {
                    viewModel.saveBio(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Bio: $it")
                }
            }
            sharedViewModel.userProfileImage.observe(viewLifecycleOwner) { uri ->
                uri?.let {
                    viewModel.saveUserProfileImage(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Image Uri: $it")
                }
            }
            sharedViewModel.petType.observe(viewLifecycleOwner) { selectedPetType ->
                selectedPetType?.let {
                    viewModel.savePetType(uid, it)
                    android.util.Log.d(
                        "FinalSignUpFragment",
                        "Saved Pet Type: $selectedPetType for User: $uid"
                    )
                }
            }
            sharedViewModel.petName.observe(viewLifecycleOwner) { petName ->
                petName?.let {
                    viewModel.savePetName(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Pet Name: $it")
                }
            }
            sharedViewModel.petGender.observe(viewLifecycleOwner) { petGender ->
                petGender?.let {
                    viewModel.savePetGender(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Pet Gender: $it")
                }
            }
            sharedViewModel.petWeight.observe(viewLifecycleOwner) { petWeight ->
                petWeight?.let {
                    viewModel.savePetWeight(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Pet Weight: $it")
                }
            }
            sharedViewModel.petBreed.observe(viewLifecycleOwner) { petBreed ->
                petBreed?.let {
                    viewModel.savePetBreed(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Pet Breed: $it")
                }
            }
            sharedViewModel.petDateOfBirth.observe(viewLifecycleOwner) { petDateOfBirth ->
                petDateOfBirth?.let {
                    viewModel.savePetDateOfBirth(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Pet Date of Birth: $it")
                }
            }
            sharedViewModel.petHeight.observe(viewLifecycleOwner) { petHeight ->
                petHeight?.let {
                    viewModel.savePetHeight(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Pet Height: $it")
                }
            }
            sharedViewModel.petImageProfile.observe(viewLifecycleOwner) { uri ->
                uri?.let {
                    viewModel.savePetProfileImage(uid, it)
                    android.util.Log.d("FinalSignUpFragment", "Saved Pet Image Uri: $it")
                }
            }

            binding.root.postDelayed({
                hideLoader()
                Toast.makeText(requireContext(), "All data saved successfully!", Toast.LENGTH_LONG)
                    .show()
            }, 2000)
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