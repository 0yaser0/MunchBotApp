package com.munchbot.munchbot.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.munchbot.munchbot.data.repository.DataRepository

class MyViewModelDataFactory(private val repository: DataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyViewModelData::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyViewModelData(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}