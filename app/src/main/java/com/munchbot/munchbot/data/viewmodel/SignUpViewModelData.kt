package com.munchbot.munchbot.data.viewmodel


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munchbot.munchbot.data.repository.SignUpDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SignUpViewModelData @Inject constructor(private val repository: SignUpDataRepository) : ViewModel() {

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
