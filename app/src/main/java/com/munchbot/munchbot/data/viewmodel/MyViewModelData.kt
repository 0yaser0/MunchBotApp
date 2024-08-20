package com.munchbot.munchbot.data.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munchbot.munchbot.data.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MyViewModelData(private val repository: DataRepository) : ViewModel() {

    fun getUsername(userID: String): Flow<String?> = repository.getUsername(userID)
    fun getStatus(userID: String): Flow<String?> = repository.getStatus(userID)
    fun getBio(userID: String): Flow<String?> = repository.getBio(userID)
    fun getPetType(userID: String): Flow<String?> = repository.getPetType(userID)

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

}
