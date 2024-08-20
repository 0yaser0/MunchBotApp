package com.munchbot.munchbot.ui.fragments.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.DataStoreManager
import com.munchbot.munchbot.data.repository.DataRepository
import com.munchbot.munchbot.data.viewmodel.MyViewModelData
import com.munchbot.munchbot.data.viewmodel.MyViewModelDataFactory
import com.munchbot.munchbot.databinding.SignUp3Binding
import com.munchbot.munchbot.ui.adapters.ImageAdapterChose
import kotlin.math.abs

class SignUpStep3Fragment : Fragment(), ImageAdapterChose.OnImageClickListener,
    AdapterView.OnItemSelectedListener {
    private lateinit var petViewModel: MyViewModelData
    private lateinit var binding: SignUp3Binding
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: ImageAdapterChose
    private var selectedPosition = -1
    private lateinit var spinner: Spinner
    var selectedSpinnerValue: String? = null
    var selectedImageValue: String? = null

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
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = DataRepository(DataStoreManager(requireContext()))
        val factory = MyViewModelDataFactory(repository)
        petViewModel = ViewModelProvider(this, factory)[MyViewModelData::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)
        SetupUI.setupUI(binding.root)

        viewPager = binding.viewPager2
        spinner = binding.spinner

        setupViewPager()
        setupSpinner()
    }

    fun saveSelectedPetType() {
        val selectedPetType = selectedImageValue ?: selectedSpinnerValue

        if (selectedPetType.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please choose a type of your pet", Toast.LENGTH_SHORT).show()
            return
        }

        val userID = FirebaseAuth.getInstance().currentUser?.uid
        userID?.let {
            petViewModel.savePetType(userID, selectedPetType)
            android.util.Log.d("SignUpStep3Fragment", "Saved Pet Type: $selectedPetType for User: $userID")
        }
    }

    private fun setupViewPager() {
        adapter = ImageAdapterChose(images, this)
        viewPager.adapter = adapter
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
            adapter.setSelectedPosition(selectedPosition)

            selectedImageValue = when (images[position]) {
                R.drawable.horse -> "Horse"
                R.drawable.cat -> "Cat"
                R.drawable.cow -> "Cow"
                R.drawable.dog -> "Dog"
                R.drawable.sheep -> "Sheep"
                else -> null
            }
            android.util.Log.d("SignUpStep3Fragment", "Image: $selectedImageValue")

            val currentItem = viewPager.currentItem
            val imagesSize = images.size
            val targetItem = when {
                position > currentItem % imagesSize -> currentItem + (position - (currentItem % imagesSize))
                position < currentItem % imagesSize -> currentItem - ((currentItem % imagesSize) - position)
                else -> currentItem
            }
            viewPager.setCurrentItem(targetItem, true)
            spinner.setSelection(0)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position != 0) {
            adapter.setSelectedPosition(-1)
            selectedPosition = -1

            selectedSpinnerValue = parent?.getItemAtPosition(position).toString()

            val startPosition = images.size * 1000
            viewPager.setCurrentItem(startPosition, false)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
