package com.munchbot.munchbot.ui.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.viewmodel.SignUpSharedViewModel
import com.munchbot.munchbot.data.viewmodel.SignUpViewModelData
import com.munchbot.munchbot.databinding.SignUp3Binding
import com.munchbot.munchbot.ui.adapters.ImageAdapterChose
import com.munchbot.munchbot.ui.adapters.SignUpAdapter
import com.munchbot.munchbot.ui.main_view.auth.SignUp
import kotlin.math.abs

class SignUpStep3Fragment : MunchBotFragments(), ImageAdapterChose.OnImageClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var sharedViewModel: SignUpSharedViewModel
    private lateinit var binding: SignUp3Binding
    private lateinit var viewPager: ViewPager2
    private lateinit var adapterImage: ImageAdapterChose
    private var selectedPosition = -1
    private lateinit var spinner: Spinner
    var selectedSpinnerValue: String? = null
    var selectedImageValue: String? = null
    private lateinit var adapter: SignUpAdapter
    private lateinit var signUp: SignUp

    private val images = listOf(
        R.drawable.horse,
        R.drawable.cat,
        R.drawable.cow,
        R.drawable.dog,
        R.drawable.sheep
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUp3Binding.inflate(inflater, container, false)
        setupViewModel()
        return binding.root
    }

    private fun setupViewModel() {
        sharedViewModel = ViewModelProvider(requireActivity())[SignUpSharedViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.secondColor)
        SetupUI.setupUI(binding.root)

        viewPager = binding.viewPager2
        spinner = binding.spinner

        setupViewPager()
        setupSpinner()

        signUp = activity as SignUp
        adapter = signUp.adapter

    }

    fun saveSelectedPetType() {
        val selectedPetType = selectedImageValue ?: selectedSpinnerValue

        if (selectedPetType.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please choose a type of your pet", Toast.LENGTH_SHORT).show()
            return
        }

        showLoader()
            sharedViewModel.setPetType(selectedPetType)
            android.util.Log.d("SignUpStep3Fragment", "Saved Pet Type: $selectedPetType ")

            binding.root.postDelayed({
                hideLoader()
            }, 2000)
    }

    private fun setupViewPager() {
        adapterImage = ImageAdapterChose(images, this)
        viewPager.adapter = adapterImage
        viewPager.offscreenPageLimit = 1

        val startPosition = images.size * 1000
        viewPager.setCurrentItem(startPosition, false)

        viewPager.setPageTransformer { page, position ->
            page.translationX = position * ((-250).dpToPx())
            val scaleFactor = 0.85f.coerceAtLeast(1 - abs(position))
            page.scaleY = scaleFactor
            page.alpha = scaleFactor
        }
    }

    private fun setupSpinner() {
        spinner.onItemSelectedListener = this
    }

    override fun onImageClick(position: Int) {
        if (selectedPosition != position) {
            selectedPosition = position
            adapterImage.setSelectedPosition(selectedPosition)

            selectedImageValue = when (images[position]) {
                R.drawable.horse -> "Horse"
                R.drawable.cat -> "Cat"
                R.drawable.cow -> "Cow"
                R.drawable.dog -> "Dog"
                R.drawable.sheep -> "Sheep"
                else -> null
            }
            android.util.Log.d("SignUpStep3Fragment", "Image: $selectedImageValue")

            selectedSpinnerValue = null
            spinner.setSelection(0)

            val currentItem = viewPager.currentItem
            val imagesSize = images.size
            val targetItem = when {
                position > currentItem % imagesSize -> currentItem + (position - (currentItem % imagesSize))
                position < currentItem % imagesSize -> currentItem - ((currentItem % imagesSize) - position)
                else -> currentItem
            }
            viewPager.setCurrentItem(targetItem, true)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position != 0) {
            adapterImage.setSelectedPosition(-1)
            selectedPosition = -1

            selectedSpinnerValue = parent?.getItemAtPosition(position).toString()
            android.util.Log.d("SignUpStep3Fragment", "Spinner: $selectedSpinnerValue")

            selectedImageValue = null
            viewPager.setCurrentItem(images.size * 1000, false)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
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
