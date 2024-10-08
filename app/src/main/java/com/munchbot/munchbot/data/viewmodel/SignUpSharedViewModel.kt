package com.munchbot.munchbot.data.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.munchbot.munchbot.data.model.Medications
import com.munchbot.munchbot.data.model.Pet
import com.munchbot.munchbot.data.model.User
import com.munchbot.munchbot.data.model.Vet
import com.munchbot.munchbot.data.repository.MedicationsRepository
import com.munchbot.munchbot.data.repository.PetRepository
import com.munchbot.munchbot.data.repository.UserRepository
import com.munchbot.munchbot.data.repository.VetRepository
import com.munchbot.munchbot.ui.adapters.MedicationsAdapter
import com.munchbot.munchbot.ui.adapters.VetAdapter

class SignUpSharedViewModel : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private val _bio = MutableLiveData<String>()
    val bio: LiveData<String> get() = _bio

    private val _userProfileImage = MutableLiveData<Uri>()
    val userProfileImage: LiveData<Uri> get() = _userProfileImage

    private val _vetProfileImage = MutableLiveData<Uri>()
    val vetProfileImage: LiveData<Uri> get() = _vetProfileImage

    private val _petType = MutableLiveData<String>()
    val petType: LiveData<String> get() = _petType

    private val _petName = MutableLiveData<String>()
    val petName: LiveData<String> get() = _petName

    private val _petGender = MutableLiveData<String>()
    val petGender: LiveData<String> get() = _petGender

    private val _petWeight = MutableLiveData<String>()
    val petWeight: LiveData<String> get() = _petWeight

    private val _petBreed = MutableLiveData<String>()
    val petBreed: LiveData<String> get() = _petBreed

    private val _petDateOfBirth = MutableLiveData<String>()
    val petDateOfBirth: LiveData<String> get() = _petDateOfBirth

    private val _petHeight = MutableLiveData<String>()
    val petHeight: LiveData<String> get() = _petHeight

    private val _petImageProfile = MutableLiveData<Uri>()
    val petImageProfile: LiveData<Uri> get() = _petImageProfile

    fun setUsername(username: String) {
        _username.value = username
    }

    fun setStatus(status: String) {
        _status.value = status
    }

    fun setBio(bio: String) {
        _bio.value = bio
    }

    fun setUserProfileImage(uri: Uri) {
        _userProfileImage.value = uri
    }

    fun setVetProfileImage(uri: Uri) {
        _vetProfileImage.value = uri
    }

    fun setPetType(petType: String) {
        _petType.value = petType
    }

    fun setPetName(name: String) {
        _petName.value = name
    }

    fun setPetGender(gender: String) {
        _petGender.value = gender
    }

    fun setPetWeight(weight: String) {
        _petWeight.value = weight
    }

    fun setPetBreed(breed: String) {
        _petBreed.value = breed
    }

    fun setPetDateOfBirth(dateOfBirth: String) {
        _petDateOfBirth.value = dateOfBirth
    }

    fun setPetHeight(height: String) {
        _petHeight.value = height
    }

    fun setPetImageProfile(imageUri: Uri) {
        _petImageProfile.value = imageUri
    }

    fun getPetImageProfile(): Uri? {
        return _petImageProfile.value
    }

    fun getUserImageProfile(): Uri? {
        return _userProfileImage.value
    }



    fun saveUserAndPet(uid: String) {

        val user = User(
            username = _username.value ?: "",
            status = _status.value ?: "",
            bio = _bio.value ?: "",
            userProfileImage = _userProfileImage.value?.toString() ?: "",
            pets = mapOf()
        )

        val userRepository = UserRepository()
        userRepository.saveUser(uid, user)

        val petId = "${uid}pet"
        val pet = Pet(
            petType = _petType.value ?: "",
            petName = _petName.value ?: "",
            petGender = _petGender.value ?: "",
            petWeight = _petWeight.value?.toDoubleOrNull() ?: 0.0,
            petBreed = _petBreed.value ?: "",
            petDateOfBirth = _petDateOfBirth.value ?: "",
            petHeight = _petHeight.value?.toDoubleOrNull() ?: 0.0,
            petProfileImage = _petImageProfile.value?.toString() ?: ""
        )

        val petRepository = PetRepository()
        petRepository.savePet(uid, petId, pet)
    }

    fun saveNewVet(
        uid: String,
        name: String,
        category: String,
        phoneNumber: String,
        email: String,
        vetAdapter: VetAdapter,
        imageUrl: String? = null
    ) {
        val vetId = "${uid}vet${System.currentTimeMillis()}"

        val vet = Vet(
            vetProfileImage = imageUrl,
            name = name,
            category = category,
            phoneNumber = phoneNumber,
            email = email,
            vetId = vetId
        )

        Log.d("Vet Object", vet.toString())

        val vetRepository = VetRepository()
        vetRepository.saveVet(uid, vetId, vet)
        vetAdapter.addVet(vet)
    }

    fun saveNewMedication(
        uid: String,
        name: String,
        dosage: String,
        amount: String,
        duration: String,
        notify: Boolean,
        medicationAdapter: MedicationsAdapter
    ) {
        val medicationId = "${uid}medication${System.currentTimeMillis()}"

        val medication = Medications(
            name = name,
            dosage = dosage,
            amount = amount,
            duration = duration,
            notify = notify,
            medicationId = medicationId
        )

        Log.d("Medication Object", medication.toString())

        val medicationRepository = MedicationsRepository()
        medicationRepository.saveMedication(uid, medicationId, medication)
        medicationAdapter.addMedication(medication)
    }

}
