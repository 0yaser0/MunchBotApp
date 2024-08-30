package com.munchbot.munchbot.data.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.munchbot.munchbot.data.model.Vet
import com.munchbot.munchbot.data.repository.VetRepository

class VetViewModel : ViewModel() {
    private val vetRepository = VetRepository()
    val vetLiveData = MutableLiveData<Vet?>()
    val vetsLiveData = MutableLiveData<List<Vet>?>()

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()


    fun loadVet(userId: String, vetId: String) {
        vetRepository.getVet(userId, vetId) { vet ->
            vetLiveData.postValue(vet)
        }
    }

    fun loadAllVets(userId: String) {
        vetRepository.getAllVets(userId) { vets ->
            vetsLiveData.postValue(vets)
        }
    }

    fun updateVet(userId: String, vetId: String, vet: Vet) {
        database.child("users").child(userId).child("vets").child(userId).child(vetId).setValue(vet)
            .addOnSuccessListener {
                Log.wtf("Firebase", "Vet updated successfully to RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to update vet to RTDB", e)
            }
    }

    fun deleteVet(userId: String, vetId: String) {
        Log.wtf("Firebase", "Deleting vet with ID: $vetId from user: $userId")

        database.child("users").child(userId).child("vets").child(vetId).removeValue()
            .addOnSuccessListener {
                Log.wtf("Firebase", "Vet deleted successfully from RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete vet from RTDB", e)
            }
    }

}

