package com.munchbot.munchbot.data.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.munchbot.munchbot.data.model.Medications
import com.munchbot.munchbot.data.repository.MedicationsRepository

class MedicationsViewModel : ViewModel() {
    private val medicationRepository = MedicationsRepository()
    val medicationLiveData = MutableLiveData<Medications?>()
    val medicationsLiveData = MutableLiveData<List<Medications>?>()

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    fun loadMedication(userId: String, medicationId: String) {
        medicationRepository.getMedication(userId, medicationId) { medication ->
            medicationLiveData.postValue(medication)
        }
    }

    fun loadAllMedications(userId: String) {
        medicationRepository.getAllMedications(userId) { medications ->
            Log.d("MedicationsViewModel", "Fetched medications: $medications")
            medicationsLiveData.postValue(medications)
        }
    }


    fun updateMedication(userId: String, medicationId: String, medication: Medications) {
        database.child("users").child(userId).child("medications").child(medicationId).setValue(medication)
            .addOnSuccessListener {
                Log.wtf("Firebase", "Medication updated successfully to RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to update medication to RTDB", e)
            }
    }

    fun deleteMedication(userId: String, medicationId: String) {
        Log.wtf("Firebase", "Deleting medication with ID: $medicationId from user: $userId")

        database.child("users").child(userId).child("medications").child(medicationId).removeValue()
            .addOnSuccessListener {
                Log.wtf("Firebase", "Medication deleted successfully from RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete medication from RTDB", e)
            }
    }
}
