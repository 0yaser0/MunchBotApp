package com.munchbot.munchbot.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchbot.munchbot.data.model.Vet

class VetRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    fun saveVet(userId: String, vetId: String, vet: Vet) {
        database.child("users").child(userId).child("vets").child(vetId).setValue(vet)
            .addOnSuccessListener {
                Log.wtf("Firebase", "Vet added successfully to RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to add vet to RTDB", e)
            }
    }

    fun getVet(userId: String, vetId: String, callback: (Vet?) -> Unit) {
        database.child("users").child(userId).child("vets").child(vetId).get()
            .addOnSuccessListener {
                callback(it.getValue(Vet::class.java))
            }.addOnFailureListener {
                callback(null)
            }
    }

    fun getAllVets(userId: String, callback: (List<Vet>?) -> Unit) {
        database.child("users").child(userId).child("vets")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val vets = mutableListOf<Vet>()
                    for (vetSnapshot in snapshot.children) {
                        val vet = vetSnapshot.getValue(Vet::class.java)
                        if (vet != null) {
                            vets.add(vet)
                        }
                    }
                    callback(vets)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

}
