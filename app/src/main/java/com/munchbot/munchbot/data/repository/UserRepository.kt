package com.munchbot.munchbot.data.repository

import com.google.firebase.database.DatabaseReference
import com.munchbot.munchbot.data.database.FirebaseDatabase
import com.munchbot.munchbot.data.model.User

class UserRepository {
    private val database: DatabaseReference = FirebaseDatabase.getDatabaseReference()

    fun saveUser(userId: String, user: User) {
        database.child("users").child(userId).child("user").setValue(user)
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        database.child("users").child(userId).child("user").get().addOnSuccessListener {
            callback(it.getValue(User::class.java))
        }.addOnFailureListener {
            callback(null)
        }
    }

    fun updateUser(userId: String, user: User) {
        database.child("users").child(userId).child("user").setValue(user)
    }

}