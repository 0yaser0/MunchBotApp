package com.munchbot.munchbot.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.munchbot.munchbot.data.repository.SignUpDataRepository

class MyViewModelDataFactory(private val repository: SignUpDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModelData::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModelData(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
