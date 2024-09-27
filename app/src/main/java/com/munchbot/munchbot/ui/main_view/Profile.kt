package com.munchbot.munchbot.ui.main_view

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.munchbot.munchbot.MunchBotActivity
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.getPetId
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.model.Pet
import com.munchbot.munchbot.data.model.User
import com.munchbot.munchbot.data.viewmodel.PetViewModel
import com.munchbot.munchbot.data.viewmodel.UserViewModel
import com.munchbot.munchbot.databinding.ProfileBinding
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class Profile : MunchBotActivity() {
    private lateinit var binding: ProfileBinding
    lateinit var viewPager: ViewPager
    private val userViewModel: UserViewModel by viewModels()
    private val petViewModel: PetViewModel by viewModels()
    private var isEditing = false
    private var currentUser: User? = null
    private var isEditingPet = false
    private var currentPet: Pet? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.setStatusBarColor(this.window, R.color.black)
        SetupUI.setupUI(binding.root)

        setupObservers()

        setupListeners()
    }

    private fun setupListeners() {
        binding.icBack.setOnClickListener {
            finish()
        }

        binding.editOwnerDetails.setOnClickListener {
            if (isEditing) {
                saveUserDetails()
                setupGetter()
            } else {
                enableEditing()
                Toast.makeText(this, "Edit user mode enabled", Toast.LENGTH_SHORT).show()
            }
            isEditing = !isEditing
        }

        binding.editPetDetails.setOnClickListener {
            if (isEditingPet) {
                savePetDetails()
                Toast.makeText(this, "Pet details saved", Toast.LENGTH_SHORT).show()
                setupGetter()
            } else {
                enablePetEditing()
                Toast.makeText(this, "Edit pet mode enabled", Toast.LENGTH_SHORT).show()
            }
            isEditingPet = !isEditingPet
        }
    }

    private fun setupObservers() {
        petViewModel.petLiveData.observe(this@Profile) { pet ->
            pet?.let {
                val petImageView = binding.petProfil
                if (it.petProfileImage.isNotEmpty()) {
                    Log.d("HealthFragment", "Loading image URL: ${it.petProfileImage}")
                    Glide.with(this@Profile.applicationContext)
                        .load(it.petProfileImage)
                        .error(R.drawable.ic_error)
                        .into(petImageView)
                } else {
                    petImageView.setImageResource(R.drawable.ic_error)
                }

                currentPet = it
                petDetails(it)

            }
        }

        userViewModel.userLiveData.observe(this@Profile) { user ->
            user?.let {
                currentUser = it
                userDetails(it)
                val userImageView = binding.actionBarProfile
                if (it.userProfileImage.isNotEmpty()) {
                    Log.d("HealthFragment", "Loading image URL: ${it.userProfileImage}")
                    Glide.with(this@Profile.applicationContext)
                        .load(it.userProfileImage)
                        .error(R.drawable.ic_error)
                        .into(userImageView)
                } else {
                    userImageView.setImageResource(R.drawable.ic_error)
                }
            }
        }

        setupGetter()
    }

    private fun enableEditing() {
        binding.ownerNameValue.visibility = View.GONE
        binding.ownerNameEdit.visibility = View.VISIBLE

        binding.ownerStatusValue.visibility = View.GONE
        binding.ownerStatusEdit.visibility = View.VISIBLE

        binding.ownerBioValue.visibility = View.GONE
        binding.ownerBioEdit.visibility = View.VISIBLE

        binding.ownerNameEdit.setText(binding.ownerNameValue.text)
        binding.ownerStatusEdit.setText(binding.ownerStatusValue.text)
        binding.ownerBioEdit.setText(binding.ownerBioValue.text)

    }

    private fun enablePetEditing() {
        binding.petNameValue.visibility = View.GONE
        binding.petNameEdit.visibility = View.VISIBLE
        binding.petNameEdit.setText(binding.petNameValue.text)

        binding.petTypeValue.visibility = View.GONE
        binding.petTypeEdit.visibility = View.VISIBLE
        binding.petTypeEdit.setText(binding.petTypeValue.text)

        binding.petBreedValue.visibility = View.GONE
        binding.petBreedEdit.visibility = View.VISIBLE
        binding.petBreedEdit.setText(binding.petBreedValue.text)

        binding.petGenderValue.visibility = View.GONE
        binding.petGenderEdit.visibility = View.VISIBLE
        binding.petGenderEdit.setText(binding.petGenderValue.text)

        binding.petWeightValue.visibility = View.GONE
        binding.petWeightEdit.visibility = View.VISIBLE
        binding.petWeightEdit.setText(binding.petWeightValue.text)

        binding.petHeightValue.visibility = View.GONE
        binding.petHeightEdit.visibility = View.VISIBLE
        binding.petHeightEdit.setText(binding.petHeightValue.text)

    }

    private fun saveUserDetails() {

        val username = binding.ownerNameEdit.text.toString().trim()
        val status = binding.ownerStatusEdit.text.toString().trim()
        val bio = binding.ownerBioEdit.text.toString().trim()

        val lettersOnlyPattern = "^[A-Za-z]+$"
        val lettersRequiredPattern = ".*[A-Za-z].*"

        var userState = 0

        if (username.isEmpty()) {
            binding.ownerNameEdit.error = "Username cannot be empty"
        }
        if (!username.matches(lettersOnlyPattern.toRegex())) {
            binding.ownerNameEdit.error = "Username must contain only letters"
        }
        if (status.isEmpty()) {
            binding.ownerStatusEdit.error = "Status cannot be empty"
        }
        if (!status.matches(lettersOnlyPattern.toRegex())) {
            binding.ownerStatusEdit.error = "Status must contain only letters"
        }
        if (bio.isEmpty()) {
            binding.ownerBioEdit.error = "Bio cannot be empty"
        }
        if (!bio.matches(lettersRequiredPattern.toRegex())) {
            binding.ownerBioEdit.error = "Bio must contain at least one letter"
        }
        if (username.matches(lettersOnlyPattern.toRegex()) && status.matches(lettersOnlyPattern.toRegex()) && bio.matches(lettersRequiredPattern.toRegex())) {
            userState = 1
        } else (
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        )

        if (userState == 1) {
            val updatedUser = currentUser?.let {
                User(
                    username = binding.ownerNameEdit.text.toString(),
                    status = binding.ownerStatusEdit.text.toString(),
                    bio = binding.ownerBioEdit.text.toString(),
                    userProfileImage = it.userProfileImage
                )
            }

            val userId = getUserId() ?: return
            if (updatedUser != null) {
                userViewModel.updateUser(userId, updatedUser)
            }

            binding.ownerNameValue.visibility = View.VISIBLE
            binding.ownerNameEdit.visibility = View.GONE

            binding.ownerStatusValue.visibility = View.VISIBLE
            binding.ownerStatusEdit.visibility = View.GONE

            binding.ownerBioValue.visibility = View.VISIBLE
            binding.ownerBioEdit.visibility = View.GONE
            Toast.makeText(this, "User details saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePetDetails() {

        val petName = binding.petNameEdit.text.toString()
        val petGender = binding.petGenderEdit.text.toString()
        val petWeight = binding.petWeightEdit.text.toString()
        val petBreed = binding.petBreedEdit.text.toString()
        val petHeight = binding.petHeightEdit.text.toString()

        validatePetDetails()
        validatePetMeasurements()
        choseGender()

        var petState = 0

        if (petName.isEmpty()) {
            binding.petNameEdit.error = "Name cannot be empty"
        }
        if (petGender.isEmpty()) {
            binding.petGenderEdit.error = "Gender cannot be empty"
        }
        if (petWeight.isEmpty()) {
            binding.petWeightEdit.error = "Weight cannot be empty"
        }
        if (petBreed.isEmpty()) {
            binding.petBreedEdit.error = "Breed cannot be empty"
        }
        if (petHeight.isEmpty()) {
            binding.petHeightEdit.error = "Height cannot be empty"
        }
        if (petName.isNotEmpty() && petGender.isNotEmpty() && petWeight.isNotEmpty() && petBreed.isNotEmpty() && petHeight.isNotEmpty()) {
            petState = 1
        }

        if (petState == 1) {
            val updatedPet = currentPet?.let {
                Pet(
                    petName = binding.petNameEdit.text.toString(),
                    petType = binding.petTypeEdit.text.toString(),
                    petBreed = binding.petBreedEdit.text.toString(),
                    petGender = binding.petGenderEdit.text.toString(),
                    petWeight = binding.petWeightEdit.text.toString().toDoubleOrNull() ?: 0.0,
                    petHeight = binding.petHeightEdit.text.toString().toDoubleOrNull() ?: 0.0,
                    petDateOfBirth = it.petDateOfBirth,
                    petProfileImage = it.petProfileImage
                )
            }

            Log.d(TAG, "Updated Pet: $updatedPet")

            val userId = getUserId() ?: return
            val petId = getPetId(userId)

            Log.d(TAG, "Updating pet in RTDB at path: users/$userId/pets/$petId")

            if (updatedPet != null) {
                petViewModel.updatePet(userId, petId, updatedPet)
            } else {
                Log.e(TAG, "Updated pet is null.")
            }

            binding.petNameValue.visibility = View.VISIBLE
            binding.petNameEdit.visibility = View.GONE

            binding.petTypeValue.visibility = View.VISIBLE
            binding.petTypeEdit.visibility = View.GONE

            binding.petBreedValue.visibility = View.VISIBLE
            binding.petBreedEdit.visibility = View.GONE

            binding.petGenderValue.visibility = View.VISIBLE
            binding.petGenderEdit.visibility = View.GONE

            binding.petWeightValue.visibility = View.VISIBLE
            binding.petWeightEdit.visibility = View.GONE

            binding.petHeightValue.visibility = View.VISIBLE
            binding.petHeightEdit.visibility = View.GONE
        } else{
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validatePetDetails() {
        val petName = binding.petNameEdit.text.toString().trim()
        val petBreed = binding.petBreedEdit.text.toString().trim()

        val namePattern = "^[A-Za-z]+$"
        val isValidName = petName.matches(namePattern.toRegex())
        val isValidBreed = petBreed.matches(namePattern.toRegex())

        if (!isValidName) {
            binding.petNameEdit.error = "Name should only contain letters"
        } else {
            binding.petNameEdit.error = null
        }

        if (!isValidBreed) {
            binding.petBreedEdit.error = "Breed should only contain letters"
        } else {
            binding.petBreedEdit.error = null
        }
    }

    private fun choseGender() {
        val genderEditText = binding.petGenderEdit
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            arrayOf("Male", "Female")
        )
        genderEditText.setAdapter(adapter)

        genderEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = genderEditText.text.toString().trim()
                val validInputs = arrayOf("Male", "Female")

                if (validInputs.none { it.equals(input, ignoreCase = true) }) {
                    binding.petGenderEdit.error = "Please specify Male or Female"
                    genderEditText.error = "Please specify Male or Female"
                } else {
                    binding.petGenderEdit.error = null
                    genderEditText.error = null
                }
            }
        }
    }

    private fun validatePetMeasurements() {
        val petWeightStr = binding.petWeightEdit.text.toString().trim()
        val petHeightStr = binding.petHeightEdit.text.toString().trim()

        val petWeight = petWeightStr.toDoubleOrNull()
        val petHeight = petHeightStr.toDoubleOrNull()

        if (petWeight == null || petWeight <= 0) {
            binding.petWeightEdit.error = "Weight must be greater than 0"
        } else {
            binding.petWeightEdit.error = null
        }

        if (petHeight == null || petHeight <= 0) {
            binding.petHeightEdit.error = "Height must be greater than 0"
        } else {
            binding.petHeightEdit.error = null
        }
    }


    private fun setupGetter() {
        val userId = getUserId() ?: return
        Log.d(TAG, "Health User ID $userId")

        val petId = getPetId(userId)
        Log.d(TAG, "Health Pet ID $petId")

        petViewModel.loadPet(userId, petId)
        userViewModel.loadUser(userId)
    }

    private fun userDetails(user: User) {
        binding.ownerNameValue.text = user.username
        binding.ownerStatusValue.text = user.status
        binding.ownerBioValue.text = user.bio
    }

    private fun petDetails(pet: Pet) {
        binding.petNameValue.text = pet.petName
        binding.petTypeValue.text = pet.petType
        binding.petBreedValue.text = pet.petBreed
        binding.petGenderValue.text = pet.petGender
        binding.petWeightValue.text = pet.petWeight.toString()
        binding.petHeightValue.text = pet.petHeight.toString()
        binding.petBirthday.text =
            getString(
                R.string.pet_birthday,
                pet.petDateOfBirth
            )
//        binding.petAge.text =
//            getString(
//                R.string.pet_age,
//                petAge(pet.petDateOfBirth)
//            )
    }

    private fun petAge(birthday: String): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val birthDate = LocalDate.parse(birthday, formatter)
        val currentDate = LocalDate.now()
        val age = Period.between(birthDate, currentDate)

        val years = age.years
        val months = age.months
        val days = age.days

        val ageString = StringBuilder()

        if (years > 0) {
            ageString.append("$years" + "year${if (years > 1) "" else ""}")
        }
        if (months > 0) {
            if (ageString.isNotEmpty()) ageString.append(", ")
            ageString.append("$months" + "month${if (months > 1) "" else ""}")
        }
        if (days > 0) {
            if (ageString.isNotEmpty()) ageString.append(", ")
            ageString.append("$days" + "day${if (days > 1) "" else ""}")
        }

        val result = ageString.toString()
        Log.d("petAge", "Pet Age: $result")
        return result
    }


}
