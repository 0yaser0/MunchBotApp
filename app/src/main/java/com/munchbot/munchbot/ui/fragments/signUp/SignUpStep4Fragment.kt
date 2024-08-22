package com.munchbot.munchbot.ui.fragments.signUp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.SignUpDataStoreManager
import com.munchbot.munchbot.data.repository.SignUpDataRepository
import com.munchbot.munchbot.data.viewmodel.MyViewModelDataFactory
import com.munchbot.munchbot.data.viewmodel.SignUpSharedViewModel
import com.munchbot.munchbot.data.viewmodel.SignUpViewModelData
import com.munchbot.munchbot.databinding.SignUp4Binding
import com.munchbot.munchbot.ui.adapters.SignUpAdapter
import com.munchbot.munchbot.ui.main_view.auth.SignUp
import java.util.Calendar

@Suppress("DEPRECATION")
class SignUpStep4Fragment : MunchBotFragments() {
    private lateinit var binding: SignUp4Binding
    private val pickImageRequest = 1
    private val cameraRequest = 2
    private lateinit var selectedImageUri: Uri
    private lateinit var petViewModel: SignUpViewModelData
    private lateinit var sharedViewModel: SignUpSharedViewModel
    private lateinit var adapter: SignUpAdapter
    private lateinit var signUp: SignUp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUp4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)
        SetupUI.setupUI(binding.root)

        setupViewModel()

        signUp = activity as SignUp
        adapter = signUp.adapter

        chosePetImage()

        handleErrorsCalendar()

        choseGender()

    }

    private fun setupViewModel() {
        val repository = SignUpDataRepository(SignUpDataStoreManager(requireContext()))
        val factory = MyViewModelDataFactory(repository)
        petViewModel = ViewModelProvider(this, factory)[SignUpViewModelData::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SignUpSharedViewModel::class.java]
    }

    fun validatePetProfileInputAndProceed(callback: (Boolean) -> Unit) {
        val petName = binding.nameEditText.text.toString()
        val petGender = binding.genderEditText.text.toString()
        val petWeight = binding.weightEditText.text.toString()
        val petBreed = binding.breedEditText.text.toString()
        val petDateOfBirth = binding.calendarEditText.text.toString()
        val petHeight = binding.heightEditText.text.toString()

        validatePetDetails()
        validatePetMeasurements()

        if (petName.isEmpty()) {
            binding.nameEditText.error = "Name cannot be empty"
            callback(false)
        } else if (petGender.isEmpty()) {
            binding.genderEditText.error = "Gender cannot be empty"
            callback(false)
        } else if (petWeight.isEmpty()) {
            binding.weightEditText.error = "Weight cannot be empty"
            callback(false)
        } else if (petBreed.isEmpty()) {
            binding.breedEditText.error = "Breed cannot be empty"
            callback(false)
        } else if (petDateOfBirth.isEmpty()) {
            binding.calendarEditText.error = "Date of Birth cannot be empty"
            callback(false)
        } else if (petHeight.isEmpty()) {
            binding.heightEditText.error = "Height cannot be empty"
            callback(false)
        } else if (!::selectedImageUri.isInitialized) {
            Toast.makeText(requireContext(), "Please select a picture.", Toast.LENGTH_LONG).show()
            callback(false)
        } else {
            showLoader()
            android.util.Log.d("PetProfileFragment", "Saving Name: $petName")
            android.util.Log.d("PetProfileFragment", "Saving Gender: $petGender")
            android.util.Log.d("PetProfileFragment", "Saving Weight: $petWeight")
            android.util.Log.d("PetProfileFragment", "Saving Breed: $petBreed")
            android.util.Log.d("PetProfileFragment", "Saving Date of Birth: $petDateOfBirth")
            android.util.Log.d("PetProfileFragment", "Saving Height: $petHeight")

            sharedViewModel.savePetName(petName)
            sharedViewModel.savePetGender(petGender)
            sharedViewModel.savePetWeight(petWeight)
            sharedViewModel.savePetBreed(petBreed)
            sharedViewModel.savePetDateOfBirth(petDateOfBirth)
            sharedViewModel.savePetHeight(petHeight)
            sharedViewModel.savePetImageProfile(selectedImageUri)

            binding.root.postDelayed({
                hideLoader()
                Toast.makeText(requireContext(), "Pet profile saved successfully!", Toast.LENGTH_LONG)
                    .show()
                callback(true)
            }, 2000)
        }
    }

    private fun handleErrorsCalendar() {
        binding.calendarEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.calendarEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = binding.calendarEditText.text.toString()
                val regex = Regex("^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-[0-9]{4}\$")
                val dayMonthYear = input.split("-")

                if (dayMonthYear.size == 3) {
                    val day = dayMonthYear[0].toIntOrNull() ?: 0
                    val month = dayMonthYear[1].toIntOrNull() ?: 0
                    val year = dayMonthYear[2].toIntOrNull() ?: 0

                    if (!input.matches(regex)) {
                        binding.calendarEditText.error =
                            "Invalid date format. Please use DD-MM-YYYY."
                    } else if (day !in 1..31) {
                        binding.calendarEditText.error =
                            "Invalid date. Day must be between 1 and 31."
                    } else if (month !in 1..12) {
                        binding.calendarEditText.error =
                            "Invalid date. Month must be between 1 and 12."
                    } else if (year < 1900 || year > 2100) {
                        binding.calendarEditText.error =
                            "Invalid year. Year must be between 1900 and 2100."
                    } else {
                        if (isFutureDate(day, month, year)) {
                            binding.calendarEditText.error =
                                "Invalid date. You cannot select a future date."
                        } else {
                            binding.calendarEditText.error = null
                        }
                    }
                } else {
                    binding.calendarEditText.error = "Invalid date format. Please use DD-MM-YYYY."
                }
            }
        }
    }

    private fun isFutureDate(day: Int, month: Int, year: Int): Boolean {
        val today = Calendar.getInstance()
        val inputDate = Calendar.getInstance().apply {
            set(year, month - 1, day)
        }
        return inputDate.after(today)
    }



    private fun validatePetDetails() {
        val petName = binding.nameEditText.text.toString().trim()
        val petBreed = binding.breedEditText.text.toString().trim()

        val namePattern = "^[A-Za-z]+$"
        val isValidName = petName.matches(namePattern.toRegex())
        val isValidBreed = petBreed.matches(namePattern.toRegex())

        if (!isValidName) {
            binding.nameEditText.error = "Name should only contain letters"
        } else {
            binding.nameEditText.error = null
        }

        if (!isValidBreed) {
            binding.breedEditText.error = "Breed should only contain letters"
        } else {
            binding.breedEditText.error = null
        }
    }


    private fun choseGender() {
        val genderEditText = binding.genderEditText
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            arrayOf("Male", "Female")
        )
        genderEditText.setAdapter(adapter)

        genderEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = genderEditText.text.toString().trim()
                val validInputs = arrayOf("Male", "Female")

               if (validInputs.none { it.equals(input, ignoreCase = true) }) {
                    binding.genderTextInputLayout.error = "Please specify Male or Female"
                    genderEditText.error = "Please specify Male or Female"
                } else {
                    binding.genderTextInputLayout.error = null
                    genderEditText.error = null
                }
            }
        }
    }

    private fun validatePetMeasurements() {
        val petWeightStr = binding.weightEditText.text.toString().trim()
        val petHeightStr = binding.heightEditText.text.toString().trim()

        val petWeight = petWeightStr.toDoubleOrNull()
        val petHeight = petHeightStr.toDoubleOrNull()

        if (petWeight == null || petWeight <= 0) {
            binding.weightEditText.error = "Weight must be greater than 0"
        } else {
            binding.weightEditText.error = null
        }

        if (petHeight == null || petHeight <= 0) {
            binding.heightEditText.error = "Height must be greater than 0"
        } else {
            binding.heightEditText.error = null
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialog,
            { _, year, month, day ->
                val selectedDate = "$day-${month + 1}-$year"
                binding.calendarEditText.setText(selectedDate)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        datePickerDialog.window?.setGravity(Gravity.CENTER)
        datePickerDialog.show()
    }

    private fun chosePetImage() {
        binding.camera.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            AlertDialog.Builder(requireContext())
                .setTitle("Select Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> dispatchTakePictureIntent()
                        1 -> dispatchChoosePictureIntent()
                        2 -> {}
                    }
                }
                .show()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, cameraRequest)
        }
    }

    private fun dispatchChoosePictureIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, pickImageRequest)
    }

    private fun handleImageUpload(imageUri: Uri) {
        val requestOptions = RequestOptions()
            .transforms(CircleCrop())
            .placeholder(R.drawable.ic_camera)
            .error(R.drawable.ic_error)

        Glide.with(this)
            .load(imageUri)
            .apply(requestOptions)
            .into(binding.camera)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                pickImageRequest -> {
                    selectedImageUri = data.data!!
                    handleImageUpload(selectedImageUri)
                }

                cameraRequest -> {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    val uri = Uri.parse(MediaStore.Images.Media.insertImage(requireContext().contentResolver, imageBitmap, null, null))
                    handleImageUpload(uri)
                }
            }
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

