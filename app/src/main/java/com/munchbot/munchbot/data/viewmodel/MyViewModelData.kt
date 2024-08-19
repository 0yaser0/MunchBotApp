package com.munchbot.munchbot.data.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.munchbot.munchbot.data.repository.DataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyViewModelData @Inject constructor(private val repository: DataRepository) : ViewModel() {

    val username: Flow<String?> = repository.getUsername()
    val status: Flow<String?> = repository.getStatus()
    val bio: Flow<String?> = repository.getBio()

    fun saveUsername(username: String) {
        viewModelScope.launch {
            repository.setUsername(username)
        }
    }

    fun saveStatus(status: String) {
        viewModelScope.launch {
            repository.setStatus(status)
        }
    }

    fun saveBio(bio: String) {
        viewModelScope.launch {
            repository.setBio(bio)
        }
    }
}
