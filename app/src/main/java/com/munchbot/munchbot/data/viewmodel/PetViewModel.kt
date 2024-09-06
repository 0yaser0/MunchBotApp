package com.munchbot.munchbot.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.munchbot.munchbot.data.model.Pet
import com.munchbot.munchbot.data.repository.PetRepository

class PetViewModel : ViewModel() {
    private val petRepository = PetRepository()
    val petLiveData = MutableLiveData<Pet?>()

    fun loadPet(userId: String, petId: String) {
        petRepository.getPet(userId, petId) { pet ->
            petLiveData.postValue(pet)
        }
    }

    fun updatePet(userId: String, petId: String, pet: Pet) {
        petRepository.updatePet(userId, petId, pet)
    }
}
