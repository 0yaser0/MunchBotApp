package com.munchbot.munchbot.data.viewmodel


import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munchbot.munchbot.data.repository.SignUpDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SignUpViewModelData(private val repository: SignUpDataRepository) : ViewModel() {

    fun getUsername(userID: String): Flow<String?> = repository.getUsername(userID)
    fun getStatus(userID: String): Flow<String?> = repository.getStatus(userID)
    fun getBio(userID: String): Flow<String?> = repository.getBio(userID)
    fun getPetType(userID: String): Flow<String?> = repository.getPetType(userID)
    fun getPetName(userID: String): Flow<String?> = repository.getPetName(userID)
    fun getPetGender(userID: String): Flow<String?> = repository.getPetGender(userID)
    fun getPetWeight(userID: String): Flow<String?> = repository.getPetWeight(userID)
    fun getPetBreed(userID: String): Flow<String?> = repository.getPetBreed(userID)
    fun getPetDateOfBirth(userID: String): Flow<String?> = repository.getPetDateOfBirth(userID)
    fun getPetHeight(userID: String): Flow<String?> = repository.getPetHeight(userID)
    fun getUserProfileImage(userID: String): Flow<String?> = repository.getUserProfileImage(userID)
    fun getPetProfileImage(userID: String): Flow<String?> = repository.getPetProfileImage(userID)

    fun saveUsername(userID: String, username: String) {
        viewModelScope.launch {
            repository.setUsername(userID, username)
        }
    }

    fun saveStatus(userID: String, status: String) {
        viewModelScope.launch {
            repository.setStatus(userID, status)
        }
    }

    fun saveBio(userID: String, bio: String) {
        viewModelScope.launch {
            repository.setBio(userID, bio)
        }
    }

    fun savePetType(userID: String, petType: String) {
        viewModelScope.launch {
            repository.setPetType(userID, petType)
        }
    }

    fun savePetName(userID: String, petName: String) {
        viewModelScope.launch {
            repository.setPetName(userID, petName)
        }
    }

    fun savePetGender(userID: String, petGender: String) {
        viewModelScope.launch {
            repository.setPetGender(userID, petGender)
        }
    }

    fun savePetWeight(userID: String, petWeight: String) {
        viewModelScope.launch {
            repository.setPetWeight(userID, petWeight)
        }
    }

    fun savePetBreed(userID: String, petBreed: String) {
        viewModelScope.launch {
            repository.setPetBreed(userID, petBreed)
        }
    }

    fun savePetDateOfBirth(userID: String, petDateOfBirth: String) {
        viewModelScope.launch {
            repository.setPetDateOfBirth(userID, petDateOfBirth)
        }
    }

    fun savePetHeight(userID: String, petHeight: String) {
        viewModelScope.launch {
            repository.setPetHeight(userID, petHeight)
        }
    }

    fun saveUserProfileImage(userID: String, uri: Uri) {
        viewModelScope.launch {
            repository.saveUserProfileImage(userID, uri)
        }
    }

    fun savePetProfileImage(userID: String, uri: Uri) {
        viewModelScope.launch {
            repository.savePetProfileImage(userID, uri)
        }
    }
}

class SignUpSharedViewModel : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _status = MutableLiveData<String>()
    val status: LiveData<String> get() = _status

    private val _bio = MutableLiveData<String>()
    val bio: LiveData<String> get() = _bio

    private val _userProfileImage = MutableLiveData<Uri>()
    val userProfileImage: LiveData<Uri> get() = _userProfileImage

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

    fun userProfileImage(uri: Uri) {
        _userProfileImage.value = uri
    }

    fun setPetType(petType: String) {
        _petType.value = petType
    }

    fun savePetName(name: String) {
        _petName.value = name
    }

    fun savePetGender(gender: String) {
        _petGender.value = gender
    }

    fun savePetWeight(weight: String) {
        _petWeight.value = weight
    }

    fun savePetBreed(breed: String) {
        _petBreed.value = breed
    }

    fun savePetDateOfBirth(dateOfBirth: String) {
        _petDateOfBirth.value = dateOfBirth
    }

    fun savePetHeight(height: String) {
        _petHeight.value = height
    }

    fun savePetImageProfile(imageUri: Uri) {
        _petImageProfile.value = imageUri
    }

}
