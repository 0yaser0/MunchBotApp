package com.munchbot.munchbot.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.munchbot.munchbot.data.model.User
import com.munchbot.munchbot.data.repository.UserRepository

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    val userLiveData = MutableLiveData<User?>()

    fun loadUser(userId: String) {
        userRepository.getUser(userId) { user ->
            userLiveData.postValue(user)
        }
    }

    fun updateUser(userId: String, user: User) {
        userRepository.updateUser(userId, user)
    }
}
