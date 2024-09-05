package com.munchbot.munchbot.ui.fragments.home

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.getPetId
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.viewmodel.PetViewModel
import com.munchbot.munchbot.data.viewmodel.UserViewModel
import com.munchbot.munchbot.databinding.FragmentHealthBinding
import com.munchbot.munchbot.ui.fragments.home.health.Health1Fragment
import com.munchbot.munchbot.ui.fragments.home.health.Health2Fragment
import com.munchbot.munchbot.ui.fragments.home.health.Health3Fragment
import com.munchbot.munchbot.ui.fragments.home.health.Health4Fragment

@Suppress("DEPRECATION")
@GlideModule
class HealthFragment : Fragment() {
    private var _binding: FragmentHealthBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private val petViewModel: PetViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.black)

        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.vaccinations.setOnClickListener {
            handleButtonClick(
                it,
                R.drawable.ic_btn_getstarted_clicked,
                R.style.btn_40px_blue,
                R.drawable.ic_vaccinations_clicked,
                R.drawable.ic_vaccinations
            )
            navigateToAnotherFragment(Health1Fragment())
        }

        binding.vetVisits.setOnClickListener {
            handleButtonClick(
                it,
                R.drawable.ic_btn_getstarted_clicked,
                R.style.btn_40px_blue,
                R.drawable.ic_vet_visits_clicked,
                R.drawable.ic_vet_visits
            )
            navigateToAnotherFragment(Health2Fragment())
        }

        binding.reports.setOnClickListener {
            handleButtonClick(
                it,
                R.drawable.ic_btn_getstarted_clicked,
                R.style.btn_40px_blue,
                R.drawable.ic_reports_clilcked,
                R.drawable.ic_reports
            )
            navigateToAnotherFragment(Health3Fragment())
        }

        binding.medications.setOnClickListener {
            handleButtonClick(
                it,
                R.drawable.ic_btn_getstarted_clicked,
                R.style.btn_40px_blue,
                R.drawable.ic_medications_clicked,
                R.drawable.ic_medications
            )
            navigateToAnotherFragment(Health4Fragment())
        }
    }

    private fun navigateToAnotherFragment(fragment: Fragment) {
        Log.d("HealthFragment", "Navigating to fragment: ${fragment::class.java.simpleName}")
        parentFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .setCustomAnimations(
                R.animator.slide_in_right,
                R.animator.slide_out_left,
                R.animator.slide_in_left,
                R.animator.slide_out_right
            )
            .replace(R.id.fragment_container_health, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun handleButtonClick(
        view: View,
        backgroundRes: Int,
        textStyle: Int,
        imageRes: Int,
        defaultImageRes: Int
    ) {
        val button = view as? TextView ?: return
        val imageView = when (view.id) {
            R.id.vaccinations -> binding.icVaccinations
            R.id.vetVisits -> binding.icVetVisits
            R.id.reports -> binding.icReports
            R.id.medications -> binding.icMedications
            else -> {
                Log.e("HealthFragment", "Unhandled button ID: ${view.id}")
                return
            }
        }

        button.setBackgroundResource(backgroundRes)
        button.setTextAppearance(textStyle)
        imageView.setImageResource(imageRes)

        Handler().postDelayed({

            button.setBackgroundResource(R.drawable.ic_btn_getstarted)
            button.setTextAppearance(R.style.btn_40px_white)
            imageView.setImageResource(defaultImageRes)
        }, 400)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupObservers() {
        petViewModel.petLiveData.observe(viewLifecycleOwner) { pet ->
            pet?.let {
                val petImageView = binding.petImageHealth
                if (it.petProfileImage.isNotEmpty()) {
                    Log.d("HealthFragment", "Loading image URL: ${it.petProfileImage}")
                    Glide.with(this)
                        .load(it.petProfileImage)
                        .error(R.drawable.ic_error)
                        .into(petImageView)
                } else {
                    petImageView.setImageResource(R.drawable.ic_error)
                }
            }
        }
        setupGetter()
    }

    private fun setupGetter() {
        val userId = getUserId() ?: return
        Log.d(TAG, "Health User ID $userId")

        val petId = getPetId(userId)
        Log.d(TAG, "Health Pet ID $petId")

        petViewModel.loadPet(userId, petId)
        userViewModel.loadUser(userId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}