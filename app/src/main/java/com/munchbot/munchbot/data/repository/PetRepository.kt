package com.munchbot.munchbot.data.repository

import com.google.firebase.database.DatabaseReference
import com.munchbot.munchbot.data.database.FirebaseDatabase
import com.munchbot.munchbot.data.model.Pet

class PetRepository {
    private val database: DatabaseReference = FirebaseDatabase.getDatabaseReference()

    fun savePet(userId: String, petId: String, pet: Pet) {
        database.child("users").child(userId).child("pet").child(petId).setValue(pet)
    }

    fun getPet(userId: String, petId: String, callback: (Pet?) -> Unit) {
        database.child("users").child(userId).child("pet").child(petId).get().addOnSuccessListener {
            callback(it.getValue(Pet::class.java))
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun updatePet(userId: String, petId: String, pet: Pet) {
        database.child("users").child(userId).child("pet").child(petId).setValue(pet)
    }
}