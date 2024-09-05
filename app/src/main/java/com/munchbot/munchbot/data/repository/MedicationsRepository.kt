package com.munchbot.munchbot.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchbot.munchbot.data.model.Medications

class MedicationsRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    fun saveMedication(userId: String, medicationId: String, medication: Medications) {
        database.child("users").child(userId).child("medications").child(medicationId).setValue(medication)
            .addOnSuccessListener {
                Log.wtf("Firebase", "Medication added successfully to RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to add medication to RTDB", e)
            }
    }

    fun getMedication(userId: String, medicationId: String, callback: (Medications?) -> Unit) {
        database.child("users").child(userId).child("medications").child(medicationId).get()
            .addOnSuccessListener {
                callback(it.getValue(Medications::class.java))
            }.addOnFailureListener {
                callback(null)
            }
    }

    fun getAllMedications(userId: String, callback: (List<Medications>?) -> Unit) {
        database.child("users").child(userId).child("medications")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val medications = mutableListOf<Medications>()
                    for (medicationSnapshot in snapshot.children) {
                        val medication = medicationSnapshot.getValue(Medications::class.java)
                        if (medication != null) {
                            medications.add(medication)
                        }
                    }
                    callback(medications)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }
}
